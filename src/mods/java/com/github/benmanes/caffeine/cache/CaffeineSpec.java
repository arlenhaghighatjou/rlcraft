package com.github.benmanes.caffeine.cache;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class CaffeineSpec {
   static final String SPLIT_OPTIONS = ",";
   static final String SPLIT_KEY_VALUE = "=";
   final String specification;
   int initialCapacity = -1;
   long maximumWeight = -1L;
   long maximumSize = -1L;
   boolean recordStats;
   @Nullable
   Caffeine.Strength keyStrength;
   @Nullable
   Caffeine.Strength valueStrength;
   long expireAfterAccessDuration = -1L;
   @Nullable
   TimeUnit expireAfterAccessTimeUnit;
   long expireAfterWriteDuration = -1L;
   @Nullable
   TimeUnit expireAfterWriteTimeUnit;
   long refreshAfterWriteDuration = -1L;
   @Nullable
   TimeUnit refreshAfterWriteTimeUnit;

   private CaffeineSpec(String specification) {
      this.specification = (String)Objects.requireNonNull(specification);
   }

   Caffeine<Object, Object> toBuilder() {
      Caffeine<Object, Object> builder = Caffeine.newBuilder();
      if (this.initialCapacity != -1) {
         builder.initialCapacity(this.initialCapacity);
      }

      if (this.maximumSize != -1L) {
         builder.maximumSize(this.maximumSize);
      }

      if (this.maximumWeight != -1L) {
         builder.maximumWeight(this.maximumWeight);
      }

      if (this.keyStrength != null) {
         Caffeine.requireState(this.keyStrength == Caffeine.Strength.WEAK);
         builder.weakKeys();
      }

      if (this.valueStrength != null) {
         if (this.valueStrength == Caffeine.Strength.WEAK) {
            builder.weakValues();
         } else {
            if (this.valueStrength != Caffeine.Strength.SOFT) {
               throw new IllegalStateException();
            }

            builder.softValues();
         }
      }

      if (this.expireAfterAccessTimeUnit != null) {
         builder.expireAfterAccess(this.expireAfterAccessDuration, this.expireAfterAccessTimeUnit);
      }

      if (this.expireAfterWriteTimeUnit != null) {
         builder.expireAfterWrite(this.expireAfterWriteDuration, this.expireAfterWriteTimeUnit);
      }

      if (this.refreshAfterWriteTimeUnit != null) {
         builder.refreshAfterWrite(this.refreshAfterWriteDuration, this.refreshAfterWriteTimeUnit);
      }

      if (this.recordStats) {
         builder.recordStats();
      }

      return builder;
   }

   @NonNull
   public static CaffeineSpec parse(@NonNull String specification) {
      CaffeineSpec spec = new CaffeineSpec(specification);

      for(String option : specification.split(",")) {
         spec.parseOption(option.trim());
      }

      return spec;
   }

   void parseOption(String option) {
      if (!option.isEmpty()) {
         String[] keyAndValue = option.split("=");
         Caffeine.requireArgument(keyAndValue.length <= 2, "key-value pair %s with more than one equals sign", option);
         String key = keyAndValue[0].trim();
         String value = keyAndValue.length == 1 ? null : keyAndValue[1].trim();
         this.configure(key, value);
      }
   }

   void configure(String key, @Nullable String value) {
      byte var4 = -1;
      switch(key.hashCode()) {
      case -1076762142:
         if (key.equals("expireAfterWrite")) {
            var4 = 7;
         }
         break;
      case -737229428:
         if (key.equals("weakKeys")) {
            var4 = 3;
         }
         break;
      case -83937812:
         if (key.equals("softValues")) {
            var4 = 5;
         }
         break;
      case 336225217:
         if (key.equals("expireAfterAccess")) {
            var4 = 6;
         }
         break;
      case 502967994:
         if (key.equals("weakValues")) {
            var4 = 4;
         }
         break;
      case 706249886:
         if (key.equals("refreshAfterWrite")) {
            var4 = 8;
         }
         break;
      case 817286328:
         if (key.equals("maximumWeight")) {
            var4 = 2;
         }
         break;
      case 1306358478:
         if (key.equals("recordStats")) {
            var4 = 9;
         }
         break;
      case 1685649985:
         if (key.equals("maximumSize")) {
            var4 = 1;
         }
         break;
      case 1725385758:
         if (key.equals("initialCapacity")) {
            var4 = 0;
         }
      }

      switch(var4) {
      case 0:
         this.initialCapacity(key, value);
         return;
      case 1:
         this.maximumSize(key, value);
         return;
      case 2:
         this.maximumWeight(key, value);
         return;
      case 3:
         this.weakKeys(value);
         return;
      case 4:
         this.valueStrength(key, value, Caffeine.Strength.WEAK);
         return;
      case 5:
         this.valueStrength(key, value, Caffeine.Strength.SOFT);
         return;
      case 6:
         this.expireAfterAccess(key, value);
         return;
      case 7:
         this.expireAfterWrite(key, value);
         return;
      case 8:
         this.refreshAfterWrite(key, value);
         return;
      case 9:
         this.recordStats(value);
         return;
      default:
         throw new IllegalArgumentException("Unknown key " + key);
      }
   }

   void initialCapacity(String key, @Nullable String value) {
      Caffeine.requireArgument(this.initialCapacity == -1, "initial capacity was already set to %,d", this.initialCapacity);
      this.initialCapacity = parseInt(key, value);
   }

   void maximumSize(String key, @Nullable String value) {
      Caffeine.requireArgument(this.maximumSize == -1L, "maximum size was already set to %,d", this.maximumSize);
      Caffeine.requireArgument(this.maximumWeight == -1L, "maximum weight was already set to %,d", this.maximumWeight);
      this.maximumSize = parseLong(key, value);
   }

   void maximumWeight(String key, @Nullable String value) {
      Caffeine.requireArgument(this.maximumWeight == -1L, "maximum weight was already set to %,d", this.maximumWeight);
      Caffeine.requireArgument(this.maximumSize == -1L, "maximum size was already set to %,d", this.maximumSize);
      this.maximumWeight = parseLong(key, value);
   }

   void weakKeys(@Nullable String value) {
      Caffeine.requireArgument(value == null, "weak keys does not take a value");
      Caffeine.requireArgument(this.keyStrength == null, "weak keys was already set");
      this.keyStrength = Caffeine.Strength.WEAK;
   }

   void valueStrength(String key, @Nullable String value, Caffeine.Strength strength) {
      Caffeine.requireArgument(value == null, "%s does not take a value", key);
      Caffeine.requireArgument(this.valueStrength == null, "%s was already set to %s", key, this.valueStrength);
      this.valueStrength = strength;
   }

   void expireAfterAccess(String key, @Nullable String value) {
      Caffeine.requireArgument(this.expireAfterAccessDuration == -1L, "expireAfterAccess was already set");
      this.expireAfterAccessDuration = parseDuration(key, value);
      this.expireAfterAccessTimeUnit = parseTimeUnit(key, value);
   }

   void expireAfterWrite(String key, @Nullable String value) {
      Caffeine.requireArgument(this.expireAfterWriteDuration == -1L, "expireAfterWrite was already set");
      this.expireAfterWriteDuration = parseDuration(key, value);
      this.expireAfterWriteTimeUnit = parseTimeUnit(key, value);
   }

   void refreshAfterWrite(String key, @Nullable String value) {
      Caffeine.requireArgument(this.refreshAfterWriteDuration == -1L, "refreshAfterWrite was already set");
      this.refreshAfterWriteDuration = parseDuration(key, value);
      this.refreshAfterWriteTimeUnit = parseTimeUnit(key, value);
   }

   void recordStats(@Nullable String value) {
      Caffeine.requireArgument(value == null, "record stats does not take a value");
      Caffeine.requireArgument(!this.recordStats, "record stats was already set");
      this.recordStats = true;
   }

   static int parseInt(String key, @Nullable String value) {
      Caffeine.requireArgument(value != null && !value.isEmpty(), "value of key %s was omitted", key);

      try {
         return Integer.parseInt(value);
      } catch (NumberFormatException var3) {
         throw new IllegalArgumentException(String.format("key %s value was set to %s, must be an integer", key, value), var3);
      }
   }

   static long parseLong(String key, @Nullable String value) {
      Caffeine.requireArgument(value != null && !value.isEmpty(), "value of key %s was omitted", key);

      try {
         return Long.parseLong(value);
      } catch (NumberFormatException var3) {
         throw new IllegalArgumentException(String.format("key %s value was set to %s, must be a long", key, value), var3);
      }
   }

   static long parseDuration(String key, @Nullable String value) {
      Caffeine.requireArgument(value != null && !value.isEmpty(), "value of key %s omitted", key);
      String duration = value.substring(0, value.length() - 1);
      return parseLong(key, duration);
   }

   static TimeUnit parseTimeUnit(String key, @Nullable String value) {
      Caffeine.requireArgument(value != null && !value.isEmpty(), "value of key %s omitted", key);
      char lastChar = Character.toLowerCase(value.charAt(value.length() - 1));
      switch(lastChar) {
      case 'd':
         return TimeUnit.DAYS;
      case 'h':
         return TimeUnit.HOURS;
      case 'm':
         return TimeUnit.MINUTES;
      case 's':
         return TimeUnit.SECONDS;
      default:
         throw new IllegalArgumentException(String.format("key %s invalid format; was %s, must end with one of [dDhHmMsS]", key, value));
      }
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof CaffeineSpec)) {
         return false;
      } else {
         CaffeineSpec spec = (CaffeineSpec)o;
         return Objects.equals(Integer.valueOf(this.initialCapacity), Integer.valueOf(spec.initialCapacity)) && Objects.equals(Long.valueOf(this.maximumSize), Long.valueOf(spec.maximumSize)) && Objects.equals(Long.valueOf(this.maximumWeight), Long.valueOf(spec.maximumWeight)) && Objects.equals(this.keyStrength, spec.keyStrength) && Objects.equals(this.valueStrength, spec.valueStrength) && Objects.equals(Boolean.valueOf(this.recordStats), Boolean.valueOf(spec.recordStats)) && durationInNanos(this.expireAfterAccessDuration, this.expireAfterAccessTimeUnit) == durationInNanos(spec.expireAfterAccessDuration, spec.expireAfterAccessTimeUnit) && durationInNanos(this.expireAfterWriteDuration, this.expireAfterWriteTimeUnit) == durationInNanos(spec.expireAfterWriteDuration, spec.expireAfterWriteTimeUnit) && durationInNanos(this.refreshAfterWriteDuration, this.refreshAfterWriteTimeUnit) == durationInNanos(spec.refreshAfterWriteDuration, spec.refreshAfterWriteTimeUnit);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.initialCapacity, this.maximumSize, this.maximumWeight, this.keyStrength, this.valueStrength, this.recordStats, durationInNanos(this.expireAfterAccessDuration, this.expireAfterAccessTimeUnit), durationInNanos(this.expireAfterWriteDuration, this.expireAfterWriteTimeUnit), durationInNanos(this.refreshAfterWriteDuration, this.refreshAfterWriteTimeUnit)});
   }

   static long durationInNanos(long duration, @Nullable TimeUnit unit) {
      return unit == null ? -1L : unit.toNanos(duration);
   }

   public String toParsableString() {
      return this.specification;
   }

   public String toString() {
      return this.getClass().getSimpleName() + '{' + this.toParsableString() + '}';
   }
}
