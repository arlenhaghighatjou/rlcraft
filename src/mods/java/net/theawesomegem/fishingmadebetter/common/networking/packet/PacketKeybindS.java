package net.theawesomegem.fishingmadebetter.common.networking.packet;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.theawesomegem.fishingmadebetter.Primary;
import net.theawesomegem.fishingmadebetter.common.capability.fishing.FishingCapabilityProvider;
import net.theawesomegem.fishingmadebetter.common.capability.fishing.IFishingData;

public class PacketKeybindS implements IMessage {
   private PacketKeybindS.Keybind bind;

   public PacketKeybindS.Keybind getKeyBind() {
      return this.bind;
   }

   public PacketKeybindS() {
      this.bind = PacketKeybindS.Keybind.NONE;
   }

   public PacketKeybindS(PacketKeybindS.Keybind keybind) {
      this.bind = keybind;
   }

   public void fromBytes(ByteBuf buf) {
      this.bind = PacketKeybindS.Keybind.values()[buf.readShort()];
   }

   public void toBytes(ByteBuf buf) {
      buf.writeShort(this.bind.ordinal());
   }

   public static enum Keybind {
      REEL_IN,
      REEL_OUT,
      NONE;

      private Keybind() {
      }
   }

   public static class KeybindMessageHandler implements IMessageHandler<PacketKeybindS, IMessage> {
      public KeybindMessageHandler() {
      }

      public IMessage onMessage(PacketKeybindS message, MessageContext ctx) {
         EntityPlayer player = Primary.proxy.getPlayer(ctx);
         PacketKeybindS.Keybind bind = message.getKeyBind();
         if (player == null) {
            return null;
         } else {
            IThreadListener thread = Primary.proxy.getListener(ctx);
            thread.addScheduledTask(() -> {
               IFishingData fishingData = (IFishingData)player.getCapability(FishingCapabilityProvider.FISHING_DATA_CAP, (EnumFacing)null);
               if (fishingData != null) {
                  if (fishingData.isFishing()) {
                     fishingData.setKeybind(bind);
                  }
               }
            });
            return null;
         }
      }
   }
}
