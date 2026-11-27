package slimeknights.mantle.pulsar.pulse;

import net.minecraftforge.fml.common.eventhandler.EventBus;

public interface IPulse {
   default void handlePulseEvent(Object evt) {
   }

   default void register(EventBus bus) {
   }
}
