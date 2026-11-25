package codersafterdark.compatskills.common.compats.bloodmagic;

import WayofTime.bloodmagic.BloodMagic;
import WayofTime.bloodmagic.ritual.Ritual;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.mc1120.commands.CraftTweakerCommand;
import crafttweaker.mc1120.commands.SpecialMessagesChat;
import java.util.Collection;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public class RitualCommand extends CraftTweakerCommand {
   public RitualCommand() {
      super("ritualDump");
   }

   protected void init() {
      this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText(TextFormatting.DARK_GREEN + "/ct ritualDump", "/ct rituals", true), SpecialMessagesChat.getNormalMessage(TextFormatting.DARK_AQUA + "Outputs a list of all Rituals names in the game to the crafttweaker.log")});
   }

   public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
      CraftTweakerAPI.logCommand("Ritual Dump: ");
      Collection<Ritual> rituals = BloodMagic.RITUAL_MANAGER.getRituals();

      for(Ritual ritual : rituals) {
         CraftTweakerAPI.logCommand("##");
         CraftTweakerAPI.logCommand("Ritual ID: " + BloodMagic.RITUAL_MANAGER.getId(ritual));
         CraftTweakerAPI.logCommand("Ritual Cost: " + ritual.getActivationCost());
         CraftTweakerAPI.logCommand("Ritual Crystal Level: " + ritual.getCrystalLevel());
         CraftTweakerAPI.logCommand("##");
      }

      sender.sendMessage(SpecialMessagesChat.getNormalMessage("List of Rituals generated;"));
      sender.sendMessage(SpecialMessagesChat.getLinkToCraftTweakerLog("List Size: " + rituals.size() + " Entries;", sender));
   }
}
