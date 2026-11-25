package vazkii.quark.world.feature;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.audio.GuardianSound;
import net.minecraft.client.audio.ISound;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.module.Feature;
import vazkii.quark.world.client.sound.GuardianSound2UnderwaterBoogaloo;

public class OceanGuardians extends Feature {
   public static boolean deepOceanOnly;
   public static int weight;
   public static int min;
   public static int max;
   public static boolean tweakSound;

   public OceanGuardians() {
   }

   public void setupConfig() {
      deepOceanOnly = this.loadPropBool("Deep ocean only", "", true);
      weight = this.loadPropInt("Spawn Weight", "Squids have 10. The higher, the more will spawn. (Note that guardians only spawn 5% of the time, so the default 4 is actually 0.8)", 4);
      min = this.loadPropInt("Smallest spawn group", "", 1);
      max = this.loadPropInt("Largest spawn group", "", 3);
      tweakSound = this.loadPropBool("Shut Guardians Up", "Disables guardians' attack sound if they aren't attacking a player", true);
   }

   public void init() {
      for(Biome b : deepOceanOnly ? ImmutableSet.of(Biomes.DEEP_OCEAN) : ImmutableSet.of(Biomes.OCEAN, Biomes.DEEP_OCEAN)) {
         b.getSpawnableList(EnumCreatureType.MONSTER).add(new SpawnListEntry(EntityGuardian.class, weight, min, max));
      }

   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void onSound(PlaySoundEvent event) {
      if (tweakSound) {
         ISound sound = event.getSound();
         if (sound instanceof GuardianSound) {
            GuardianSound guardianSound = (GuardianSound)sound;
            EntityGuardian guardian = (EntityGuardian)ObfuscationReflectionHelper.getPrivateValue(GuardianSound.class, guardianSound, "guardian");
            event.setResultSound(new GuardianSound2UnderwaterBoogaloo(guardian));
         }

      }
   }

   public boolean hasSubscriptions() {
      return this.isClient();
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
