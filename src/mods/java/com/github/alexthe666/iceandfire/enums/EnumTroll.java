package com.github.alexthe666.iceandfire.enums;

import com.github.alexthe666.iceandfire.core.ModItems;
import com.github.alexthe666.iceandfire.item.ItemTrollArmor;
import com.github.alexthe666.iceandfire.item.ItemTrollLeather;
import com.github.alexthe666.iceandfire.item.ItemTrollWeapon;
import java.util.ArrayList;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

public enum EnumTroll {
   FOREST(Type.FOREST, ModItems.troll_forest, new EnumTroll.Weapon[]{EnumTroll.Weapon.TRUNK, EnumTroll.Weapon.COLUMN_FOREST, EnumTroll.Weapon.AXE, EnumTroll.Weapon.HAMMER}),
   FROST(Type.SNOWY, ModItems.troll_frost, new EnumTroll.Weapon[]{EnumTroll.Weapon.COLUMN_FROST, EnumTroll.Weapon.TRUNK_FROST, EnumTroll.Weapon.AXE, EnumTroll.Weapon.HAMMER}),
   MOUNTAIN(Type.MOUNTAIN, ModItems.troll_mountain, new EnumTroll.Weapon[]{EnumTroll.Weapon.COLUMN, EnumTroll.Weapon.AXE, EnumTroll.Weapon.HAMMER});

   public ResourceLocation TEXTURE;
   public ResourceLocation TEXTURE_STONE;
   public ResourceLocation TEXTURE_EYES;
   public Type spawnBiome;
   public ArmorMaterial material;
   private EnumTroll.Weapon[] weapons;
   @ObjectHolder("iceandfire:troll_leather")
   public Item leather;
   @ObjectHolder("iceandfire:troll_helmet")
   public Item helmet;
   @ObjectHolder("iceandfire:troll_chestplate")
   public Item chestplate;
   @ObjectHolder("iceandfire:troll_leggings")
   public Item leggings;
   @ObjectHolder("iceandfire:troll_boots")
   public Item boots;

   private EnumTroll(Type biome, ArmorMaterial material, EnumTroll.Weapon[] weapons) {
      this.spawnBiome = biome;
      this.weapons = weapons;
      this.material = material;
      this.TEXTURE = new ResourceLocation("iceandfire:textures/models/troll/troll_" + this.name().toLowerCase() + ".png");
      this.TEXTURE_STONE = new ResourceLocation("iceandfire:textures/models/troll/troll_" + this.name().toLowerCase() + "_stone.png");
      this.TEXTURE_EYES = new ResourceLocation("iceandfire:textures/models/troll/troll_" + this.name().toLowerCase() + "_eyes.png");
      this.leather = new ItemTrollLeather(this);
      this.helmet = new ItemTrollArmor(this, material, 0, EntityEquipmentSlot.HEAD);
      this.chestplate = new ItemTrollArmor(this, material, 1, EntityEquipmentSlot.CHEST);
      this.leggings = new ItemTrollArmor(this, material, 2, EntityEquipmentSlot.LEGS);
      this.boots = new ItemTrollArmor(this, material, 3, EntityEquipmentSlot.FEET);
   }

   public static EnumTroll getBiomeType(Biome biome) {
      List<EnumTroll> types = new ArrayList();

      for(EnumTroll type : values()) {
         if (BiomeDictionary.hasType(biome, type.spawnBiome)) {
            types.add(type);
         }
      }

      if (types.isEmpty()) {
         return values()[(new EaglercraftRandom()).nextInt(values().length)];
      } else {
         return (EnumTroll)types.get((new EaglercraftRandom()).nextInt(types.size()));
      }
   }

   public static EnumTroll.Weapon getWeaponForType(EnumTroll troll) {
      return troll.weapons[(new EaglercraftRandom()).nextInt(troll.weapons.length)];
   }

   public static enum Weapon {
      AXE,
      COLUMN,
      COLUMN_FOREST,
      COLUMN_FROST,
      HAMMER,
      TRUNK,
      TRUNK_FROST;

      public ResourceLocation TEXTURE = new ResourceLocation("iceandfire:textures/models/troll/weapon/weapon_" + this.name().toLowerCase() + ".png");
      public Item item = new ItemTrollWeapon(this);

      private Weapon() {
      }
   }
}
