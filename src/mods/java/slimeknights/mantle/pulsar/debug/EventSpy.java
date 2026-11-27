package slimeknights.mantle.pulsar.debug;

import slimeknights.mantle.pulsar.pulse.IPulse;
import net.minecraftforge.fml.common.Loader;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import slimeknights.mantle.pulsar.pulse.Pulse;

@Pulse(
   id = "EventSpy",
   description = "we iz in ur buses, monitorin ur eventz",
   forced = true
)
public class EventSpy implements IPulse {
   private final Logger log = LogManager.getLogger("EventSpy/" + Loader.instance().activeModContainer().getModId());

   public EventSpy() {
   }

   public void handlePulseEvent(Object evt) {
      this.receive(evt);
   }

   public void receive(Object evt) {
      this.log.info("Received event: " + evt);
   }
}
