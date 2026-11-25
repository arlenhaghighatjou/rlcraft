package noppes.vc.enchants;

import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.util.ResourceLocation;
import noppes.vc.items.ItemGun;
import noppes.vc.items.ItemStaff;

public class EnchantInfinite extends VCEnchant {
   public EnchantInfinite() {
      super(Rarity.VERY_RARE, new ResourceLocation("variedcommodities", "vc_infinite"), ItemStaff.class, ItemGun.class);
      this.setName("infinite");
   }

   public int getMinEnchantability(int par1) {
      return 20;
   }

   public int getMaxEnchantability(int par1) {
      return 50;
   }

   public int getMaxLevel() {
      return 1;
   }
}
