package vazkii.quark.world.block;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.block.BlockQuarkBush;
import vazkii.quark.world.feature.UndergroundBiomes;

public class BlockGlowshroom extends BlockQuarkBush implements IGrowable {
   protected static final AxisAlignedBB MUSHROOM_AABB = new AxisAlignedBB(0.2D, 0.0D, 0.2D, 0.8D, 0.6D, 0.8D);

   public BlockGlowshroom() {
      super("glowshroom");
      this.setLightLevel(0.9375F);
      this.setTickRandomly(true);
      this.setSoundType(SoundType.PLANT);
   }

   @Nonnull
   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return MUSHROOM_AABB;
   }

   protected boolean canSustainBush(IBlockState state) {
      return state.getBlock() == UndergroundBiomes.glowcelium;
   }

   public void updateTick(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, EaglercraftRandom rand) {
      if (rand.nextInt(UndergroundBiomes.glowshroomGrowthRate) == 0) {
         int i = 5;

         for(BlockPos targetPos : BlockPos.getAllInBoxMutable(pos.add(-4, -1, -4), pos.add(4, 1, 4))) {
            if (worldIn.getBlockState(targetPos).getBlock() == this) {
               --i;
               if (i <= 0) {
                  return;
               }
            }
         }

         BlockPos shiftedPos = pos.add(rand.nextInt(3) - 1, rand.nextInt(2) - rand.nextInt(2), rand.nextInt(3) - 1);

         for(int k = 0; k < 4; ++k) {
            if (worldIn.isAirBlock(shiftedPos) && this.canBlockStay(worldIn, shiftedPos, this.getDefaultState())) {
               pos = shiftedPos;
            }

            shiftedPos = pos.add(rand.nextInt(3) - 1, rand.nextInt(2) - rand.nextInt(2), rand.nextInt(3) - 1);
         }

         if (worldIn.isAirBlock(shiftedPos) && this.canBlockStay(worldIn, shiftedPos, this.getDefaultState())) {
            worldIn.setBlockState(shiftedPos, this.getDefaultState(), 2);
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, EaglercraftRandom rand) {
      super.randomDisplayTick(stateIn, worldIn, pos, rand);
      if (rand.nextInt(20) == 0) {
         worldIn.spawnParticle(EnumParticleTypes.END_ROD, (double)pos.getX() + 0.2D + (double)rand.nextFloat() * 0.6D, (double)((float)pos.getY() + 0.3F), (double)pos.getZ() + 0.2D + (double)rand.nextFloat() * 0.6D, 0.0D, 0.0D, 0.0D, new int[0]);
      }

   }

   public boolean canGrow(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, boolean isClient) {
      return UndergroundBiomes.bigGlowshroomsEnabled;
   }

   public boolean canUseBonemeal(@Nonnull World worldIn, @Nonnull EaglercraftRandom rand, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
      return UndergroundBiomes.bigGlowshroomsEnabled && (double)rand.nextFloat() < 0.4D;
   }

   public void grow(@Nonnull World worldIn, @Nonnull EaglercraftRandom rand, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
      if (UndergroundBiomes.bigGlowshroomsEnabled) {
         worldIn.setBlockToAir(pos);
         if (!BlockHugeGlowshroom.setInPosition(worldIn, rand, pos, true)) {
            worldIn.setBlockState(pos, this.getDefaultState());
         }
      }

   }
}
