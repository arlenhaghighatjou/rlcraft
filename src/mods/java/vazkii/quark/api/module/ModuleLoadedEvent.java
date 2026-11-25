package vazkii.quark.api.module;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class ModuleLoadedEvent extends Event {
   private final IModule module;

   public ModuleLoadedEvent(IModule module) {
      this.module = module;
   }

   public IModule getModule() {
      return this.module;
   }
}
