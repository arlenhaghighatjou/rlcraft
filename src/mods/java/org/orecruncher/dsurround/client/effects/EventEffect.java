package org.orecruncher.dsurround.client.effects;

import javax.annotation.Nonnull;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class EventEffect {
   private IEventEffectLibraryState library;

   public EventEffect() {
   }

   void setState(@Nonnull IEventEffectLibraryState state) {
      this.library = state;
   }

   protected IEventEffectLibraryState getState() {
      return this.library;
   }

   protected boolean isClientValid(@Nonnull PlayerEvent event) {
      return event.player != null ? event.player.getEntityWorld().isRemote : false;
   }
}
