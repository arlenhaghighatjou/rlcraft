package com.teamacronymcoders.base.materialsystem.blocks;

import com.teamacronymcoders.base.materialsystem.materialparts.MaterialPart;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.apache.logging.log4j.util.Strings;

public class FluidMaterial extends Fluid {
   private MaterialPart materialPart;
   private boolean vaporize = false;

   public FluidMaterial(MaterialPart materialPart, ResourceLocation texture) {
      super(createFluidName(materialPart), texture, new ResourceLocation(texture.getResourceDomain(), texture.getResourcePath() + "_flowing"));
      this.materialPart = materialPart;
   }

   private static String createFluidName(MaterialPart materialPart) {
      String name = materialPart.getMaterial().getUnlocalizedName();
      if (!Strings.isBlank(materialPart.getPart().getOreDictPrefix())) {
         name = name + "_" + materialPart.getPart().getOreDictPrefix();
      }

      return name;
   }

   public int getColor() {
      return this.materialPart.getColor();
   }

   public void setVaporize(boolean vaporize) {
      this.vaporize = vaporize;
   }

   public boolean doesVaporize(FluidStack fluidStack) {
      return this.vaporize;
   }

   public String getLocalizedName(FluidStack stack) {
      return this.materialPart.getLocalizedName();
   }

   public String getUnlocalizedName() {
      return this.materialPart.getPart().getUnlocalizedName();
   }
}
