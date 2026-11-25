package net.blay09.mods.waystones.client;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.blay09.mods.waystones.util.WaystoneEntry;

public class ClientWaystones {
   private static final Map<String, WaystoneEntry> knownWaystones = Maps.newHashMap();

   public ClientWaystones() {
   }

   public static void setKnownWaystones(WaystoneEntry[] entries) {
      knownWaystones.clear();

      for(WaystoneEntry entry : entries) {
         knownWaystones.put(entry.getName(), entry);
      }

   }

   @Nullable
   public static WaystoneEntry getKnownWaystone(String name) {
      return (WaystoneEntry)knownWaystones.get(name);
   }

   public static WaystoneEntry[] getKnownWaystones() {
      return (WaystoneEntry[])knownWaystones.values().toArray(new WaystoneEntry[knownWaystones.size()]);
   }
}
