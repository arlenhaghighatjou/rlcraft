package com.teamacronymcoders.contenttweaker.modules.materials.materials;

import com.teamacronymcoders.base.materialsystem.MaterialException;
import com.teamacronymcoders.contenttweaker.api.ctobjects.color.CTColor;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.contenttweaker.MaterialBuilder")
public interface IMaterialBuilder {
   @ZenMethod
   IMaterialBuilder setName(String var1);

   @ZenMethod
   IMaterialBuilder setColor(int var1);

   @ZenMethod
   IMaterialBuilder setColor(CTColor var1);

   @ZenMethod
   IMaterialBuilder setHasEffect(boolean var1);

   @ZenMethod
   IMaterial build() throws MaterialException;
}
