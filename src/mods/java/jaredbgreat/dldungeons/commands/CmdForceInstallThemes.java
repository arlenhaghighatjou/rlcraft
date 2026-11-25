package jaredbgreat.dldungeons.commands;

import jaredbgreat.dldungeons.ConfigHandler;
import jaredbgreat.dldungeons.setup.Externalizer;
import jaredbgreat.dldungeons.themes.BiomeSets;
import jaredbgreat.dldungeons.themes.ThemeReader;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CmdForceInstallThemes extends CommandBase {
   private List aliases = new ArrayList();

   public CmdForceInstallThemes() {
   }

   public String getName() {
      return "dldForceInstallThemes";
   }

   public String getUsage(ICommandSender sender) {
      return "/dldForceInstallThemes";
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      if (ConfigHandler.installCmd) {
         Externalizer exporter = new Externalizer(ThemeReader.getThemesDir());
         exporter.forceThemes();
         exporter = new Externalizer(ConfigHandler.getConfigDir());
         exporter.forceChestCfg();
         sender.sendMessage(new TextComponentString("[DLDUNGEONS] " + sender.getDisplayName().getFormattedText() + " has forced reinstalled default themes (existing themes will be overwritten!)"));
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
