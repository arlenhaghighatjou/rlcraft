package net.theawesomegem.fishingmadebetter.common.capability;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.theawesomegem.fishingmadebetter.common.capability.fishing.FishingCapabilityProvider;
import net.theawesomegem.fishingmadebetter.common.capability.world.ChunkCapabilityProvider;

public class CapabilityHandler {
   public static final ResourceLocation FISHING_DATA_CAP_RESOURCE = new ResourceLocation("fishingmadebetter", "fishingdata");
   public static final ResourceLocation CHUNK_FISHING_DATA_CAP_RESOURCE = new ResourceLocation("fishingmadebetter", "chunkfishingdata");

   public CapabilityHandler() {
   }

   public void register(EventBus bus) {
      bus.registerGeneric(AttachCapabilitiesEvent.class, Entity.class, this::onAttachCapabilityEntity);
      bus.registerGeneric(AttachCapabilitiesEvent.class, Chunk.class, this::onAttachCapabilityChunk);
   }

   public void onAttachCapabilityEntity(AttachCapabilitiesEvent<Entity> e) {
      if (e.getObject() instanceof EntityPlayer) {
         e.addCapability(FISHING_DATA_CAP_RESOURCE, new FishingCapabilityProvider());
      }
   }

   public void onAttachCapabilityChunk(AttachCapabilitiesEvent<Chunk> e) {
      e.addCapability(CHUNK_FISHING_DATA_CAP_RESOURCE, new ChunkCapabilityProvider());
   }
}
