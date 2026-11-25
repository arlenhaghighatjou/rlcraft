package org.orecruncher.dsurround.capabilities.season;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.registry.biome.BiomeInfo;

@SideOnly(Side.CLIENT)
public interface ISeasonInfo {
   @Nullable
   World getWorld();

   @Nonnull
   SeasonType getSeasonType();

   @Nonnull
   SeasonType.SubType getSeasonSubType();

   @Nonnull
   String getSeasonString();

   @Nonnull
   TemperatureRating getPlayerTemperature();

   @Nonnull
   TemperatureRating getBiomeTemperature(@Nonnull BlockPos var1);

   @Nonnull
   BlockPos getPrecipitationHeight(@Nonnull BlockPos var1);

   float getFloatTemperature(@Nonnull Biome var1, @Nonnull BlockPos var2);

   float getTemperature(@Nonnull BlockPos var1);

   boolean canWaterFreeze(@Nonnull BlockPos var1);

   boolean showFrostBreath(@Nonnull BlockPos var1);

   @Nonnull
   PrecipitationType getPrecipitationType(@Nonnull BlockPos var1, @Nullable BiomeInfo var2);
}
