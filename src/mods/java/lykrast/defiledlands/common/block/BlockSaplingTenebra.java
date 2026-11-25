package lykrast.defiledlands.common.block;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import lykrast.defiledlands.common.init.ModBlocks;
import lykrast.defiledlands.common.world.feature.WorldGenTenebra;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.TerrainGen;

public class BlockSaplingTenebra extends BlockSapling {
   public BlockSaplingTenebra(float hardness) {
      this.setSoundType(SoundType.PLANT);
      this.setHardness(hardness);
      this.setDefaultState(this.blockState.getBaseState().withProperty(STAGE, Integer.valueOf(0)));
   }

   public void generateTree(World worldIn, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
      if (TerrainGen.saplingGrowTree(worldIn, rand, pos)) {
         int i = 0;
         int j = 0;
         WorldGenerator worldgenerator = new WorldGenTenebra(true);
         IBlockState iblockstate2 = Blocks.AIR.getDefaultState();
         worldIn.setBlockState(pos, iblockstate2, 4);
         if (!worldgenerator.generate(worldIn, rand, pos.add(i, 0, j))) {
            worldIn.setBlockState(pos, state, 4);
         }

      }
   }

   protected boolean canSustainBush(IBlockState state) {
      return state.getBlock() == ModBlocks.dirtDefiled || state.getBlock() == ModBlocks.grassDefiled;
   }

   public int damageDropped(IBlockState state) {
      return 0;
   }

   public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
      items.add(new ItemStack(this));
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(STAGE, Integer.valueOf((meta & 8) >> 3));
   }

   public int getMetaFromState(IBlockState state) {
      int i = 0;
      i = i | ((Integer)state.getValue(STAGE)).intValue() << 3;
      return i;
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{TYPE, STAGE});
   }
}
