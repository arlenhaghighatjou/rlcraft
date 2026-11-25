package vazkii.quark.base.network.message;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.network.NetworkHandler;
import vazkii.arl.network.NetworkMessage;
import vazkii.arl.util.ClientTicker;

public class MessageSpamlessChat extends NetworkMessage<MessageSpamlessChat> {
   public ITextComponent message;
   public int id;

   public MessageSpamlessChat() {
   }

   public MessageSpamlessChat(ITextComponent message, int id) {
      this.message = message;
      this.id = id;
   }

   @SideOnly(Side.CLIENT)
   public IMessage handleMessage(MessageContext context) {
      ClientTicker.addAction(() -> {
         Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(this.message, this.id);
      });
      return null;
   }

   public static void sendToPlayer(EntityPlayer player, int id, ITextComponent component) {
      if (player instanceof EntityPlayerMP) {
         NetworkHandler.INSTANCE.sendTo(new MessageSpamlessChat(component, id), (EntityPlayerMP)player);
      }

   }
}
