package com.blamejared.compat.tconstruct;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.mc1120.commands.CTChatCommand;
import crafttweaker.mc1120.commands.CraftTweakerCommand;
import crafttweaker.mc1120.commands.SpecialMessagesChat;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.IMaterialStats;
import slimeknights.tconstruct.library.materials.Material;

public class TConstruct {
   public TConstruct() {
   }

   public static void registerCommands() {
      CTChatCommand.registerCommand(new CraftTweakerCommand("ticmat") {
         protected void init() {
            this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText("\u00a72/ct ticmat", "/ct ticmat", true), SpecialMessagesChat.getNormalMessage(" \u00a73Dumps all the Tinkers Construct material names to the crafttweaker log")});
         }

         public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
            CraftTweakerAPI.logCommand("Tinkers Construct Materials:");

            for(Material material : TinkerRegistry.getAllMaterials()) {
               StringBuilder builder = (new StringBuilder("-")).append(material.identifier).append("\n");

               for(IMaterialStats stats : material.getAllStats()) {
                  for(String s : stats.getLocalizedInfo()) {
                     builder.append(s.replaceAll(TextFormatting.RESET.toString(), "").replaceAll("[^\\x00-\\x7F]", "")).append("\n");
                  }
               }

               CraftTweakerAPI.logCommand(builder.toString());
            }

            sender.sendMessage(SpecialMessagesChat.getLinkToCraftTweakerLog("Tinkers Construct Materials list generated", sender));
         }
      });
   }
}
