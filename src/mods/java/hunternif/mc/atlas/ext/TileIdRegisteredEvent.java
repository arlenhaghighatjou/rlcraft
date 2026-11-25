package hunternif.mc.atlas.ext;

import java.util.Map;
import net.minecraftforge.fml.common.eventhandler.Event;

public class TileIdRegisteredEvent extends Event {
   public final Map<String, Integer> nameToIdMap;

   public TileIdRegisteredEvent(Map<String, Integer> nameToIdMap) {
      this.nameToIdMap = nameToIdMap;
   }
}
