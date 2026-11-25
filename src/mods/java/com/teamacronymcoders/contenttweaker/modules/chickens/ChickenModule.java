package com.teamacronymcoders.contenttweaker.modules.chickens;

import com.teamacronymcoders.base.modulesystem.Module;
import com.teamacronymcoders.base.modulesystem.ModuleBase;
import com.teamacronymcoders.base.modulesystem.dependencies.IDependency;
import com.teamacronymcoders.base.modulesystem.dependencies.ModDependency;
import java.util.List;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Module("contenttweaker")
public class ChickenModule extends ModuleBase {
   public ChickenModule() {
   }

   public String getName() {
      return "Chickens";
   }

   public List<IDependency> getDependencies(List<IDependency> dependencies) {
      dependencies.add(new ModDependency("chickens"));
      return super.getDependencies(dependencies);
   }

   public void init(FMLInitializationEvent event) {
      super.init(event);
      ChickenFactory.registerChickens();
   }
}
