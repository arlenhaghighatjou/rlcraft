package com.teamacronymcoders.contenttweaker.modules.materials.parts;

import com.teamacronymcoders.base.materialsystem.MaterialException;
import com.teamacronymcoders.contenttweaker.modules.materials.parttypes.IPartType;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.contenttweaker.PartBuilder")
public interface IPartBuilder {
   @ZenMethod
   IPartBuilder setName(String var1);

   @ZenMethod
   IPartBuilder setPartType(IPartType var1);

   @ZenMethod
   IPartBuilder setOreDictName(String var1);

   @ZenMethod
   IPartBuilder setAdditionalOreDictNames(String... var1);

   @ZenMethod
   IPartBuilder setHasOverlay(boolean var1);

   @ZenMethod
   IPart build() throws MaterialException;
}
