package org.orecruncher.dsurround.client.sound;

import com.google.common.base.MoreObjects;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;
import org.orecruncher.dsurround.client.fx.ISpecialEffect;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;
import org.orecruncher.dsurround.client.handlers.SoundEffectHandler;
import org.orecruncher.dsurround.expression.ExpressionEngine;
import org.orecruncher.dsurround.registry.RegistryManager;
import org.orecruncher.dsurround.registry.config.SoundConfig;
import org.orecruncher.dsurround.registry.config.SoundType;
import org.orecruncher.dsurround.registry.sound.SoundMetadata;
import org.orecruncher.dsurround.registry.sound.SoundRegistry;
import org.orecruncher.lib.WeightTable.IEntrySource;
import org.orecruncher.lib.WeightTable.IItem;
import org.orecruncher.lib.chunk.IBlockAccessEx;
import org.orecruncher.lib.random.XorShiftRandom;

@SideOnly(Side.CLIENT)
public final class SoundEffect implements ISpecialEffect, IEntrySource<SoundEffect>, IItem<SoundEffect> {
   private static final int SPOT_SOUND_RANGE = 8;
   private static final float[] pitchDelta = new float[]{-0.2F, 0.0F, 0.0F, 0.2F, 0.2F, 0.2F};
   private static final EaglercraftRandom RANDOM = XorShiftRandom.current();
   private final SoundEvent sound;
   private final String soundName;
   private SoundType type;
   private String conditions;
   private SoundCategory category;
   private float volume;
   private float pitch;
   private int weight;
   private boolean variable;
   private int repeatDelayRandom;
   private int repeatDelay;
   private String soundTitle;

   protected SoundEffect(ResourceLocation resource, SoundCategory category) {
      this(resource, category, 1.0F, 1.0F, 0, false);
   }

   protected SoundEffect(ResourceLocation resource, SoundCategory category, float volume, float pitch, int repeatDelay, boolean variable) {
      this.soundTitle = "";
      this.soundName = resource.toString();
      this.sound = RegistryManager.SOUND.getSound(resource);
      this.volume = volume;
      this.pitch = pitch;
      this.conditions = "";
      this.weight = 10;
      this.type = SoundType.SPOT;
      this.category = (SoundCategory)MoreObjects.firstNonNull(category, SoundCategory.BLOCKS);
      this.variable = variable;
      this.repeatDelayRandom = 0;
      this.repeatDelay = repeatDelay;
   }

   protected SoundEffect setVolume(float vol) {
      this.volume = vol;
      return this;
   }

   protected SoundEffect setPitch(float pitch) {
      this.pitch = pitch;
      return this;
   }

   protected SoundEffect setVariable(boolean flag) {
      this.variable = flag;
      return this;
   }

   protected SoundEffect setSoundCategory(@Nonnull SoundCategory cat) {
      this.category = cat;
      return this;
   }

   protected SoundEffect setConditions(@Nonnull String cond) {
      this.conditions = cond;
      return this;
   }

   protected SoundEffect setWeight(int w) {
      this.weight = w;
      return this;
   }

   protected SoundEffect setSoundType(@Nonnull SoundType type) {
      this.type = type;
      return this;
   }

   protected SoundEffect setRepeatDelay(int d) {
      this.repeatDelay = d;
      return this;
   }

   protected SoundEffect setRepeatDelayRandom(int r) {
      this.repeatDelayRandom = r;
      return this;
   }

   protected SoundEffect setSoundTitle(@Nonnull String title) {
      this.soundTitle = title;
      return this;
   }

   public String getSoundName() {
      return this.soundName;
   }

   public String getSoundTitle() {
      return this.soundTitle;
   }

   public SoundEvent getSound() {
      return this.sound;
   }

   public SoundCategory getCategory() {
      return this.category;
   }

   public SoundType getSoundType() {
      return this.type;
   }

   protected float getVolume() {
      return this.volume;
   }

   protected float getPitch(EaglercraftRandom rand) {
      return rand != null && this.variable ? this.pitch + pitchDelta[rand.nextInt(pitchDelta.length)] : this.pitch;
   }

   protected int getRepeat(EaglercraftRandom rand) {
      return this.repeatDelayRandom <= 0 ? Math.max(this.repeatDelay, 0) : this.repeatDelay + rand.nextInt(this.repeatDelayRandom);
   }

   protected boolean isRepeatable() {
      return this.type == SoundType.PERIODIC || this.type == SoundType.BACKGROUND;
   }

   private float randomRange(int range) {
      return (float)(RANDOM.nextInt(range) - RANDOM.nextInt(range));
   }

   @SideOnly(Side.CLIENT)
   public SoundInstance createSoundAt(@Nonnull BlockPos pos) {
      return SoundBuilder.builder(this.sound, SoundRegistry.BIOME).setPosition(pos).build();
   }

   @SideOnly(Side.CLIENT)
   public SoundInstance createSoundNear(@Nonnull Entity player) {
      float posX = (float)(player.posX + (double)this.randomRange(8));
      float posY = (float)(player.posY + (double)player.getEyeHeight() + (double)this.randomRange(8));
      float posZ = (float)(player.posZ + (double)this.randomRange(8));
      return SoundBuilder.builder(this.sound, SoundRegistry.BIOME).setPosition(posX, posY, posZ).build();
   }

   @SideOnly(Side.CLIENT)
   public SoundInstance createTrackingSound(@Nonnull Entity player, boolean fadeIn) {
      TrackingSoundInstance sound = new TrackingSoundInstance(player, this, fadeIn);
      if (EnvironStateHandler.EnvironState.isPlayer(player)) {
         sound.setAttenuationType(SoundInstance.noAttenuation());
      }

      return sound;
   }

   public boolean canTrigger(@Nonnull IBlockAccessEx provider, @Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull EaglercraftRandom random) {
      return true;
   }

   public void doEffect(@Nonnull IBlockAccessEx provider, @Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull EaglercraftRandom random) {
      SoundEffectHandler.INSTANCE.playSoundAt(pos, this, 0);
   }

   public boolean equals(Object anObj) {
      return this == anObj || this.soundName.equals(((SoundEffect)anObj).soundName);
   }

   public int hashCode() {
      return this.sound.hashCode();
   }

   public SoundEffect getItem() {
      return this;
   }

   public int getWeight() {
      return this.weight;
   }

   public IItem<SoundEffect> getEntry() {
      return this;
   }

   public boolean matches() {
      return ExpressionEngine.instance().check(this.conditions);
   }

   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append(this.soundName);
      builder.append('(').append(this.conditions).append(')');
      builder.append(", v:").append(this.volume);
      builder.append(", p:").append(this.pitch);
      builder.append(", t:").append(this.type);
      if (this.type == SoundType.SPOT) {
         builder.append(", w:").append(this.weight);
      }

      if (this.repeatDelay != 0 || this.repeatDelayRandom != 0) {
         builder.append(", d:").append(this.repeatDelay).append('+').append(this.repeatDelayRandom);
      }

      return builder.toString();
   }

   public static class Builder {
      private final SoundEffect effect;

      public Builder(@Nonnull String sound, @Nonnull SoundCategory cat) {
         this(new ResourceLocation("dsurround", sound), cat);
      }

      public Builder(@Nonnull ResourceLocation resource, @Nonnull SoundCategory cat) {
         this.effect = new SoundEffect(resource, cat);
      }

      public Builder(@Nonnull SoundConfig record) {
         ResourceLocation resource = new ResourceLocation(record.sound);
         this.effect = new SoundEffect(resource, (SoundCategory)null);
         this.setConditions(StringUtils.isEmpty(record.conditions) ? "" : record.conditions.intern());
         this.setVolume(record.volume == null ? 1.0F : record.volume.floatValue());
         this.setPitch(record.pitch == null ? 1.0F : record.pitch.floatValue());
         this.setWeight(record.weight == null ? 10 : record.weight.intValue());
         this.setVariablePitch(record.variable != null && record.variable.booleanValue());
         this.setRepeatDelay(record.repeatDelay == null ? 0 : record.repeatDelay.intValue());
         this.setRepeatDelayRandom(record.repeatDelayRandom == null ? 0 : record.repeatDelayRandom.intValue());
         this.setSoundTitle(record.title != null ? record.title : "");
         SoundType t = null;
         if (record.soundType != null) {
            t = SoundType.getType(record.soundType);
         }

         if (t == null) {
            if (record.repeatDelay != null && record.repeatDelay.intValue() > 0) {
               t = SoundType.PERIODIC;
            } else if (record.spotSound != null && record.spotSound.booleanValue()) {
               t = SoundType.SPOT;
            } else {
               t = SoundType.BACKGROUND;
            }
         }

         this.setSoundType(t);
         SoundCategory sc = null;
         if (record.soundCategory != null) {
            sc = SoundCategory.getByName(record.soundCategory);
         }

         if (sc == null) {
            SoundMetadata meta = RegistryManager.SOUND.getSoundMetadata(resource);
            if (meta != null) {
               sc = meta.getCategory();
            }

            if (sc == null) {
               sc = SoundCategory.AMBIENT;
            }
         }

         this.setSoundCategory(sc);
      }

      public SoundEffect.Builder setSoundTitle(@Nonnull String title) {
         this.effect.setSoundTitle(title);
         return this;
      }

      public SoundEffect.Builder setVolume(float v) {
         this.effect.setVolume(v);
         return this;
      }

      public SoundEffect.Builder setPitch(float p) {
         this.effect.setPitch(p);
         return this;
      }

      public SoundEffect.Builder setVariablePitch(boolean flag) {
         this.effect.setVariable(flag);
         return this;
      }

      public SoundEffect.Builder setSoundCategory(@Nonnull SoundCategory cat) {
         this.effect.setSoundCategory(cat);
         return this;
      }

      public SoundEffect.Builder setConditions(@Nullable String cond) {
         this.effect.setConditions(cond == null ? "" : cond);
         return this;
      }

      public SoundEffect.Builder setWeight(int w) {
         this.effect.setWeight(w);
         return this;
      }

      public SoundEffect.Builder setRepeatDelay(int d) {
         this.effect.setRepeatDelay(d);
         return this;
      }

      public SoundEffect.Builder setRepeatDelayRandom(int r) {
         this.effect.setRepeatDelayRandom(r);
         return this;
      }

      public SoundEffect.Builder setSoundType(SoundType type) {
         this.effect.setSoundType(type);
         return this;
      }

      public SoundEffect build() {
         return this.effect;
      }
   }
}
