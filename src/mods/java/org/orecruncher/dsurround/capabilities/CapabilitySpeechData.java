package org.orecruncher.dsurround.capabilities;

import java.util.concurrent.Callable;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.capabilities.speech.ISpeechData;
import org.orecruncher.dsurround.capabilities.speech.SpeechData;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;
import org.orecruncher.lib.capability.CapabilityProviderSerializable;
import org.orecruncher.lib.capability.CapabilityUtils;
import org.orecruncher.lib.capability.NullStorage;

public final class CapabilitySpeechData {
   @CapabilityInject(ISpeechData.class)
   public static final Capability<ISpeechData> SPEECH_DATA = null;
   public static final ResourceLocation CAPABILITY_ID = new ResourceLocation("dsurround", "speech");

   public CapabilitySpeechData() {
   }

   @SideOnly(Side.CLIENT)
   public static void register() {
      CapabilityManager.INSTANCE.register(ISpeechData.class, new NullStorage(), SpeechData::new);
   }

   @SideOnly(Side.CLIENT)
   @Nullable
   public static ISpeechData getCapability(@Nonnull Entity entity) {
      return (ISpeechData)CapabilityUtils.getCapability(entity, SPEECH_DATA, (EnumFacing)null);
   }

   @SideOnly(Side.CLIENT)
   @Nonnull
   public static ICapabilityProvider createProvider(ISpeechData data) {
      return new CapabilityProviderSerializable(SPEECH_DATA, (EnumFacing)null, data);
   }

   @SideOnly(Side.CLIENT)
   public static boolean isCandidate(@Nonnull Entity entity) {
      return entity instanceof EntityPlayer || entity instanceof EntityLiving;
   }

   @SideOnly(Side.CLIENT)
   public static boolean isCandidate(@Nullable Class<? extends Entity> clazz) {
      return clazz != null && (EntityPlayer.class.isAssignableFrom(clazz) || EntityLiving.class.isAssignableFrom(clazz));
   }

   @EventBusSubscriber(
      modid = "dsurround",
      value = {Side.CLIENT}
   )
   public static class EventHandler {
      public EventHandler() {
      }

      @SubscribeEvent
      public static void attachCapabilities(@Nonnull AttachCapabilitiesEvent<Entity> event) {
         World world = ((Entity)event.getObject()).getEntityWorld();
         if (world != null && world.isRemote && CapabilitySpeechData.isCandidate((Entity)event.getObject())) {
            SpeechData speechData = new SpeechData();
            event.addCapability(CapabilitySpeechData.CAPABILITY_ID, CapabilitySpeechData.createProvider(speechData));
         }

      }

      @SubscribeEvent
      public static void livingUpdate(@Nonnull LivingUpdateEvent event) {
         Entity entity = event.getEntity();
         World world = entity.getEntityWorld();
         if (world.isRemote && entity.ticksExisted % 5 == 0) {
            ISpeechData data = CapabilitySpeechData.getCapability(event.getEntity());
            if (data != null) {
               data.onUpdate(EnvironStateHandler.EnvironState.getTickCounter());
            }

         }
      }
   }
}
