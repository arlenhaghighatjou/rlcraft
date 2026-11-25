package org.orecruncher.dsurround.client.effects;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.sound.SoundEffect;
import org.orecruncher.dsurround.client.sound.SoundInstance;

@SideOnly(Side.CLIENT)
public class EventEffectLibrary extends EffectStateBase implements IEventEffectLibraryState {
   protected final List<EventEffect> effects = new ArrayList();

   public EventEffectLibrary(@Nonnull IParticleHelper ph, @Nonnull ISoundHelper sh) {
      super(ph, sh);
   }

   public void register(@Nonnull EventEffect effect) {
      effect.setState(this);
      this.effects.add(effect);
      MinecraftForge.EVENT_BUS.register(effect);
   }

   public void cleanup() {
      List var10000 = this.effects;
      EventBus var10001 = MinecraftForge.EVENT_BUS;
      MinecraftForge.EVENT_BUS.getClass();
      var10000.forEach(var10001::unregister);
      this.effects.clear();
   }

   public boolean isActivePlayer(@Nonnull Entity player) {
      EntityPlayer ep = this.thePlayer();
      return ep.getEntityId() == player.getEntityId();
   }

   @Nonnull
   public SoundInstance createSound(@Nonnull SoundEffect se, @Nonnull Entity player) {
      return this.isActivePlayer(player) ? se.createTrackingSound(player, false) : se.createSoundNear(player);
   }
}
