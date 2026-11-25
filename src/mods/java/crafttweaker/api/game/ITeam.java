package crafttweaker.api.game;

import crafttweaker.annotations.ZenRegister;
import java.util.List;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("crafttweaker.game.ITeam")
@ZenRegister
public interface ITeam {
   @ZenGetter("name")
   String getName();

   @ZenMethod
   String formatString(String var1);

   @ZenGetter("allowFriendlyFire")
   boolean getAllowFriendlyFire();

   @ZenGetter("colorPrefix")
   String getColorPrefix();

   @ZenGetter("membershipCollection")
   List<String> getMembershipCollection();

   @ZenGetter("deathMessageVisibility")
   String getDeathMessageVisibility();

   @ZenGetter("collisionRule")
   String getCollisionRule();

   Object getInternal();
}
