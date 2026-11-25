package vazkii.quark.base.network.message;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import vazkii.arl.network.NetworkMessage;
import vazkii.arl.util.ClientTicker;
import vazkii.quark.base.Quark;

public class MessageDoEmote extends NetworkMessage<MessageDoEmote> {
   public String emoteName;
   public String playerName;
   public int playerTier;

   public MessageDoEmote() {
   }

   public MessageDoEmote(String emoteName, String playerName, int tier) {
      this.emoteName = emoteName;
      this.playerName = playerName;
      this.playerTier = tier;
   }

   public IMessage handleMessage(MessageContext context) {
      ClientTicker.addAction(() -> {
         Quark.proxy.doEmote(this.playerName, this.emoteName, this.playerTier);
      });
      return null;
   }
}
