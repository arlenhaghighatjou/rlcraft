package crafttweaker.mc1120.chat;

import crafttweaker.api.chat.IChatMessage;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class MCChatMessage implements IChatMessage {
   private final ITextComponent data;

   public MCChatMessage(String message) {
      this.data = new TextComponentString(message);
   }

   public MCChatMessage(ITextComponent data) {
      this.data = data;
   }

   public IChatMessage add(IChatMessage other) {
      return new MCChatMessage(this.data.appendSibling((ITextComponent)other.getInternal()));
   }

   public Object getInternal() {
      return this.data;
   }
}
