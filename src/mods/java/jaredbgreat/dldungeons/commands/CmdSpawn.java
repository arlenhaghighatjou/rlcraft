package jaredbgreat.dldungeons.commands;

import jaredbgreat.dldungeons.ConfigHandler;
import jaredbgreat.dldungeons.builder.Builder;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class CmdSpawn extends CommandBase {
   public CmdSpawn() {
   }

   public String getName() {
      return "dldspawn";
   }

   public String getUsage(ICommandSender sender) {
      return "/dldspawn";
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      World world = sender.getEntityWorld();
      BlockPos location = sender.getPosition();

      try {
         Builder.placeDungeon(new EaglercraftRandom(), location.getX() / 16, location.getZ() / 16, world);
      } catch (Throwable var7) {
         System.err.println("[DLDUNGEONS] Danger!  Failed to finalize a dungeon after building!");
         var7.printStackTrace();
      }

      if (ConfigHandler.announceCommands) {
         sender.sendMessage(new TextComponentString("[DLDUNGEONS] " + sender.getDisplayName().getFormattedText() + " just spawned a dungeon at X=" + location.getX() + ", Z=" + location.getZ()));
      }

   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public boolean isUsernameIndex(String[] astring, int i) {
      return false;
   }
}
