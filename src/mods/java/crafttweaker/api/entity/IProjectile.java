package crafttweaker.api.entity;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("crafttweaker.entity.IProjectile")
@ZenRegister
public interface IProjectile {
   @ZenMethod
   void shoot(double var1, double var3, double var5, float var7, float var8);
}
