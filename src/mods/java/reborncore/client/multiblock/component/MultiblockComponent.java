package reborncore.client.multiblock.component;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MultiblockComponent {
   public BlockPos relPos;
   public final IBlockState state;

   public MultiblockComponent(BlockPos relPos, IBlockState state) {
      this.relPos = relPos;
      this.state = state;
   }

   public BlockPos getRelativePosition() {
      return this.relPos;
   }

   public Block getBlock() {
      return this.state.getBlock();
   }

   public IBlockState getState() {
      return this.state;
   }

   public boolean matches(World world, int x, int y, int z) {
      return world.getBlockState(new BlockPos(x, y, z)).getBlock() == this.getBlock();
   }

   public void rotate(double angle) {
      double x = (double)this.relPos.getX();
      double z = (double)this.relPos.getZ();
      double sin = Math.sin(angle);
      double cos = Math.cos(angle);
      double xn = x * cos - z * sin;
      double zn = x * sin + z * cos;
      this.relPos = new BlockPos((int)Math.round(xn), this.relPos.getY(), (int)Math.round(zn));
   }

   public MultiblockComponent copy() {
      return new MultiblockComponent(this.relPos, this.state);
   }
}
