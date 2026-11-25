package lykrast.defiledlands.common.block;

import java.util.Random;
import lykrast.defiledlands.common.util.CorruptionHelper;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockLog.EnumAxis;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockLogTenebra extends BlockLog {
   public BlockLogTenebra(float hardness, float resistance) {
      this.setHardness(hardness);
      this.setResistance(resistance);
      this.setDefaultState(this.blockState.getBaseState().withProperty(LOG_AXIS, EnumAxis.Y));
      this.setTickRandomly(true);
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      CorruptionHelper.spread(worldIn, pos, state, rand);
      super.updateTick(worldIn, pos, state, rand);
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{LOG_AXIS});
   }

   public IBlockState getStateFromMeta(int meta) {
      IBlockState iblockstate = this.getDefaultState();
      switch(meta & 12) {
      case 0:
         iblockstate = iblockstate.withProperty(LOG_AXIS, EnumAxis.Y);
         break;
      case 4:
         iblockstate = iblockstate.withProperty(LOG_AXIS, EnumAxis.X);
         break;
      case 8:
         iblockstate = iblockstate.withProperty(LOG_AXIS, EnumAxis.Z);
         break;
      default:
         iblockstate = iblockstate.withProperty(LOG_AXIS, EnumAxis.NONE);
      }

      return iblockstate;
   }

   public int getMetaFromState(IBlockState state) {
      int i = 0;
      switch((EnumAxis)state.getValue(LOG_AXIS)) {
      case X:
         i |= 4;
         break;
      case Z:
         i |= 8;
         break;
      case NONE:
         i |= 12;
      }

      return i;
   }
}
