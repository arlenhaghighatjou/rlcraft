package noppes.vc.enchants;

import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.util.ResourceLocation;
import noppes.vc.items.ItemGun;
import noppes.vc.items.ItemStaff;

public class EnchantDamage extends VCEnchant {
   public EnchantDamage() {
      super(Rarity.COMMON, new ResourceLocation("variedcommodities", "vc_damage"), ItemStaff.class, ItemGun.class);
      this.setName("damage");
   }

   public int getMinEnchantability(int par1) {
      return 1 + (par1 - 1) * 10;
   }

   public int getMaxEnchantability(int par1) {
      return this.getMinEnchantability(par1) + 15;
   }

   public int getMaxLevel() {
      return 5;
   }
}
