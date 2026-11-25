package crafttweaker.mc1120.commands;

import crafttweaker.mc1120.CraftTweaker;
import crafttweaker.mc1120.network.MessageCopyClipboard;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;

public class ClipboardHelper {
   static final String copyCommandBase = "/ct copy ";

   public ClipboardHelper() {
   }

   public static void sendMessageWithCopy(EntityPlayer player, String holeMessage, String copyMessage) {
      player.sendMessage(SpecialMessagesChat.getCopyMessage(holeMessage, copyMessage));
   }

   static void copyCommandRun(ICommandSender sender, String[] args) {
      StringBuilder message = new StringBuilder();

      for(int i = 0; i < args.length; ++i) {
         message.append(args[i]);
         if (i != args.length - 1) {
            message.append(" ");
         }
      }

      if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
         copyStringPlayer((EntityPlayer)sender.getCommandSenderEntity(), message.toString());
         sender.sendMessage(new TextComponentString("Copied [\u00a76" + message.toString() + "\u00a7r] to the clipboard"));
      } else {
         sender.sendMessage(new TextComponentString("This command can only be executed as a Player (InGame)"));
      }

   }

   static void copyStringPlayer(EntityPlayer player, String s) {
      if (player instanceof EntityPlayerMP) {
         CraftTweaker.NETWORK.sendTo(new MessageCopyClipboard(s), (EntityPlayerMP)player);
      }

   }
}
