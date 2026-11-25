package crafttweaker.api.world;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("crafttweaker.world.IVector3d")
@ZenRegister
public interface IVector3d {
   @ZenGetter("x")
   double getX();

   @ZenGetter("y")
   double getY();

   @ZenGetter("z")
   double getZ();

   @ZenGetter("normalized")
   IVector3d getNormalized();

   @ZenMethod
   double dotProduct(IVector3d var1);

   @ZenMethod
   IVector3d crossProduct(IVector3d var1);

   @ZenMethod
   IVector3d subtract(IVector3d var1);

   @ZenMethod
   default IVector3d subtractReverse(IVector3d other) {
      return null;
   }

   @ZenMethod
   IVector3d add(IVector3d var1);

   @ZenMethod
   double distanceTo(IVector3d var1);

   @ZenMethod
   IVector3d scale(double var1);

   Object getInternal();
}
