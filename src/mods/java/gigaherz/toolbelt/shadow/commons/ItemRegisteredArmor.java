package gigaherz.toolbelt.shadow.commons;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;

public class ItemRegisteredArmor extends ItemArmor {
   public ItemRegisteredArmor(String name, ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn) {
      super(materialIn, renderIndexIn, equipmentSlotIn);
      this.setRegistryName(name);
      this.setUnlocalizedName(this.getRegistryName().getResourceDomain() + "." + name);
      this.setCreativeTab(CreativeTabs.COMBAT);
   }
}
