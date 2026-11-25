package com.dhanantry.scapeandrunparasites.world.biome;

import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.BiomeProperties;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BiomeParasite extends Biome {
   protected WorldGenAbstractTree tree;

   public BiomeParasite(BiomeProperties properties) {
      super(properties.setRainDisabled());
      this.topBlock = SRPBlocks.ParasiteStain.getDefaultState();
      this.fillerBlock = SRPBlocks.ParasiteStain.getDefaultState();
      this.decorator = new BiomeParasiteDecorator();
   }

   public void mobListClear() {
      this.spawnableCreatureList.clear();
      this.spawnableMonsterList.clear();
      this.spawnableWaterCreatureList.clear();
      this.spawnableCaveCreatureList.clear();
   }

   @SideOnly(Side.CLIENT)
   public int getSkyColorByTemp(float currentTemperature) {
      return SRPConfigWorld.biomeSkyColor;
   }

   @SideOnly(Side.CLIENT)
   public int getGrassColorAtPos(BlockPos blockPos) {
      return SRPConfigWorld.biomeGrassColor;
   }

   @SideOnly(Side.CLIENT)
   public int getFoliageColorAtPos(BlockPos blockPos) {
      return SRPConfigWorld.biomeFoliageColor;
   }

   public int getWaterColorMultiplier() {
      return SRPConfigWorld.biomeWaterColor;
   }
}
