package ivorius.reccomplex.events.handlers;

import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.events.FileLoadEvent;
import ivorius.reccomplex.events.RCEventBus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RCRecurrentComplexEventHandler {
   public RCRecurrentComplexEventHandler() {
   }

   public void register() {
      RCEventBus.INSTANCE.register(this);
   }

   @SubscribeEvent
   public void onLoad(FileLoadEvent.Pre event) {
      if (!RCConfig.generateNature && event.domain.equals("reccomplex") && (event.path.getParent().endsWith("nature") || event.path.getParent().getParent().endsWith("nature"))) {
         event.active = false;
      }

   }
}
