package crafttweaker.mc1120.player;

import crafttweaker.api.player.IUser;
import crafttweaker.api.server.IServer;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IWorld;
import crafttweaker.mc1120.server.MCServer;
import crafttweaker.mc1120.world.MCBlockPos;
import crafttweaker.mc1120.world.MCWorld;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;

public class CommandBlockPlayer implements IUser {
   private final ICommandSender sender;

   public CommandBlockPlayer(ICommandSender sender) {
      this.sender = sender;
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

   public Object getInternal() {
      return this.sender;
   }
}
