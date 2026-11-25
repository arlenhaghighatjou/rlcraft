package crafttweaker.api.player;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.IData;
import stanhebben.zenscript.annotations.ZenCaster;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenClass("crafttweaker.player.IFoodStats")
@ZenRegister
public interface IFoodStats {
   @ZenMethod
   void addStats(int var1, float var2);

   @ZenMethod
   void onUpdate(IPlayer var1);

   @ZenCaster
   @ZenMethod
   IData asNBT();

   @ZenGetter("foodLevel")
   int getFoodLevel();

   @ZenSetter("foodLevel")
   void setFootLevel(int var1);

   @ZenGetter
   boolean needFood();

   @ZenMethod
   void addExhaustion(float var1);

   @ZenGetter("saturationLevel")
   float getSaturationLevel();

   @ZenSetter("saturationLevel")
   void setSaturationLevel(float var1);
}
