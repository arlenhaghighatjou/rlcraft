package com.creativemd.creativecore.server.command;

import com.creativemd.creativecore.common.gui.opener.GuiHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;

public class ConfigCommand extends CommandBase {
   public ConfigCommand() {
   }

   public String getName() {
      return "cmdconfig";
   }

   public String getUsage(ICommandSender sender) {
      return "opens config gui";
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
         GuiHandler.openGui("config", new NBTTagCompound(), (EntityPlayer)sender.getCommandSenderEntity());
      }

   }
}
