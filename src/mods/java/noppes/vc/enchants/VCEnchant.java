package noppes.vc.enchants;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public abstract class VCEnchant extends Enchantment {
   public static EnumEnchantmentType CustomNpcsType;
   public static VCEnchant Damage;
   public static VCEnchant Poison;
   public static VCEnchant Confusion;
   public static VCEnchant Infinite;
   private Class[] classes;

   protected VCEnchant(Rarity par2, ResourceLocation location, Class... obs) {
      super(par2, CustomNpcsType, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.classes = obs;
      this.setRegistryName(location);
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack) {
      return this.canApply(stack);
   }

   public boolean canApply(ItemStack par1ItemStack) {
      if (par1ItemStack.getItem() == null) {
         return false;
      } else {
         for(Class cls : this.classes) {
            if (cls.isInstance(par1ItemStack.getItem())) {
               return true;
            }
         }

         return false;
      }
   }

   public static int getLevel(VCEnchant enchant, ItemStack stack) {
      return enchant == null ? 0 : EnchantmentHelper.getEnchantmentLevel(enchant, stack);
   }
}
