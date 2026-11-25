package c4.corpsecomplex.common.modules.spawning;

import c4.corpsecomplex.common.Module;
import c4.corpsecomplex.common.modules.compatibility.reskillable.ReskillableModule;
import c4.corpsecomplex.common.modules.spawning.capability.DeathLocation;
import c4.corpsecomplex.common.modules.spawning.capability.IDeathLocation;
import java.util.ArrayList;
import java.util.Collections;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.items.ItemHandlerHelper;

public class SpawningModule extends Module {
   @ObjectHolder("corpsecomplex:scroll")
   public static ItemScroll scroll;
   public static boolean returnScroll;
   public static boolean registerScroll;
   public static boolean giveScroll;
   private static String[] spawnMobs;
   private static boolean disableBeds;
   private static String[] respawnItems;
   private static boolean cfgEnabled;

   public SpawningModule() {
      super("Respawning", "Customize general respawning rules");
      this.submoduleClasses = new ArrayList();
      this.addSubmodule("reskillable", ReskillableModule.class);
   }

   public void loadModuleConfig() {
      this.setCategoryComment();
      cfgEnabled = this.getBool("Enable Respawning Module", false, "Set to true to enable respawning module features", true);
      spawnMobs = this.getStringList("Spawn Mobs on Death", new String[0], "Mobs to spawn at location on death", false);
      disableBeds = this.getBool("Disable Bed Spawn Points", false, "Set to true to disable beds setting spawn points", false);
      returnScroll = this.getBool("Return Scroll", false, "Set to true to enable a craftable return scroll that teleports players to their death location", true);
      giveScroll = this.getBool("Give Scroll on Respawn", false, "If Return Scroll is true, set to true to automatically give players a return scroll on respawn", false);
      respawnItems = this.getStringList("Respawn Items", new String[0], "A list of items to give players on respawn. The format is modid:item_name;stacksize;metadata.", false);
      registerScroll = cfgEnabled && returnScroll;
   }

   public void initPropOrder() {
      this.propOrder = new ArrayList(Collections.singletonList("Enable Respawning Module"));
   }

   public void setEnabled() {
      this.enabled = cfgEnabled;
   }

   @SubscribeEvent
   public void onPlayerDeath(LivingDeathEvent e) {
      if (e.getEntity() instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)e.getEntityLiving();
         if (returnScroll) {
            IDeathLocation deathLoc = (IDeathLocation)player.getCapability(DeathLocation.Provider.DEATH_LOC_CAP, (EnumFacing)null);
            if (deathLoc != null) {
               deathLoc.setUsedScroll(false);
               deathLoc.setDeathLocation(player.getPosition());
               deathLoc.setDeathDimension(player.getEntityWorld().provider.getDimension());
               deathLoc.setHasDeathLocation(true);
            }
         }

         if (disableBeds) {
            player.setSpawnPoint((BlockPos)null, false);
         }

         for(String s : spawnMobs) {
            Entity entity = EntityList.createEntityByIDFromName(new ResourceLocation(s), player.world);
            if (entity != null) {
               entity.getEntityData().setBoolean("PersistenceRequired", true);
               if (entity instanceof EntityLiving) {
                  EntityLiving entityliving = (EntityLiving)entity;
                  entity.setLocationAndAngles(player.posX + 2.0D, player.posY, player.posZ + 2.0D, MathHelper.wrapDegrees(player.world.rand.nextFloat() * 360.0F), 0.0F);
                  entityliving.rotationYawHead = entityliving.rotationYaw;
                  entityliving.renderYawOffset = entityliving.rotationYaw;
                  entityliving.onInitialSpawn(player.world.getDifficultyForLocation(new BlockPos(entityliving)), (IEntityLivingData)null);
                  player.world.spawnEntity(entity);
                  entityliving.playLivingSound();
               }
            }
         }
      }

   }

   @SubscribeEvent
   public void onPlayerRespawn(Clone e) {
      if (e.isWasDeath()) {
         if (returnScroll) {
            EntityPlayer player = e.getEntityPlayer();
            EntityPlayer oldPlayer = e.getOriginal();
            IDeathLocation oldDeathLoc = (IDeathLocation)oldPlayer.getCapability(DeathLocation.Provider.DEATH_LOC_CAP, (EnumFacing)null);
            IDeathLocation newDeathLoc = (IDeathLocation)player.getCapability(DeathLocation.Provider.DEATH_LOC_CAP, (EnumFacing)null);
            if (oldDeathLoc != null && newDeathLoc != null) {
               newDeathLoc.setUsedScroll(oldDeathLoc.hasUsedScroll());
               newDeathLoc.setDeathDimension(oldDeathLoc.getDeathDimension());
               newDeathLoc.setDeathLocation(oldDeathLoc.getDeathLocation());
               newDeathLoc.setHasDeathLocation(oldDeathLoc.hasDeathLocation());
            }

            if (!e.getEntityPlayer().world.isRemote && giveScroll) {
               ItemHandlerHelper.giveItemToPlayer(e.getEntityPlayer(), new ItemStack(scroll));
            }
         }

         if (!e.getEntityPlayer().world.isRemote && respawnItems.length > 0) {
            for(String s : respawnItems) {
               String[] parse = s.split(";");
               Item item = Item.getByNameOrId(parse[0]);
               if (item != null) {
                  int stacksize = parse.length > 1 ? Integer.parseInt(parse[1]) : 1;
                  int metadata = parse.length > 2 ? Integer.parseInt(parse[2]) : 0;
                  ItemHandlerHelper.giveItemToPlayer(e.getEntityPlayer(), new ItemStack(item, stacksize, metadata));
               }
            }
         }

      }
   }
}
