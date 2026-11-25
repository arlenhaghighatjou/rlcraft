package net.silentchaos512.scalinghealth.network.message;

import javax.annotation.Nullable;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.lib.event.ClientTicks;
import net.silentchaos512.scalinghealth.event.DifficultyHandler;
import net.silentchaos512.scalinghealth.network.Message;

public class MessageDebugData extends Message {
   public int mobsProcessed = DifficultyHandler.debugGetMobsProcessed();

   public MessageDebugData() {
   }

   @Nullable
   @SideOnly(Side.CLIENT)
   public IMessage handleMessage(MessageContext context) {
      ClientTicks.scheduleAction(() -> {
         DifficultyHandler.debugHandleSyncMessage(this);
      });
      return null;
   }
}
