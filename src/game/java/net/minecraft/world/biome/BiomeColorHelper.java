package net.minecraft.world.biome;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BiomeColorHelper
{
    public static BiomeColorHelper.ColorResolver GRASS_COLOR = new BiomeColorHelper.ColorResolver()
    {
        public int getColorAtPos(Biome biome, BlockPos blockPosition)
        {
            return biome.getGrassColorAtPos(blockPosition);
        }
    };
    public static BiomeColorHelper.ColorResolver FOLIAGE_COLOR = new BiomeColorHelper.ColorResolver()
    {
        public int getColorAtPos(Biome biome, BlockPos blockPosition)
        {
            return biome.getFoliageColorAtPos(blockPosition);
        }
    };
    public static final BiomeColorHelper.ColorResolver WATER_COLOR = new BiomeColorHelper.ColorResolver()
    {
        public int getColorAtPos(Biome biome, BlockPos blockPosition)
        {
            return biome.getWaterColor();
        }
    };

    private static int getColorAtPos(IBlockAccess blockAccess, BlockPos pos, BiomeColorHelper.ColorResolver colorResolver)
    {
        return fionathemortal.betterbiomeblend.BiomeColor.getColorAtPos(blockAccess, pos, colorResolver);
    }

    public static int getGrassColorAtPos(IBlockAccess blockAccess, BlockPos pos)
    {
        return fionathemortal.betterbiomeblend.BiomeColor.getGrassColorAtPos(blockAccess, pos);
    }

    public static int getFoliageColorAtPos(IBlockAccess blockAccess, BlockPos pos)
    {
        return fionathemortal.betterbiomeblend.BiomeColor.getFoliageColorAtPos(blockAccess, pos);
    }

    public static int getWaterColorAtPos(IBlockAccess blockAccess, BlockPos pos)
    {
        return fionathemortal.betterbiomeblend.BiomeColor.getWaterColorAtPos(blockAccess, pos);
    }

    @SideOnly(Side.CLIENT)
    public interface ColorResolver
    {
        int getColorAtPos(Biome biome, BlockPos blockPosition);
    }
}
