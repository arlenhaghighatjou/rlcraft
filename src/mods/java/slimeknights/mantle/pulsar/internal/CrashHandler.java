package slimeknights.mantle.pulsar.internal;

import net.minecraftforge.fml.common.ICrashCallable;
import slimeknights.mantle.pulsar.control.PulseManager;
import slimeknights.mantle.pulsar.pulse.PulseMeta;

public class CrashHandler implements ICrashCallable {
   private String id;
   private PulseManager manager;

   public CrashHandler(String modId, PulseManager manager) {
      this.id = "Pulsar/" + modId + " loaded Pulses";
      this.manager = manager;
   }

   public String getLabel() {
      return this.id;
   }

   public String call() throws Exception {
      String out = "\n";

      for(PulseMeta meta : this.manager.getAllPulseMetadata()) {
         String state = getStateFromMeta(meta);
         out = out + "\t\t- " + meta.getId() + " (" + state + ")\n";
      }

      return out;
   }

   private static String getStateFromMeta(PulseMeta meta) {
      if (meta.isForced()) {
         return "Enabled/Forced";
      } else {
         return meta.isEnabled() ? "Enabled/Not Forced" : "Disabled/Not Forced";
      }
   }
}
