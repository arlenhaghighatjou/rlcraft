package reborncore.common.registration;

import net.minecraftforge.fml.common.LoaderState.ModState;
import net.minecraftforge.fml.common.event.FMLStateEvent;

public class RegistryConstructionEvent extends FMLStateEvent {
   public RegistryConstructionEvent() {
      super(new Object[0]);
   }

   public ModState getModState() {
      return null;
   }
}
