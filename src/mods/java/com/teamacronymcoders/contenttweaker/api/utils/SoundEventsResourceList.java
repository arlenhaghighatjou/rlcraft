package com.teamacronymcoders.contenttweaker.api.utils;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class SoundEventsResourceList extends ResourceList<SoundEvent> {
   public SoundEventsResourceList() {
      super(SoundEvent.class, SoundEvents.class);
   }

   public void addResource(String name, SoundEvent resource) {
   }

   public SoundEvent getResource(String name) {
      return (SoundEvent)ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(name));
   }

   public List<String> getAllNames() {
      return (List)ForgeRegistries.SOUND_EVENTS.getKeys().stream().map(ResourceLocation::toString).collect(Collectors.toList());
   }

   public Collection<SoundEvent> getAllResources() {
      return ForgeRegistries.SOUND_EVENTS.getValuesCollection();
   }
}
