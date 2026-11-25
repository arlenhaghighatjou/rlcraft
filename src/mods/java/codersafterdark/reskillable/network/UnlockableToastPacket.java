package codersafterdark.reskillable.network;

import codersafterdark.reskillable.api.ReskillableRegistries;
import codersafterdark.reskillable.api.toast.ToastHelper;
import codersafterdark.reskillable.api.unlockable.Unlockable;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.util.concurrent.Callable;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class UnlockableToastPacket implements IMessage, IMessageHandler<UnlockableToastPacket, IMessage> {
   private ResourceLocation unlockableName;

   public UnlockableToastPacket() {
   }

   public UnlockableToastPacket(ResourceLocation unlockableName) {
      this.unlockableName = unlockableName;
   }

   public void fromBytes(ByteBuf buf) {
      this.unlockableName = new ResourceLocation(ByteBufUtils.readUTF8String(buf));
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeUTF8String(buf, this.unlockableName.toString());
   }

   public IMessage onMessage(UnlockableToastPacket message, MessageContext ctx) {
      Minecraft.getMinecraft().addScheduledTask(() -> {
         return this.handleMessage(message, ctx);
      });
      return null;
   }

   public IMessage handleMessage(UnlockableToastPacket message, MessageContext ctx) {
      ToastHelper.sendUnlockableToast((Unlockable)ReskillableRegistries.UNLOCKABLES.getValue(message.unlockableName));
      return null;
   }
}
