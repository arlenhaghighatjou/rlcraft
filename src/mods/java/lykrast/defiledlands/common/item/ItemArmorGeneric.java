package lykrast.defiledlands.common.item;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;

public class ItemArmorGeneric extends ItemArmor {
   private static final String PATH = "defiledlands:textures/armor/";
   protected String texture;

   public ItemArmorGeneric(String name, ArmorMaterial materialIn, EntityEquipmentSlot equipmentSlotIn) {
      super(materialIn, 0, equipmentSlotIn);
      this.texture = "defiledlands:textures/armor/" + name;
      if (equipmentSlotIn == EntityEquipmentSlot.LEGS) {
         this.texture = this.texture + "_2.png";
      } else {
         this.texture = this.texture + "_1.png";
      }

   }

   @Nullable
   public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
      return this.texture;
   }
}
