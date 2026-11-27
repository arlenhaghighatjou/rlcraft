package com.Shultrea.Rin.Ench0_4_0;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.Shultrea.Rin.Enchantments_Sector.Smc_040;
import com.Shultrea.Rin.Utility_Sector.EnchantmentsUtility;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.fml.common.eventhandler.EventPriority;

public class EnchantmentMultifisher extends Enchantment {
   public EnchantmentMultifisher() {
      super(Rarity.VERY_RARE, EnumEnchantmentType.FISHING_ROD, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
      this.setName("Multifisher");
      this.setRegistryName("Multifisher");
   }

   public int getMaxLevel() {
      return 3;
   }

   public int getMinEnchantability(int par1) {
      return 15 + 15 * par1;
   }

   public int getMaxEnchantability(int par1) {
      return super.getMinEnchantability(par1) + 30;
   }

   public boolean canApplyTogether(Enchantment fTest) {
      return super.canApplyTogether(fTest);
   }

   public boolean canApply(ItemStack fTest) {
      return super.canApply(fTest);
   }

   public void register(EventBus bus) {
      bus.register(EntityJoinWorldEvent.class, EventPriority.HIGHEST, false, this::onEvent);
      bus.register(RightClickItem.class, this::onEvent);
   }

   public void onEvent(EntityJoinWorldEvent fEvent) {
      if (fEvent.getEntity() instanceof EntityFishHook) {
         EntityFishHook hook = (EntityFishHook)fEvent.getEntity();
         EntityPlayer fisher = hook.getAngler();
         ItemStack fishingRod = fisher.getHeldItemMainhand();
         if (fishingRod == null || fishingRod == ItemStack.EMPTY) {
            fishingRod = fisher.getHeldItemOffhand();
            if (fishingRod == null || fishingRod == ItemStack.EMPTY) {
               return;
            }
         }

         if (!fishingRod.hasTagCompound()) {
            fishingRod.setTagCompound(new NBTTagCompound());
         }

         if (fishingRod.getTagCompound().getBoolean("isMultiFishing")) {
            return;
         }

         int level = EnchantmentHelper.getEnchantmentLevel(Smc_040.Multifisher, fishingRod);
         if (level <= 0) {
            return;
         }

         fishingRod.getTagCompound().setBoolean("isMultiFishing", true);

         for(int x = 0; x < level; ++x) {
            EntityFishHook multiHook = new EntityFishHook(fisher.world, fisher);
            multiHook.copyLocationAndAnglesFrom(hook);
            multiHook.rotationYaw = EnchantmentsUtility.RANDOM.nextFloat() * 25.0F * (float)fisher.getRNG().nextInt() * -1.0F;
            this.hookShoot(multiHook);
            fisher.world.spawnEntity(multiHook);
         }
      }

   }

   public void onEvent(RightClickItem event) {
      if (event.getEntityPlayer() != null) {
         EntityPlayer player = event.getEntityPlayer();
         ItemStack stack = player.getHeldItemMainhand();
         if (!(stack.getItem() instanceof ItemFishingRod)) {
            stack = player.getHeldItemOffhand();
            if (!(stack.getItem() instanceof ItemFishingRod)) {
               return;
            }
         }

         int level = EnchantmentHelper.getEnchantmentLevel(this, stack);
         if (level > 0) {
            if (!stack.hasTagCompound()) {
               stack.setTagCompound(new NBTTagCompound());
            }

            stack.getTagCompound().setBoolean("isMultiFishing", player.fishEntity != null);
         }
      }
   }

   private void hookShoot(EntityFishHook fishHook) {
      EaglercraftRandom rand = new EaglercraftRandom();
      float f = fishHook.rotationYaw + (fishHook.rotationYaw - fishHook.prevRotationYaw);
      float f1 = fishHook.prevRotationPitch + (fishHook.rotationPitch - fishHook.prevRotationPitch);
      float f2 = MathHelper.cos(-f1 * 0.017453292F - 3.1415927F);
      float f3 = MathHelper.sin(-f1 * 0.017453292F - 3.1415927F);
      float f4 = -MathHelper.cos(-f * 0.017453292F);
      float f5 = MathHelper.sin(-f * 0.017453292F);
      double d0 = fishHook.getAngler().prevPosX + (fishHook.getAngler().posX - fishHook.getAngler().prevPosX) - (double)f3 * 0.3D;
      double d1 = fishHook.getAngler().prevPosY + (fishHook.getAngler().posY - fishHook.getAngler().prevPosY) + (double)fishHook.getAngler().getEyeHeight();
      double d2 = fishHook.getAngler().prevPosZ + (fishHook.getAngler().posZ - fishHook.getAngler().prevPosZ) - (double)f2 * 0.3D;
      fishHook.setLocationAndAngles(d0, d1, d2, f1, f);
      fishHook.motionX = (double)(-f3);
      fishHook.motionY = (double)MathHelper.clamp(-(f5 / f4), -5.0F, 5.0F);
      fishHook.motionZ = (double)(-f2);
      float f6 = MathHelper.sqrt(fishHook.motionX * fishHook.motionX + fishHook.motionY * fishHook.motionY + fishHook.motionZ * fishHook.motionZ);
      fishHook.motionX *= 0.6D / (double)f6 + 0.5D + rand.nextGaussian() * 0.0045D;
      fishHook.motionY *= 0.6D / (double)f6 + 0.5D + rand.nextGaussian() * 0.0045D;
      fishHook.motionZ *= 0.6D / (double)f6 + 0.5D + rand.nextGaussian() * 0.0045D;
      float f7 = MathHelper.sqrt(fishHook.motionX * fishHook.motionX + fishHook.motionZ * fishHook.motionZ);
      fishHook.rotationYaw = (float)(MathHelper.atan2(fishHook.motionX, fishHook.motionZ) * 57.29577951308232D);
      fishHook.rotationPitch = (float)(MathHelper.atan2(fishHook.motionY, (double)f7) * 57.29577951308232D);
      fishHook.prevRotationYaw = fishHook.rotationYaw;
      fishHook.prevRotationPitch = fishHook.rotationPitch;
   }
}
