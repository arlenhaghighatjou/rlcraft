package org.orecruncher.dsurround.registry.acoustics;

import java.util.EnumMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EventSelectorAcoustics implements IAcoustic {
   private final String name;
   private final Map<EventType, IAcoustic> pairs = new EnumMap(EventType.class);

   public EventSelectorAcoustics(@Nonnull String acousticName) {
      this.name = acousticName;
   }

   @Nonnull
   public String getName() {
      return this.name;
   }

   public void playSound(@Nonnull ISoundPlayer player, @Nonnull Vec3d location, @Nullable EventType event, @Nullable IOptions inputOptions) {
      IAcoustic acoustic = (IAcoustic)this.pairs.get(event);
      if (acoustic != null) {
         acoustic.playSound(player, location, event, inputOptions);
      } else if (event != null && event.canTransition()) {
         this.playSound(player, location, event.getTransitionDestination(), inputOptions);
      }

   }

   public void setAcousticPair(@Nonnull EventType type, @Nonnull IAcoustic acoustic) {
      this.pairs.put(type, acoustic);
   }

   public String toString() {
      return this.getName();
   }
}
