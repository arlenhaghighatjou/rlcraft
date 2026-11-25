package net.silentchaos512.scalinghealth.api;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.silentchaos512.scalinghealth.config.Config;
import net.silentchaos512.scalinghealth.event.BlightHandler;
import net.silentchaos512.scalinghealth.event.DifficultyHandler;
import net.silentchaos512.scalinghealth.utils.SHPlayerDataHandler;
import net.silentchaos512.scalinghealth.utils.StackProducer;

public final class ScalingHealthAPI {
   private ScalingHealthAPI() {
      throw new IllegalAccessError("Utility class");
   }

   public static void spawnWithoutDifficulty(World world, EntityLivingBase entity) {
      entity.getEntityData().setShort("scalinghealth:difficulty", (short)-1);
      world.spawnEntity(entity);
   }

   public static double getAreaDifficulty(World world, BlockPos pos) {
      return Config.Difficulty.AREA_DIFFICULTY_MODE.getAreaDifficulty(world, pos);
   }

   public static double getPlayerDifficulty(EntityPlayer player) {
      SHPlayerDataHandler.PlayerData data = SHPlayerDataHandler.get(player);
      return data == null ? Double.NaN : data.getDifficulty();
   }

   public static void addPlayerDifficulty(EntityPlayer player, double amount) {
      SHPlayerDataHandler.PlayerData data = SHPlayerDataHandler.get(player);
      if (data != null) {
         data.incrementDifficulty(amount, false);
      }

   }

   public static double getEntityDifficulty(EntityLivingBase entity) {
      return entity instanceof EntityPlayer ? getPlayerDifficulty((EntityPlayer)entity) : (double)entity.getEntityData().getShort("scalinghealth:difficulty");
   }

   public static boolean isBlight(EntityLivingBase entity) {
      return BlightHandler.isBlight(entity);
   }

   public static void addBlightEquipment(StackProducer producer, EntityEquipmentSlot slot, int tier) {
      switch(slot) {
      case CHEST:
         DifficultyHandler.INSTANCE.mapChestplates.put(producer, tier);
         break;
      case FEET:
         DifficultyHandler.INSTANCE.mapBoots.put(producer, tier);
         break;
      case HEAD:
         DifficultyHandler.INSTANCE.mapHelmets.put(producer, tier);
         break;
      case LEGS:
         DifficultyHandler.INSTANCE.mapLeggings.put(producer, tier);
         break;
      case MAINHAND:
         DifficultyHandler.INSTANCE.mapMainhands.put(producer, tier);
         break;
      case OFFHAND:
         DifficultyHandler.INSTANCE.mapOffhands.put(producer, tier);
      }

   }

   public static void addBlightEquipment(ItemStack stack, EntityEquipmentSlot slot, int tier) {
      addBlightEquipment(new StackProducer(stack), slot, tier);
   }

   public static void addMobSpawnPotion(Potion potion, int cost, int amplifier) {
      DifficultyHandler.INSTANCE.potionMap.put(potion, cost, amplifier);
   }
}
