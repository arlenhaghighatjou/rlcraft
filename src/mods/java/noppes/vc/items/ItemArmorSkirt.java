package noppes.vc.items;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.vc.VariedCommodities;

public class ItemArmorSkirt extends ItemArmorBasic {
   private final String texture;
   private String id = "minecraft";

   public ItemArmorSkirt(ArmorMaterial par2EnumArmorMaterial, String texture) {
      super(par2EnumArmorMaterial, EntityEquipmentSlot.LEGS, texture);
      this.texture = texture;
   }

   public ItemArmorSkirt(ArmorMaterial par2EnumArmorMaterial, String id, String texture) {
      super(par2EnumArmorMaterial, EntityEquipmentSlot.LEGS, texture);
      this.texture = texture;
      this.id = id;
   }

   public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
      return "overlay".equals(type) ? null : this.id + ":textures/models/armor/" + this.texture + "_2.png";
   }

   @SideOnly(Side.CLIENT)
   public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
      return VariedCommodities.proxy.getSkirtModel();
   }
}
