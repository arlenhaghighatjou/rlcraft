package com.ferreusveritas.dynamictrees.blocks;

import com.ferreusveritas.dynamictrees.entities.EntityFallingTree;
import com.ferreusveritas.dynamictrees.util.CoordUtils;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockTrunkShell extends Block {
   public static final PropertyEnum<CoordUtils.Surround> COREDIR = PropertyEnum.create("coredir", CoordUtils.Surround.class);
   public static final String name = "trunkshell";

   public BlockTrunkShell() {
      super(Material.WOOD);
      this.setDefaultState(this.blockState.getBaseState().withProperty(COREDIR, CoordUtils.Surround.S));
      this.setRegistryName("trunkshell");
      this.setUnlocalizedName("trunkshell");
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(COREDIR, CoordUtils.Surround.values()[meta & 7]);
   }

   public int getMetaFromState(IBlockState state) {
      return ((CoordUtils.Surround)state.getValue(COREDIR)).ordinal() & 7;
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{COREDIR});
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (this.getMuseUnchecked(worldIn, state, pos) == null) {
         worldIn.setBlockToAir(pos);
      }

   }

   public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
      BlockTrunkShell.ShellMuse muse = this.getMuse(world, state, pos);
      return muse != null ? muse.state.getBlock().removedByPlayer(muse.state, world, muse.pos, player, willHarvest) : false;
   }

   public float getBlockHardness(IBlockState blockState, World world, BlockPos pos) {
      if (world == null && pos == null) {
         return 2.0F;
      } else {
         BlockTrunkShell.ShellMuse muse = this.getMuse(world, blockState, pos);
         return muse != null ? muse.state.getBlock().getBlockHardness(muse.state, world, muse.pos) : 0.0F;
      }
   }

   public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
      BlockTrunkShell.ShellMuse muse = this.getMuse(world, state, pos);
      return muse != null ? muse.state.getBlock().getSoundType(muse.state, world, muse.pos, entity) : SoundType.WOOD;
   }

   public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
      BlockTrunkShell.ShellMuse muse = this.getMuse(world, pos);
      return muse != null ? muse.state.getBlock().getExplosionResistance(world, muse.pos, exploder, explosion) : 0.0F;
   }

   public boolean isReplaceable(IBlockAccess access, BlockPos pos) {
      return this.getMuse(access, pos) == null;
   }

   public CoordUtils.Surround getMuseDir(@Nonnull IBlockState state, @Nonnull BlockPos pos) {
      return (CoordUtils.Surround)state.getValue(COREDIR);
   }

   @Nullable
   public BlockTrunkShell.ShellMuse getMuseUnchecked(@Nonnull IBlockAccess access, @Nonnull BlockPos pos) {
      return this.getMuseUnchecked(access, access.getBlockState(pos), pos);
   }

   @Nullable
   public BlockTrunkShell.ShellMuse getMuseUnchecked(@Nonnull IBlockAccess access, @Nonnull IBlockState state, @Nonnull BlockPos pos) {
      CoordUtils.Surround museDir = this.getMuseDir(state, pos);
      BlockPos musePos = pos.add(museDir.getOffset());
      IBlockState museState = access.getBlockState(musePos);
      Block block = museState.getBlock();
      return block instanceof IMusable && ((IMusable)block).isMusable() ? new BlockTrunkShell.ShellMuse(museState, musePos, museDir) : null;
   }

   @Nullable
   public BlockTrunkShell.ShellMuse getMuse(@Nonnull IBlockAccess access, @Nonnull BlockPos pos) {
      return this.getMuse(access, access.getBlockState(pos), pos);
   }

   @Nullable
   public BlockTrunkShell.ShellMuse getMuse(@Nonnull IBlockAccess access, @Nonnull IBlockState state, @Nonnull BlockPos pos) {
      BlockTrunkShell.ShellMuse muse = this.getMuseUnchecked(access, state, pos);
      if (muse == null || muse.getRadius() <= 8) {
         this.scheduleForClearing(access, pos);
      }

      return muse;
   }

   public void scheduleForClearing(IBlockAccess access, BlockPos pos) {
      if (access instanceof World) {
         World world = (World)access;
         if (!world.isRemote) {
            world.scheduleBlockUpdate(pos.toImmutable(), this, 0, 3);
         }
      }

   }

   public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
      this.getMuse(world, pos);
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess access, BlockPos pos) {
      BlockTrunkShell.ShellMuse muse = this.getMuse(access, state, pos);
      if (muse != null) {
         AxisAlignedBB aabb = muse.state.getBoundingBox(access, muse.pos);
         return aabb.offset(new BlockPos(muse.dir.getOffset())).intersect(FULL_BLOCK_AABB);
      } else {
         return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
      }
   }

   public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
      AxisAlignedBB aabb = super.getCollisionBoundingBox(blockState, worldIn, pos);
      return aabb == FULL_BLOCK_AABB ? NULL_AABB : aabb;
   }

   public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState) {
      if (!(entityIn instanceof EntityFallingTree)) {
         super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState);
      }
   }

   @SideOnly(Side.CLIENT)
   public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
      BlockTrunkShell.ShellMuse muse = this.getMuseUnchecked(worldIn, state, pos);
      return muse.state.getBoundingBox(worldIn, muse.pos).offset(muse.pos);
   }

   public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
      BlockTrunkShell.ShellMuse muse = this.getMuse(world, state, pos);
      return muse != null ? muse.state.getBlock().getPickBlock(muse.state, target, world, muse.pos, player) : ItemStack.EMPTY;
   }

   public boolean isAir(IBlockState state, IBlockAccess access, BlockPos pos) {
      if (pos == null) {
         return false;
      } else {
         return this.getMuse(access, state, pos) == null;
      }
   }

   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.INVISIBLE;
   }

   public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
      BlockTrunkShell.ShellMuse muse = this.getMuse(world, pos);
      if (muse != null) {
         muse.state.getBlock().onBlockExploded(world, muse.pos, explosion);
      }

   }

   protected CoordUtils.Surround findDetachedMuse(World world, BlockPos pos) {
      for(CoordUtils.Surround s : CoordUtils.Surround.values()) {
         IBlockState state = world.getBlockState(pos.add(s.getOffset()));
         if (state.getBlock() instanceof IMusable) {
            return s;
         }
      }

      return null;
   }

   public void breakBlock(World world, BlockPos pos, IBlockState state) {
      IBlockState newState = world.getBlockState(pos);
      if (newState.getBlock() == Blocks.AIR) {
         CoordUtils.Surround surr = this.findDetachedMuse(world, pos);
         if (surr != null) {
            world.setBlockState(pos, this.getDefaultState().withProperty(COREDIR, surr));
         }
      }

   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      BlockTrunkShell.ShellMuse muse = this.getMuse(world, pos);
      return muse != null ? muse.state.getBlock().onBlockActivated(world, muse.pos, muse.state, playerIn, hand, facing, hitX, hitY, hitZ) : false;
   }

   public boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return false;
   }

   public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return 0;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return null;
   }

   public int quantityDropped(Random random) {
      return 0;
   }

   public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
      return false;
   }

   public EnumPushReaction getMobilityFlag(IBlockState state) {
      return EnumPushReaction.BLOCK;
   }

   @SideOnly(Side.CLIENT)
   public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
      IBlockState state = world.getBlockState(pos);
      if (state.getBlock() == this) {
         BlockTrunkShell.ShellMuse muse = this.getMuseUnchecked(world, state, pos);
         if (muse == null) {
            return true;
         }

         IBlockState museState = muse.state;
         BlockPos musePos = muse.pos;
         manager.addBlockDestroyEffects(musePos, museState);
      }

      return true;
   }

   @SideOnly(Side.CLIENT)
   public boolean addHitEffects(IBlockState state, World world, RayTraceResult target, ParticleManager manager) {
      BlockPos shellPos = target.getBlockPos();
      if (state.getBlock() == this) {
         BlockTrunkShell.ShellMuse muse = this.getMuseUnchecked(world, state, shellPos);
         if (muse == null) {
            return true;
         }

         IBlockState museState = muse.state;
         BlockPos musePos = muse.pos;
         Random rand = world.rand;
         int x = musePos.getX();
         int y = musePos.getY();
         int z = musePos.getZ();
         AxisAlignedBB axisalignedbb = museState.getBoundingBox(world, musePos);
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

         ParticleDigging particle = (ParticleDigging)manager.spawnEffectParticle(EnumParticleTypes.BLOCK_CRACK.getParticleID(), d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[]{Block.getStateId(museState)});
         if (particle != null) {
            particle.setBlockPos(musePos).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F);
         }
      }

      return true;
   }

   public static class ShellMuse {
      public final IBlockState state;
      public final BlockPos pos;
      public final CoordUtils.Surround dir;

      public ShellMuse(IBlockState state, BlockPos pos, CoordUtils.Surround dir) {
         this.state = state;
         this.pos = pos;
         this.dir = dir;
      }

      public int getRadius() {
         Block block = this.state.getBlock();
         return block instanceof BlockBranch ? ((BlockBranch)block).getRadius(this.state) : 0;
      }
   }
}
