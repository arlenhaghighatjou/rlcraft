package vazkii.arl.network;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public abstract class TileEntityMessage<T extends TileEntity> extends NetworkMessage {
   public BlockPos pos;
   public transient T tile;
   public transient MessageContext context;

   public void toBytes(ByteBuf buf) {
      writeBlockPos(this.pos, buf);
   }

   public void fromBytes(ByteBuf buf) {
      this.pos = readBlockPos(buf);
   }

   public TileEntityMessage() {
   }

   public TileEntityMessage(BlockPos pos) {
      this.pos = pos;
   }

   public final IMessage handleMessage(MessageContext context) {
      this.context = context;
      World world = context.getServerHandler().player.getEntityWorld();
      TileEntity tile = world.getTileEntity(this.pos);
      if (tile != null) {
         try {
            this.tile = (T)tile;
            ((WorldServer)world).addScheduledTask(this.getAction());
         } catch (ClassCastException var5) {
            ;
         }
      }

      return super.handleMessage(context);
   }

   public abstract Runnable getAction();
}
