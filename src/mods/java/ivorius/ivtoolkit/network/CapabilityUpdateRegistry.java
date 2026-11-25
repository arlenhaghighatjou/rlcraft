package ivorius.ivtoolkit.network;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import javax.annotation.Nullable;
import net.minecraftforge.common.capabilities.Capability;

public class CapabilityUpdateRegistry {
   public static final CapabilityUpdateRegistry INSTANCE = new CapabilityUpdateRegistry();
   private final BiMap<String, Capability> capabilityBiMap = HashBiMap.create();

   public CapabilityUpdateRegistry() {
   }

   public Capability register(@Nullable String key, @Nullable Capability value) {
      return (Capability)this.capabilityBiMap.put(key, value);
   }

   public Capability capability(String key) {
      return (Capability)this.capabilityBiMap.get(key);
   }

   public String key(Capability key) {
      return (String)this.capabilityBiMap.inverse().get(key);
   }
}
