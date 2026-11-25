package org.orecruncher.dsurround.capabilities;

import java.util.concurrent.Callable;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.orecruncher.dsurround.capabilities.entitydata.EntityData;
import org.orecruncher.dsurround.capabilities.entitydata.EntityDataTables;
import org.orecruncher.dsurround.capabilities.entitydata.IEntityData;
import org.orecruncher.dsurround.capabilities.entitydata.IEntityDataSettable;
import org.orecruncher.dsurround.network.Network;
import org.orecruncher.dsurround.network.PacketEntityData;
import org.orecruncher.lib.capability.CapabilityProviderSerializable;
import org.orecruncher.lib.capability.CapabilityUtils;
import org.orecruncher.lib.capability.SimpleStorage;

public final class CapabilityEntityData {
   @CapabilityInject(IEntityData.class)
   public static final Capability<IEntityData> ENTITY_DATA = null;
   public static final ResourceLocation CAPABILITY_ID = new ResourceLocation("dsurround", "data");

   public CapabilityEntityData() {
   }

   public static void register() {
      CapabilityManager.INSTANCE.register(IEntityData.class, new SimpleStorage(), EntityData::new);
   }

   @Nullable
   public static IEntityData getCapability(@Nonnull Entity entity) {
      return (IEntityData)CapabilityUtils.getCapability(entity, ENTITY_DATA, (EnumFacing)null);
   }

   @Nonnull
   public static ICapabilityProvider createProvider(IEntityData data) {
      return new CapabilityProviderSerializable(ENTITY_DATA, (EnumFacing)null, data);
   }

   @EventBusSubscriber(
      modid = "dsurround"
   )
   public static class EventHandler {
      public EventHandler() {
      }

      @SubscribeEvent
      public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
         if (event.getObject() instanceof EntityLiving) {
            EntityData emojiData = new EntityData((EntityLiving)event.getObject());
            event.addCapability(CapabilityEntityData.CAPABILITY_ID, CapabilityEntityData.createProvider(emojiData));
         }

      }

      @SubscribeEvent
      public static void trackingEvent(@Nonnull StartTracking event) {
         if (event.getTarget() instanceof EntityLiving) {
            IEntityData data = (IEntityData)event.getTarget().getCapability(CapabilityEntityData.ENTITY_DATA, (EnumFacing)null);
            if (data != null) {
               Network.sendToPlayer((EntityPlayerMP)event.getEntityPlayer(), new PacketEntityData(data));
            }
         }

      }

      @SubscribeEvent
      public static void livingUpdate(@Nonnull LivingUpdateEvent event) {
         Entity entity = event.getEntity();
         World world = entity.getEntityWorld();
         if (!world.isRemote && entity.ticksExisted % 5 == 0) {
            IEntityDataSettable data = (IEntityDataSettable)CapabilityEntityData.getCapability(event.getEntity());
            if (data != null) {
               EntityDataTables.assess(data);
               data.sync();
            }

         }
      }
   }
}
