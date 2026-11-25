package reborncore.common.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.text.ITextComponent;

public class ChatUtils {
   private static final int DELETION_ID = 1337;

   public ChatUtils() {
   }

   public static void sendNoSpamMessages(int messageID, ITextComponent message) {
      int deleteID = 1337 + messageID;
      GuiNewChat chat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
      chat.printChatMessageWithOptionalDeletion(message, deleteID);
   }
}
