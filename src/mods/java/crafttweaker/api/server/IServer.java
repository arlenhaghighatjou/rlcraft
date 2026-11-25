package crafttweaker.api.server;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.command.ICommandManager;
import crafttweaker.api.command.ICommandSender;
import crafttweaker.api.event.IEventHandle;
import crafttweaker.api.event.PlayerLoggedInEvent;
import crafttweaker.api.event.PlayerLoggedOutEvent;
import crafttweaker.api.player.IPlayer;
import crafttweaker.util.IEventHandler;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("crafttweaker.server.IServer")
@ZenRegister
public interface IServer extends ICommandSender {
   @ZenMethod
   boolean isOp(IPlayer var1);

   @ZenMethod
   IEventHandle onPlayerLoggedIn(IEventHandler<PlayerLoggedInEvent> var1);

   @ZenMethod
   IEventHandle onPlayerLoggedOut(IEventHandler<PlayerLoggedOutEvent> var1);

   @ZenGetter("commandManager")
   ICommandManager getCommandManager();

   Object getInternal();
}
