package com.blamejared.compat.tconstruct.materials;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;

@ZenClass("modtweaker.tconstruct.ITICMaterialDefinition")
@ZenRegister
public interface ITICMaterialDefinition {
   @ZenGetter("stack")
   ITICMaterial asMaterial();

   @ZenGetter("name")
   String getName();

   @ZenGetter("displayName")
   String getDisplayName();
}
