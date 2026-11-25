package com.elenai.elenaidodge.capability;

import com.elenai.elenaidodge.capability.airborne.AirborneProvider;
import com.elenai.elenaidodge.capability.airborne.IAirborne;
import com.elenai.elenaidodge.capability.enabled.EnabledProvider;
import com.elenai.elenaidodge.capability.enabled.IEnabled;
import com.elenai.elenaidodge.capability.invincibility.InvincibilityProvider;
import com.elenai.elenaidodge.capability.joined.IJoined;
import com.elenai.elenaidodge.capability.joined.JoinedProvider;
import com.elenai.elenaidodge.capability.ledgegrabcooldown.LedgeGrabCooldownProvider;
import com.elenai.elenaidodge.capability.ledgegrabs.LedgeGrabsProvider;
import com.elenai.elenaidodge.capability.particles.ParticlesProvider;
import com.elenai.elenaidodge.capability.regen.RegenProvider;
import com.elenai.elenaidodge.capability.walljumpcooldown.WallJumpCooldownProvider;
import com.elenai.elenaidodge.capability.walljumps.WallJumpsProvider;
import com.elenai.elenaidodge.capability.weight.WeightProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CapabilityHandler {
   public static final ResourceLocation JOINED_CAP = new ResourceLocation("elenaidodge", "joined");
   public static final ResourceLocation WEIGHT_CAP = new ResourceLocation("elenaidodge", "weight");
   public static final ResourceLocation INVINCIBILITY_CAP = new ResourceLocation("elenaidodge", "invincibility");
   public static final ResourceLocation PARTICLES_CAP = new ResourceLocation("elenaidodge", "particles");
   public static final ResourceLocation REGEN_CAP = new ResourceLocation("elenaidodge", "regen");
   public static final ResourceLocation ENABLED_CAP = new ResourceLocation("elenaidodge", "enabled");
   public static final ResourceLocation AIRBORNE_CAP = new ResourceLocation("elenaidodge", "airborne");
   public static final ResourceLocation WALLJUMPS_CAP = new ResourceLocation("elenaidodge", "walljumps");
   public static final ResourceLocation LEDGEGRABS_CAP = new ResourceLocation("elenaidodge", "ledgegrabs");
   public static final ResourceLocation LEDGEGRABCOOLDOWN_CAP = new ResourceLocation("elenaidodge", "ledgegrabcooldown");
   public static final ResourceLocation WALLJUMPCOOLDOWN_CAP = new ResourceLocation("elenaidodge", "walljumpcooldown");

   public CapabilityHandler() {
   }

   @SubscribeEvent
   public void onEntityConstructing(AttachCapabilitiesEvent<Entity> event) {
      if (event.getObject() instanceof EntityPlayer) {
         if (!((Entity)event.getObject()).hasCapability(JoinedProvider.JOINED_CAP, (EnumFacing)null)) {
            event.addCapability(JOINED_CAP, new JoinedProvider());
         }

         if (!((Entity)event.getObject()).hasCapability(WeightProvider.WEIGHT_CAP, (EnumFacing)null)) {
            event.addCapability(WEIGHT_CAP, new WeightProvider());
         }

         if (!((Entity)event.getObject()).hasCapability(InvincibilityProvider.INVINCIBILITY_CAP, (EnumFacing)null)) {
            event.addCapability(INVINCIBILITY_CAP, new InvincibilityProvider());
         }

         if (!((Entity)event.getObject()).hasCapability(ParticlesProvider.PARTICLES_CAP, (EnumFacing)null)) {
            event.addCapability(PARTICLES_CAP, new ParticlesProvider());
         }

         if (!((Entity)event.getObject()).hasCapability(RegenProvider.REGEN_CAP, (EnumFacing)null)) {
            event.addCapability(REGEN_CAP, new RegenProvider());
         }

         if (!((Entity)event.getObject()).hasCapability(EnabledProvider.ENABLED_CAP, (EnumFacing)null)) {
            event.addCapability(ENABLED_CAP, new EnabledProvider());
         }

         if (!((Entity)event.getObject()).hasCapability(AirborneProvider.AIRBORNE_CAP, (EnumFacing)null)) {
            event.addCapability(AIRBORNE_CAP, new AirborneProvider());
         }

         if (!((Entity)event.getObject()).hasCapability(WallJumpsProvider.WALLJUMPS_CAP, (EnumFacing)null)) {
            event.addCapability(WALLJUMPS_CAP, new WallJumpsProvider());
         }

         if (!((Entity)event.getObject()).hasCapability(LedgeGrabsProvider.LEDGEGRABS_CAP, (EnumFacing)null)) {
            event.addCapability(LEDGEGRABS_CAP, new LedgeGrabsProvider());
         }

         if (!((Entity)event.getObject()).hasCapability(LedgeGrabCooldownProvider.LEDGEGRABCOOLDOWN_CAP, (EnumFacing)null)) {
            event.addCapability(LEDGEGRABCOOLDOWN_CAP, new LedgeGrabCooldownProvider());
         }

         if (!((Entity)event.getObject()).hasCapability(WallJumpCooldownProvider.WALLJUMPCOOLDOWN_CAP, (EnumFacing)null)) {
            event.addCapability(WALLJUMPCOOLDOWN_CAP, new WallJumpCooldownProvider());
         }
      }

   }

   @SubscribeEvent
   public void onPlayerCloned(Clone event) {
      EntityPlayer player = event.getEntityPlayer();
      IJoined joined = (IJoined)player.getCapability(JoinedProvider.JOINED_CAP, (EnumFacing)null);
      IJoined oldJoined = (IJoined)event.getOriginal().getCapability(JoinedProvider.JOINED_CAP, (EnumFacing)null);
      joined.set(oldJoined.hasJoined());
      IEnabled enabled = (IEnabled)player.getCapability(EnabledProvider.ENABLED_CAP, (EnumFacing)null);
      IEnabled oldEnabled = (IEnabled)event.getOriginal().getCapability(EnabledProvider.ENABLED_CAP, (EnumFacing)null);
      enabled.set(oldEnabled.isEnabled());
      IAirborne airborne = (IAirborne)player.getCapability(AirborneProvider.AIRBORNE_CAP, (EnumFacing)null);
      IAirborne oldAirborne = (IAirborne)event.getOriginal().getCapability(AirborneProvider.AIRBORNE_CAP, (EnumFacing)null);
      airborne.set(oldAirborne.isEnabled());
   }
}
