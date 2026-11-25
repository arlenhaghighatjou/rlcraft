package com.blamejared.compat.inspirations;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.mc1120.commands.CTChatCommand;
import crafttweaker.mc1120.commands.CraftTweakerCommand;
import crafttweaker.mc1120.commands.SpecialMessagesChat;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.potion.PotionType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class Inspirations {
   public Inspirations() {
   }

   public static void registerCommands() {
      CTChatCommand.registerCommand(new CraftTweakerCommand("inspirations") {
         protected void init() {
            this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText("§2/ct inspirations {dyes|potions}", "/ct inspirations {dyes|potions}", false), SpecialMessagesChat.getNormalMessage(" §3Dumps all the dye names or potion type resource locations to the log")});
         }

         public void executeCommand(MinecraftServer server, ICommandSender sender, String[] arguments) {
            List<String> args = Arrays.asList(arguments);
            if (args.contains("dyes")) {
               CraftTweakerAPI.logCommand("Inspirations Dye Colors:");

               for(EnumDyeColor dye : EnumDyeColor.values()) {
                  CraftTweakerAPI.logCommand(dye.getName());
               }
            } else if (args.contains("potions")) {
               CraftTweakerAPI.logCommand("Inspirations Potion Types:");

               for(ResourceLocation potion : new TreeSet(PotionType.REGISTRY.getKeys())) {
                  CraftTweakerAPI.logCommand(potion.toString());
               }
            } else if (sender != null) {
               sender.sendMessage(new TextComponentString("Invalid arguments for command. Valid arguments: dyes, potions"));
            }

            if (sender != null) {
               sender.sendMessage(SpecialMessagesChat.getLinkToCraftTweakerLog("List generated;", sender));
            }

         }
      });
   }
}
