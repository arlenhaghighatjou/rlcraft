package net.silentchaos512.lib.util;

import java.util.Set;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class BiomeHelper {
   public BiomeHelper() {
   }

   public static Set<Type> getTypes(Biome biome) {
      return BiomeDictionary.getTypes(biome);
   }

   public static String getTypeName(Type type) {
      return type.getName();
   }
}
