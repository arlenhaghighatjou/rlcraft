package crafttweaker.mc1120.block;

import crafttweaker.api.entity.IEntity;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IFacing;
import crafttweaker.api.world.IRayTraceResult;
import java.util.Objects;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;

public class MCRayTraceResult implements IRayTraceResult {
   private final RayTraceResult traceResult;

   public MCRayTraceResult(RayTraceResult traceResult) {
      this.traceResult = traceResult;
   }

   public boolean isMiss() {
      return this.traceResult.typeOfHit == Type.MISS;
   }

   public boolean isEntity() {
      return this.traceResult.typeOfHit == Type.ENTITY;
   }

   public boolean isBlock() {
      return this.traceResult.typeOfHit == Type.BLOCK;
   }

   public IEntity getEntity() {
      return CraftTweakerMC.getIEntity(this.traceResult.entityHit);
   }

   public IBlockPos getBlockPos() {
      return CraftTweakerMC.getIBlockPos(this.traceResult.getBlockPos());
   }

   public IFacing getSideHit() {
      return CraftTweakerMC.getIFacing(this.traceResult.sideHit);
   }

   public RayTraceResult getInternal() {
      return this.traceResult;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         MCRayTraceResult that = (MCRayTraceResult)o;
         return Objects.equals(this.traceResult, that.traceResult);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.traceResult});
   }
}
