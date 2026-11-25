package codersafterdark.compatskills.common.compats.minecraft.tileentity;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.mc1120.commands.CraftTweakerCommand;
import crafttweaker.mc1120.commands.SpecialMessagesChat;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public class TileEntityCommand extends CraftTweakerCommand {
   public TileEntityCommand() {
      super("tileentityDump");
   }

   protected void init() {
      this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText(TextFormatting.DARK_GREEN + "/ct tileentityDump", "/ct tileentities", true), SpecialMessagesChat.getNormalMessage(TextFormatting.DARK_AQUA + "Outputs a list of all Tile Entities in the game to the crafttweaker.log")});
   }

   public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
      Set<ResourceLocation> keys = TileEntity.REGISTRY.getKeys();
      CraftTweakerAPI.logCommand("Tile Entity Name Dump: ");
      keys.stream().map(ResourceLocation::toString).forEach(CraftTweakerAPI::logCommand);
      sender.sendMessage(SpecialMessagesChat.getNormalMessage("List of Tile Entities generated;"));
      sender.sendMessage(SpecialMessagesChat.getLinkToCraftTweakerLog("List Size: " + keys.size() + " Entries;", sender));
   }
}
