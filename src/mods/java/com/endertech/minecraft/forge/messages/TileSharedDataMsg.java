package com.endertech.minecraft.forge.messages;

import com.endertech.minecraft.forge.ForgeMain;
import com.endertech.minecraft.forge.units.ForgeTileEntity;
import com.endertech.minecraft.forge.world.ForgeWorld;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class TileSharedDataMsg extends ForgeNetMsg {
   public NBTTagCompound compound;
   public BlockPos pos;

   public TileSharedDataMsg() {
   }

   public TileSharedDataMsg(NBTTagCompound compound, BlockPos pos) {
      this.compound = compound;
      this.pos = pos;
   }

   public static class Handler extends ForgeNetMsgHandler<TileSharedDataMsg> {
      public Handler() {
      }

      public IMessage onMessage(TileSharedDataMsg message, MessageContext ctx) {
         World world = this.getWorld(ctx);
         if (world != null) {
            this.addScheduledTask(ctx, () -> {
               TileEntity tileEntity = world.getTileEntity(message.pos);
               if (tileEntity instanceof ForgeTileEntity) {
                  ForgeTileEntity tile = (ForgeTileEntity)tileEntity;
                  tile.readSharedData(message.compound);
                  if (ForgeWorld.isServerSide(world)) {
                     tile.markDirty();
                     ForgeMain.instance.getConnection().sendToAll(message);
                  }
               }

            });
         }

         return null;
      }
   }
}
