package c4.corpsecomplex.network;

import io.netty.buffer.ByteBuf;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TeleportSoundMessage implements IMessage {
   private static Random rand = new Random();

   public TeleportSoundMessage() {
   }

   public void toBytes(ByteBuf buf) {
   }

   public void fromBytes(ByteBuf buf) {
   }

   public static class TeleportSoundMessageHandler implements IMessageHandler<TeleportSoundMessage, IMessage> {
      public TeleportSoundMessageHandler() {
      }

      @SideOnly(Side.CLIENT)
      public IMessage onMessage(TeleportSoundMessage message, MessageContext ctx) {
         IThreadListener mainThread = Minecraft.getMinecraft();
         mainThread.addScheduledTask(() -> {
            Minecraft mc = Minecraft.getMinecraft();
            mc.ingameGUI.getBossOverlay().clearBossInfos();
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_PORTAL_TRAVEL, TeleportSoundMessage.rand.nextFloat() * 0.4F + 0.8F));
         });
         return null;
      }
   }
}
