package tschipp.callablehorses.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import tschipp.callablehorses.CallableHorses;
import tschipp.callablehorses.common.capabilities.horseowner.HorseOwnerProvider;
import tschipp.callablehorses.common.capabilities.horseowner.IHorseOwner;
import tschipp.callablehorses.common.helper.HorseHelper;

public class OwnerSyncShowStatsPacket implements IMessage, IMessageHandler<OwnerSyncShowStatsPacket, IMessage> {
   private NBTTagCompound ownerNBT = null;

   public OwnerSyncShowStatsPacket() {
   }

   public OwnerSyncShowStatsPacket(IHorseOwner owner) {
      this.ownerNBT = (NBTTagCompound)HorseOwnerProvider.OWNER_CAPABILITY.getStorage().writeNBT(HorseOwnerProvider.OWNER_CAPABILITY, owner, (EnumFacing)null);
   }

   public void fromBytes(ByteBuf buf) {
      this.ownerNBT = ByteBufUtils.readTag(buf);
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeTag(buf, this.ownerNBT);
   }

   public IMessage onMessage(final OwnerSyncShowStatsPacket message, MessageContext ctx) {
      IThreadListener mainThread = Minecraft.getMinecraft();
      mainThread.addScheduledTask(new Runnable() {
         World world;
         EntityPlayer player;

         {
            this.world = Minecraft.getMinecraft().world;
            this.player = Minecraft.getMinecraft().player;
         }

         public void run() {
            if (this.player != null) {
               IHorseOwner owner = HorseHelper.getOwnerCap(this.player);
               HorseOwnerProvider.OWNER_CAPABILITY.getStorage().readNBT(HorseOwnerProvider.OWNER_CAPABILITY, owner, (EnumFacing)null, message.ownerNBT);
               this.player.openGui(CallableHorses.instance, 0, this.player.world, 0, 0, 0);
            }

         }
      });
      return null;
   }
}
