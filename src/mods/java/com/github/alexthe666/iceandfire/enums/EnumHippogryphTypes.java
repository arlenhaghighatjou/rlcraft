package com.github.alexthe666.iceandfire.enums;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public enum EnumHippogryphTypes {
   BLACK(false, new Type[]{Type.DRY, Type.HOT, Type.SANDY}),
   BROWN(false, new Type[]{Type.MOUNTAIN}),
   GRAY(false, new Type[]{Type.SPOOKY, Type.DENSE, Type.FOREST}),
   CHESTNUT(false, new Type[]{Type.FOREST}),
   CREAMY(false, new Type[]{Type.SAVANNA}),
   DARK_BROWN(false, new Type[]{Type.CONIFEROUS}),
   WHITE(false, new Type[]{Type.SNOWY}),
   RAPTOR(true, new Type[0]),
   ALEX(true, new Type[0]),
   DODO(true, new Type[0]);

   public boolean developer;
   public Type[] spawnBiomes;
   public ResourceLocation TEXTURE;
   public ResourceLocation TEXTURE_BLINK;

   private EnumHippogryphTypes(boolean developer, Type[] biomes) {
      this.spawnBiomes = biomes;
      this.developer = developer;
      this.TEXTURE = new ResourceLocation("iceandfire:textures/models/hippogryph/" + this.name().toLowerCase() + ".png");
      this.TEXTURE_BLINK = new ResourceLocation("iceandfire:textures/models/hippogryph/" + this.name().toLowerCase() + "_blink.png");
   }

   public static EnumHippogryphTypes[] getWildTypes() {
      return new EnumHippogryphTypes[]{BLACK, BROWN, GRAY, CHESTNUT, CREAMY, DARK_BROWN, WHITE};
   }

   public static EnumHippogryphTypes getRandomType() {
      return getWildTypes()[(new Random()).nextInt(getWildTypes().length - 1)];
   }

   public static EnumHippogryphTypes getBiomeType(Biome biome) {
      List<EnumHippogryphTypes> types = new ArrayList();

      for(EnumHippogryphTypes type : getWildTypes()) {
         for(Type biomeTypes : type.spawnBiomes) {
            if (BiomeDictionary.hasType(biome, biomeTypes)) {
               types.add(type);
            }
         }
      }

      if (types.isEmpty()) {
         return getRandomType();
      } else if (types.contains(GRAY) && types.contains(CHESTNUT)) {
         return GRAY;
      } else {
         return (EnumHippogryphTypes)types.get((new Random()).nextInt(types.size()));
      }
   }
}
