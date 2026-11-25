package ivorius.reccomplex.world.gen.feature.selector;

import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;

public interface EnvironmentalSelection<C> {
   double getGenerationWeight(WorldProvider var1, Biome var2);

   C generationCategory();
}
