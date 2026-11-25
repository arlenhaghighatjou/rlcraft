package lykrast.defiledlands.common.block;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import lykrast.defiledlands.common.util.CorruptionHelper;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BlockVariantCorrupted extends BlockVariant {
   public BlockVariantCorrupted(Material material, SoundType soundType, float hardness, float resistance) {
      super(material, soundType, hardness, resistance);
      this.setTickRandomly(true);
   }

   public BlockVariantCorrupted(Material material, SoundType soundType, float hardness, float resistance, boolean names) {
      super(material, soundType, hardness, resistance);
      this.variantNames = names;
      this.setTickRandomly(true);
   }

   public BlockVariantCorrupted(Material material, SoundType soundType, float hardness, float resistance, String tool, int harvestLevel) {
      super(material, soundType, hardness, resistance, tool, harvestLevel);
      this.setTickRandomly(true);
   }

   public BlockVariantCorrupted(Material material, SoundType soundType, float hardness, float resistance, String tool, int harvestLevel, boolean names) {
      super(material, soundType, hardness, resistance, tool, harvestLevel);
      this.variantNames = names;
      this.setTickRandomly(true);
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
      CorruptionHelper.spread(worldIn, pos, state, rand);
      super.updateTick(worldIn, pos, state, rand);
   }
}
