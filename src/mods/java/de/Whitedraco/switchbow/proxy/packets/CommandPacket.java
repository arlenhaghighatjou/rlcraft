package de.Whitedraco.switchbow.proxy.packets;

import de.Whitedraco.switchbow.Config.SwitchBowConfig;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.io.IOException;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CommandPacket implements IMessage {
   int text;
   boolean value;

   public CommandPacket() {
   }

   public CommandPacket(int text, boolean value) {
      this.text = text;
      this.value = value;
   }

   public void fromBytes(ByteBuf buf) {
      this.text = buf.readInt();
      this.value = buf.readBoolean();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.text);
      buf.writeBoolean(this.value);
   }

   public void execute(MessageContext ctx) {
      if (this.text == 0) {
         SwitchBowConfig.spawnFirewithLightning.setBool(this.value);

         try {
            SwitchBowConfig.writeConfigSwitchBow();
         } catch (IOException var4) {
            var4.printStackTrace();
         }
      } else if (this.text == 1) {
         SwitchBowConfig.destroyBlockswithTNTArrow.setBool(this.value);

         try {
            SwitchBowConfig.writeConfigSwitchBow();
         } catch (IOException var3) {
            var3.printStackTrace();
         }
      }

   }

   public static class Handler implements IMessageHandler<CommandPacket, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(CommandPacket message, MessageContext ctx) {
         message.execute(ctx);
         return null;
      }
   }
}
