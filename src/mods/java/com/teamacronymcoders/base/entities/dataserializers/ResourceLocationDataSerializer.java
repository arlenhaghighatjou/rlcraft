package com.teamacronymcoders.base.entities.dataserializers;

import java.io.IOException;
import javax.annotation.Nonnull;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.util.ResourceLocation;

public class ResourceLocationDataSerializer implements DataSerializer<ResourceLocation> {
   public ResourceLocationDataSerializer() {
   }

   public void write(@Nonnull PacketBuffer buf, @Nonnull ResourceLocation value) {
      buf.writeString(value.getResourceDomain());
      buf.writeString(value.getResourcePath());
   }

   @Nonnull
   public ResourceLocation read(@Nonnull PacketBuffer buf) throws IOException {
      return new ResourceLocation(buf.readString(256), buf.readString(256));
   }

   @Nonnull
   public DataParameter<ResourceLocation> createKey(int id) {
      return new DataParameter(id, this);
   }

   @Nonnull
   public ResourceLocation copyValue(@Nonnull ResourceLocation value) {
      return new ResourceLocation(value.getResourceDomain(), value.getResourcePath());
   }
}
