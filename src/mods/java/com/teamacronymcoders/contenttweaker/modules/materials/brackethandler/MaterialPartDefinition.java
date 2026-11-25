package com.teamacronymcoders.contenttweaker.modules.materials.brackethandler;

import com.teamacronymcoders.base.materialsystem.materialparts.MaterialPart;
import com.teamacronymcoders.contenttweaker.api.ctobjects.color.CTColor;
import com.teamacronymcoders.contenttweaker.modules.materials.materialpartdata.CTMaterialPartData;
import com.teamacronymcoders.contenttweaker.modules.materials.materialpartdata.IMaterialPartData;
import com.teamacronymcoders.contenttweaker.modules.materials.materialparts.IMaterialPart;
import com.teamacronymcoders.contenttweaker.modules.materials.materials.CTMaterial;
import com.teamacronymcoders.contenttweaker.modules.materials.materials.IMaterial;
import com.teamacronymcoders.contenttweaker.modules.materials.parts.CTPart;
import com.teamacronymcoders.contenttweaker.modules.materials.parts.IPart;
import crafttweaker.api.item.IItemStack;
import crafttweaker.mc1120.item.MCItemStack;
import net.minecraft.util.ResourceLocation;

public class MaterialPartDefinition extends MCItemStack implements IMaterialPart {
   private MaterialPart materialPart;

   public MaterialPartDefinition(MaterialPart materialPart) {
      super(materialPart.getItemStack());
      this.materialPart = materialPart;
   }

   public String getName() {
      return this.materialPart.getLocalizedName();
   }

   public String getLocalizedName() {
      return this.materialPart.getLocalizedName();
   }

   public boolean hasEffect() {
      return this.materialPart.hasEffect();
   }

   public IMaterial getMaterial() {
      return new CTMaterial(this.materialPart.getMaterial());
   }

   public IPart getPart() {
      return new CTPart(this.materialPart.getPart());
   }

   public IItemStack getItemStack() {
      return new MCItemStack(this.materialPart.getItemStack());
   }

   public String getTextureLocation() {
      return this.materialPart.getTextureLocation().toString();
   }

   public void setTextureLocation(String textureLocation) {
      this.materialPart.setTextureLocation(new ResourceLocation(textureLocation));
   }

   public int getColor() {
      return this.materialPart.getColor();
   }

   public CTColor getCTColor() {
      return CTColor.fromInt(this.getColor());
   }

   public boolean hasOverlay() {
      return this.materialPart.getPart().hasOverlayTexture();
   }

   public boolean isColorized() {
      return this.materialPart.isColorized();
   }

   public void setColorized(boolean colorized) {
      this.materialPart.setColorized(colorized);
   }

   public IMaterialPartData getData() {
      return new CTMaterialPartData(this.materialPart.getData());
   }
}
