package crafttweaker.api.util;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.world.IVector3d;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("crafttweaker.util.IAxisAlignedBB")
@ZenRegister
public interface IAxisAlignedBB {
   Object getInternal();

   @ZenGetter("minX")
   double getMinX();

   @ZenGetter("minY")
   double getMinY();

   @ZenGetter("minZ")
   double getMinZ();

   @ZenGetter("maxX")
   double getMaxX();

   @ZenGetter("maxY")
   double getMaxY();

   @ZenGetter("maxZ")
   double getMaxZ();

   @ZenGetter("center")
   IVector3d getCenter();

   @ZenMethod
   IAxisAlignedBB contract(double var1, double var3, double var5);

   @ZenMethod
   IAxisAlignedBB expand(double var1, double var3, double var5);

   @ZenMethod
   IAxisAlignedBB grow(double var1);

   @ZenMethod
   IAxisAlignedBB grow(double var1, double var3, double var5);

   @ZenMethod
   IAxisAlignedBB shrink(double var1);

   @ZenMethod
   IAxisAlignedBB intersect(IAxisAlignedBB var1);

   @ZenMethod
   boolean intersects(IAxisAlignedBB var1);

   @ZenMethod
   boolean contains(IVector3d var1);

   @ZenMethod
   IAxisAlignedBB union(IAxisAlignedBB var1);
}
