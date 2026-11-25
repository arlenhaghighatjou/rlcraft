package codersafterdark.compatskills.utils.multiblock;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.mc1120.commands.CraftTweakerCommand;
import crafttweaker.mc1120.commands.SpecialMessagesChat;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public abstract class MultiBlockCommand extends CraftTweakerCommand {
   public MultiBlockCommand(String setName) {
      super(setName + "MultiBlocks");
   }

   protected void init() {
      this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText(TextFormatting.DARK_GREEN + "/ct " + this.subCommandName, "/ct " + this.subCommandName, true), SpecialMessagesChat.getNormalMessage(TextFormatting.DARK_AQUA + "Outputs a list of all " + this.subCommandName + "names in the game to the crafttweaker.log")});
   }

   public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
      CraftTweakerAPI.logCommand("#### ");
      CraftTweakerAPI.logCommand("### " + this.subCommandName + ':');
      List<String> multiBlockList = this.getMultiBlockNames();
      CraftTweakerAPI.logCommand("## Multiblocks");
      multiBlockList.stream().map((name) -> {
         return "# " + name;
      }).forEach(CraftTweakerAPI::logCommand);
      CraftTweakerAPI.logCommand("####");
      sender.sendMessage(SpecialMessagesChat.getNormalMessage("List of " + this.subCommandName + " generated;"));
      sender.sendMessage(SpecialMessagesChat.getLinkToCraftTweakerLog("List Size: " + multiBlockList.size() + " Entries;", sender));
   }

   public abstract List<String> getMultiBlockNames();
}
