package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.core.ModItems;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDeathwormArmor extends ItemArmor {
   public ItemDeathwormArmor(ArmorMaterial material, int renderIndex, EntityEquipmentSlot slot, String gameName, String name) {
      super(material, renderIndex, slot);
      this.setCreativeTab(IceAndFire.TAB);
      this.setUnlocalizedName(name);
      this.setRegistryName("iceandfire", gameName);
   }

   @SideOnly(Side.CLIENT)
   public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
      return (ModelBiped)IceAndFire.PROXY.getArmorModel(this.renderIndex == 2 ? 5 : 4);
   }

   public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
      if (this.getArmorMaterial() == ModItems.red_deathworm) {
         return "iceandfire:textures/models/armor/armor_deathworm_red" + (this.renderIndex == 2 ? "_legs.png" : ".png");
      } else {
         return this.getArmorMaterial() == ModItems.white_deathworm ? "iceandfire:textures/models/armor/armor_deathworm_white" + (this.renderIndex == 2 ? "_legs.png" : ".png") : "iceandfire:textures/models/armor/armor_deathworm_yellow" + (this.renderIndex == 2 ? "_legs.png" : ".png");
      }
   }
}
