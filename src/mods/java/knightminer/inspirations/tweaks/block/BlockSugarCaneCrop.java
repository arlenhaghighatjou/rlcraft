package knightminer.inspirations.tweaks.block;

import knightminer.inspirations.tweaks.InspirationsTweaks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;

public class BlockSugarCaneCrop extends BlockBlockCrop {
   private static final AxisAlignedBB[] BOUNDS = new AxisAlignedBB[]{new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.125D, 0.875D), new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.25D, 0.875D), new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.375D, 0.875D), new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.5D, 0.875D), new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.625D, 0.875D), new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.75D, 0.875D), new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.875D, 0.875D)};

   public BlockSugarCaneCrop() {
      super(Blocks.REEDS, EnumPlantType.Beach, BOUNDS);
      this.setUnlocalizedName("reeds");
   }

   public boolean canBlockStay(World world, BlockPos pos, IBlockState state) {
      return Blocks.REEDS.canBlockStay(world, pos);
   }

   public Item getSeed() {
      return InspirationsTweaks.sugarCaneSeeds;
   }
}
