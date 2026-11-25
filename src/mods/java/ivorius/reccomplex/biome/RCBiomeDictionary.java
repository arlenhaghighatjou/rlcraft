package ivorius.reccomplex.biome;

import net.minecraft.init.Biomes;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class RCBiomeDictionary {
   private static Type BIRCH_TREES;
   private static Type SUPER_BIRCH_TREES;
   private static Type ROOFED_TREES;
   private static Type MEGA_TAIGA_TREES;
   private static Type MEGA_TAIGA_SPRUCE_TREES;
   private static Type THEMED_TREES;

   public RCBiomeDictionary() {
   }

   public static void registerTypes() {
      BIRCH_TREES = Type.getType("BIRCH_TREES", new Type[0]);
      SUPER_BIRCH_TREES = Type.getType("SUPER_BIRCH_TREES", new Type[0]);
      ROOFED_TREES = Type.getType("ROOFED_TREES", new Type[0]);
      MEGA_TAIGA_TREES = Type.getType("MEGA_TAIGA_TREES", new Type[0]);
      MEGA_TAIGA_SPRUCE_TREES = Type.getType("MEGA_TAIGA_SPRUCE_TREES", new Type[0]);
      THEMED_TREES = Type.getType("THEMED_TREES", new Type[]{BIRCH_TREES, SUPER_BIRCH_TREES, ROOFED_TREES, MEGA_TAIGA_TREES, MEGA_TAIGA_SPRUCE_TREES});
      BiomeDictionary.addTypes(Biomes.BIRCH_FOREST, new Type[]{BIRCH_TREES});
      BiomeDictionary.addTypes(Biomes.BIRCH_FOREST_HILLS, new Type[]{BIRCH_TREES});
      BiomeDictionary.addTypes(Biomes.MUTATED_BIRCH_FOREST, new Type[]{SUPER_BIRCH_TREES});
      BiomeDictionary.addTypes(Biomes.MUTATED_BIRCH_FOREST_HILLS, new Type[]{SUPER_BIRCH_TREES});
      BiomeDictionary.addTypes(Biomes.ROOFED_FOREST, new Type[]{BIRCH_TREES});
      BiomeDictionary.addTypes(Biomes.REDWOOD_TAIGA, new Type[]{MEGA_TAIGA_TREES});
      BiomeDictionary.addTypes(Biomes.REDWOOD_TAIGA_HILLS, new Type[]{MEGA_TAIGA_TREES});
      BiomeDictionary.addTypes(Biomes.MUTATED_REDWOOD_TAIGA, new Type[]{MEGA_TAIGA_SPRUCE_TREES});
      BiomeDictionary.addTypes(Biomes.MUTATED_REDWOOD_TAIGA_HILLS, new Type[]{MEGA_TAIGA_SPRUCE_TREES});
   }
}
