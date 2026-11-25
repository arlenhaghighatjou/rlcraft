package org.orecruncher.dsurround.client.handlers;

import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.client.handlers.scanners.BiomeScanner;
import org.orecruncher.dsurround.client.sound.SoundEffect;
import org.orecruncher.dsurround.registry.RegistryManager;
import org.orecruncher.dsurround.registry.biome.BiomeInfo;
import org.orecruncher.lib.collections.ObjectArray;

@SideOnly(Side.CLIENT)
public class BiomeSoundEffectsHandler extends EffectHandlerBase {
   public static final int SCAN_INTERVAL = 4;
   protected final BiomeScanner biomes = new BiomeScanner();

   public BiomeSoundEffectsHandler() {
      super("Biome Sound Effects");
   }

   public boolean doTick(int tick) {
      return ModOptions.sound.enableBiomeSounds && tick % 4 == 0 && EnvironStateHandler.EnvironState.getDimensionInfo().playBiomeSounds() && EnvironStateHandler.EnvironState.getWorld().isBlockLoaded(EnvironStateHandler.EnvironState.getPlayerPosition());
   }

   private boolean doBiomeSounds() {
      return EnvironStateHandler.EnvironState.isPlayerUnderground() || EnvironStateHandler.EnvironState.getDimensionInfo().alwaysOutside() || !EnvironStateHandler.EnvironState.isPlayerInside();
   }

   private void getBiomeSounds(@Nonnull Object2FloatOpenHashMap<SoundEffect> result) {
      this.biomes.getBiomes().reference2FloatEntrySet().forEach((e) -> {
         ((BiomeInfo)e.getKey()).findSoundMatches().forEach((fx) -> {
            result.addTo(fx, e.getFloatValue());
         });
      });
      float area = (float)this.biomes.getBiomeArea();
      result.replaceAll((fx, v) -> {
         return (void)0.1F + 0.9F * (v.floatValue() / area);
      });
   }

   public void process(@Nonnull EntityPlayer player) {
      this.biomes.update();
      Object2FloatOpenHashMap<SoundEffect> sounds = new Object2FloatOpenHashMap();
      sounds.defaultReturnValue(0.0F);
      if (player.isEntityAlive()) {
         if (this.doBiomeSounds()) {
            this.getBiomeSounds(sounds);
         }

         ObjectArray<SoundEffect> playerSounds = new ObjectArray();
         RegistryManager.BIOME.PLAYER_INFO.findSoundMatches(playerSounds);
         RegistryManager.BIOME.BATTLE_MUSIC_INFO.findSoundMatches(playerSounds);
         if (EnvironStateHandler.EnvironState.inVillage()) {
            RegistryManager.BIOME.VILLAGE_INFO.findSoundMatches(playerSounds);
         }

         playerSounds.forEach((fx) -> {
            sounds.put(fx, 1.0F);
         });
         if (this.doBiomeSounds()) {
            BiomeInfo playerBiome = EnvironStateHandler.EnvironState.getPlayerBiome();
            SoundEffect sound = playerBiome.getSpotSound(this.RANDOM);
            if (sound != null) {
               SoundEffectHandler.INSTANCE.playSoundAtPlayer(player, sound);
            }
         }

         SoundEffect sound = RegistryManager.BIOME.PLAYER_INFO.getSpotSound(this.RANDOM);
         if (sound != null) {
            SoundEffectHandler.INSTANCE.playSoundAtPlayer(player, sound);
         }
      }

      SoundEffectHandler.INSTANCE.queueAmbientSounds(sounds);
   }
}
