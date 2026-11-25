package org.orecruncher.dsurround.capabilities;

import java.util.concurrent.Callable;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
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
import org.orecruncher.dsurround.capabilities.entityfx.EntityFXData;
import org.orecruncher.dsurround.capabilities.entityfx.IEntityFX;
import org.orecruncher.lib.capability.CapabilityProviderSerializable;
import org.orecruncher.lib.capability.CapabilityUtils;
import org.orecruncher.lib.capability.NullStorage;

public class CapabilityEntityFXData {
   @CapabilityInject(IEntityFX.class)
   public static final Capability<IEntityFX> FX_INFO = null;
   public static final ResourceLocation CAPABILITY_ID = new ResourceLocation("dsurround", "entityfx");

   public CapabilityEntityFXData() {
   }

   @SideOnly(Side.CLIENT)
   public static void register() {
      CapabilityManager.INSTANCE.register(IEntityFX.class, new NullStorage(), EntityFXData::new);
   }

   @SideOnly(Side.CLIENT)
   @Nullable
   public static IEntityFX getCapability(@Nonnull Entity entity) {
      return (IEntityFX)CapabilityUtils.getCapability(entity, FX_INFO, (EnumFacing)null);
   }

   @SideOnly(Side.CLIENT)
   @Nonnull
   public static ICapabilityProvider createProvider(IEntityFX data) {
      return new CapabilityProviderSerializable(FX_INFO, (EnumFacing)null, data);
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
         if (world != null && world.isRemote && event.getObject() instanceof EntityLivingBase) {
            EntityFXData info = new EntityFXData();
            event.addCapability(CapabilityEntityFXData.CAPABILITY_ID, CapabilityEntityFXData.createProvider(info));
         }

      }
   }
}
