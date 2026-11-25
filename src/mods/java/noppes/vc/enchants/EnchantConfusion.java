package noppes.vc.enchants;

import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.util.ResourceLocation;
import noppes.vc.items.ItemGun;
import noppes.vc.items.ItemStaff;

public class EnchantConfusion extends VCEnchant {
   public EnchantConfusion() {
      super(Rarity.RARE, new ResourceLocation("variedcommodities", "vc_confusion"), ItemStaff.class, ItemGun.class);
      this.setName("confusion");
   }

   public int getMinEnchantability(int par1) {
      return 12 + (par1 - 1) * 20;
   }

   public int getMaxEnchantability(int par1) {
      return this.getMinEnchantability(par1) + 25;
   }

   public int getMaxLevel() {
      return 2;
   }
}
