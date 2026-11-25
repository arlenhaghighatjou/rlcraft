package com.elenai.elenaidodge.command.main;

import com.elenai.elenaidodge.capability.enabled.EnabledProvider;
import com.elenai.elenaidodge.capability.enabled.IEnabled;
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

public class EnableDodge implements ICommand {
   private final List<String> aliases = new ArrayList();

   public EnableDodge() {
      this.aliases.add("enable");
   }

   public int compareTo(ICommand o) {
      return 0;
   }

   public String getName() {
      return "enable";
   }

   public String getUsage(ICommandSender sender) {
      return "/elenaidodge enable <player>";
   }

   public List<String> getAliases() {
      return this.aliases;
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      if (args.length == 0) {
         sender.sendMessage(new TextComponentString(TextFormatting.RED + "/elenaidodge enable <player>"));
      } else {
         try {
            EntityPlayer target = CommandBase.getPlayer(server, sender, args[0]);
            World world = sender.getEntityWorld();
            if (!world.isRemote) {
               IEnabled e = (IEnabled)target.getCapability(EnabledProvider.ENABLED_CAP, (EnumFacing)null);
               e.enable();
               sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Enabled Elenai Dodge for " + target.getName() + "!"));
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
