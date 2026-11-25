package org.orecruncher.dsurround.registry.biome;

import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.TempCategory;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IBiome {
   Biome getBiome();

   int getId();

   ResourceLocation getKey();

   String getName();

   Set<Type> getTypes();

   boolean canRain();

   boolean getEnableSnow();

   float getFloatTemperature(@Nonnull BlockPos var1);

   float getTemperature();

   TempCategory getTempCategory();

   boolean isHighHumidity();

   float getRainfall();

   default boolean isFake() {
      return false;
   }
}
