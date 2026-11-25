package org.orecruncher.dsurround.client.fx;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent.Unload;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.client.fx.particle.mote.IParticleMote;
import org.orecruncher.dsurround.client.fx.particle.mote.MoteFireFly;
import org.orecruncher.dsurround.client.fx.particle.mote.MoteFootprint;
import org.orecruncher.dsurround.client.fx.particle.mote.MoteRainSplash;
import org.orecruncher.dsurround.client.fx.particle.mote.MoteWaterRipple;
import org.orecruncher.dsurround.client.fx.particle.mote.MoteWaterSpray;
import org.orecruncher.dsurround.client.fx.particle.mote.ParticleCollectionFireFly;
import org.orecruncher.dsurround.client.fx.particle.mote.ParticleCollectionFootprint;
import org.orecruncher.dsurround.client.fx.particle.mote.ParticleCollectionRipples;
import org.orecruncher.dsurround.event.DiagnosticEvent;
import org.orecruncher.dsurround.registry.footstep.FootprintStyle;

@SideOnly(Side.CLIENT)
public final class ParticleCollections {
   private static final ResourceLocation RIPPLE_TEXTURE = new ResourceLocation("dsurround", "textures/particles/ripple.png");
   private static final ResourceLocation SPRAY_TEXTURE = new ResourceLocation("dsurround", "textures/particles/rainsplash.png");
   private static final ResourceLocation FOOTPRINT_TEXTURE = new ResourceLocation("dsurround", "textures/particles/footprint.png");
   private static final ResourceLocation FIREFLY_TEXTURE = new ResourceLocation("textures/particle/particles.png");
   private static final CollectionHelper theRipples = new CollectionHelper("Rain Ripples", ParticleCollectionRipples.FACTORY, RIPPLE_TEXTURE);
   private static final CollectionHelper theSprays = new CollectionHelper("Water Spray", SPRAY_TEXTURE);
   private static final CollectionHelper thePrints = new CollectionHelper("Footprints", ParticleCollectionFootprint.FACTORY, FOOTPRINT_TEXTURE);
   private static final CollectionHelper theFireFlies = new LightedCollectionHelper("Fireflies", ParticleCollectionFireFly.FACTORY, FIREFLY_TEXTURE);

   public ParticleCollections() {
   }

   @Nullable
   public static IParticleMote addWaterRipple(@Nonnull World world, double x, double y, double z) {
      IParticleMote mote = null;
      if (theRipples.get().canFit()) {
         mote = new MoteWaterRipple(world, x, y, z);
         theRipples.get().addParticle(mote);
      }

      return mote;
   }

   @Nullable
   public static IParticleMote addWaterSpray(@Nonnull World world, double x, double y, double z, double dX, double dY, double dZ) {
      IParticleMote mote = null;
      if (theSprays.get().canFit()) {
         mote = new MoteWaterSpray(world, x, y, z, dX, dY, dZ);
         theSprays.get().addParticle(mote);
      }

      return mote;
   }

   public static boolean canFitWaterSpray() {
      return theSprays.get().canFit();
   }

   @Nullable
   public static IParticleMote addRainSplash(@Nonnull World world, double x, double y, double z) {
      IParticleMote mote = null;
      if (theSprays.get().canFit()) {
         mote = new MoteRainSplash(world, x, y, z);
         theSprays.get().addParticle(mote);
      }

      return mote;
   }

   @Nullable
   public static IParticleMote addFootprint(@Nonnull FootprintStyle style, @Nonnull World world, Vec3d loc, float rot, float scale, boolean isRight) {
      IParticleMote mote = null;
      if (thePrints.get().canFit()) {
         mote = new MoteFootprint(style, world, loc.x, loc.y, loc.z, rot, scale, isRight);
         thePrints.get().addParticle(mote);
      }

      return mote;
   }

   public static void addFireFly(@Nonnull World world, double x, double y, double z) {
      if (theFireFlies.get().canFit()) {
         IParticleMote mote = new MoteFireFly(world, x, y, z);
         theFireFlies.get().addParticle(mote);
      }

   }

   @SubscribeEvent
   public static void onWorldUnload(@Nonnull Unload event) {
      if (event.getWorld() instanceof WorldClient) {
         ModBase.log().debug("World [%s] unloading, clearing particle collections", new Object[]{event.getWorld().provider.getDimensionType().getName()});
         theRipples.clear();
         theSprays.clear();
         thePrints.clear();
         theFireFlies.clear();
      }

   }

   @SubscribeEvent
   public static void diagnostics(@Nonnull DiagnosticEvent.Gather event) {
      event.output.add(TextFormatting.AQUA + thePrints.toString());
      event.output.add(TextFormatting.AQUA + theRipples.toString());
      event.output.add(TextFormatting.AQUA + theSprays.toString());
      event.output.add(TextFormatting.AQUA + theFireFlies.toString());
   }
}
