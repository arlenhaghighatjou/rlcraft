package com.github.alexthe666.iceandfire.enums;

import com.github.alexthe666.iceandfire.item.ItemSeaSerpentArmor;
import com.github.alexthe666.iceandfire.item.ItemSeaSerpentScales;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

public enum EnumSeaSerpent {
   BLUE(TextFormatting.BLUE),
   BRONZE(TextFormatting.GOLD),
   DEEPBLUE(TextFormatting.DARK_BLUE),
   GREEN(TextFormatting.DARK_GREEN),
   PURPLE(TextFormatting.DARK_PURPLE),
   RED(TextFormatting.DARK_RED),
   TEAL(TextFormatting.AQUA);

   public String resourceName = this.name().toLowerCase();
   public TextFormatting color;
   public ArmorMaterial armorMaterial;
   @ObjectHolder("iceandfire:sea_serpent_scale")
   public Item scale;
   @ObjectHolder("iceandfire:sea_serpent_helmet")
   public Item helmet;
   @ObjectHolder("iceandfire:sea_serpent_chestplate")
   public Item chestplate;
   @ObjectHolder("iceandfire:sea_serpent_leggings")
   public Item leggings;
   @ObjectHolder("iceandfire:sea_serpent_boots")
   public Item boots;

   private EnumSeaSerpent(TextFormatting color) {
      this.color = color;
   }

   public static void initArmors() {
      for(EnumSeaSerpent color : values()) {
         color.armorMaterial = EnumHelper.addArmorMaterial("SeaSerpentScales" + color.resourceName, "iceandfire:sea_serpent_scales_" + color.resourceName, 30, new int[]{4, 8, 7, 4}, 25, SoundEvents.ITEM_ARMOR_EQUIP_GOLD, 2.5F);
         color.scale = new ItemSeaSerpentScales(color.resourceName, color.color);
         color.helmet = (new ItemSeaSerpentArmor(color, color.armorMaterial, 0, EntityEquipmentSlot.HEAD)).setUnlocalizedName("iceandfire.sea_serpent_helmet");
         color.chestplate = (new ItemSeaSerpentArmor(color, color.armorMaterial, 1, EntityEquipmentSlot.CHEST)).setUnlocalizedName("iceandfire.sea_serpent_chestplate");
         color.leggings = (new ItemSeaSerpentArmor(color, color.armorMaterial, 2, EntityEquipmentSlot.LEGS)).setUnlocalizedName("iceandfire.sea_serpent_leggings");
         color.boots = (new ItemSeaSerpentArmor(color, color.armorMaterial, 3, EntityEquipmentSlot.FEET)).setUnlocalizedName("iceandfire.sea_serpent_boots");
         color.helmet.setRegistryName("tide_" + color.resourceName + "_helmet");
         color.chestplate.setRegistryName("tide_" + color.resourceName + "_chestplate");
         color.leggings.setRegistryName("tide_" + color.resourceName + "_leggings");
         color.boots.setRegistryName("tide_" + color.resourceName + "_boots");
      }

   }
}
