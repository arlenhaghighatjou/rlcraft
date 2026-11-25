package rtg.api.world.gen.feature.tree.rtg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockDirt.DirtType;
import net.minecraft.block.BlockLog.EnumAxis;
import net.minecraft.block.BlockSand.EnumType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import rtg.RTGConfig;
import rtg.api.util.BlockUtil;
import rtg.api.util.RTGTreeData;

public abstract class TreeRTG extends WorldGenAbstractTree {
   protected IBlockState logBlock;
   protected IBlockState leavesBlock;
   protected int trunkSize;
   protected int crownSize;
   protected boolean noLeaves;
   protected IBlockState saplingBlock;
   protected int generateFlag;
   protected int minTrunkSize;
   protected int maxTrunkSize;
   protected int minCrownSize;
   protected int maxCrownSize;
   protected ArrayList<IBlockState> validGroundBlocks;
   protected ArrayList<Material> canGrowIntoMaterials;
   private boolean allowBarkCoveredLogs;

   public TreeRTG(boolean notify) {
      super(notify);
   }

   public TreeRTG() {
      this(false);
      this.setLogBlock(Blocks.LOG.getDefaultState());
      this.setLeavesBlock(Blocks.LEAVES.getDefaultState());
      this.trunkSize = 2;
      this.crownSize = 4;
      this.setNoLeaves(false);
      this.saplingBlock = Blocks.SAPLING.getDefaultState();
      this.generateFlag = 2;
      this.setMinTrunkSize(0);
      this.setMaxTrunkSize(0);
      this.setMinCrownSize(0);
      this.setMaxCrownSize(0);
      this.validGroundBlocks = new ArrayList(Arrays.asList(Blocks.GRASS.getDefaultState(), Blocks.DIRT.getDefaultState(), BlockUtil.getStateDirt(DirtType.PODZOL), BlockUtil.getStateSand(EnumType.RED_SAND)));
      this.canGrowIntoMaterials = new ArrayList(Arrays.asList(Material.AIR, Material.WOOD, Material.LEAVES, Material.GRASS, Material.GROUND, Material.PLANTS, Material.VINE, Material.WATER, Material.SNOW));
      this.allowBarkCoveredLogs = RTGConfig.barkCoveredLogs();
   }

   public void buildTrunk(World world, Random rand, int x, int y, int z) {
   }

   public void buildBranch(World world, Random rand, int x, int y, int z, int dX, int dZ, int logLength, int leaveSize) {
   }

   public void buildLeaves(World world, int x, int y, int z) {
   }

   public void buildLeaves(World world, Random rand, int x, int y, int z, int size) {
   }

   protected boolean isGroundValid(World world, BlockPos trunkPos) {
      return this.isGroundValid(world, trunkPos, RTGConfig.treesCanGenerateOnSand());
   }

   protected boolean isGroundValid(World world, BlockPos trunkPos, boolean sandAllowed) {
      IBlockState g = world.getBlockState(new BlockPos(trunkPos.getX(), trunkPos.getY() - 1, trunkPos.getZ()));
      if (g.getBlock() == Blocks.SAND && !sandAllowed) {
         return false;
      } else {
         for(int i = 0; i < this.validGroundBlocks.size(); ++i) {
            if (g == this.validGroundBlocks.get(i)) {
               return true;
            }
         }

         return false;
      }
   }

   protected boolean isGroundValid(World world, ArrayList<BlockPos> trunkPos) {
      if (trunkPos.isEmpty()) {
         throw new RuntimeException("Unable to determine if ground is valid. No trunks.");
      } else {
         for(int i = 0; i < trunkPos.size(); ++i) {
            if (!this.isGroundValid(world, (BlockPos)trunkPos.get(i))) {
               return false;
            }
         }

         return true;
      }
   }

   protected void placeLogBlock(World world, BlockPos pos, IBlockState logBlock, int generateFlag) {
      if (this.isReplaceable(world, pos)) {
         world.setBlockState(pos, logBlock, generateFlag);
      }

   }

   protected void placeLeavesBlock(World world, BlockPos pos, IBlockState leavesBlock, int generateFlag) {
      if (world.isAirBlock(pos)) {
         world.setBlockState(pos, leavesBlock, generateFlag);
      }

   }

   protected void placeLogBlock(World world, BlockPos pos, IBlockState logBlock, int generateFlag, RTGTreeData treeData) {
      this.placeLogBlock(world, pos, logBlock, generateFlag);
   }

   protected void placeLeavesBlock(World world, BlockPos pos, IBlockState leavesBlock, int generateFlag, RTGTreeData treeData) {
      this.placeLeavesBlock(world, pos, leavesBlock, generateFlag);
   }

   public boolean isReplaceable(World world, BlockPos pos) {
      IBlockState state = world.getBlockState(pos);
      return state.getBlock().isAir(state, world, pos) || state.getBlock().isLeaves(state, world, pos) || state.getBlock().isWood(world, pos) || this.canGrowInto(state.getBlock());
   }

   protected boolean canGrowInto(Block block) {
      if (block instanceof BlockPlanks) {
         return false;
      } else {
         Material material = block.getDefaultState().getMaterial();

         for(int i = 0; i < this.canGrowIntoMaterials.size(); ++i) {
            if (material == this.canGrowIntoMaterials.get(i)) {
               return true;
            }
         }

         return false;
      }
   }

   public IBlockState getTrunkLog(IBlockState defaultLog) {
      if (!this.allowBarkCoveredLogs) {
         return defaultLog;
      } else {
         IBlockState trunkLog;
         try {
            trunkLog = defaultLog.withProperty(BlockLog.LOG_AXIS, EnumAxis.NONE);
         } catch (Exception var4) {
            trunkLog = defaultLog;
         }

         return trunkLog;
      }
   }

   public IBlockState getLogBlock() {
      return this.logBlock;
   }

   public TreeRTG setLogBlock(IBlockState logBlock) {
      this.logBlock = logBlock;
      return this;
   }

   public IBlockState getLeavesBlock() {
      return this.leavesBlock;
   }

   public TreeRTG setLeavesBlock(IBlockState leavesBlock) {
      this.leavesBlock = leavesBlock;
      return this;
   }

   public int getTrunkSize() {
      return this.trunkSize;
   }

   public TreeRTG setTrunkSize(int trunkSize) {
      this.trunkSize = trunkSize;
      return this;
   }

   public int getCrownSize() {
      return this.crownSize;
   }

   public TreeRTG setCrownSize(int crownSize) {
      this.crownSize = crownSize;
      return this;
   }

   public boolean getNoLeaves() {
      return this.noLeaves;
   }

   public TreeRTG setNoLeaves(boolean noLeaves) {
      this.noLeaves = noLeaves;
      return this;
   }

   public IBlockState getSaplingBlock() {
      return this.saplingBlock;
   }

   public TreeRTG setSaplingBlock(IBlockState saplingBlock) {
      this.saplingBlock = saplingBlock;
      return this;
   }

   public int getGenerateFlag() {
      return this.generateFlag;
   }

   public TreeRTG setGenerateFlag(int generateFlag) {
      this.generateFlag = generateFlag;
      return this;
   }

   public int getMinTrunkSize() {
      return this.minTrunkSize;
   }

   public TreeRTG setMinTrunkSize(int minTrunkSize) {
      this.minTrunkSize = minTrunkSize;
      return this;
   }

   public int getMaxTrunkSize() {
      return this.maxTrunkSize;
   }

   public TreeRTG setMaxTrunkSize(int maxTrunkSize) {
      this.maxTrunkSize = maxTrunkSize;
      return this;
   }

   public int getMinCrownSize() {
      return this.minCrownSize;
   }

   public TreeRTG setMinCrownSize(int minCrownSize) {
      this.minCrownSize = minCrownSize;
      return this;
   }

   public int getMaxCrownSize() {
      return this.maxCrownSize;
   }

   public TreeRTG setMaxCrownSize(int maxCrownSize) {
      this.maxCrownSize = maxCrownSize;
      return this;
   }

   public ArrayList<IBlockState> getValidGroundBlocks() {
      return this.validGroundBlocks;
   }

   public TreeRTG setValidGroundBlocks(ArrayList<IBlockState> validGroundBlocks) {
      this.validGroundBlocks = validGroundBlocks;
      return this;
   }
}
