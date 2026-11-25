package com.teamacronymcoders.contenttweaker.modules.materials.materials;

import com.teamacronymcoders.contenttweaker.modules.materials.materialparts.IMaterialPart;
import com.teamacronymcoders.contenttweaker.modules.materials.parts.IPart;
import crafttweaker.annotations.ZenRegister;
import java.util.List;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.contenttweaker.Material")
public interface IMaterial {
   @ZenMethod
   String getName();

   @ZenMethod
   int getColor();

   @ZenMethod
   boolean isHasEffect();

   @ZenMethod
   String getUnlocalizedName();

   @ZenMethod
   List<IMaterialPart> registerParts(String[] var1);

   @ZenMethod
   List<IMaterialPart> registerParts(IPart[] var1);

   @ZenMethod
   IMaterialPart registerPart(String var1);

   @ZenMethod
   IMaterialPart registerPart(IPart var1);
}
