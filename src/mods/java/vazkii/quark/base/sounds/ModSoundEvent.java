package vazkii.quark.base.sounds;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class ModSoundEvent extends SoundEvent {
   public static final List<ModSoundEvent> allEvents = Lists.newArrayList();

   public ModSoundEvent(String name) {
      this(new ResourceLocation("quark", name));
   }

   public ModSoundEvent(ResourceLocation soundName) {
      super(soundName);
      this.setRegistryName(soundName);
      allEvents.add(this);
   }
}
