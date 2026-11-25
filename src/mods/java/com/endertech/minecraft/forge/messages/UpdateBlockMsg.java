package com.endertech.minecraft.forge.messages;

import com.endertech.common.CommonMath;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class UpdateBlockMsg extends ForgeNetMsg {
   public BlockPos pos;

   public UpdateBlockMsg() {
      this.pos = BlockPos.ORIGIN;
   }

   public UpdateBlockMsg(BlockPos pos) {
      this.pos = BlockPos.ORIGIN;
      if (pos != null) {
         this.pos = pos;
      }

   }

   public UpdateBlockMsg(int x, int y, int z) {
      this(new BlockPos(x, y, z));
   }

   public static class Handler extends ForgeNetMsgHandler<UpdateBlockMsg> {
      public Handler() {
      }

      public IMessage onMessage(UpdateBlockMsg message, MessageContext ctx) {
         World world = this.getWorld(ctx);
         if (world != null) {
            this.addScheduledTask(ctx, () -> {
               IBlockState blockState = world.getBlockState(message.pos);
               if (blockState != null) {
                  Block block = blockState.getBlock();
                  if (block != null && block != Blocks.AIR) {
                     block.updateTick(world, message.pos, blockState, CommonMath.RANDOM);
                  }
               }

            });
         }

         return null;
      }
   }
}
