package com.ferreusveritas.dynamictrees.blocks;

import com.ferreusveritas.dynamictrees.DynamicTrees;
import com.ferreusveritas.dynamictrees.ModBlocks;
import com.ferreusveritas.dynamictrees.ModConfigs;
import com.ferreusveritas.dynamictrees.api.IAgeable;
import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.cells.CellNull;
import com.ferreusveritas.dynamictrees.api.cells.ICell;
import com.ferreusveritas.dynamictrees.api.network.MapSignal;
import com.ferreusveritas.dynamictrees.api.treedata.ILeavesProperties;
import com.ferreusveritas.dynamictrees.api.treedata.ITreePart;
import com.ferreusveritas.dynamictrees.entities.EntityFallingTree;
import com.ferreusveritas.dynamictrees.items.Seed;
import com.ferreusveritas.dynamictrees.systems.GrowSignal;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.trees.TreeFamily;
import com.ferreusveritas.dynamictrees.util.IRayTraceCollision;
import com.ferreusveritas.dynamictrees.util.SafeChunkBounds;
import java.util.ArrayList;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockDoublePlant.EnumBlockHalf;
import net.minecraft.block.BlockDoublePlant.EnumPlantType;
import net.minecraft.block.BlockTallGrass.EnumType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockDynamicLeaves extends BlockLeaves implements ITreePart, IAgeable, IRayTraceCollision {
   public static boolean passableLeavesModLoaded = false;
   protected static EaglercraftRandom backupRng = new EaglercraftRandom();
   public static final PropertyInteger HYDRO = PropertyInteger.create("hydro", 1, 4);
   public static final PropertyInteger TREE = PropertyInteger.create("tree", 0, 3);
   public ILeavesProperties[] properties = new ILeavesProperties[]{LeavesProperties.NULLPROPERTIES, LeavesProperties.NULLPROPERTIES, LeavesProperties.NULLPROPERTIES, LeavesProperties.NULLPROPERTIES};

   public BlockDynamicLeaves() {
      this.setDefaultState(this.blockState.getBaseState().withProperty(HYDRO, Integer.valueOf(4)).withProperty(TREE, Integer.valueOf(0)));
   }

   public Block setDefaultNaming(String modid, String name) {
      this.setRegistryName(modid, name);
      this.setUnlocalizedName(this.getRegistryName().toString());
      return this;
   }

   public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
      return this.getProperties(state).getPrimitiveLeavesItemStack();
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{HYDRO, TREE, DECAYABLE});
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(TREE, Integer.valueOf(meta >> 2 & 3)).withProperty(HYDRO, Integer.valueOf((meta & 3) + 1));
   }

   public int getMetaFromState(IBlockState state) {
      return ((Integer)state.getValue(HYDRO)).intValue() - 1 | ((Integer)state.getValue(TREE)).intValue() << 2;
   }

   public void setProperties(int tree, ILeavesProperties properties) {
      this.properties[tree & 3] = properties;
   }

   public ILeavesProperties getProperties(IBlockState state) {
      return this.properties[((Integer)state.getValue(TREE)).intValue() & 3];
   }

   public TreeFamily getFamily(IBlockState state, IBlockAccess world, BlockPos pos) {
      return this.getProperties(state).getTree();
   }

   public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return this.getProperties(world.getBlockState(pos)).getFlammability();
   }

   public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return this.getProperties(world.getBlockState(pos)).getFireSpreadSpeed();
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
      if (rand == null) {
         rand = backupRng;
      }

      if (rand.nextInt(ModConfigs.treeGrowthFolding) == 0) {
         float attempts = (float)ModConfigs.treeGrowthFolding * ModConfigs.treeGrowthMultiplier;
         if (attempts >= 1.0F || rand.nextFloat() < attempts) {
            this.doTick(worldIn, pos, state, rand);
         }

         int start = rand.nextInt(26);

         while(--attempts > 0.0F) {
            if (attempts >= 1.0F || rand.nextFloat() < attempts) {
               int r = start++ % 26 + 14;
               r = r > 26 ? r - 13 : r - 14;
               BlockPos dPos = pos.add(r % 3 - 1, r / 3 % 3 - 1, r / 9 % 3 - 1);
               IBlockState dState = worldIn.getBlockState(dPos);
               if (dState.getBlock() instanceof BlockDynamicLeaves) {
                  ((BlockDynamicLeaves)dState.getBlock()).doTick(worldIn, dPos, dState, rand);
               }
            }
         }
      }

   }

   protected void doTick(World worldIn, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
      if (this.canTickAt(worldIn, pos) && this.getProperties(state).updateTick(worldIn, pos, state, rand)) {
         this.age(worldIn, pos, state, rand, SafeChunkBounds.ANY);
      }

   }

   protected boolean canTickAt(World worldIn, BlockPos pos) {
      int xm = pos.getX() & 15;
      int zm = pos.getZ() & 15;
      return xm > 1 && xm < 14 && zm > 1 && zm < 14 && worldIn.isBlockLoaded(pos) || worldIn.isAreaLoaded(pos, 2);
   }

   public int age(World world, BlockPos pos, IBlockState state, EaglercraftRandom rand, SafeChunkBounds safeBounds) {
      ILeavesProperties leavesProperties = this.getProperties(state);
      int oldHydro = ((Integer)state.getValue(HYDRO)).intValue();
      boolean worldGen = safeBounds != SafeChunkBounds.ANY;
      int newHydro = this.getHydrationLevelFromNeighbors(world, pos, leavesProperties);
      if (newHydro != 0 && (worldGen || this.hasAdequateLight(state, world, leavesProperties, pos))) {
         if (oldHydro != newHydro) {
            world.setBlockState(pos, leavesProperties.getDynamicLeavesState(newHydro), leavesProperties.appearanceChangesWithHydro() ? 2 : 4);
         }

         BlockDynamicLeaves.NewLeavesPropertiesHandler newLeavesHander = this.getNewLeavesPropertiesHandler(world, pos, state, newHydro, worldGen);

         for(EnumFacing dir : EnumFacing.VALUES) {
            if (newHydro > 1 || rand.nextInt(4) == 0) {
               BlockPos offpos = pos.offset(dir);
               if (safeBounds.inBounds(offpos, true) && this.isLocationSuitableForNewLeaves(world, leavesProperties, offpos)) {
                  int hydro = this.getHydrationLevelFromNeighbors(world, offpos, leavesProperties);
                  if (hydro > 0) {
                     world.setBlockState(offpos, newLeavesHander.getLeaves(world, offpos, leavesProperties.getDynamicLeavesState(hydro)), 2);
                  }
               }
            }
         }

         return newHydro;
      } else {
         world.setBlockToAir(pos);
         return -1;
      }
   }

   protected BlockDynamicLeaves.NewLeavesPropertiesHandler getNewLeavesPropertiesHandler(World world, BlockPos pos, IBlockState state, int newHydro, boolean worldGen) {
      return (w, p, l) -> {
         return l;
      };
   }

   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
   }

   public boolean isPassable(IBlockAccess access, BlockPos pos) {
      return passableLeavesModLoaded ? super.isPassable(access, pos) : ModConfigs.isLeavesPassable;
   }

   public PathNodeType getAiPathNodeType(IBlockState state, IBlockAccess world, BlockPos pos) {
      return !passableLeavesModLoaded && !ModConfigs.isLeavesPassable ? PathNodeType.BLOCKED : PathNodeType.OPEN;
   }

   public MapColor getMapColor(IBlockState state, IBlockAccess world, BlockPos pos) {
      return this.getProperties(state).getPrimitiveLeaves().getMapColor(world, pos);
   }

   public boolean addLandingEffects(IBlockState state, WorldServer worldObj, BlockPos blockPosition, IBlockState iblockstate, EntityLivingBase entity, int numberOfParticles) {
      return true;
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return FULL_BLOCK_AABB;
   }

   public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean unknown) {
      if (entityIn instanceof EntityItem) {
         EntityItem item = (EntityItem)entityIn;
         if (!(item.getItem().getItem() instanceof Seed)) {
            super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, unknown);
         }
      } else if (entityIn != null && !(entityIn instanceof EntityFallingTree)) {
         if (!passableLeavesModLoaded && !ModConfigs.vanillaLeavesCollision) {
            if (!ModConfigs.isLeavesPassable) {
               AxisAlignedBB aabb = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.5D, 0.875D);
               addCollisionBoxToList(pos, entityBox, collidingBoxes, aabb);
            }
         } else {
            super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, unknown);
         }

      }
   }

   public void onFallenUpon(World world, BlockPos pos, Entity entity, float fallDistance) {
      if (ModConfigs.canopyCrash && entity instanceof EntityLivingBase) {
         --entity.fallDistance;
         AxisAlignedBB aabb = entity.getEntityBoundingBox();
         int minX = MathHelper.floor(aabb.minX + 0.001D);
         int minZ = MathHelper.floor(aabb.minZ + 0.001D);
         int maxX = MathHelper.floor(aabb.maxX - 0.001D);
         int maxZ = MathHelper.floor(aabb.maxZ - 0.001D);
         boolean crushing = true;
         boolean hasLeaves = true;
         SoundType stepSound = this.getSoundType();
         float volume = MathHelper.clamp(stepSound.getVolume() / 16.0F * fallDistance, 0.0F, 3.0F);
         world.playSound(entity.posX, entity.posY, entity.posZ, stepSound.getBreakSound(), SoundCategory.BLOCKS, volume, stepSound.getPitch(), false);

         for(int iy = 0; entity.fallDistance > 3.0F && crushing && pos.getY() - iy > 0; ++iy) {
            if (hasLeaves) {
               entity.fallDistance *= 0.66F;
               hasLeaves = false;
            }

            for(int ix = minX; ix <= maxX; ++ix) {
               for(int iz = minZ; iz <= maxZ; ++iz) {
                  BlockPos iPos = new BlockPos(ix, pos.getY() - iy, iz);
                  IBlockState state = world.getBlockState(iPos);
                  if (TreeHelper.isLeaves(state)) {
                     hasLeaves = true;
                     DynamicTrees.proxy.crushLeavesBlock(world, iPos, state, entity);
                     world.setBlockToAir(iPos);
                  } else if (!world.isAirBlock(iPos)) {
                     crushing = false;
                  }
               }
            }
         }
      }

   }

   public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
      if (!(entity instanceof EntityItem) && !(entity instanceof IProjectile) && !passableLeavesModLoaded && !ModConfigs.vanillaLeavesCollision) {
         if (entity.motionY < 0.0D && entity.fallDistance < 2.0F) {
            entity.fallDistance = 0.0F;
            entity.motionY *= 0.5D;
         } else if (entity.motionY > 0.0D && entity.motionY < 0.25D) {
            entity.motionY += 0.025D;
         }

         entity.setSprinting(false);
         entity.motionX *= 0.25D;
         entity.motionZ *= 0.25D;
      } else {
         super.onEntityCollidedWithBlock(world, pos, state, entity);
      }

   }

   public void beginLeavesDecay(IBlockState state, World world, BlockPos pos) {
   }

   public boolean growLeavesIfLocationIsSuitable(World world, ILeavesProperties leavesProp, BlockPos pos, int hydro) {
      hydro = hydro == 0 ? leavesProp.getCellKit().getDefaultHydration() : hydro;
      if (this.isLocationSuitableForNewLeaves(world, leavesProp, pos)) {
         world.setBlockState(pos, leavesProp.getDynamicLeavesState(hydro), 2 | (leavesProp.appearanceChangesWithHydro() ? 1 : 0));
         return true;
      } else {
         return false;
      }
   }

   public boolean isLocationSuitableForNewLeaves(World world, ILeavesProperties leavesProperties, BlockPos pos) {
      IBlockState blockState = world.getBlockState(pos);
      Block block = blockState.getBlock();
      if (block instanceof BlockDynamicLeaves) {
         return false;
      } else {
         IBlockState belowBlockState = world.getBlockState(pos.down());
         if ((!belowBlockState.isFullCube() || belowBlockState.getBlock() instanceof BlockLeaves) && !(belowBlockState.getBlock() instanceof BlockLiquid)) {
            if (block == Blocks.DOUBLE_PLANT) {
               IBlockState bs = world.getBlockState(pos);
               EnumBlockHalf half = (EnumBlockHalf)bs.getValue(BlockDoublePlant.HALF);
               if (half == EnumBlockHalf.UPPER && belowBlockState.getBlock() == Blocks.DOUBLE_PLANT) {
                  EnumPlantType type = (EnumPlantType)belowBlockState.getValue(BlockDoublePlant.VARIANT);
                  if (type == EnumPlantType.GRASS || type == EnumPlantType.FERN) {
                     world.setBlockToAir(pos);
                     world.setBlockState(pos.down(), Blocks.TALLGRASS.getDefaultState().withProperty(BlockTallGrass.TYPE, type == EnumPlantType.GRASS ? EnumType.GRASS : EnumType.FERN), 3);
                  }
               }
            }

            boolean isReplaceable = blockState.getMaterial() == Material.AIR || block == Blocks.SNOW_LAYER;
            return isReplaceable && this.hasAdequateLight(blockState, world, leavesProperties, pos);
         } else {
            return false;
         }
      }
   }

   public boolean hasAdequateLight(IBlockState blockState, World world, ILeavesProperties leavesProperties, BlockPos pos) {
      if (world.canBlockSeeSky(pos)) {
         return true;
      } else {
         int smother = leavesProperties.getSmotherLeavesMax();
         if (smother != 0 && isBottom(world, pos)) {
            int smotherLeaves = 0;

            for(int i = 0; i < smother; ++i) {
               smotherLeaves += TreeHelper.isTreePart(world, pos.up(i + 1)) ? 1 : 0;
            }

            if (smotherLeaves >= smother) {
               return false;
            }
         }

         return world.getLightFor(EnumSkyBlock.SKY, pos) >= (TreeHelper.isLeaves(blockState) ? leavesProperties.getLightRequirement() - 2 : leavesProperties.getLightRequirement());
      }
   }

   public static boolean isBottom(World world, BlockPos pos) {
      IBlockState belowBlockState = world.getBlockState(pos.down());
      ITreePart belowTreepart = TreeHelper.getTreePart(belowBlockState);
      if (belowTreepart != TreeHelper.nullTreePart) {
         return belowTreepart.getRadius(belowBlockState) > 1;
      } else {
         return true;
      }
   }

   public int getHydrationLevelFromNeighbors(IBlockAccess access, BlockPos pos, ILeavesProperties leavesProp) {
      ICell[] cells = new ICell[6];

      for(EnumFacing dir : EnumFacing.VALUES) {
         BlockPos deltaPos = pos.offset(dir);
         IBlockState state = access.getBlockState(deltaPos);
         ITreePart part = TreeHelper.getTreePart(state);
         cells[dir.ordinal()] = part.getHydrationCell(access, deltaPos, state, dir, leavesProp);
      }

      return leavesProp.getCellKit().getCellSolver().solve(cells);
   }

   public ICell getHydrationCell(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing dir, ILeavesProperties leavesProperties) {
      return (ICell)(dir != null ? leavesProperties.getCellKit().getCellForLeaves(((Integer)state.getValue(HYDRO)).intValue()) : CellNull.NULLCELL);
   }

   public GrowSignal growSignal(World world, BlockPos pos, GrowSignal signal) {
      if (signal.step()) {
         this.branchOut(world, pos, signal);
      }

      return signal;
   }

   public boolean needLeaves(World world, BlockPos pos, ILeavesProperties leavesProperties) {
      if (world.isAirBlock(pos)) {
         return this.growLeavesIfLocationIsSuitable(world, leavesProperties, pos, leavesProperties.getCellKit().getDefaultHydration());
      } else {
         IBlockState blockState = world.getBlockState(pos);
         ITreePart treepart = TreeHelper.getTreePart(blockState);
         return treepart == this && leavesProperties == this.getProperties(blockState);
      }
   }

   public GrowSignal branchOut(World world, BlockPos pos, GrowSignal signal) {
      ILeavesProperties leavesProperties = signal.getSpecies().getLeavesProperties();
      if (!this.needLeaves(world, pos, leavesProperties)) {
         signal.success = false;
         return signal;
      } else {
         EnumFacing originDir = signal.dir.getOpposite();

         for(EnumFacing dir : EnumFacing.VALUES) {
            if (!dir.equals(originDir) && TreeHelper.isBranch(world.getBlockState(pos.offset(dir)))) {
               signal.success = false;
               return signal;
            }
         }

         boolean hasLeaves = false;

         for(EnumFacing dir : EnumFacing.VALUES) {
            if (this.needLeaves(world, pos.offset(dir), leavesProperties)) {
               hasLeaves = true;
               break;
            }
         }

         if (hasLeaves) {
            TreeFamily family = signal.getSpecies().getFamily();
            family.getDynamicBranch().setRadius(world, pos, (int)family.getPrimaryThickness(), (EnumFacing)null);
            signal.radius = family.getSecondaryThickness();
         }

         signal.success = hasLeaves;
         return signal;
      }
   }

   public int probabilityForBlock(IBlockState state, IBlockAccess world, BlockPos pos, BlockBranch from) {
      return from.getFamily().isCompatibleDynamicLeaves(state, world, pos) ? 2 : 0;
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      if (ModConfigs.enableAltLeavesSnow && fromPos.getY() - pos.getY() == 1) {
         IBlockState newState = worldIn.getBlockState(fromPos);
         if (newState.getBlock() == Blocks.SNOW_LAYER) {
            worldIn.setBlockState(fromPos, ModBlocks.blockStates.snowLayer.get(), 2);
         }
      }

   }

   public List<ItemStack> getDrops(IBlockAccess access, BlockPos pos, IBlockState state, int fortune) {
      ArrayList<ItemStack> ret = new ArrayList();
      this.getExactSpecies(access, pos, this.getProperties(state)).getLeavesDrops(access, pos, ret, fortune);
      return ret;
   }

   Species getExactSpecies(IBlockAccess access, BlockPos pos, ILeavesProperties leavesProperties) {
      if (access instanceof World) {
         World world = (World)access;
         ArrayList<BlockPos> branchList = new ArrayList();

         for(BlockPos dPos : leavesProperties.getCellKit().getLeafCluster().getAllNonZero()) {
            dPos = pos.add(BlockPos.ORIGIN.subtract(dPos));
            IBlockState state = access.getBlockState(dPos);
            if (TreeHelper.isBranch(state)) {
               BlockBranch branch = TreeHelper.getBranch(state);
               if (branch.getFamily() == leavesProperties.getTree() && branch.getRadius(state) == 1) {
                  branchList.add(dPos);
               }
            }
         }

         if (!branchList.isEmpty()) {
            BlockPos closest = (BlockPos)branchList.get(0);
            double minDist = 999.0D;

            for(BlockPos dPos : branchList) {
               double d = pos.distanceSq(dPos);
               if (d < minDist) {
                  minDist = d;
                  closest = dPos;
               }
            }

            return TreeHelper.getExactSpecies(world, closest);
         }
      }

      return Species.NULLSPECIES;
   }

   public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune) {
      return null;
   }

   public int quantityDropped(EaglercraftRandom random) {
      return 0;
   }

   public int damageDropped(IBlockState state) {
      return 0;
   }

   public ArrayList<ItemStack> onSheared(ItemStack item, IBlockAccess blockAccess, BlockPos pos, int fortune) {
      ArrayList<ItemStack> ret = new ArrayList();
      ret.add(this.copyPrimitiveLeavesStack(this.getProperties(blockAccess.getBlockState(pos))));
      return ret;
   }

   protected ItemStack getSilkTouchDrop(IBlockState state) {
      return this.copyPrimitiveLeavesStack(this.getProperties(state));
   }

   public ItemStack copyPrimitiveLeavesStack(ILeavesProperties properties) {
      ItemStack stack = properties.getPrimitiveLeavesItemStack().copy();
      stack.setCount(1);
      return stack;
   }

   public int getRadiusForConnection(IBlockState state, IBlockAccess blockAccess, BlockPos pos, BlockBranch from, EnumFacing side, int fromRadius) {
      return this.getProperties(state).getRadiusForConnection(state, blockAccess, pos, from, side, fromRadius);
   }

   public boolean isFoliage(IBlockAccess world, BlockPos pos) {
      return true;
   }

   public int getRadius(IBlockState state) {
      return 0;
   }

   public boolean shouldAnalyse() {
      return false;
   }

   public MapSignal analyse(IBlockState state, World world, BlockPos pos, EnumFacing fromDir, MapSignal signal) {
      return signal;
   }

   public int branchSupport(IBlockState state, IBlockAccess world, BlockBranch branch, BlockPos pos, EnumFacing dir, int radius) {
      return radius == 1 && branch.getFamily() == this.getFamily(state, world, pos) ? BlockBranch.setSupport(0, 1) : 0;
   }

   public EnumPushReaction getMobilityFlag(IBlockState state) {
      return EnumPushReaction.DESTROY;
   }

   public net.minecraft.block.BlockPlanks.EnumType getWoodType(int meta) {
      return net.minecraft.block.BlockPlanks.EnumType.OAK;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return Blocks.LEAVES.isOpaqueCube(state);
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return Blocks.LEAVES.getBlockLayer();
   }

   @SideOnly(Side.CLIENT)
   public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
      this.setGraphicsLevel(!Blocks.LEAVES.isOpaqueCube(blockState));
      return super.shouldSideBeRendered(blockState, blockAccess, pos, side);
   }

   public final ITreePart.TreePartType getTreePartType() {
      return ITreePart.TreePartType.LEAVES;
   }

   public boolean isRayTraceCollidable() {
      return true;
   }

   protected interface NewLeavesPropertiesHandler {
      IBlockState getLeaves(World var1, BlockPos var2, IBlockState var3);
   }
}
