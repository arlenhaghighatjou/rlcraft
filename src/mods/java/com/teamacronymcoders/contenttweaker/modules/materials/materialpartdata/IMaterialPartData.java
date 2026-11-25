package com.teamacronymcoders.contenttweaker.modules.materials.materialpartdata;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.contenttweaker.MaterialPartData")
public interface IMaterialPartData {
   @ZenMethod
   void addDataValue(String var1, String var2);

   @ZenMethod
   int getIntValue(String var1, int var2);

   @ZenMethod
   String getStringValue(String var1, String var2);
}
