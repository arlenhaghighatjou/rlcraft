package com.teamacronymcoders.base.event;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.GenericEvent;

public class BaseRegistryEvent<T> extends GenericEvent<T> {
   private Map<String, T> entries = Maps.newHashMap();

   public BaseRegistryEvent(Class<T> type) {
      super(type);
   }

   public void register(ResourceLocation resourceLocation, T entry) {
      this.entries.put(resourceLocation.toString(), entry);
   }

   public Map<String, T> getEntries() {
      return ImmutableMap.copyOf(this.entries);
   }
}
