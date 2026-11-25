package vazkii.quark.base.handler;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import org.apache.commons.lang3.text.WordUtils;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.world.world.StoneInfoBasedGenerator;

public final class BiomeTypeConfigHandler {
   public BiomeTypeConfigHandler() {
   }

   public static List<Type> parseBiomeTypeArrayConfig(String name, String category, Type... biomes) {
      String[] defaultBiomes = (String[])Arrays.stream(biomes).map(Type::getName).toArray((x$0) -> {
         return new String[x$0];
      });
      String[] readBiomes = ModuleLoader.config.getStringList(name, category, defaultBiomes, "Biome Type List: https://github.com/MinecraftForge/MinecraftForge/blob/1.11.x/src/main/java/net/minecraftforge/common/BiomeDictionary.java#L44-L90\nTypes per Biome: https://github.com/MinecraftForge/MinecraftForge/blob/1.11.x/src/main/java/net/minecraftforge/common/BiomeDictionary.java#L402-L463");
      return (List)Arrays.stream(readBiomes).map((s) -> {
         return Type.getType(s, new Type[0]);
      }).collect(Collectors.toList());
   }

   public static boolean biomeTypeIntersectCheck(Iterable<Type> biomeItr, Biome b) {
      Set<Type> currentTypes = BiomeDictionary.getTypes(b);

      for(Type type : biomeItr) {
         if (currentTypes.contains(type)) {
            return true;
         }
      }

      return false;
   }

   public static void debugStoneGeneration(Iterable<StoneInfoBasedGenerator> generators) {
      System.out.println("### OUTPUTTING BIOME CSV DATA ###");
      System.out.print("sep=;\nBiome");

      for(StoneInfoBasedGenerator gen : generators) {
         System.out.print(";" + WordUtils.capitalize(gen.name));
      }

      System.out.print(";Biome Type");
      System.out.println();

      for(ResourceLocation r : Biome.REGISTRY.getKeys()) {
         Biome b = (Biome)Biome.REGISTRY.getObject(r);
         if (b != null) {
            System.out.print(b.getBiomeName());

            for(StoneInfoBasedGenerator gen : generators) {
               if (gen.canGenerateInBiome(b)) {
                  System.out.print(";yes");
               } else {
                  System.out.print(";no");
               }
            }

            System.out.print(";" + (b.isMutation() ? "mutation" : "normal"));
            System.out.println();
         }
      }

      System.out.println("### DONE ###");
   }
}
