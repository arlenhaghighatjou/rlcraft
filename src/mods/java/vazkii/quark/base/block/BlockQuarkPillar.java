package vazkii.quark.base.block;

import com.google.common.collect.UnmodifiableIterator;
import javax.annotation.Nonnull;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.arl.block.BlockMod;

public class BlockQuarkPillar extends BlockMod implements IQuarkBlock {
   public BlockQuarkPillar(String name, Material materialIn, String... variants) {
      super(name, materialIn, variants);
   }

   public boolean rotateBlock(World world, @Nonnull BlockPos pos, @Nonnull EnumFacing axis) {
      IBlockState state = world.getBlockState(pos);
      UnmodifiableIterator var5 = state.getProperties().keySet().iterator();

      while(var5.hasNext()) {
         IProperty<?> prop = (IProperty)var5.next();
         if (prop == BlockRotatedPillar.AXIS) {
            world.setBlockState(pos, state.cycleProperty(prop));
            return true;
         }
      }

      return false;
   }

   @Nonnull
   public IBlockState withRotation(@Nonnull IBlockState state, Rotation rot) {
      switch(rot) {
      case COUNTERCLOCKWISE_90:
      case CLOCKWISE_90:
         switch((Axis)state.getValue(BlockRotatedPillar.AXIS)) {
         case X:
            return state.withProperty(BlockRotatedPillar.AXIS, Axis.Z);
         case Z:
            return state.withProperty(BlockRotatedPillar.AXIS, Axis.X);
         default:
            return state;
         }
      default:
         return state;
      }
   }

   @Nonnull
   public IBlockState getStateFromMeta(int meta) {
      Axis axis = Axis.Y;
      int i = meta & 12;
      if (i == 4) {
         axis = Axis.X;
      } else if (i == 8) {
         axis = Axis.Z;
      }

      return this.getDefaultState().withProperty(BlockRotatedPillar.AXIS, axis);
   }

   public int getMetaFromState(IBlockState state) {
      int i = 0;
      Axis axis = (Axis)state.getValue(BlockRotatedPillar.AXIS);
      if (axis == Axis.X) {
         i |= 4;
      } else if (axis == Axis.Z) {
         i |= 8;
      }

      return i;
   }

   @Nonnull
   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{BlockRotatedPillar.AXIS});
   }

   @Nonnull
   public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getStateFromMeta(meta).withProperty(BlockRotatedPillar.AXIS, facing.getAxis());
   }
}
