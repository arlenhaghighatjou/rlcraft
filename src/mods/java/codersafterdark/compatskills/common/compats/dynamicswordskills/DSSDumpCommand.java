package codersafterdark.compatskills.common.compats.dynamicswordskills;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.mc1120.commands.CraftTweakerCommand;
import crafttweaker.mc1120.commands.SpecialMessagesChat;
import dynamicswordskills.skills.SkillBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public class DSSDumpCommand extends CraftTweakerCommand {
   public DSSDumpCommand() {
      super("dynamicswordskills");
   }

   protected void init() {
      this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText(TextFormatting.DARK_GREEN + "/ct dynamicswordskills", "/ct dynamicswordskills", true), SpecialMessagesChat.getNormalMessage(TextFormatting.DARK_AQUA + "Outputs a list of names for all the dynamic sword skills to the crafttweaker.log")});
   }

   public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
      CraftTweakerAPI.logCommand("Dynamic Sword Skills' Skill Dump:");
      String[] skillNames = SkillBase.getSkillNames();

      for(String skill : skillNames) {
         CraftTweakerAPI.logCommand(skill);
      }

      sender.sendMessage(SpecialMessagesChat.getNormalMessage("List of Dynamic Sword Skills' Skill Generated;"));
      sender.sendMessage(SpecialMessagesChat.getLinkToCraftTweakerLog("List Size: " + skillNames.length + " Entries;", sender));
   }
}
