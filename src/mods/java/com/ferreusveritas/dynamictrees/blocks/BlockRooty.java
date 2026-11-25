package com.ferreusveritas.dynamictrees.blocks;

import com.ferreusveritas.dynamictrees.ModConfigs;
import com.ferreusveritas.dynamictrees.api.ICustomRootDecay;
import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.cells.CellNull;
import com.ferreusveritas.dynamictrees.api.cells.ICell;
import com.ferreusveritas.dynamictrees.api.network.MapSignal;
import com.ferreusveritas.dynamictrees.api.treedata.ILeavesProperties;
import com.ferreusveritas.dynamictrees.api.treedata.ITreePart;
import com.ferreusveritas.dynamictrees.entities.EntityFallingTree;
import com.ferreusveritas.dynamictrees.systems.GrowSignal;
import com.ferreusveritas.dynamictrees.tileentity.TileEntitySpecies;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.trees.TreeFamily;
import com.ferreusveritas.dynamictrees.util.BranchDestructionData;
import com.ferreusveritas.dynamictrees.util.CoordUtils;
import java.util.ArrayList;
import java.util.Optional;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockRooty extends Block implements ITreePart, ITileEntityProvider, MimicProperty.IMimic {
   public static ICustomRootDecay customRootDecay = null;
   public static final PropertyInteger LIFE = PropertyInteger.create("life", 0, 15);

   public BlockRooty(String name, Material material, boolean isTileEntity) {
      super(material);
      this.hasTileEntity = isTileEntity;
      this.setRegistryName(name);
      this.setUnlocalizedName(this.getRegistryName().toString());
      this.setSoundType(SoundType.GROUND);
      this.setDefaultState(this.blockState.getBaseState().withProperty(LIFE, Integer.valueOf(15)));
      this.setTickRandomly(true);
   }

   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      super.breakBlock(worldIn, pos, state);
      if (this.hasTileEntity(state)) {
         worldIn.removeTileEntity(pos);
      }

   }

   public boolean hasTileEntity(IBlockState state) {
      return this.hasTileEntity;
   }

   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return this.hasTileEntity ? new TileEntitySpecies() : null;
   }

   public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      return tileentity != null && tileentity.receiveClientEvent(id, param);
   }

   protected BlockStateContainer createBlockState() {
      return new ExtendedBlockState(this, new IProperty[]{LIFE}, new IUnlistedProperty[]{MimicProperty.MIMIC});
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(LIFE, Integer.valueOf(meta));
   }

   public int getMetaFromState(IBlockState state) {
      return ((Integer)state.getValue(LIFE)).intValue();
   }

   public void randomTick(World world, BlockPos pos, IBlockState state, EaglercraftRandom random) {
      if (random.nextInt(ModConfigs.treeGrowthFolding) == 0) {
         this.updateTree(state, world, pos, random, true);
      }

   }

   public EnumFacing getTrunkDirection(IBlockAccess access, BlockPos rootPos) {
      return EnumFacing.UP;
   }

   public void updateTree(IBlockState rootyState, World world, BlockPos rootPos, EaglercraftRandom random, boolean natural) {
      if (CoordUtils.isSurroundedByLoadedChunks(world, rootPos)) {
         boolean viable = false;
         Species species = this.getSpecies(rootyState, world, rootPos);
         if (species.isValid()) {
            BlockPos treePos = rootPos.offset(this.getTrunkDirection(world, rootPos));
            ITreePart treeBase = TreeHelper.getTreePart(world.getBlockState(treePos));
            if (treeBase != TreeHelper.nullTreePart) {
               viable = species.update(world, this, rootPos, this.getSoilLife(rootyState, world, rootPos), treeBase, treePos, random, natural);
            }
         }

         if (!viable) {
            world.setBlockState(rootPos, this.getDecayBlockState(world, rootPos), 3);
         }
      }

   }

   public IBlockState getDecayBlockState(IBlockAccess access, BlockPos pos) {
      return Blocks.DIRT.getDefaultState();
   }

   public void doDecay(World world, BlockPos rootPos, IBlockState rootyState, Species species) {
      if (!world.isRemote && TreeHelper.isRooty(rootyState)) {
         this.updateTree(rootyState, world, rootPos, world.rand, true);
         IBlockState newState = world.getBlockState(rootPos);
         if (!TreeHelper.isRooty(newState)) {
            if (customRootDecay != null && customRootDecay.doDecay(world, rootPos, rootyState, species)) {
               return;
            }

            Biome biome = world.getBiome(rootPos);
            IBlockState mimic = this.getMimic(world, rootPos);
            if (mimic != biome.topBlock && mimic != biome.fillerBlock) {
               if (biome.topBlock.getMaterial() == newState.getMaterial()) {
                  world.setBlockState(rootPos, biome.topBlock);
               } else if (biome.fillerBlock.getMaterial() == newState.getMaterial()) {
                  world.setBlockState(rootPos, biome.fillerBlock);
               }
            } else {
               world.setBlockState(rootPos, mimic);
            }
         }
      }

   }

   public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune) {
      return Item.getItemFromBlock(Blocks.DIRT);
   }

   public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
      IBlockState mimicState = this.getMimic(world, pos);
      return new ItemStack(mimicState.getBlock(), 1, mimicState.getBlock().damageDropped(mimicState));
   }

   public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
      return 20.0F;
   }

   protected boolean canSilkHarvest() {
      return false;
   }

   public boolean hasComparatorInputOverride(IBlockState state) {
      return true;
   }

   public int getComparatorInputOverride(IBlockState blockState, World world, BlockPos pos) {
      return this.getSoilLife(blockState, world, pos);
   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      ItemStack heldItem = player.getHeldItem(hand);
      return this.getFamily(state, world, pos).onTreeActivated(world, pos, state, player, hand, heldItem, facing, hitX, hitY, hitZ);
   }

   public void destroyTree(World world, BlockPos rootPos) {
      Optional<BlockBranch> branch = TreeHelper.getBranchOpt(world.getBlockState(rootPos.up()));
      if (branch.isPresent()) {
         BranchDestructionData destroyData = ((BlockBranch)branch.get()).destroyBranchFromNode(world, rootPos.up(), EnumFacing.DOWN, true);
         EntityFallingTree.dropTree(world, destroyData, new ArrayList(0), EntityFallingTree.DestroyType.ROOT);
      }

   }

   public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
      this.destroyTree(world, pos);
   }

   public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
      this.destroyTree(world, pos);
   }

   public int getSoilLife(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos) {
      return ((Integer)blockState.getValue(LIFE)).intValue();
   }

   public void setSoilLife(World world, BlockPos rootPos, int life) {
      Species species = this.getSpecies(world.getBlockState(rootPos), world, rootPos);
      world.setBlockState(rootPos, this.getDefaultState().withProperty(LIFE, Integer.valueOf(MathHelper.clamp(life, 0, 15))), 3);
      world.notifyNeighborsOfStateChange(rootPos, this, false);
      this.setSpecies(world, rootPos, species);
   }

   public boolean fertilize(World world, BlockPos pos, int amount) {
      int soilLife = this.getSoilLife(world.getBlockState(pos), world, pos);
      if ((soilLife != 0 || amount >= 0) && (soilLife != 15 || amount <= 0)) {
         this.setSoilLife(world, pos, soilLife + amount);
         return true;
      } else {
         return false;
      }
   }

   public ICell getHydrationCell(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing dir, ILeavesProperties leavesTree) {
      return CellNull.NULLCELL;
   }

   public GrowSignal growSignal(World world, BlockPos pos, GrowSignal signal) {
      return signal;
   }

   public int getRadius(IBlockState state) {
      return 8;
   }

   public int getRadiusForConnection(IBlockState state, IBlockAccess blockAccess, BlockPos pos, BlockBranch from, EnumFacing side, int fromRadius) {
      return 8;
   }

   public int probabilityForBlock(IBlockState state, IBlockAccess world, BlockPos pos, BlockBranch from) {
      return 0;
   }

   public MapSignal startAnalysis(World world, BlockPos rootPos, MapSignal signal) {
      EnumFacing dir = this.getTrunkDirection(world, rootPos);
      BlockPos treePos = rootPos.offset(dir);
      IBlockState treeState = world.getBlockState(treePos);
      TreeHelper.getTreePart(treeState).analyse(treeState, world, treePos, (EnumFacing)null, signal);
      return signal;
   }

   public boolean shouldAnalyse() {
      return true;
   }

   public MapSignal analyse(IBlockState state, World world, BlockPos pos, EnumFacing fromDir, MapSignal signal) {
      signal.run(state, world, pos, fromDir);
      if (signal.root == null) {
         signal.root = pos;
      } else {
         signal.multiroot = true;
      }

      signal.found = true;
      return signal;
   }

   public int branchSupport(IBlockState state, IBlockAccess world, BlockBranch branch, BlockPos pos, EnumFacing dir, int radius) {
      return dir == EnumFacing.DOWN ? BlockBranch.setSupport(1, 1) : 0;
   }

   public TreeFamily getFamily(IBlockState state, IBlockAccess world, BlockPos rootPos) {
      BlockPos treePos = rootPos.offset(this.getTrunkDirection(world, rootPos));
      IBlockState treeState = world.getBlockState(treePos);
      return TreeHelper.isBranch(treeState) ? TreeHelper.getBranch(treeState).getFamily(treeState, world, treePos) : TreeFamily.NULLFAMILY;
   }

   private TileEntitySpecies getTileEntitySpecies(World world, BlockPos pos) {
      return (TileEntitySpecies)world.getTileEntity(pos);
   }

   public Species getSpecies(IBlockState blockState, World world, BlockPos rootPos) {
      TreeFamily tree = this.getFamily(blockState, world, rootPos);
      if (this.hasTileEntity) {
         TileEntitySpecies rootyDirtTE = this.getTileEntitySpecies(world, rootPos);
         if (rootyDirtTE instanceof TileEntitySpecies) {
            Species species = rootyDirtTE.getSpecies();
            if (species.getFamily() == tree) {
               return rootyDirtTE.getSpecies();
            }
         }
      }

      return tree.getSpeciesForLocation(world, rootPos.offset(this.getTrunkDirection(world, rootPos)));
   }

   public void setSpecies(World world, BlockPos rootPos, Species species) {
      if (this.hasTileEntity) {
         TileEntitySpecies rootyDirtTE = this.getTileEntitySpecies(world, rootPos);
         if (rootyDirtTE instanceof TileEntitySpecies) {
            rootyDirtTE.setSpecies(species);
         }
      }

   }

   public EnumPushReaction getMobilityFlag(IBlockState state) {
      return EnumPushReaction.BLOCK;
   }

   public final ITreePart.TreePartType getTreePartType() {
      return ITreePart.TreePartType.ROOT;
   }

   public final boolean isRootNode() {
      return true;
   }

   public IBlockState getExtendedState(IBlockState state, IBlockAccess access, BlockPos pos) {
      return (IBlockState)(state instanceof IExtendedBlockState ? ((IExtendedBlockState)state).withProperty(MimicProperty.MIMIC, this.getMimic(access, pos)) : state);
   }

   public IBlockState getMimic(IBlockAccess access, BlockPos pos) {
      return Blocks.DIRT.getDefaultState();
   }

   @SideOnly(Side.CLIENT)
   public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
      IBlockState mimicState = this.getMimic(world, pos);
      manager.addBlockDestroyEffects(pos, mimicState);
      return true;
   }

   @SideOnly(Side.CLIENT)
   public boolean addHitEffects(IBlockState state, World world, RayTraceResult target, ParticleManager manager) {
      BlockPos pos = target.getBlockPos();
      IBlockState mimicState = (IBlockState)((IExtendedBlockState)this.getExtendedState(state, world, pos)).getValue(MimicProperty.MIMIC);
      EaglercraftRandom rand = world.rand;
      int x = pos.getX();
      int y = pos.getY();
      int z = pos.getZ();
      AxisAlignedBB axisalignedbb = state.getBoundingBox(world, pos);
      double d0 = (double)x + rand.nextDouble() * (axisalignedbb.maxX - axisalignedbb.minX - 0.2D) + 0.1D + axisalignedbb.minX;
      double d1 = (double)y + rand.nextDouble() * (axisalignedbb.maxY - axisalignedbb.minY - 0.2D) + 0.1D + axisalignedbb.minY;
      double d2 = (double)z + rand.nextDouble() * (axisalignedbb.maxZ - axisalignedbb.minZ - 0.2D) + 0.1D + axisalignedbb.minZ;
      switch(target.sideHit) {
      case DOWN:
         d1 = (double)y + axisalignedbb.minY - 0.1D;
         break;
      case UP:
         d1 = (double)y + axisalignedbb.maxY + 0.1D;
         break;
      case NORTH:
         d2 = (double)z + axisalignedbb.minZ - 0.1D;
         break;
      case SOUTH:
         d2 = (double)z + axisalignedbb.maxZ + 0.1D;
         break;
      case WEST:
         d0 = (double)x + axisalignedbb.minX - 0.1D;
         break;
      case EAST:
         d0 = (double)x + axisalignedbb.maxX + 0.1D;
      }

      ParticleDigging particle = (ParticleDigging)manager.spawnEffectParticle(EnumParticleTypes.BLOCK_CRACK.getParticleID(), d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[]{Block.getStateId(mimicState)});
      if (particle != null) {
         particle.setBlockPos(pos).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F);
      }

      return true;
   }

   @SideOnly(Side.CLIENT)
   public int rootColor(IBlockState state, IBlockAccess blockAccess, BlockPos pos) {
      return this.getFamily(state, blockAccess, pos).getRootColor(state, blockAccess, pos);
   }
}
