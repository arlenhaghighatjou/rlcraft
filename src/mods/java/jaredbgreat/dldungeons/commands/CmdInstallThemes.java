package jaredbgreat.dldungeons.commands;

import jaredbgreat.dldungeons.ConfigHandler;
import jaredbgreat.dldungeons.setup.Externalizer;
import jaredbgreat.dldungeons.themes.BiomeSets;
import jaredbgreat.dldungeons.themes.ThemeReader;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CmdInstallThemes extends CommandBase {
   public CmdInstallThemes() {
   }

   public String getName() {
      return "dldInstallThemes";
   }

   public String getUsage(ICommandSender sender) {
      return "/dldInstallThemes";
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      if (ConfigHandler.installCmd) {
         Externalizer exporter = new Externalizer(ThemeReader.getThemesDir());
         exporter.makeThemes();
         exporter = new Externalizer(ConfigHandler.getConfigDir());
         exporter.makeChestCfg();
         sender.sendMessage(new TextComponentString("[DLDUNGEONS] " + sender.getDisplayName().getFormattedText() + " has reinstalled default themes (nothing will be overwritten)"));
         BiomeSets.reset();
      }
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public boolean isUsernameIndex(String[] astring, int i) {
      return false;
   }
}
