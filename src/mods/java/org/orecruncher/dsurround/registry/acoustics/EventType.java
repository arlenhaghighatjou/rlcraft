package org.orecruncher.dsurround.registry.acoustics;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public enum EventType {
   WALK((EventType)null),
   WANDER((EventType)null),
   SWIM((EventType)null),
   RUN(WALK),
   JUMP(WANDER),
   LAND(RUN),
   CLIMB(WALK),
   CLIMB_RUN(RUN),
   DOWN(WALK),
   DOWN_RUN(RUN),
   UP(WALK),
   UP_RUN(RUN);

   private final EventType destination;
   private final String jsonName;

   private EventType(@Nullable EventType dest) {
      this.destination = dest;
      this.jsonName = this.name().toLowerCase();
   }

   @Nonnull
   public String jsonName() {
      return this.jsonName;
   }

   public boolean canTransition() {
      return this.destination != null;
   }

   public EventType getTransitionDestination() {
      return this.destination;
   }
}
