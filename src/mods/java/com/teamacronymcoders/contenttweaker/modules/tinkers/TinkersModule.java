package com.teamacronymcoders.contenttweaker.modules.tinkers;

import com.teamacronymcoders.base.modulesystem.Module;
import com.teamacronymcoders.base.modulesystem.ModuleBase;
import com.teamacronymcoders.base.modulesystem.dependencies.IDependency;
import com.teamacronymcoders.base.modulesystem.dependencies.ModDependency;
import com.teamacronymcoders.contenttweaker.modules.tinkers.materials.CoTTConMaterial;
import com.teamacronymcoders.contenttweaker.modules.tinkers.materials.CoTTConMaterialBuilder;
import java.util.List;
import java.util.function.Consumer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Module("contenttweaker")
public class TinkersModule extends ModuleBase {
   public TinkersModule() {
   }

   public List<IDependency> getDependencies(List<IDependency> dependencies) {
      dependencies.add(new ModDependency("tconstruct"));
      return super.getDependencies(dependencies);
   }

   public String getName() {
      return "Tinkers";
   }

   public void init(FMLInitializationEvent event) {
      super.init(event);
      CoTTConMaterialBuilder.addedMaterials.forEach(CoTTConMaterial::registerTraits);
   }
}
