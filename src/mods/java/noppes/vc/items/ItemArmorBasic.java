package noppes.vc.items;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.util.ResourceLocation;
import noppes.vc.VCTabs;

public class ItemArmorBasic extends ItemArmor {
   private final String texture;

   public ItemArmorBasic(ArmorMaterial par2EnumArmorMaterial, EntityEquipmentSlot par4, String texture) {
      super(par2EnumArmorMaterial, 0, par4);
      this.texture = texture;
      this.setCreativeTab(VCTabs.ARMORS);
      this.setMaxStackSize(1);
   }

   public Item register(String name) {
      this.setUnlocalizedName(name);
      this.setRegistryName(new ResourceLocation("variedcommodities", name));
      return this;
   }

   public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
      return this.armorType == EntityEquipmentSlot.LEGS ? "variedcommodities:textures/models/armor/" + this.texture + "_2.png" : "variedcommodities:textures/models/armor/" + this.texture + "_1.png";
   }
}
