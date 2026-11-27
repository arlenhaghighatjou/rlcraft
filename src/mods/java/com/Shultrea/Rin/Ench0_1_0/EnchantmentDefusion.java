package com.Shultrea.Rin.Ench0_1_0;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enchantments_Sector.Smc_010;
import com.Shultrea.Rin.Interfaces.IEnchantmentDamage;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import com.Shultrea.Rin.Main_Sector.somanyenchantments;
import com.Shultrea.Rin.Utility_Sector.UtilityAccessor;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDamage;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;

public class EnchantmentDefusion extends EnchantmentBase implements IEnchantmentDamage {
   public EnchantmentDefusion() {
      super(Rarity.RARE, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.setName("Defusion");
      this.setRegistryName("Defusion");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.DefusionEnable;
   }

   public int getMaxLevel() {
      return ModConfig.level.Defusion;
   }

   public int getMinEnchantability(int par1) {
      return 10 + 12 * (par1 - 1);
   }

   public int getMaxEnchantability(int par1) {
      return super.getMinEnchantability(par1) + 30;
   }

   public boolean canApplyTogether(Enchantment fTest) {
      return super.canApplyTogether(fTest) && !(fTest instanceof EnchantmentDamage) && !(fTest instanceof IEnchantmentDamage);
   }

   public boolean canApply(ItemStack fTest) {
      return fTest.getItem() instanceof ItemAxe ? true : super.canApply(fTest);
   }

   public void register(EventBus bus) {
      bus.register(LivingHurtEvent.class, EventPriority.HIGHEST, false, this::HandleEnchant);
   }

   public void HandleEnchant(LivingHurtEvent fEvent) {
      if (fEvent.getSource().damageType == "player" || fEvent.getSource().damageType == "mob") {
         if (fEvent.getSource().getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase attacker = (EntityLivingBase)fEvent.getSource().getTrueSource();
            if (attacker != null) {
               ItemStack dmgSource = ((EntityLivingBase)fEvent.getSource().getTrueSource()).getHeldItemMainhand();
               if (dmgSource != null) {
                  if (EnchantmentHelper.getEnchantmentLevel(Smc_010.Defusion, dmgSource) > 0) {
                     if (!isOffensivePetDisallowed(fEvent.getSource().getImmediateSource(), fEvent.getSource().getTrueSource())) {
                        int levelDefusing = EnchantmentHelper.getEnchantmentLevel(Smc_010.Defusion, dmgSource);
                        if (fEvent.getEntity() instanceof EntityCreeper) {
                           float Damage = fEvent.getAmount();
                           UtilityAccessor.damageTarget(fEvent.getEntityLiving(), somanyenchantments.PhysicalDamage, (float)levelDefusing * 2.5F);
                           fEvent.setAmount(Damage);
                           if (Math.random() < (double)(0.02F * (float)levelDefusing)) {
                              EntityCreeper creeper = (EntityCreeper)fEvent.getEntityLiving();
                              NBTTagCompound fuse = new NBTTagCompound();
                              creeper.writeEntityToNBT(fuse);
                              short fuseTime = 32767;
                              fuse.setShort("Fuse", fuseTime);
                              creeper.readEntityFromNBT(fuse);
                           }
                        }

                     }
                  }
               }
            }
         }
      }
   }
}
