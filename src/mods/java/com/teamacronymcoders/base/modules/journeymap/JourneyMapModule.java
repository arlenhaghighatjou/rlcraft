package com.teamacronymcoders.base.modules.journeymap;

import com.teamacronymcoders.base.modulesystem.Module;
import com.teamacronymcoders.base.modulesystem.ModuleBase;
import com.teamacronymcoders.base.modulesystem.dependencies.IDependency;
import com.teamacronymcoders.base.modulesystem.dependencies.ModDependency;
import java.util.List;

@Module("base")
public class JourneyMapModule extends ModuleBase {
   public JourneyMapModule() {
   }

   public String getName() {
      return "JourneyMap";
   }

   public List<IDependency> getDependencies(List<IDependency> dependencies) {
      dependencies.add(new ModDependency("journeymap"));
      return super.getDependencies(dependencies);
   }
}
