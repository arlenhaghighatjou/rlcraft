package xzeroair.trinkets.blocks.materials;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class TrinketBlockMaterial extends Material {
   public static final Material ClothTeddyBear = (new TrinketBlockMaterial(MapColor.CLOTH)).setBlockMovement(false).setBlockLight(false).setAdventureModeExempt();
   protected boolean blockMovement = true;
   protected boolean isSolid = true;
   protected boolean isLiquid = false;
   protected boolean blockLight = true;

   public TrinketBlockMaterial(MapColor color) {
      super(color);
   }

   protected TrinketBlockMaterial setBlockMovement(boolean blockMovement) {
      this.blockMovement = blockMovement;
      return this;
   }

   public boolean blocksMovement() {
      return this.blockMovement;
   }

   public boolean isSolid() {
      return this.isSolid;
   }

   protected TrinketBlockMaterial setSolid(boolean isSolid) {
      this.isSolid = isSolid;
      return this;
   }

   public boolean isLiquid() {
      return !this.isSolid() && this.isLiquid;
   }

   protected TrinketBlockMaterial setLiquid(boolean isLiquid) {
      this.isLiquid = isLiquid;
      return this;
   }

   public boolean blocksLight() {
      return this.blockLight;
   }

   protected TrinketBlockMaterial setBlockLight(boolean blockLight) {
      this.blockLight = blockLight;
      return this;
   }
}
