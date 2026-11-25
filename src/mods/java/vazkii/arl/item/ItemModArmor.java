package vazkii.arl.item;

import javax.annotation.Nonnull;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.util.ResourceLocation;
import vazkii.arl.interf.IVariantHolder;
import vazkii.arl.util.ProxyRegistry;

public abstract class ItemModArmor extends ItemArmor implements IVariantHolder {
   private final String bareName;

   public ItemModArmor(String name, ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn) {
      super(materialIn, renderIndexIn, equipmentSlotIn);
      this.setUnlocalizedName(name);
      this.bareName = name;
      this.setCreativeTab(CreativeTabs.COMBAT);
   }

   @Nonnull
   public Item setUnlocalizedName(@Nonnull String name) {
      super.setUnlocalizedName(name);
      this.setRegistryName(new ResourceLocation(this.getPrefix() + name));
      ProxyRegistry.register(this);
      return this;
   }

   @Nonnull
   public String getUnlocalizedName(ItemStack par1ItemStack) {
      par1ItemStack.getItemDamage();
      return "item." + this.getPrefix() + this.bareName;
   }

   public String[] getVariants() {
      return new String[]{this.bareName};
   }
}
