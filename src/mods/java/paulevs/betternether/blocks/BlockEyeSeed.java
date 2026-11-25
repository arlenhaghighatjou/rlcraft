package paulevs.betternether.blocks;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import paulevs.betternether.BetterNether;

public class BlockEyeSeed extends Block implements IGrowable {
   protected static final AxisAlignedBB SEED_AABB = new AxisAlignedBB(0.25D, 0.25D, 0.25D, 0.75D, 1.0D, 0.75D);

   public BlockEyeSeed() {
      super(Material.PLANTS, MapColor.RED);
      this.setRegistryName("eye_seed");
      this.setUnlocalizedName("eye_seed");
      this.setTickRandomly(true);
      this.setCreativeTab(BetterNether.BN_TAB);
      this.setSoundType(SoundType.PLANT);
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return SEED_AABB;
   }

   public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
      for(int y = 1; y < 5; ++y) {
         if (worldIn.getBlockState(pos.down(y)).getBlock() != Blocks.AIR) {
            return false;
         }
      }

      return true;
   }

   public boolean canUseBonemeal(World worldIn, EaglercraftRandom rand, BlockPos pos, IBlockState state) {
      return (double)worldIn.rand.nextFloat() < 0.45D;
   }

   public void grow(World worldIn, EaglercraftRandom rand, BlockPos pos, IBlockState state) {
      int h = rand.nextInt(19) + 5;
      int h2 = h;
      boolean interrupted = false;

      for(int y = 1; y < h; ++y) {
         if (worldIn.getBlockState(pos.down(y)).getBlock() != Blocks.AIR) {
            h2 = y;
            interrupted = true;
            break;
         }
      }

      if (interrupted) {
         if (h2 < 5) {
            return;
         }

         h = (h2 >> 2) + rand.nextInt(h2 >> 2);
         if (h < 5) {
            h = 5;
         }

         --h;
      }

      IBlockState vineState = BlocksRegister.BLOCK_EYE_VINE.getDefaultState();
      IBlockState eyeState = null;
      if (rand.nextInt(2) == 0) {
         eyeState = BlocksRegister.BLOCK_EYEBALL.getDefaultState();
      } else {
         eyeState = BlocksRegister.BLOCK_EYEBALL_SMALL.getDefaultState();
      }

      for(int y = 0; y < h; ++y) {
         worldIn.setBlockState(pos.down(y), vineState, 2);
      }

      worldIn.setBlockState(pos.down(h), eyeState);
   }

   public void randomTick(World worldIn, BlockPos pos, IBlockState state, EaglercraftRandom random) {
      if (!worldIn.isRemote) {
         super.updateTick(worldIn, pos, state, random);
         if (worldIn.getBlockState(pos.up()).getBlock() != Blocks.NETHERRACK) {
            worldIn.destroyBlock(pos, true);
         } else if (random.nextInt(16) == 0 && this.canGrow(worldIn, pos, state, false)) {
            this.grow(worldIn, random, pos, state);
         }
      }

   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   @Nullable
   public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
      return NULL_AABB;
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      if (worldIn.getBlockState(pos.up()).getBlock() != Blocks.NETHERRACK) {
         worldIn.destroyBlock(pos, true);
      }

   }

   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos.up()).getBlock() == Blocks.NETHERRACK;
   }
}
