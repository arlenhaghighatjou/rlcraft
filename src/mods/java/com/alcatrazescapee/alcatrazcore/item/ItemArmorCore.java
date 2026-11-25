package com.alcatrazescapee.alcatrazcore.item;

import com.alcatrazescapee.alcatrazcore.client.IModelProvider;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@ParametersAreNonnullByDefault
public class ItemArmorCore extends ItemArmor implements IModelProvider {
   public ItemArmorCore(ArmorMaterial material, EntityEquipmentSlot slot) {
      super(material, 0, slot);
   }

   public ItemArmorCore(ArmorMaterial material, String slotName) {
      super(material, 0, EntityEquipmentSlot.fromString(slotName));
   }

   @SideOnly(Side.CLIENT)
   public void registerModel() {
      ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.getRegistryName().toString()));
   }
}
