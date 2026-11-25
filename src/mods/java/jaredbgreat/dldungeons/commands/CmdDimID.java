package jaredbgreat.dldungeons.commands;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CmdDimID extends CommandBase {
   private List aliases = new ArrayList();

   public CmdDimID() {
   }

   public String getName() {
      return "dlddimid";
   }

   public String getUsage(ICommandSender sender) {
      return "/dlddimid";
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      int dim = sender.getEntityWorld().provider.getDimension();
      System.out.println(dim);
      sender.sendMessage(new TextComponentString("[DLDUNGEONS] " + sender.getDisplayName().getFormattedText() + " is in dimension " + dim));
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public boolean isUsernameIndex(String[] astring, int i) {
      return false;
   }
}
