package c4.corpsecomplex.network;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import toughasnails.api.temperature.Temperature;
import toughasnails.api.temperature.TemperatureHelper;
import toughasnails.api.thirst.ThirstHelper;

public class TANMessage implements IMessage {
   private int thirst;
   private int temp;
   private static final String MOD_ID = "toughasnails";

   public TANMessage() {
   }

   public TANMessage(int thirst, int temp) {
      this.thirst = thirst;
      this.temp = temp;
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.thirst);
      buf.writeInt(this.temp);
   }

   public void fromBytes(ByteBuf buf) {
      this.thirst = buf.readInt();
      this.temp = buf.readInt();
   }

   public static class TANMessageHandler implements IMessageHandler<TANMessage, IMessage> {
      public TANMessageHandler() {
      }

      @SideOnly(Side.CLIENT)
      @Method(
         modid = "toughasnails"
      )
      public IMessage onMessage(TANMessage message, MessageContext ctx) {
         IThreadListener mainThread = Minecraft.getMinecraft();
         mainThread.addScheduledTask(() -> {
            EntityPlayerSP playerSP = Minecraft.getMinecraft().player;
            ThirstHelper.getThirstData(playerSP).setThirst(message.thirst);
            TemperatureHelper.getTemperatureData(playerSP).setTemperature(new Temperature(message.temp));
         });
         return null;
      }
   }
}
