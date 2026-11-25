package org.orecruncher.dsurround.capabilities;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import java.util.concurrent.Callable;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.capabilities.dimension.DimensionInfo;
import org.orecruncher.dsurround.capabilities.dimension.IDimensionInfo;
import org.orecruncher.lib.capability.CapabilityProviderSerializable;
import org.orecruncher.lib.capability.CapabilityUtils;
import org.orecruncher.lib.capability.SimpleStorage;

public class CapabilityDimensionInfo {
   @CapabilityInject(IDimensionInfo.class)
   public static final Capability<IDimensionInfo> DIMENSION_INFO = null;
   public static final ResourceLocation CAPABILITY_ID = new ResourceLocation("dsurround", "dimensioninfo");

   public CapabilityDimensionInfo() {
   }

   public static void register() {
      CapabilityManager.INSTANCE.register(IDimensionInfo.class, new SimpleStorage(), DimensionInfo::new);
   }

   @Nullable
   public static IDimensionInfo getCapability(@Nonnull World world) {
      return (IDimensionInfo)CapabilityUtils.getCapability(world, DIMENSION_INFO, (EnumFacing)null);
   }

   @Nonnull
   public static ICapabilityProvider createProvider(IDimensionInfo data) {
      return new CapabilityProviderSerializable(DIMENSION_INFO, (EnumFacing)null, data);
   }

   @EventBusSubscriber(
      modid = "dsurround"
   )
   public static class EventHandler {
      public EventHandler() {
      }

      @SubscribeEvent
      public static void attachCapabilities(@Nonnull AttachCapabilitiesEvent<World> event) {
         if (!ModBase.isInitialized()) {
            ModBase.log().debug(4, "Attempt to attach world capability before mod is initialized - silly fake worlds.", new Object[0]);
         } else {
            World world = (World)event.getObject();
            if (world != null) {
               String side = ModBase.proxy().effectiveSide().toString();
               ModBase.log().debug(4, "Attaching capabilities to world [%s] (%s)", new Object[]{world.provider.getDimensionType().getName(), side});
               DimensionInfo info = new DimensionInfo(world);
               event.addCapability(CapabilityDimensionInfo.CAPABILITY_ID, CapabilityDimensionInfo.createProvider(info));
               ToStringHelper builder = MoreObjects.toStringHelper(info);
               builder.add("id", info.getId());
               builder.add("name", info.getName());
               builder.add("seaLevel", info.getSeaLevel());
               builder.add("cloudHeight", info.getCloudHeight());
               builder.add("skyHeight", info.getSkyHeight());
               builder.add("haze", info.hasHaze());
               builder.add("aurora", info.hasAuroras());
               builder.add("weather", info.hasWeather());
               builder.add("fog", info.hasFog());
               ModBase.log().debug(builder.toString(), new Object[0]);
            }

         }
      }
   }
}
