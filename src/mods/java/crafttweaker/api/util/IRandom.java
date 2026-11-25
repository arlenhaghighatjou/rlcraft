package crafttweaker.api.util;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("crafttweaker.util.IRandom")
@ZenRegister
public interface IRandom {
   @ZenMethod
   int nextInt();

   @ZenMethod
   int nextInt(int var1);

   @ZenMethod
   double nextDouble();

   @ZenMethod
   float nextFloat();

   @ZenMethod
   boolean nextBoolean();

   @ZenMethod
   int nextInt(int var1, int var2);

   @ZenMethod
   float nextFloat(float var1, float var2);

   @ZenMethod
   double nextDouble(double var1, double var3);

   @ZenMethod
   String getRandomUUID();
}
