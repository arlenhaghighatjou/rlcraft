package crafttweaker.api.command;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.server.IServer;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IWorld;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("crafttweaker.command.ICommandSender")
@ZenRegister
public interface ICommandSender {
   @ZenGetter("displayName")
   String getDisplayName();

   @ZenGetter("position")
   IBlockPos getPosition();

   @ZenGetter("world")
   IWorld getWorld();

   @ZenGetter("server")
   IServer getServer();

   @ZenMethod
   void sendMessage(String var1);

   Object getInternal();
}
