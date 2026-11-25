package com.teamacronymcoders.base.modulesystem.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.teamacronymcoders.base.BaseMods;
import com.teamacronymcoders.base.IBaseMod;
import com.teamacronymcoders.base.modulesystem.ModuleHandler;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

public class ConditionFactoryModuleActive implements IConditionFactory {
   public ConditionFactoryModuleActive() {
   }

   public BooleanSupplier parse(JsonContext context, JsonObject json) {
      String moduleName = JsonUtils.getString(json, "name");
      String modName = (String)Optional.ofNullable(json.get("mod")).map(JsonElement::getAsString).orElse(context.getModId());
      String moduleHandlerName = (String)Optional.ofNullable(json.get("handler")).map(JsonElement::getAsString).orElse(modName);
      IBaseMod mod = (IBaseMod)Optional.ofNullable(BaseMods.getBaseMod(modName)).orElse(BaseMods.getBaseMod(moduleHandlerName));
      if (mod != null) {
         if (mod.getID().equals(moduleHandlerName)) {
            return () -> {
               return mod.getModuleHandler().isModuleEnabled(moduleName);
            };
         } else {
            for(ModuleHandler moduleHandler : mod.getOtherModuleHandlers()) {
               if (moduleHandlerName.equals(moduleHandler.getName())) {
                  return () -> {
                     return moduleHandler.isModuleEnabled(moduleName);
                  };
               }
            }

            return () -> {
               return false;
            };
         }
      } else {
         return () -> {
            return false;
         };
      }
   }
}
