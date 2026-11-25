package org.orecruncher.dsurround.registry.biome;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.TempCategory;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;
import org.orecruncher.dsurround.capabilities.season.TemperatureRating;
import org.orecruncher.dsurround.client.sound.SoundEffect;
import org.orecruncher.dsurround.registry.RegistryManager;
import org.orecruncher.dsurround.registry.config.BiomeConfig;
import org.orecruncher.dsurround.registry.config.SoundConfig;
import org.orecruncher.dsurround.registry.config.SoundType;
import org.orecruncher.lib.Color;
import org.orecruncher.lib.MyUtils;
import org.orecruncher.lib.WeightTable;
import org.orecruncher.lib.collections.ObjectArray;

@SideOnly(Side.CLIENT)
public final class BiomeInfo implements Comparable<BiomeInfo> {
   private static final float DEFAULT_FOG_DENSITY = 0.4F;
   private static final Color DEFAULT_FOG_COLOR = (new Color(64, 96, 64)).asImmutable();
   private static final Color DEFAULT_DUST_COLOR = (new Color(255, 234, 151)).asImmutable();
   public static final int DEFAULT_SPOT_CHANCE = 250;
   public static final SoundEffect[] NO_SOUNDS = new SoundEffect[0];
   protected final IBiome biome;
   protected boolean hasPrecipitation;
   protected boolean hasDust;
   protected boolean hasAurora;
   protected boolean hasFog;
   private Color dustColor;
   private Color fogColor;
   private float fogDensity;
   protected SoundEffect[] sounds;
   protected SoundEffect[] spotSounds;
   protected int spotSoundChance;
   protected final List<String> comments;
   protected final boolean isRiver;
   protected final boolean isOcean;
   protected final boolean isDeepOcean;
   protected final String traits;

   public BiomeInfo(@Nonnull IBiome biome) {
      this.dustColor = DEFAULT_DUST_COLOR;
      this.fogColor = DEFAULT_FOG_COLOR;
      this.fogDensity = 0.4F;
      this.sounds = NO_SOUNDS;
      this.spotSounds = NO_SOUNDS;
      this.spotSoundChance = 250;
      this.comments = Lists.newArrayList();
      this.biome = biome;
      if (!this.isFake()) {
         this.hasPrecipitation = this.canRain() || this.getEnableSnow();
      }

      if (!biome.isFake()) {
         Biome b = biome.getBiome();
         if (BiomeUtil.isBoPBiome(b)) {
            int color = BiomeUtil.getBoPBiomeFogColor(b);
            if (color > 0) {
               this.hasFog = true;
               this.fogColor = new Color(color);
               this.fogDensity = BiomeUtil.getBoPBiomeFogDensity(b);
            }
         }
      }

      this.isRiver = this.biome.getTypes().contains(Type.RIVER);
      this.isOcean = this.biome.getTypes().contains(Type.OCEAN);
      this.isDeepOcean = this.isOcean && this.getBiomeName().matches("(?i).*deep.*ocean.*|.*abyss.*");
      this.traits = (String)this.getBiomeTypes().stream().map(Type::getName).collect(Collectors.joining(" "));
   }

   public boolean isRiver() {
      return this.isRiver;
   }

   public boolean isOcean() {
      return this.isOcean;
   }

   public boolean isDeepOcean() {
      return this.isDeepOcean;
   }

   public ResourceLocation getKey() {
      return this.biome.getKey();
   }

   public int getBiomeId() {
      return this.biome.getId();
   }

   public Biome getBiome() {
      return this.biome.getBiome();
   }

   public Set<Type> getBiomeTypes() {
      return this.biome.getTypes();
   }

   public String getBiomeTraits() {
      return this.traits;
   }

   void addComment(@Nonnull String comment) {
      if (!StringUtils.isEmpty(comment)) {
         this.comments.add(comment);
      }

   }

   public List<String> getComments() {
      return this.comments;
   }

   public String getBiomeName() {
      return this.biome.getName();
   }

   public boolean hasWeatherEffect() {
      return this.getHasPrecipitation() || this.getHasDust();
   }

   public boolean getHasPrecipitation() {
      return this.hasPrecipitation;
   }

   public boolean canRain() {
      return this.biome.canRain();
   }

   public boolean getEnableSnow() {
      return this.biome.getEnableSnow();
   }

   void setHasPrecipitation(boolean flag) {
      this.hasPrecipitation = flag;
   }

   public boolean getHasDust() {
      return this.hasDust;
   }

   void setHasDust(boolean flag) {
      this.hasDust = flag;
   }

   public boolean getHasAurora() {
      return this.hasAurora;
   }

   void setHasAurora(boolean flag) {
      this.hasAurora = flag;
   }

   public boolean getHasFog() {
      return this.hasFog;
   }

   void setHasFog(boolean flag) {
      this.hasFog = flag;
   }

   public Color getDustColor() {
      return this.dustColor;
   }

   void setDustColor(Color color) {
      this.dustColor = color;
   }

   public Color getFogColor() {
      return this.fogColor;
   }

   void setFogColor(@Nonnull Color color) {
      this.fogColor = color;
   }

   public float getFogDensity() {
      return this.fogDensity;
   }

   void setFogDensity(float density) {
      this.fogDensity = density;
   }

   void setSpotSoundChance(int chance) {
      this.spotSoundChance = chance;
   }

   void addSound(SoundEffect sound) {
      this.sounds = (SoundEffect[])MyUtils.append(this.sounds, sound);
   }

   void addSpotSound(SoundEffect sound) {
      this.spotSounds = (SoundEffect[])MyUtils.append(this.spotSounds, sound);
   }

   public boolean isFake() {
      return this.biome instanceof FakeBiome;
   }

   public float getFloatTemperature(@Nonnull BlockPos pos) {
      return this.biome.getFloatTemperature(pos);
   }

   public float getTemperature() {
      return this.biome.getTemperature();
   }

   public TempCategory getTempCategory() {
      return this.biome.getTempCategory();
   }

   public TemperatureRating getTemperatureRating() {
      return TemperatureRating.fromTemp(this.getTemperature());
   }

   public boolean isHighHumidity() {
      return this.biome.isHighHumidity();
   }

   public float getRainfall() {
      return this.biome.getRainfall();
   }

   @Nonnull
   public Collection<SoundEffect> findSoundMatches() {
      return this.findSoundMatches(new ObjectArray(8));
   }

   @Nonnull
   public Collection<SoundEffect> findSoundMatches(@Nonnull Collection<SoundEffect> results) {
      for(SoundEffect sound : this.sounds) {
         if (sound.matches()) {
            results.add(sound);
         }
      }

      return results;
   }

   @Nullable
   public SoundEffect getSpotSound(@Nonnull Random random) {
      return this.spotSounds != NO_SOUNDS && random.nextInt(this.spotSoundChance) == 0 ? (SoundEffect)(new WeightTable(this.spotSounds)).next() : null;
   }

   void resetSounds() {
      this.sounds = NO_SOUNDS;
      this.spotSounds = NO_SOUNDS;
      this.spotSoundChance = 250;
   }

   public boolean isBiomeType(@Nonnull Type type) {
      return this.getBiomeTypes().contains(type);
   }

   public boolean areBiomesSameClass(@Nonnull Biome biome) {
      return BiomeUtil.areBiomesSimilar(this.biome.getBiome(), biome);
   }

   public void update(@Nonnull BiomeConfig entry) {
      this.addComment(entry.comment);
      if (entry.hasPrecipitation != null) {
         this.setHasPrecipitation(entry.hasPrecipitation.booleanValue());
      }

      if (entry.hasAurora != null) {
         this.setHasAurora(entry.hasAurora.booleanValue());
      }

      if (entry.hasDust != null) {
         this.setHasDust(entry.hasDust.booleanValue());
      }

      if (entry.hasFog != null) {
         this.setHasFog(entry.hasFog.booleanValue());
      }

      if (entry.fogDensity != null) {
         this.setFogDensity(entry.fogDensity.floatValue());
      }

      if (entry.fogColor != null) {
         int[] rgb = MyUtils.splitToInts(entry.fogColor, ',');
         if (rgb.length == 3) {
            this.setFogColor(new Color(rgb[0], rgb[1], rgb[2]));
         }
      }

      if (entry.dustColor != null) {
         int[] rgb = MyUtils.splitToInts(entry.dustColor, ',');
         if (rgb.length == 3) {
            this.setDustColor(new Color(rgb[0], rgb[1], rgb[2]));
         }
      }

      if (entry.soundReset != null && entry.soundReset.booleanValue()) {
         this.addComment("> Sound Reset");
         this.resetSounds();
      }

      if (entry.spotSoundChance != null) {
         this.setSpotSoundChance(entry.spotSoundChance.intValue());
      }

      for(SoundConfig sr : entry.sounds) {
         if (!RegistryManager.SOUND.isSoundBlocked(new ResourceLocation(sr.sound))) {
            SoundEffect.Builder b = new SoundEffect.Builder(sr);
            SoundEffect s = b.build();
            if (s.getSoundType() == SoundType.SPOT) {
               this.addSpotSound(s);
            } else {
               this.addSound(s);
            }
         }
      }

   }

   @Nonnull
   public String toString() {
      ResourceLocation rl = this.biome.getKey();
      String registryName = rl == null ? (this.isFake() ? "FAKE" : "UNKNOWN") : rl.toString();
      StringBuilder builder = new StringBuilder();
      builder.append("Biome [").append(this.getBiomeName()).append('/').append(registryName).append("] (").append(this.getBiomeId()).append("):");
      if (!this.isFake()) {
         builder.append("\n+ ").append('<');
         builder.append(this.getBiomeTraits());
         builder.append('>').append('\n');
         builder.append("+ temp: ").append(this.getTemperature()).append(" (").append(this.getTemperatureRating().getValue()).append(")");
         builder.append(" rain: ").append(this.getRainfall());
      }

      if (this.hasPrecipitation) {
         builder.append(" PRECIPITATION");
      }

      if (this.hasDust) {
         builder.append(" DUST");
      }

      if (this.hasAurora) {
         builder.append(" AURORA");
      }

      if (this.hasFog) {
         builder.append(" FOG");
      }

      if (this.hasDust && this.dustColor != null) {
         builder.append(" dustColor:").append(this.dustColor.toString());
      }

      if (this.hasFog && this.fogColor != null) {
         builder.append(" fogColor:").append(this.fogColor.toString());
         builder.append(" fogDensity:").append(this.fogDensity);
      }

      if (this.sounds.length > 0) {
         builder.append("\n+ sounds [\n");
         builder.append((String)Arrays.stream(this.sounds).map((c) -> {
            return "+   " + c.toString();
         }).collect(Collectors.joining("\n")));
         builder.append("\n+ ]");
      }

      if (this.spotSounds.length > 0) {
         builder.append("\n+ spot sound chance:").append(this.spotSoundChance);
         builder.append("\n+ spot sounds [\n");
         builder.append((String)Arrays.stream(this.spotSounds).map((c) -> {
            return "+   " + c.toString();
         }).collect(Collectors.joining("\n")));
         builder.append("\n+ ]");
      }

      if (this.comments.size() > 0) {
         builder.append("\n+ comments:\n");
         builder.append((String)this.comments.stream().map((c) -> {
            return "+   " + c;
         }).collect(Collectors.joining("\n")));
         builder.append('\n');
      }

      return builder.toString();
   }

   public int compareTo(@Nonnull BiomeInfo o) {
      return this.getBiomeName().compareTo(o.getBiomeName());
   }
}
