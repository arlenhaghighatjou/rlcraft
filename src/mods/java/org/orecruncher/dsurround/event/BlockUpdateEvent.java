package org.orecruncher.dsurround.event;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BlockUpdateEvent extends Event {
   public final World world;
   public final BlockPos pos;
   public final IBlockState oldState;
   public final IBlockState newState;
   public final int flags;

   public BlockUpdateEvent(World world, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
      this.world = world;
      this.pos = pos;
      this.oldState = oldState;
      this.newState = newState;
      this.flags = flags;
   }
}
