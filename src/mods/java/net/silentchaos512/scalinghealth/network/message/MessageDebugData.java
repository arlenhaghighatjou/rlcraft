package net.silentchaos512.scalinghealth.network.message;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import javax.annotation.Nullable;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.lib.event.ClientTicks;
import net.silentchaos512.scalinghealth.event.DifficultyHandler;
import net.silentchaos512.scalinghealth.network.Message;

public class MessageDebugData extends Message<MessageDebugData> {
   public int mobsProcessed = DifficultyHandler.debugGetMobsProcessed();

   public MessageDebugData() {
   }

   @Nullable
   @SideOnly(Side.CLIENT)
   public void fromBytes(ByteBuf buf) {
      this.mobsProcessed = readInt(buf);
   }

   public void toBytes(ByteBuf buf) {
      writeInt(this.mobsProcessed, buf);
   }

   public IMessage handleMessage(MessageContext context) {
      ClientTicks.scheduleAction(() -> {
         DifficultyHandler.debugHandleSyncMessage(this);
      });
      return null;
   }
}
