package com.teamacronymcoders.contenttweaker.modules.tinkers.materials;

import slimeknights.tconstruct.library.MaterialIntegration;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

public class CoTTConMaterialIntegration extends MaterialIntegration {
   public CoTTConMaterialIntegration(Material material) {
      super(material, material.getFluid());
   }

   public void preInit() {
   }

   public void integrate() {
      if (this.material.isCastable()) {
         TinkerSmeltery.registerToolpartMeltingCasting(this.material);
      }

   }
}
