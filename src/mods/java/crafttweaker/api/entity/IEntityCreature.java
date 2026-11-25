package crafttweaker.api.entity;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.world.IBlockPos;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("crafttweaker.entity.IEntityCreature")
@ZenRegister
public interface IEntityCreature extends IEntityLiving {
   @ZenGetter
   boolean hasPath();

   @ZenGetter
   boolean isWithinHomeDistance();

   @ZenMethod
   boolean isPositionWithinHomeDistance(IBlockPos var1);

   @ZenMethod
   void setHomePositionAndDistance(IBlockPos var1, int var2);

   @ZenGetter("homePosition")
   IBlockPos getHomePosition();

   @ZenGetter("maximumHomeDistance")
   float getMaximumHomeDistance();

   @ZenMethod
   void detachHome();

   @ZenGetter
   boolean hasHome();
}
