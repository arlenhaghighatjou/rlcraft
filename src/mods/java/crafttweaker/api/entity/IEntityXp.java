package crafttweaker.api.entity;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenClass("crafttweaker.entity.IEntityXp")
@ZenRegister
public interface IEntityXp extends IEntity {
   @ZenGetter("xp")
   float getXp();

   @ZenSetter("xp")
   void setXp(int var1);
}
