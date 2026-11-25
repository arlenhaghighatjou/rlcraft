package svenhjol.charm.crafting.compat;

import net.minecraft.block.SoundType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class FutureMcSounds {
   public FutureMcSounds() {
   }

   public static SoundType getLanternSoundType() {
      SoundEvent lanternPlace = (SoundEvent)ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("futuremc", "lantern_place"));
      SoundEvent lanternBreak = (SoundEvent)ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("futuremc", "lantern_break"));
      return lanternPlace != null && lanternBreak != null ? new SoundType(1.0F, 1.0F, lanternBreak, lanternPlace, lanternPlace, lanternBreak, lanternPlace) : null;
   }
}
