package com.Shultrea.Rin.Ench0_3_0;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enchantments_Sector.Smc_030;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import com.Shultrea.Rin.Utility_Sector.MsgSP_Particle;
import com.Shultrea.Rin.Utility_Sector.SMEnetwork;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EnchantmentSmelter extends EnchantmentBase {
   EaglercraftRandom random = new EaglercraftRandom();

   public EnchantmentSmelter() {
      super(Rarity.VERY_RARE, EnumEnchantmentType.DIGGER, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.setName("Smelter");
      this.setRegistryName("Smelter");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.SmelterEnable;
   }

   public int getMinEnchantability(int par1) {
      return 30 * par1;
   }

   public int getMaxEnchantability(int par1) {
      return this.getMinEnchantability(par1) + 50;
   }

   public boolean canApplyTogether(Enchantment fTest) {
      return fTest == Enchantments.SILK_TOUCH ? false : super.canApplyTogether(fTest);
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void onBlockDropSmelt(HarvestDropsEvent fEvent) {
      if (fEvent.getHarvester() != null && ModConfig.enabled.SmelterEnable) {
         int fortuneLevel = fEvent.getFortuneLevel();
         ItemStack tool = fEvent.getHarvester().getHeldItemMainhand();
         if (fEvent.getHarvester().getHeldItemMainhand() == null) {
            return;
         }

         if (EnchantmentHelper.getEnchantmentLevel(Smc_030.Smelter, tool) <= 0) {
            return;
         }

         if (!fEvent.isSilkTouching() && !fEvent.getHarvester().isSneaking()) {
            ItemStack result = FurnaceRecipes.instance().getSmeltingResult(new ItemStack(fEvent.getState().getBlock(), 1, fEvent.getState().getBlock().getMetaFromState(fEvent.getState())));
            if (fEvent.getDrops().size() <= 0) {
               return;
            }

            if (result.getItem() instanceof ItemAir) {
               return;
            }

            if (result != null && !(result.getItem() instanceof ItemAir)) {
               int nItems = 0;
               int nParticles = 2;
               if (fortuneLevel > 0 && !(result.getItem() instanceof ItemBlock)) {
                  nItems = 1 + this.random.nextInt(fortuneLevel + 1);
               } else if (fortuneLevel <= 0 && !(result.getItem() instanceof ItemBlock)) {
                  nItems = 1;
               }

               if (result.getItem() instanceof ItemBlock) {
                  nItems = 1;
               }

               for(int i = 0; i < nItems; ++i) {
                  float f = this.random.nextFloat() * 0.6F + 0.1F;
                  float f1 = this.random.nextFloat() * 0.6F + 0.1F;
                  float f2 = this.random.nextFloat() * 0.6F + 0.1F;
                  float f3 = 0.025F;
                  EntityItem eitem = new EntityItem(fEvent.getWorld(), (double)((float)fEvent.getPos().getX() + f), (double)((float)fEvent.getPos().getY() + f1), (double)((float)fEvent.getPos().getZ() + f2), result.copy());
                  eitem.motionX = this.random.nextGaussian() * (double)f3;
                  eitem.motionY = this.random.nextGaussian() * (double)f3 + 0.20000000298023224D;
                  eitem.motionZ = this.random.nextGaussian() * (double)f3;
                  fEvent.getWorld().spawnEntity(eitem);

                  for(int j = 0; j < nParticles; ++j) {
                     double rx = eitem.posX + (double)this.random.nextFloat() - 0.5D;
                     double ry = eitem.posY + ((double)this.random.nextFloat() + 0.5D) * 0.15D;
                     double rz = eitem.posZ + (double)this.random.nextFloat() - 0.5D;
                     SMEnetwork.net.sendToAll(new MsgSP_Particle("flame", rx, ry, rz, 0.0D, 0.0D, 0.0D));
                  }
               }

               fEvent.setDropChance(0.0F);
               return;
            }
         }
      }

   }
}
