package com.teamacronymcoders.contenttweaker.api.ctobjects.blockmaterial;

import com.teamacronymcoders.contenttweaker.api.ctobjects.enums.PushReaction;
import net.minecraft.block.material.Material;

public class BlockMaterialDefinition implements IBlockMaterialDefinition {
   private Material material;

   public BlockMaterialDefinition(Material material) {
      this.material = material;
   }

   public String getName() {
      return this.material.getClass().getName();
   }

   public boolean isLiquid() {
      return this.material.isLiquid();
   }

   public boolean isSolid() {
      return this.material.isSolid();
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

   public boolean isReplaceable() {
      return this.material.isReplaceable();
   }

   public boolean isOpaque() {
      return this.material.isOpaque();
   }

   public boolean isToolNotRequired() {
      return this.material.isToolNotRequired();
   }

   public PushReaction getMobilityFlag() {
      return PushReaction.of(this.material.getMobilityFlag());
   }

   public int compare(IBlockMaterialDefinition other) {
      return this.getInternal() == other.getInternal() ? 0 : -1;
   }

   public Material getInternal() {
      return this.material;
   }
}
