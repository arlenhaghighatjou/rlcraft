package svenhjol.charm.crafting.feature;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent.EnteringChunk;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import svenhjol.charm.crafting.ai.AIFormEndermite;
import svenhjol.charm.crafting.block.BlockEnderPearl;
import svenhjol.meson.Feature;
import svenhjol.meson.handler.RecipeHandler;
import svenhjol.meson.helper.WorldHelper;
import svenhjol.meson.registry.ProxyRegistry;

public class EnderPearlBlock extends Feature {
   public static BlockEnderPearl block;
   public static float hardness;
   public static int range;
   public static boolean showParticles;
   public static boolean teleportStabilize;
   public static double endermiteChance;

   public EnderPearlBlock() {
   }

   public String getDescription() {
      return "A storage block for ender pearls. Eating a chorus fruit will teleport you to a nearby ender pearl block.\nIf a silverfish burrows into an ender pearl block, it will become an endermite.";
   }

   public void configure() {
      super.configure();
      endermiteChance = this.propDouble("Silverfish burrowing", "Chance (out of 1.0) of a silverfish burrowing into an Ender Pearl Block, creating an Endermite.", 0.8D);
      teleportStabilize = this.propBoolean("Teleport stabilization", "If true, eating a Chorus Fruit while in range of an Ender Pearl Block will teleport you to it.", true);
      hardness = 2.0F;
      range = 8;
      showParticles = true;
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      block = new BlockEnderPearl();
      RecipeHandler.addShapedRecipe(ProxyRegistry.newStack(block, 1), "EEE", "EEE", "EEE", 'E', Items.ENDER_PEARL);
      RecipeHandler.addShapelessRecipe(ProxyRegistry.newStack(Items.ENDER_PEARL, 9), ProxyRegistry.newStack(block));
   }

   public static boolean onChorusFruitEaten(World world, EntityLivingBase entity) {
      boolean didTeleport = false;
      if (!world.isRemote && entity != null && teleportStabilize) {
         double distance = 0.0D;
         BlockPos selectedPos = null;
         BlockPos entityPos = entity.getPosition();

         for(BlockPos pos : BlockPos.getAllInBox(entityPos.add(-range, -range, -range), entityPos.add(range, range, range))) {
            if (world.getBlockState(pos).getBlock() == block && !pos.up(1).equals(entityPos) && world.getBlockState(pos.up(1)).getMaterial() == Material.AIR && world.getBlockState(pos.up(2)).getMaterial() == Material.AIR) {
               double d = WorldHelper.getDistanceSq(entityPos, pos.up(1));
               if (distance == 0.0D || d < distance) {
                  distance = d;
                  selectedPos = pos.up(1);
               }
            }
         }

         if (selectedPos != null) {
            double x = (double)selectedPos.getX() + 0.5D;
            double y = (double)selectedPos.getY();
            double z = (double)selectedPos.getZ() + 0.5D;
            if (entity.attemptTeleport(x, y, z)) {
               world.playSound((EntityPlayer)null, x, y, z, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
               entity.playSound(SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, 1.0F, 1.0F);
               didTeleport = true;
            }
         }

         if (didTeleport && entity instanceof EntityPlayer) {
            ((EntityPlayer)entity).getCooldownTracker().setCooldown(Items.CHORUS_FRUIT, 20);
         }
      }

      return didTeleport;
   }

   @SubscribeEvent
   public void onEnterChunk(EnteringChunk event) {
      if (event.getEntity() instanceof EntitySilverfish) {
         EntitySilverfish silverfish = (EntitySilverfish)event.getEntity();

         for(EntityAITaskEntry task : silverfish.tasks.taskEntries) {
            if (task.action instanceof AIFormEndermite) {
               return;
            }
         }

         silverfish.tasks.addTask(2, new AIFormEndermite(silverfish));
      }

   }

   public boolean hasSubscriptions() {
      return true;
   }
}
