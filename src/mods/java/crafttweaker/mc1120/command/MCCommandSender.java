package crafttweaker.mc1120.command;

import crafttweaker.api.command.ICommandSender;
import crafttweaker.api.server.IServer;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IWorld;
import crafttweaker.mc1120.server.MCServer;
import crafttweaker.mc1120.world.MCBlockPos;
import crafttweaker.mc1120.world.MCWorld;
import net.minecraft.util.text.TextComponentString;

public class MCCommandSender implements ICommandSender {
   private final net.minecraft.command.ICommandSender sender;

   public MCCommandSender(net.minecraft.command.ICommandSender commandSender) {
      this.sender = commandSender;
   }

   public String getDisplayName() {
      return this.sender.getDisplayName().getFormattedText();
   }

   public IBlockPos getPosition() {
      return new MCBlockPos(this.sender.getPosition());
   }

   public IWorld getWorld() {
      return new MCWorld(this.sender.getEntityWorld());
   }

   public IServer getServer() {
      return new MCServer(this.sender.getServer());
   }

   public void sendMessage(String text) {
      this.sender.sendMessage(new TextComponentString(text));
   }

   public net.minecraft.command.ICommandSender getInternal() {
      return this.sender;
   }
}
