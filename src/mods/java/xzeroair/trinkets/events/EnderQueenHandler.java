package xzeroair.trinkets.events;

import com.google.common.base.Predicates;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xzeroair.trinkets.api.TrinketHelper;
import xzeroair.trinkets.entity.ai.EnderAiEdit;
import xzeroair.trinkets.entity.ai.EnderMoveAI;
import xzeroair.trinkets.entity.ai.EnderQueensKnightAI;
import xzeroair.trinkets.init.ModItems;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.config.trinkets.ConfigEnderCrown;

public class EnderQueenHandler {
   private ConfigEnderCrown serverConfig;

   public EnderQueenHandler() {
      this.serverConfig = TrinketsConfig.SERVER.Items.ENDER_CROWN;
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void soundEvent(PlaySoundEvent event) {
      EntityPlayerSP player = Minecraft.getMinecraft().player;
      if (player != null && player.world != null && (TrinketHelper.AccessoryCheck(player, ModItems.trinkets.TrinketEnderTiara) || TrinketHelper.getSlotInfoForArmor(player, (stack) -> {
         return !stack.isEmpty() && stack.getItem().getRegistryName().toString().contentEquals(ModItems.trinkets.TrinketEnderTiara.toString());
      }) != null || TrinketHelper.entityHasAbility("ender_queen".toString(), player)) && event.getSound().getSoundLocation().toString().contentEquals("minecraft:entity.endermen.stare")) {
         event.setResultSound((ISound)null);
      }

   }

   @SubscribeEvent
   public void EndermanJoinWorld(EntityJoinWorldEvent event) {
      if (event.getEntity() instanceof EntityEnderman) {
         EntityEnderman ender = (EntityEnderman)event.getEntity();

         for(Object a : ender.targetTasks.taskEntries.toArray()) {
            EntityAIBase ai = ((EntityAITaskEntry)a).action;
            if (ai.toString().startsWith("net.minecraft.entity.monster.EntityEnderman$AIFindPlayer")) {
               ender.targetTasks.removeTask(ai);
            }
         }

         ender.targetTasks.addTask(1, new EnderAiEdit(ender));
         ender.targetTasks.addTask(2, new EnderQueensKnightAI(ender));
         if (this.serverConfig.Follow) {
            ender.targetTasks.addTask(3, new EnderMoveAI(ender));
         }
      }

   }

   @SubscribeEvent
   public void EnderTeleportEvent(EnderTeleportEvent event) {
      if (TrinketsConfig.SERVER.Items.ENDER_CROWN.teleport) {
         Entity entity = event.getEntity();
         if (entity != null) {
            boolean isPlayer = entity instanceof EntityPlayer;
            boolean pvpEnabled = false;
            if (isPlayer) {
               try {
                  if (entity instanceof EntityPlayerMP) {
                     pvpEnabled = ((EntityPlayerMP)entity).getServer().isPVPEnabled();
                  }
               } catch (Exception var7) {
                  var7.printStackTrace();
               }
            }

            if (entity instanceof EntityEnderman || isPlayer && pvpEnabled) {
               AxisAlignedBB bBox = entity.getEntityBoundingBox().grow(16.0D, 4.0D, 16.0D);
               List<EntityPlayer> entLivList = entity.getEntityWorld().getEntitiesWithinAABB(EntityPlayer.class, bBox, Predicates.and(EntitySelectors.NOT_SPECTATING, (player) -> {
                  return player != null && TrinketHelper.AccessoryCheck(player, ModItems.trinkets.TrinketEnderTiara) || TrinketHelper.getSlotInfoForArmor(player, (stack) -> {
                     return !stack.isEmpty() && stack.getItem().getRegistryName().toString().contentEquals(ModItems.trinkets.TrinketEnderTiara.toString());
                  }) != null || TrinketHelper.entityHasAbility("ender_queen".toString(), player);
               }));
               if (!entLivList.isEmpty() && !entLivList.contains(entity) && !entity.isInWater()) {
                  event.setCanceled(true);
               }
            }

         }
      }
   }
}
