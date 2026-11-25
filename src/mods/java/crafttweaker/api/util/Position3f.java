package crafttweaker.api.util;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.world.IBlockPos;
import stanhebben.zenscript.annotations.ZenCaster;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenClass("crafttweaker.util.Position3f")
@ZenRegister
public interface Position3f {
   @ZenGetter("x")
   @ZenMethod
   float getX();

   @ZenGetter("y")
   @ZenMethod
   float getY();

   @ZenGetter("z")
   @ZenMethod
   float getZ();

   @ZenSetter("x")
   @ZenMethod
   void setX(float var1);

   @ZenSetter("y")
   @ZenMethod
   void setY(float var1);

   @ZenSetter("z")
   @ZenMethod
   void setZ(float var1);

   @ZenMethod
   @ZenCaster
   IBlockPos asBlockPos();
}
