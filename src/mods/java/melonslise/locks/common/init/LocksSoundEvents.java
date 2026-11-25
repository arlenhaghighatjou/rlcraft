package melonslise.locks.common.init;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent.Register;

public final class LocksSoundEvents {
   private static final List<SoundEvent> SOUNDS = new ArrayList(6);
   public static final SoundEvent KEY_RING = add("key_ring");
   public static final SoundEvent LOCK_CLOSE = add("lock.close");
   public static final SoundEvent LOCK_OPEN = add("lock.open");
   public static final SoundEvent LOCK_RATTLE = add("lock.rattle");
   public static final SoundEvent PIN_FAIL = add("pin.fail");
   public static final SoundEvent PIN_MATCH = add("pin.match");
   public static final SoundEvent SHOCK = add("shock");

   private LocksSoundEvents() {
   }

   public static void register(Register<SoundEvent> event) {
      for(SoundEvent sound : SOUNDS) {
         event.getRegistry().register(sound);
      }

   }

   public static SoundEvent add(String name) {
      ResourceLocation rl = new ResourceLocation("locks", name);
      SoundEvent sound = (SoundEvent)(new SoundEvent(rl)).setRegistryName(rl);
      SOUNDS.add(sound);
      return sound;
   }
}
