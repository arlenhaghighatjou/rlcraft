package com.Shultrea.Rin.Ench0_4_0;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enum.EnumList;
import com.Shultrea.Rin.Interfaces.IAncientEnchantment;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import com.Shultrea.Rin.Utility_Sector.EnchantmentLister;
import java.util.List;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;

public class EnchantmentAncient_CurseInflicter extends EnchantmentBase implements IAncientEnchantment {
   public EnchantmentAncient_CurseInflicter() {
      super(Rarity.VERY_RARE, EnumList.SWORD, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.setName("AncientCurseInflicter");
      this.setRegistryName("AncientCurseInflicter");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.AncientCurseInflicter;
   }

   public int getMinEnchantability(int ench) {
      return 240 * ench;
   }

   public int getMaxEnchantability(int ench) {
      return 720 * ench;
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack) {
      return false;
   }

   public boolean isAllowedOnBooks() {
      return false;
   }

   public boolean isTreasureEnchantment() {
      return true;
   }

   public void onEntityDamagedAlt(EntityLivingBase user, Entity target, ItemStack stackIn, int level) {
      if (target instanceof EntityLivingBase) {
         EntityLivingBase entity = (EntityLivingBase)target;

         for(ItemStack stack : entity.getEquipmentAndArmor()) {
            if (user.getRNG().nextInt(8) < 1) {
               List<Enchantment> list = EnchantmentLister.CURSE;
               int random = user.getRNG().nextInt(list.size());
               Enchantment ench = (Enchantment)list.get(random);
               int randLevel = user.getRNG().nextInt(ench.getMaxLevel());
               if (ench.canApply(stack)) {
                  boolean isCompatible = true;
                  NBTTagList nbt = stack.getEnchantmentTagList();

                  for(int z = 0; z < nbt.tagCount(); ++z) {
                     NBTTagCompound tag = nbt.getCompoundTagAt(z);
                     int enchId = tag.getShort("id");
                     int currEnchLevel = tag.getShort("lvl");
                     Enchantment enchantment = Enchantment.getEnchantmentByID(enchId);
                     if (enchantment != null && enchantment != this && !enchantment.isCompatibleWith(ench)) {
                        isCompatible = false;
                        break;
                     }
                  }

                  if (EnchantmentHelper.getEnchantmentLevel(ench, stack) <= 0 && isCompatible) {
                     stack.addEnchantment((Enchantment)list.get(random), randLevel + 1);
                  }
               }
            }
         }
      }

   }

   public String getPrefix() {
      return TextFormatting.YELLOW.toString();
   }
}
