package com.creativemd.creativecore.server.command;

import com.creativemd.creativecore.common.gui.opener.GuiHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;

public class GuiCommand extends CommandBase {
   public GuiCommand() {
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      GuiHandler.openGui("test-gui", new NBTTagCompound());
   }

   public String getName() {
      return "creative-gui";
   }

   public String getUsage(ICommandSender sender) {
      return "opens a test gui";
   }
}
