package com.teamacronymcoders.base.items;

import com.teamacronymcoders.base.IBaseMod;
import com.teamacronymcoders.base.client.models.IHasModel;
import java.util.List;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;

public class ItemArmorBase extends ItemArmor implements IHasModel {
   private IBaseMod mod;
   private String name;

   public ItemArmorBase(ArmorMaterial material, EntityEquipmentSlot equipmentSlot, String name) {
      super(material, 0, equipmentSlot);
      this.setUnlocalizedName(name);
      this.name = name;
   }

   public IBaseMod getMod() {
      return this.mod;
   }

   public void setMod(IBaseMod mod) {
      this.mod = mod;
   }

   public Item getItem() {
      return this;
   }

   public List<String> getModelNames(List<String> modelNames) {
      modelNames.add(this.name);
      return modelNames;
   }
}
