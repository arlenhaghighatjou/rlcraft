package com.elenai.elenaidodge.command.main;

import com.elenai.elenaidodge.capability.airborne.AirborneProvider;
import com.elenai.elenaidodge.capability.airborne.IAirborne;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ToggleAirborne implements ICommand {
   private final List<String> aliases = new ArrayList();

   public ToggleAirborne() {
      this.aliases.add("toggle_airborne");
   }

   public int compareTo(ICommand o) {
      return 0;
   }

   public String getName() {
      return "toggle_airborne";
   }

   public String getUsage(ICommandSender sender) {
      return "/elenaidodge toggle_airborne <player>";
   }

   public List<String> getAliases() {
      return this.aliases;
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      if (args.length == 0) {
         sender.sendMessage(new TextComponentString(TextFormatting.RED + "/elenaidodge toggle_airborne <player>"));
      } else {
         try {
            EntityPlayer target = CommandBase.getPlayer(server, sender, args[0]);
            World world = sender.getEntityWorld();
            if (!world.isRemote) {
               IAirborne a = (IAirborne)target.getCapability(AirborneProvider.AIRBORNE_CAP, (EnumFacing)null);
               a.set(!a.isEnabled());
               sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Toggled dodging whilst airborne for " + target.getName() + "!"));
            }
         } catch (NumberFormatException var7) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "No player found!"));
         }
      }

   }

   public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
      return true;
   }

   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
      return (List<String>)(args.length == 1 ? CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()) : new ArrayList());
   }

   public boolean isUsernameIndex(String[] args, int index) {
      return false;
   }
}
