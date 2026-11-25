package codersafterdark.compatskills.common.compats.thaumcraft.commands;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.mc1120.commands.CraftTweakerCommand;
import crafttweaker.mc1120.commands.SpecialMessagesChat;
import java.util.LinkedHashMap;
import java.util.function.BiConsumer;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;

public class ThaumcraftResearchCategoryDump extends CraftTweakerCommand {
   private LinkedHashMap<String, ResearchCategory> categoryMap = ResearchCategories.researchCategories;

   public ThaumcraftResearchCategoryDump() {
      super("TCResearchDump");
   }

   protected void init() {
      this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText(TextFormatting.DARK_GREEN + "/ct " + this.subCommandName, "/ct " + this.subCommandName, true), SpecialMessagesChat.getNormalMessage(TextFormatting.DARK_AQUA + "Outputs a list of all Research Category names in the game to the crafttweaker.log")});
   }

   public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
      CraftTweakerAPI.logCommand("#### ");
      CraftTweakerAPI.logCommand("### " + this.subCommandName);
      this.categoryMap.forEach((key, value) -> {
         CraftTweakerAPI.logCommand("## " + key + " : " + value.researchKey);
      });
      CraftTweakerAPI.logCommand("####");
      sender.sendMessage(SpecialMessagesChat.getLinkToCraftTweakerLog("Entries Generated in Crafttweaker.log", sender));
   }
}
