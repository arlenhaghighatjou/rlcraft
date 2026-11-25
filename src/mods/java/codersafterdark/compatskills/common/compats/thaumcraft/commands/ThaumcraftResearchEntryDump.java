package codersafterdark.compatskills.common.compats.thaumcraft.commands;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.mc1120.commands.CraftTweakerCommand;
import crafttweaker.mc1120.commands.SpecialMessagesChat;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.research.ResearchEntry;

public class ThaumcraftResearchEntryDump extends CraftTweakerCommand {
   private LinkedHashMap<String, ResearchCategory> categoryMap = ResearchCategories.researchCategories;

   public ThaumcraftResearchEntryDump() {
      super("TCResearchEntryDump");
   }

   protected void init() {
      this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText(TextFormatting.DARK_GREEN + "/ct " + this.subCommandName, "/ct " + this.subCommandName, true), SpecialMessagesChat.getNormalMessage(TextFormatting.DARK_AQUA + "Outputs a list of all Research Entries keys in the game to the crafttweaker.log")});
   }

   public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
      CraftTweakerAPI.logCommand("####");
      CraftTweakerAPI.logCommand("### " + this.subCommandName);

      for(String key : this.categoryMap.keySet()) {
         Map<String, ResearchEntry> entryMap = ((ResearchCategory)this.categoryMap.get(key)).research;

         for(String entryKey : entryMap.keySet()) {
            CraftTweakerAPI.logCommand("## Name: " + ((ResearchEntry)entryMap.get(entryKey)).getLocalizedName());
            CraftTweakerAPI.logCommand("## Key:  " + ((ResearchEntry)entryMap.get(entryKey)).getKey());
            CraftTweakerAPI.logCommand("####");
         }
      }

      CraftTweakerAPI.logCommand("####");
      sender.sendMessage(SpecialMessagesChat.getLinkToCraftTweakerLog("Entries Generated in Crafttweaker.log", sender));
   }
}
