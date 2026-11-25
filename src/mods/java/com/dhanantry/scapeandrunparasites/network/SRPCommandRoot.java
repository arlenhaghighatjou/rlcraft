package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.init.SRPBiomes;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPSpawning;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class SRPCommandRoot implements ICommand {
   private final List aliases = new ArrayList();

   public SRPCommandRoot() {
      this.aliases.add("srparasites");
   }

   public int compareTo(ICommand arg0) {
      return 0;
   }

   public String getName() {
      return "srparasites";
   }

   public String getUsage(ICommandSender sender) {
      return "srparasites <text>";
   }

   public List<String> getAliases() {
      return this.aliases;
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] argString) throws CommandException {
      World world = sender.getEntityWorld();
      if (!world.isRemote) {
         if (argString[0].equals("readconfigurationfile")) {
            try {
               if (SRPConfig.readConfig() && SRPConfigMobs.readConfig() && SRPConfigSystems.readConfig() && SRPConfigWorld.readConfig()) {
                  SRPAttributes.reset();
                  SRPAttributes.init();
                  SRPBlocks.init();
                  SRPBiomes.clearMobSpawnList();
                  SRPSpawning.initBiome();
                  SRPSpawning.removeInit();
                  SRPSpawning.init();
                  sender.sendMessage(new TextComponentString("Configutarion files were read successfully \n NOTE: Does not work for all options, such as registry or client-side options"));
               }
            } catch (Exception var8) {
               SRPMain.logger.log(Level.ERROR, "Problem while reading configuration file", var8);
               sender.sendMessage(new TextComponentString("There was a problem while reading configuration file, check inputs \n NOTE: Does not work for all options, such as registry or client-side options"));
            }

            return;
         }

         if (argString[0].equals("toggle_dotiledrops")) {
            boolean current = !SRPConfig.doTileDrops;
            SRPConfig.doTileDrops = current;
            sender.sendMessage(new TextComponentString("Current doTileDrop value is " + current));
            return;
         }

         if (argString[0].equals("toggle_domobevolution")) {
            boolean current = !ParasiteEventEntity.canSpawnNext;
            ParasiteEventEntity.canSpawnNext = current;
            sender.sendMessage(new TextComponentString("Current doMobEvolution value is " + current));
            return;
         }

         if (argString[0].equals("getbeckonlimit")) {
            SRPWorldData data = SRPWorldData.get(world);
            sender.sendMessage(new TextComponentString("Current number of Beckons in this dimension spawned by RS: " + data.getCurrentV()));
            return;
         }

         if (argString[0].equals("resetdatafile")) {
            SRPWorldData data = SRPWorldData.get(world);
            data.resetInstance(world);
            sender.sendMessage(new TextComponentString("Data file of this dimension has been reset"));
            return;
         }

         if (argString[0].equals("setbeckonlimit")) {
            try {
               String nani = argString[1];
               int option = Integer.parseInt(nani);
            } catch (NumberFormatException var9) {
               sender.sendMessage(new TextComponentString("Invalid arg"));
               return;
            }

            String nani = argString[1];
            int option = Integer.parseInt(nani);
            SRPWorldData data = SRPWorldData.get(world);
            sender.sendMessage(new TextComponentString("Current number of Beckons in this dimension spawned by RS was set to " + option));
            data.setCurrentV(option);
            return;
         }
      }

   }

   public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
      return sender.canUseCommand(2, this.getName());
   }

   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
      List<String> atm = new ArrayList();
      if (args.length == 1) {
         atm.add("readconfigurationfile");
         atm.add("getbeckonlimit");
         atm.add("setbeckonlimit");
         atm.add("resetdatafile");
         atm.add("toggle_dotiledrops");
         atm.add("toggle_domobevolution");
      }

      return atm;
   }

   public boolean isUsernameIndex(String[] args, int index) {
      return false;
   }
}
