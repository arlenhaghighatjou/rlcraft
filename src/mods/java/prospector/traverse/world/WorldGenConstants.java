package prospector.traverse.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.feature.WorldGenSavannaTree;
import net.minecraft.world.gen.feature.WorldGenShrub;
import net.minecraft.world.gen.feature.WorldGenTrees;
import prospector.traverse.init.TraverseBlocks;
import prospector.traverse.world.features.WorldGenFallenTree;

public interface WorldGenConstants {
   IBlockState OAK_LOG = Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT, EnumType.OAK);
   IBlockState BIRCH_LOG = Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT, EnumType.BIRCH);
   IBlockState SPRUCE_LOG = Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT, EnumType.SPRUCE);
   IBlockState JUNGLE_LOG = Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT, EnumType.JUNGLE);
   IBlockState ACACIA_LOG = Blocks.LOG2.getDefaultState().withProperty(BlockNewLog.VARIANT, EnumType.ACACIA);
   IBlockState DARK_OAK_LOG = Blocks.LOG2.getDefaultState().withProperty(BlockNewLog.VARIANT, EnumType.DARK_OAK);
   IBlockState FIR_LOG = ((Block)TraverseBlocks.blocks.get("fir_log")).getDefaultState();
   IBlockState OAK_LEAVES = Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, EnumType.OAK).withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));
   IBlockState BIRCH_LEAVES = Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, EnumType.BIRCH).withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));
   IBlockState SPRUCE_LEAVES = Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, EnumType.SPRUCE).withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));
   IBlockState JUNGLE_LEAVES = Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, EnumType.JUNGLE).withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));
   IBlockState ACACIA_LEAVES = Blocks.LEAVES2.getDefaultState().withProperty(BlockNewLeaf.VARIANT, EnumType.ACACIA).withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));
   IBlockState DARK_OAK_LEAVES = Blocks.LEAVES2.getDefaultState().withProperty(BlockNewLeaf.VARIANT, EnumType.DARK_OAK).withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));
   IBlockState RED_AUTUMNAL_LEAVES = ((Block)TraverseBlocks.blocks.get("red_autumnal_leaves")).getDefaultState().withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));
   IBlockState BROWN_AUTUMNAL_LEAVES = ((Block)TraverseBlocks.blocks.get("brown_autumnal_leaves")).getDefaultState().withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));
   IBlockState ORANGE_AUTUMNAL_LEAVES = ((Block)TraverseBlocks.blocks.get("orange_autumnal_leaves")).getDefaultState().withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));
   IBlockState YELLOW_AUTUMNAL_LEAVES = ((Block)TraverseBlocks.blocks.get("yellow_autumnal_leaves")).getDefaultState().withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));
   IBlockState FIR_LEAVES = ((Block)TraverseBlocks.blocks.get("fir_leaves")).getDefaultState().withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));
   IBlockState SAND = Blocks.SAND.getDefaultState();
   IBlockState GRASS = Blocks.GRASS.getDefaultState();
   WorldGenFallenTree OAK_FALLEN_TREE_FEATURE = new WorldGenFallenTree(true);
   WorldGenShrub OAK_SHRUB_FEATURE = new WorldGenShrub(OAK_LOG, OAK_LEAVES);
   WorldGenTrees TALLER_OAK_TREE_FEATURE = new WorldGenTrees(false, 7, OAK_LOG, OAK_LEAVES, false);
   WorldGenSavannaTree ACACIA_TREE_FEATURE = new WorldGenSavannaTree(false);
}
