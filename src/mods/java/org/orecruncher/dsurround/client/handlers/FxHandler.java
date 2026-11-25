package org.orecruncher.dsurround.client.handlers;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.capabilities.CapabilityEntityFXData;
import org.orecruncher.dsurround.capabilities.entityfx.IEntityFX;
import org.orecruncher.dsurround.client.effects.EntityEffectHandler;
import org.orecruncher.dsurround.client.effects.EntityEffectLibrary;
import org.orecruncher.dsurround.client.effects.EventEffectLibrary;
import org.orecruncher.dsurround.client.effects.IParticleHelper;
import org.orecruncher.dsurround.client.effects.ISoundHelper;
import org.orecruncher.dsurround.client.handlers.effects.BreathEffect;
import org.orecruncher.dsurround.client.handlers.effects.CraftingSoundEffect;
import org.orecruncher.dsurround.client.handlers.effects.EntityBowSoundEffect;
import org.orecruncher.dsurround.client.handlers.effects.EntityChatEffect;
import org.orecruncher.dsurround.client.handlers.effects.EntityFootprintEffect;
import org.orecruncher.dsurround.client.handlers.effects.EntityHealthPopoffEffect;
import org.orecruncher.dsurround.client.handlers.effects.EntitySwingEffect;
import org.orecruncher.dsurround.client.handlers.effects.PlayerToolBarSoundEffect;
import org.orecruncher.dsurround.client.handlers.effects.VillagerChatEffect;
import org.orecruncher.dsurround.client.sound.ISoundInstance;
import org.orecruncher.dsurround.event.DiagnosticEvent;
import org.orecruncher.dsurround.lib.OutOfBandTimerEMA;
import org.orecruncher.dsurround.registry.RegistryDataEvent;
import org.orecruncher.dsurround.registry.effect.EffectRegistry;
import org.orecruncher.lib.gfx.ParticleHelper;
import org.orecruncher.lib.math.TimerEMA;

@SideOnly(Side.CLIENT)
public class FxHandler extends EffectHandlerBase {
   private static final IParticleHelper PARTICLE_HELPER = ParticleHelper::addParticle;
   private static final ISoundHelper SOUND_HELPER = new ISoundHelper() {
      public boolean playSound(@Nonnull ISoundInstance sound) {
         return SoundEffectHandler.INSTANCE.playSound(sound);
      }

      public void stopSound(@Nonnull ISoundInstance sound) {
         SoundEffectHandler.INSTANCE.stopSound(sound);
      }
   };
   private static final EntityEffectLibrary library = new EntityEffectLibrary(PARTICLE_HELPER, SOUND_HELPER);
   private final EventEffectLibrary eventLibrary;
   private final TimerEMA compute;
   private long nanos;

   public FxHandler() {
      super("Special Effects");
      this.eventLibrary = new EventEffectLibrary(PARTICLE_HELPER, SOUND_HELPER);
      this.compute = new OutOfBandTimerEMA("Entity Effect Updates");
   }

   public void process(@Nonnull EntityPlayer player) {
      this.compute.update((double)this.nanos);
      this.nanos = 0L;
   }

   public List<String> getEffects(@Nonnull Entity entity) {
      IEntityFX caps = CapabilityEntityFXData.getCapability(entity);
      if (caps != null) {
         EntityEffectHandler eh = caps.get();
         if (eh != null) {
            return eh.getAttachedEffects();
         }
      }

      return ImmutableList.of();
   }

   @SubscribeEvent(
      receiveCanceled = true
   )
   public void onLivingUpdate(@Nonnull LivingUpdateEvent event) {
      Entity entity = event.getEntity();
      if (entity != null && entity.getEntityWorld().isRemote) {
         long start = System.nanoTime();
         IEntityFX cap = CapabilityEntityFXData.getCapability(entity);
         if (cap != null) {
            double distanceThreshold = (double)(ModOptions.effects.specialEffectRange * ModOptions.effects.specialEffectRange);
            boolean inRange = entity.getDistanceSq(EnvironStateHandler.EnvironState.getPlayer()) <= distanceThreshold;
            EntityEffectHandler handler = cap.get();
            if (handler != null && !inRange) {
               cap.clear();
            } else if (handler == null && inRange && entity.isEntityAlive()) {
               library.create(entity).ifPresent(cap::set);
            } else if (handler != null) {
               handler.update();
            }
         }

         this.nanos += System.nanoTime() - start;
      }
   }

   protected void clearHandlers() {
      EnvironStateHandler.EnvironState.getWorld().getLoadedEntityList().stream().map(CapabilityEntityFXData::getCapability).filter(Objects::nonNull).forEach(IEntityFX::clear);
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void onEntityJoin(@Nonnull EntityJoinWorldEvent event) {
      if (event.getWorld().isRemote && event.getEntity() instanceof EntityPlayerSP) {
         this.clearHandlers();
      }

   }

   @SubscribeEvent
   public void registryReload(@Nonnull RegistryDataEvent.Reload event) {
      if (event.reg instanceof EffectRegistry) {
         this.clearHandlers();
      }

   }

   public void onConnect() {
      this.eventLibrary.register(new CraftingSoundEffect());
      ((DiagnosticHandler)EffectManager.instance().lookupService(DiagnosticHandler.class)).addTimer(this.compute);
   }

   public void onDisconnect() {
      this.eventLibrary.cleanup();
   }

   @SubscribeEvent
   public void diagnostic(@Nonnull DiagnosticEvent.Gather event) {
      double range = (double)ModOptions.effects.specialEffectRange;
      BlockPos min = EnvironStateHandler.EnvironState.getPlayerPosition().add(-range, -range, -range);
      BlockPos max = EnvironStateHandler.EnvironState.getPlayerPosition().add(range, range, range);
      AxisAlignedBB box = new AxisAlignedBB(min, max);
      int count = EnvironStateHandler.EnvironState.getWorld().getEntitiesWithinAABBExcludingEntity(EnvironStateHandler.EnvironState.getPlayer(), box).size() + 1;
      event.output.add("Effected Entities: " + count);
   }

   static {
      library.register(BreathEffect.DEFAULT_FILTER, new BreathEffect.Factory());
      library.register(EntityChatEffect.DEFAULT_FILTER, new EntityChatEffect.Factory());
      library.register(VillagerChatEffect.DEFAULT_FILTER, new VillagerChatEffect.Factory());
      library.register(PlayerToolBarSoundEffect.DEFAULT_FILTER, new PlayerToolBarSoundEffect.Factory());
      library.register(EntityFootprintEffect.DEFAULT_FILTER, new EntityFootprintEffect.Factory());
      library.register(EntitySwingEffect.DEFAULT_FILTER, new EntitySwingEffect.Factory());
      library.register(EntityBowSoundEffect.DEFAULT_FILTER, new EntityBowSoundEffect.Factory());
      library.register(EntityHealthPopoffEffect.DEFAULT_FILTER, new EntityHealthPopoffEffect.Factory());
   }
}
