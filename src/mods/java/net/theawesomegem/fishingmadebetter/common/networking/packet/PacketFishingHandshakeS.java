package net.theawesomegem.fishingmadebetter.common.networking.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.theawesomegem.fishingmadebetter.Primary;
import net.theawesomegem.fishingmadebetter.common.capability.fishing.FishingCapabilityProvider;
import net.theawesomegem.fishingmadebetter.common.capability.fishing.IFishingData;
import net.theawesomegem.fishingmadebetter.common.networking.PrimaryPacketHandler;

public class PacketFishingHandshakeS implements IMessage {
   private boolean isFishing;

   public boolean getIsFishing() {
      return this.isFishing;
   }

   public PacketFishingHandshakeS() {
      this.isFishing = false;
   }

   public PacketFishingHandshakeS(boolean isFishing) {
      this.isFishing = isFishing;
   }

   public void fromBytes(ByteBuf buf) {
      this.isFishing = buf.readBoolean();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeBoolean(this.isFishing);
   }

   public static class HandshakeMessageHandler implements IMessageHandler<PacketFishingHandshakeS, IMessage> {
      public HandshakeMessageHandler() {
      }

      public IMessage onMessage(PacketFishingHandshakeS message, MessageContext ctx) {
         EntityPlayer player = Primary.proxy.getPlayer(ctx);
         if (player == null) {
            return null;
         } else {
            boolean isFishing = message.getIsFishing();
            IThreadListener thread = Primary.proxy.getListener(ctx);
            thread.addScheduledTask(() -> {
               IFishingData fishingData = (IFishingData)player.getCapability(FishingCapabilityProvider.FISHING_DATA_CAP, (EnumFacing)null);
               if (fishingData != null) {
                  if (!fishingData.isFishing() && isFishing) {
                     PrimaryPacketHandler.INSTANCE.sendTo(new PacketReelingC(fishingData.getReelAmount(), fishingData.getReelTarget(), fishingData.getFishDistance(), fishingData.getFishDeepLevel(), fishingData.getErrorVariance(), fishingData.isFishing(), fishingData.getMinigameBackground()[0].intValue(), fishingData.getMinigameBackground()[1].intValue(), fishingData.getMinigameBackground()[2].intValue(), fishingData.getMinigameBackground()[3].intValue(), fishingData.getMinigameBackground()[4].intValue(), fishingData.getLineBreak()), (EntityPlayerMP)player);
                  }

               }
            });
            return null;
         }
      }
   }
}
