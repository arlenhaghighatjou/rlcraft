package de.Whitedraco.switchbow.item.armor;

import de.Whitedraco.switchbow.Initial;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;

public class ArcherArmor extends ItemArmor {
   public ArcherArmor(String Name, ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn) {
      super(materialIn, renderIndexIn, equipmentSlotIn);
      this.setRegistryName(Name);
      this.setUnlocalizedName(Name);
      this.setCreativeTab(Initial.tabSwitchbow);
   }
}
