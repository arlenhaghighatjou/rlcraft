package rtg.world.gen;

import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;

public class MesaBiomeCombiner {
   public final int mesa = Biome.getIdForBiome(Biomes.MESA);
   public final int mesaPlateau = Biome.getIdForBiome(Biomes.MESA_CLEAR_ROCK);
   public final int mesaPlateauF = Biome.getIdForBiome(Biomes.MESA_ROCK);
   public final int mesaPlateauM = Biome.getIdForBiome(Biomes.MUTATED_MESA_CLEAR_ROCK);
   public final int mesaPlateauFM = Biome.getIdForBiome(Biomes.MUTATED_MESA_ROCK);

   public MesaBiomeCombiner() {
   }

   public void adjust(float[] result) {
      float mesaBorder = result[this.mesa];
      float plateauBorder = result[this.mesaPlateau] + result[this.mesaPlateauM];
      float plateauFBorder = result[this.mesaPlateauF] + result[this.mesaPlateauFM];
      result[this.mesa] = 0.0F;
      result[this.mesaPlateauM] = 0.0F;
      result[this.mesaPlateauFM] = 0.0F;
      if (plateauBorder > plateauFBorder) {
         result[this.mesaPlateau] = mesaBorder + plateauBorder + plateauFBorder;
         result[this.mesaPlateauF] = 0.0F;
      } else {
         result[this.mesaPlateau] = 0.0F;
         result[this.mesaPlateauF] = mesaBorder + plateauBorder + plateauFBorder;
      }

   }
}
