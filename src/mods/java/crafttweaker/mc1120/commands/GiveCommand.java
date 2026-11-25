package crafttweaker.mc1120.commands;

import crafttweaker.api.item.IItemStack;
import crafttweaker.mc1120.brackets.BracketHandlerItem;
import crafttweaker.mc1120.data.StringIDataParser;
import crafttweaker.mc1120.player.MCPlayer;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;

public class GiveCommand extends CraftTweakerCommand {
   GiveCommand() {
      super("give");
   }

   protected void init() {
      this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText("\u00a72/ct give", "/ct give", true), SpecialMessagesChat.getNormalMessage(" \u00a73Gives an item using the BH")});
   }

   public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
      String s = String.join(" ", args);
      if (!s.matches("<\\w+:\\w+(:\\d+)?>(.withTag\\(\\{.*}\\))?")) {
         sender.sendMessage(SpecialMessagesChat.getNormalMessage("Invalid string " + s));
      } else if (!(sender instanceof EntityPlayerMP)) {
         sender.sendMessage(SpecialMessagesChat.getNormalMessage("Only players can execute this command!"));
      } else {
         String[] exParts = s.split(">", 2)[0].substring(1).split(":");
         String call = exParts[0] + ":" + exParts[1];
         int meta = exParts.length > 2 ? Integer.parseInt(exParts[2]) : 0;
         IItemStack item = BracketHandlerItem.getItem(call, meta);
         if (item == null) {
            sender.sendMessage(SpecialMessagesChat.getNormalMessage("Could not find item: " + call));
         } else {
            if (s.contains("withTag")) {
               String expression = s.split("withTag\\(")[1];
               item = item.withTag(StringIDataParser.parse(expression.substring(0, expression.length() - 1)), false);
            }

            (new MCPlayer((EntityPlayer)sender)).give(item);
         }
      }
   }
}
