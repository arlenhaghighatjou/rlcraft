package com.blamejared.compat.tconstruct.materials;

import slimeknights.tconstruct.library.materials.Material;

public class TICMaterialDefinition implements ITICMaterialDefinition {
   private final Material material;

   public TICMaterialDefinition(Material material) {
      this.material = material;
   }

   public ITICMaterial asMaterial() {
      return new TICMaterial(this.material);
   }

   public String getName() {
      return this.material.getLocalizedName();
   }

   public String getDisplayName() {
      return this.material.getLocalizedName();
   }
}
