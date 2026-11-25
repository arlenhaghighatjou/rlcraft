package crafttweaker.mc1120.block;

import crafttweaker.api.block.IMaterial;
import crafttweaker.api.block.IMobilityFlag;
import java.util.Objects;
import net.minecraft.block.material.Material;

public class MCMaterial implements IMaterial {
   private final Material material;

   public MCMaterial(Material material) {
      this.material = material;
   }

   public boolean blocksLight() {
      return this.material.blocksLight();
   }

   public boolean blocksMovement() {
      return this.material.blocksMovement();
   }

   public boolean getCanBurn() {
      return this.material.getCanBurn();
   }

   public IMobilityFlag getMobilityFlag() {
      return new MCMobilityFlag(this.material.getMobilityFlag());
   }

   public boolean isLiquid() {
      return this.material.isLiquid();
   }

   public boolean isOpaque() {
      return this.material.isOpaque();
   }

   public boolean isReplaceable() {
      return this.material.isReplaceable();
   }

   public boolean isSolid() {
      return this.material.isSolid();
   }

   public boolean isToolNotRequired() {
      return this.material.isToolNotRequired();
   }

   public IMaterial setReplaceable() {
      return new MCMaterial(this.material.setReplaceable());
   }

   public boolean matches(IMaterial other) {
      return this.getInternal().equals(other.getInternal());
   }

   public Object getInternal() {
      return this.material;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         MCMaterial that = (MCMaterial)o;
         return Objects.equals(this.material, that.material);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.material});
   }
}
