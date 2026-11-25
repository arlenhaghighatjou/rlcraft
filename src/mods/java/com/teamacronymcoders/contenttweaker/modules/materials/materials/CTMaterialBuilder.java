package com.teamacronymcoders.contenttweaker.modules.materials.materials;

import com.teamacronymcoders.base.materialsystem.MaterialException;
import com.teamacronymcoders.base.materialsystem.materials.MaterialBuilder;
import com.teamacronymcoders.contenttweaker.api.ctobjects.color.CTColor;
import java.awt.Color;

public class CTMaterialBuilder implements IMaterialBuilder {
   private MaterialBuilder materialBuilder = new MaterialBuilder();

   public CTMaterialBuilder() {
   }

   public IMaterialBuilder setName(String name) {
      this.materialBuilder.setName(name);
      return this;
   }

   public IMaterialBuilder setColor(int color) {
      return this.setColor(CTColor.fromInt(color));
   }

   public IMaterialBuilder setColor(CTColor color) {
      this.materialBuilder.setColor(new Color(color.getIntColor(), true));
      return this;
   }

   public IMaterialBuilder setHasEffect(boolean hasEffect) {
      this.materialBuilder.setHasEffect(hasEffect);
      return this;
   }

   public IMaterial build() throws MaterialException {
      return new CTMaterial(this.materialBuilder.build());
   }
}
