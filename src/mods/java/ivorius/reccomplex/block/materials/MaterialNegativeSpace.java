package ivorius.reccomplex.block.materials;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class MaterialNegativeSpace extends Material {
   public MaterialNegativeSpace() {
      super(MapColor.AIR);
      this.setReplaceable();
   }

   public boolean isSolid() {
      return false;
   }

   public boolean blocksLight() {
      return false;
   }

   public boolean blocksMovement() {
      return false;
   }
}
