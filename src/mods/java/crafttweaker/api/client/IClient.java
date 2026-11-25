package crafttweaker.api.client;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.player.IPlayer;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;

@ZenClass("crafttweaker.api.IClient")
@ZenRegister
public interface IClient {
   @ZenGetter("player")
   IPlayer getPlayer();

   @ZenGetter("language")
   String getLanguage();

   boolean isKeyDown(String var1);

   boolean isKeyDown(int var1);
}
