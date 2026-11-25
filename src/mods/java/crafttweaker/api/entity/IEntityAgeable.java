package crafttweaker.api.entity;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenClass("crafttweaker.entity.IEntityAgeable")
@ZenRegister
public interface IEntityAgeable extends IEntityCreature {
   @ZenGetter("growingAge")
   int getGrowingAge();

   @ZenSetter("growingAge")
   void setGrowingAge(int var1);

   @ZenMethod
   void ageUp(int var1, @Optional boolean var2);

   @ZenMethod
   void addGrowth(int var1);

   @ZenSetter("scaleForAge")
   void setScaleForAge(boolean var1);
}
