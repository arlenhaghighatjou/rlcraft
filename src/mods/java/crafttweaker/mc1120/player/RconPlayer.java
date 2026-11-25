package crafttweaker.mc1120.player;

import crafttweaker.api.player.IUser;
import crafttweaker.mc1120.command.MCCommandSender;
import net.minecraft.command.ICommandSender;

public class RconPlayer extends MCCommandSender implements IUser {
   public RconPlayer(ICommandSender sender) {
      super(sender);
   }
}
