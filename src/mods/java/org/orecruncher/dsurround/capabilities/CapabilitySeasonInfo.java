package org.orecruncher.dsurround.capabilities;

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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.capabilities.season.ISeasonInfo;
import org.orecruncher.dsurround.capabilities.season.SeasonInfo;
import org.orecruncher.lib.capability.CapabilityProviderSerializable;
import org.orecruncher.lib.capability.CapabilityUtils;
import org.orecruncher.lib.capability.NullStorage;

public class CapabilitySeasonInfo {
   @CapabilityInject(ISeasonInfo.class)
   public static final Capability<ISeasonInfo> SEASON_INFO = null;
   public static final ResourceLocation CAPABILITY_ID = new ResourceLocation("dsurround", "seasoninfo");

   public CapabilitySeasonInfo() {
   }

   @SideOnly(Side.CLIENT)
   public static void register() {
      CapabilityManager.INSTANCE.register(ISeasonInfo.class, new NullStorage(), SeasonInfo::new);
   }

   @SideOnly(Side.CLIENT)
   @Nullable
   public static ISeasonInfo getCapability(@Nonnull World world) {
      return (ISeasonInfo)CapabilityUtils.getCapability(world, SEASON_INFO, (EnumFacing)null);
   }

   @SideOnly(Side.CLIENT)
   @Nonnull
   public static ICapabilityProvider createProvider(ISeasonInfo data) {
      return new CapabilityProviderSerializable(SEASON_INFO, (EnumFacing)null, data);
   }

   @EventBusSubscriber(
      modid = "dsurround",
      value = {Side.CLIENT}
   )
   public static class EventHandler {
      public EventHandler() {
      }

      @SubscribeEvent
      public static void attachCapabilities(@Nonnull AttachCapabilitiesEvent<World> event) {
         World world = (World)event.getObject();
         if (world != null && world.isRemote) {
            SeasonInfo info = SeasonInfo.factory(world);
            event.addCapability(CapabilitySeasonInfo.CAPABILITY_ID, CapabilitySeasonInfo.createProvider(info));
         }

      }
   }
}
