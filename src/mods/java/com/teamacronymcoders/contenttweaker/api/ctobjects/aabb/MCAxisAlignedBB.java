package com.teamacronymcoders.contenttweaker.api.ctobjects.aabb;

import com.teamacronymcoders.contenttweaker.api.ICTObject;
import crafttweaker.annotations.ZenRegister;
import net.minecraft.util.math.AxisAlignedBB;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenProperty;

@ZenRegister
@ZenClass("mods.contenttweaker.AxisAlignedBB")
public class MCAxisAlignedBB implements ICTObject<AxisAlignedBB> {
   @ZenProperty
   public double minX = 0.0D;
   @ZenProperty
   public double minY = 0.0D;
   @ZenProperty
   public double minZ = 0.0D;
   @ZenProperty
   public double maxX = 1.0D;
   @ZenProperty
   public double maxY = 1.0D;
   @ZenProperty
   public double maxZ = 1.0D;

   public MCAxisAlignedBB(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      this.minX = minX;
      this.minY = minY;
      this.minZ = minZ;
      this.maxX = maxX;
      this.maxY = maxY;
      this.maxZ = maxZ;
   }

   @ZenMethod
   public static MCAxisAlignedBB create(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      return new MCAxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
   }

   @ZenMethod
   public double getMinX() {
      return this.minX;
   }

   @ZenMethod
   public void setMinX(double minX) {
      this.minX = minX;
   }

   @ZenMethod
   public double getMinY() {
      return this.minY;
   }

   @ZenMethod
   public void setMinY(double minY) {
      this.minY = minY;
   }

   @ZenMethod
   public double getMinZ() {
      return this.minZ;
   }

   @ZenMethod
   public void setMinZ(double minZ) {
      this.minZ = minZ;
   }

   @ZenMethod
   public double getMaxX() {
      return this.maxX;
   }

   @ZenMethod
   public void setMaxX(double maxX) {
      this.maxX = maxX;
   }

   @ZenMethod
   public double getMaxY() {
      return this.maxY;
   }

   @ZenMethod
   public void setMaxY(double maxY) {
      this.maxY = maxY;
   }

   @ZenMethod
   public double getMaxZ() {
      return this.maxZ;
   }

   @ZenMethod
   public void setMaxZ(double maxZ) {
      this.maxZ = maxZ;
   }

   public AxisAlignedBB getInternal() {
      return new AxisAlignedBB(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
   }
}
