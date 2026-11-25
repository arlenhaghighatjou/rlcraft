package noppes.vc.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.Item.ToolMaterial;
import noppes.vc.VCTabs;

public class ItemWeaponBasic extends ItemSword {
   private int damage;

   public ItemWeaponBasic(ToolMaterial material) {
      super(material);
      this.damage = 1;
      this.setCreativeTab(VCTabs.WEAPONS);
   }

   public ItemWeaponBasic(ToolMaterial material, int damage) {
      this(material);
      this.damage = damage;
   }

   public Item register(String name) {
      this.setUnlocalizedName(name);
      this.setRegistryName("variedcommodities", name);
      return this;
   }

   public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
      stack.damageItem(this.damage, attacker);
      return true;
   }
}
