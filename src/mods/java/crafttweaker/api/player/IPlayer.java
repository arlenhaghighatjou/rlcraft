package crafttweaker.api.player;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.chat.IChatMessage;
import crafttweaker.api.data.IData;
import crafttweaker.api.entity.IEntityItem;
import crafttweaker.api.entity.IEntityLivingBase;
import crafttweaker.api.formatting.IFormattedText;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.util.Position3f;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenClass("crafttweaker.player.IPlayer")
@ZenRegister
public interface IPlayer extends IEntityLivingBase, IUser {
   @ZenGetter("name")
   String getName();

   @ZenGetter("uuid")
   default String getUUID() {
      CraftTweakerAPI.logError("Default method IPlayer#getUUID is not overwritten in " + this.getClass() + " please report to the author!");
      return "";
   }

   @ZenGetter("data")
   IData getData();

   @ZenGetter("xp")
   int getXP();

   @ZenSetter("xp")
   void setXP(int var1);

   @ZenMethod
   void removeXP(int var1);

   @ZenMethod
   void update(IData var1);

   @ZenMethod
   void sendChat(IChatMessage var1);

   @ZenMethod
   void sendChat(String var1);

   @ZenMethod
   void sendStatusMessage(String var1, @Optional(valueBoolean = true) boolean var2);

   @ZenMethod
   void sendStatusMessage(IFormattedText var1, @Optional(valueBoolean = true) boolean var2);

   @ZenGetter("hotbarSize")
   int getHotbarSize();

   @ZenMethod
   IItemStack getHotbarStack(int var1);

   @ZenGetter("inventorySize")
   int getInventorySize();

   @ZenMethod
   IItemStack getInventoryStack(int var1);

   @ZenGetter("currentItem")
   IItemStack getCurrentItem();

   @ZenGetter("creative")
   boolean isCreative();

   @ZenGetter("adventure")
   boolean isAdventure();

   @ZenMethod
   void give(IItemStack var1);

   void openBrowser(String var1);

   void copyToClipboard(String var1);

   @ZenMethod
   void teleport(Position3f var1);

   @ZenMethod
   void changeDimension(int var1);

   @ZenMethod
   @ZenGetter("score")
   int getScore();

   @ZenMethod
   void addScore(int var1);

   @ZenMethod
   @ZenSetter("score")
   void setScore(int var1);

   @ZenGetter("foodStats")
   IFoodStats getFoodStats();

   @ZenMethod
   void executeCommand(String var1);

   @ZenGetter("damageDisabled")
   boolean isDamageDisabled();

   @ZenSetter("damageDisabled")
   void setDamageDisabled(boolean var1);

   @ZenGetter("canFly")
   boolean canFly();

   @ZenSetter("canFly")
   void setCanFly(boolean var1);

   @ZenGetter("canEdit")
   boolean canEdit();

   @ZenSetter("canEdit")
   void setCanEdit(boolean var1);

   @ZenMethod
   default IEntityItem dropItem(boolean dropAll) {
      CraftTweakerAPI.logError("Default method IPlayer#dropItem(bool) is not overwritten in " + this.getClass() + " please report to the author!");
      return null;
   }

   @ZenMethod
   default IEntityItem dropItem(IItemStack item) {
      CraftTweakerAPI.logError("Default method IPlayer#dropItem(IItemStack) is not overwritten in " + this.getClass() + " please report to the author!");
      return null;
   }

   @ZenMethod
   default void setCooldown(IItemStack item, int time) {
      CraftTweakerAPI.logError("Default method IPlayer#setCooldown(void) is not overwritten in " + this.getClass() + " please report to the author!");
   }

   @ZenMethod
   default float getCooldown(IItemStack item) {
      CraftTweakerAPI.logError("Default method IPlayer#getCooldown(float) is not overwritten in " + this.getClass() + " please report to the author!");
      return 0.0F;
   }
}
