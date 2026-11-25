package org.orecruncher.lib;

import javax.annotation.Nonnull;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public abstract class EventMonitor<T extends Event> {
   public EventMonitor() {
   }

   public EventMonitor<T> register() {
      MinecraftForge.EVENT_BUS.register(this);
      return this;
   }

   public EventMonitor<T> unregister() {
      MinecraftForge.EVENT_BUS.unregister(this);
      return this;
   }

   @SubscribeEvent
   protected void onEvent(@Nonnull T evt) {
      this.handler(evt);
   }

   public abstract void handler(@Nonnull T var1);
}
