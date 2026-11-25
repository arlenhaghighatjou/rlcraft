package crafttweaker.mc1120.server;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.player.IUser;
import crafttweaker.api.server.IServer;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IWorld;
import crafttweaker.mc1120.world.MCBlockPos;
import net.minecraft.util.math.BlockPos;

public class ServerPlayer implements IUser {
   public static final ServerPlayer INSTANCE = new ServerPlayer();

   private ServerPlayer() {
   }

   public String getDisplayName() {
      return "SERVER";
   }

   public IBlockPos getPosition() {
      return new MCBlockPos(BlockPos.ORIGIN);
   }

   public IWorld getWorld() {
      return null;
   }

   public IServer getServer() {
      return CraftTweakerAPI.server;
   }

   public void sendMessage(String text) {
   }

   public Object getInternal() {
      return null;
   }
}
