package noppes.vc.items;

import javax.annotation.Nullable;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.vc.VCTabs;

public class ItemShieldBasic extends ItemShield {
   public VCToolMaterial material;

   public ItemShieldBasic(VCToolMaterial material) {
      this.material = material;
      this.setFull3D();
      this.setMaxStackSize(1);
      this.setMaxDamage(material.getMaxUses() * 5);
      this.setCreativeTab(VCTabs.WEAPONS);
   }

   public Item register(String name) {
      this.setUnlocalizedName(name);
      this.setRegistryName("variedcommodities", name);
      return this;
   }

   @SideOnly(Side.CLIENT)
   public CreativeTabs getCreativeTab() {
      return VCTabs.WEAPONS;
   }

   public boolean isShield(ItemStack stack, @Nullable EntityLivingBase entity) {
      return true;
   }
}
