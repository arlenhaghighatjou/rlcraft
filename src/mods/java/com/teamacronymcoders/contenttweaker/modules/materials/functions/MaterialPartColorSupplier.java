package com.teamacronymcoders.contenttweaker.modules.materials.functions;

import com.teamacronymcoders.contenttweaker.api.ctobjects.color.CTColor;
import com.teamacronymcoders.contenttweaker.modules.materials.materialparts.IMaterialPart;
import com.teamacronymcoders.contenttweaker.modules.vanilla.functions.IItemColorSupplier;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.contenttweaker.MaterialPartColorSupplier")
public class MaterialPartColorSupplier implements IItemColorSupplier {
   private static final CTColor NONE = CTColor.fromInt(-1);
   private CTColor materialPartColor;
   private boolean hasOverlay;

   public MaterialPartColorSupplier(IMaterialPart materialPart) {
      this.materialPartColor = materialPart.getCTColor();
      this.hasOverlay = materialPart.hasOverlay();
   }

   @ZenMethod
   public static MaterialPartColorSupplier create(IMaterialPart materialPart) {
      return new MaterialPartColorSupplier(materialPart);
   }

   public CTColor getColor(IItemStack itemStack, int tintIndex) {
      if (this.hasOverlay) {
         return tintIndex == 1 ? this.materialPartColor : NONE;
      } else {
         return tintIndex == 0 ? this.materialPartColor : NONE;
      }
   }
}
