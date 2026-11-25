package codersafterdark.compatskills.common.compats.thaumcraft.commands;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.mc1120.commands.CraftTweakerCommand;
import crafttweaker.mc1120.commands.SpecialMessagesChat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.research.ResearchEntry;

public class ThaumcraftFullDump extends CraftTweakerCommand {
   private LinkedHashMap<String, ResearchCategory> categoryMap = ResearchCategories.researchCategories;

   public ThaumcraftFullDump() {
      super("TCFullDump");
   }

   protected void init() {
      this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText(TextFormatting.DARK_GREEN + "/ct " + this.subCommandName, "/ct " + this.subCommandName, true), SpecialMessagesChat.getNormalMessage(TextFormatting.DARK_AQUA + "Outputs a list of all Research Categories, Entries, etc in the game to the crafttweaker.log")});
   }

   public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
      CraftTweakerAPI.logCommand("##### ");
      CraftTweakerAPI.logCommand("#### " + this.subCommandName);

      for(Entry<String, ResearchCategory> entries : this.categoryMap.entrySet()) {
         Map<String, ResearchEntry> entryMap = ((ResearchCategory)entries.getValue()).research;
         CraftTweakerAPI.logCommand("### " + (String)entries.getKey());
         CraftTweakerAPI.logCommand("## With Entries: ");

         for(String subKey : entryMap.keySet()) {
            ResearchEntry entry = (ResearchEntry)entryMap.get(subKey);
            CraftTweakerAPI.logCommand("#####");
            CraftTweakerAPI.logCommand("#### Key:              " + entry.getKey());
            CraftTweakerAPI.logCommand("###  Category:         " + entry.getCategory());
            CraftTweakerAPI.logCommand("##   Localized Name:   " + entry.getLocalizedName());
            CraftTweakerAPI.logCommand("##   Unlocalized Name: " + entry.getName());
         }

         CraftTweakerAPI.logCommand("");
         CraftTweakerAPI.logCommand("");
      }

      sender.sendMessage(SpecialMessagesChat.getLinkToCraftTweakerLog("Entries Generated in Crafttweaker.log", sender));
   }
}
