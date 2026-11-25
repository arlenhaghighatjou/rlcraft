package net.mcft.copy.backpacks.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public final class BackpackRegistry {
   private static final List<BackpackRegistry.BackpackEntityEntry> _defaultEntities = new ArrayList();
   private static final Map<String, Optional<Class<? extends EntityLivingBase>>> _entityClassLookupCache = new HashMap();
   private static Map<Class<? extends EntityLivingBase>, BackpackRegistry.BackpackEntityEntry> _entityEntryLookupCache = null;

   private BackpackRegistry() {
   }

   public static void registerEntity(String entityID, BackpackRegistry.RenderOptions renderOptions) {
      if (entityID == null) {
         throw new NullPointerException("entityID must not be null");
      } else if (getDefaultEntityEntry(entityID) != null) {
         throw new IllegalArgumentException("entityID '" + entityID + "' has already been registered");
      } else if (Loader.instance().getLoaderState().compareTo(LoaderState.PREINITIALIZATION) > 0) {
         throw new IllegalStateException("Must be called during (or before) pre-initialization phase.");
      } else {
         _defaultEntities.add(new BackpackRegistry.BackpackEntityEntry(entityID, renderOptions, new ArrayList(), true));
      }
   }

   public static void registerBackpack(String entityID, String entryID, String backpack, int chance, String lootTable, BackpackRegistry.ColorRange colorRange) {
      if (entityID == null) {
         throw new NullPointerException("entityID must not be null");
      } else if (entryID == null) {
         throw new NullPointerException("entryID must not be null");
      } else {
         BackpackRegistry.BackpackEntityEntry entityEntry = getDefaultEntityEntry(entityID);
         if (entityEntry == null) {
            new IllegalStateException("entityID '" + entityID + "' has not been registered yet");
         }

         if (entityEntry._backpackEntries.stream().anyMatch((e) -> {
            return e.id.equals(entryID);
         })) {
            throw new IllegalArgumentException("entryID '" + entryID + "' has already been used for entityID '" + entityID + "'");
         } else if (Loader.instance().getLoaderState().compareTo(LoaderState.PREINITIALIZATION) > 0) {
            throw new IllegalStateException("Must be called during (or before) pre-initialization phase.");
         } else {
            entityEntry._backpackEntries.add(new BackpackRegistry.BackpackEntry(entryID, backpack, chance, lootTable, colorRange, true));
         }
      }
   }

   public static boolean canEntityWearBackpacks(Entity entity) {
      return entity != null && EntityLivingBase.class.isAssignableFrom(entity.getClass()) && getDefaultEntityEntry(entity.getClass().asSubclass(EntityLivingBase.class)) != null;
   }

   public static List<BackpackRegistry.BackpackEntityEntry> getDefaultEntityEntries() {
      return Collections.unmodifiableList(_defaultEntities);
   }

   public static BackpackRegistry.BackpackEntityEntry getDefaultEntityEntry(String entityID) {
      return (BackpackRegistry.BackpackEntityEntry)_defaultEntities.stream().filter((e) -> {
         return e.entityID.equals(entityID);
      }).findAny().orElse((Object)null);
   }

   public static BackpackRegistry.BackpackEntityEntry getDefaultEntityEntry(Class<? extends EntityLivingBase> entityClass) {
      if (EntityPlayer.class.isAssignableFrom(entityClass)) {
         return BackpackRegistry.BackpackEntityEntry.PLAYER;
      } else {
         if (_entityEntryLookupCache == null) {
            _entityEntryLookupCache = (Map)ForgeRegistries.ENTITIES.getEntries().stream().map((e) -> {
               return new SimpleEntry(((EntityEntry)e.getValue()).getEntityClass(), getDefaultEntityEntry(((ResourceLocation)e.getKey()).toString()));
            }).filter((e) -> {
               return EntityLivingBase.class.isAssignableFrom((Class)e.getKey()) && e.getValue() != null;
            }).collect(Collectors.toMap((e) -> {
               return ((Class)e.getKey()).asSubclass(EntityLivingBase.class);
            }, Entry::getValue));
         }

         return (BackpackRegistry.BackpackEntityEntry)_entityEntryLookupCache.get(entityClass);
      }
   }

   public static List<BackpackRegistry.BackpackEntry> getDefaultBackpackEntries(Class<? extends EntityLivingBase> entityClass) {
      if (entityClass == null) {
         throw new NullPointerException("entityClass must not be null");
      } else {
         BackpackRegistry.BackpackEntityEntry entityEntry = getDefaultEntityEntry(entityClass);
         return entityEntry != null ? entityEntry.getEntries() : Collections.emptyList();
      }
   }

   public static final class BackpackEntityEntry {
      public static final BackpackRegistry.BackpackEntityEntry PLAYER = new BackpackRegistry.BackpackEntityEntry("<player>", BackpackRegistry.RenderOptions.DEFAULT, Collections.emptyList(), true);
      public final String entityID;
      public final BackpackRegistry.RenderOptions renderOptions;
      public final boolean isDefault;
      private final List<BackpackRegistry.BackpackEntry> _backpackEntries;

      public BackpackEntityEntry(String entityID, BackpackRegistry.RenderOptions renderOptions, List<BackpackRegistry.BackpackEntry> entries, boolean isDefault) {
         if (entityID == null) {
            throw new NullPointerException("entityID must not be null");
         } else if (renderOptions == null) {
            throw new NullPointerException("renderOptions must not be null");
         } else if (entries == null) {
            throw new NullPointerException("entries must not be null");
         } else {
            this.entityID = entityID;
            this.renderOptions = renderOptions;
            this.isDefault = isDefault;
            this._backpackEntries = entries;
         }
      }

      public BackpackEntityEntry(BackpackRegistry.BackpackEntityEntry value) {
         this(value.entityID, value.renderOptions, new ArrayList(value._backpackEntries), value.isDefault);
      }

      public Class<? extends EntityLivingBase> getEntityClass() {
         return (Class)((Optional)BackpackRegistry._entityClassLookupCache.computeIfAbsent(this.entityID, (id) -> {
            Optional var10000 = Optional.ofNullable(ForgeRegistries.ENTITIES.getValue(new ResourceLocation(id))).map(EntityEntry::getEntityClass);
            EntityLivingBase.class.getClass();
            return var10000.filter(EntityLivingBase.class::isAssignableFrom).map((c) -> {
               return c.asSubclass(EntityLivingBase.class);
            });
         })).orElse((Object)null);
      }

      public List<BackpackRegistry.BackpackEntry> getEntries() {
         return Collections.unmodifiableList(this._backpackEntries);
      }

      public boolean equals(Object obj) {
         if (!(obj instanceof BackpackRegistry.BackpackEntityEntry)) {
            return false;
         } else {
            BackpackRegistry.BackpackEntityEntry other = (BackpackRegistry.BackpackEntityEntry)obj;
            return Objects.equals(other.entityID, this.entityID) && other.renderOptions.equals(this.renderOptions) && other._backpackEntries.equals(this._backpackEntries);
         }
      }
   }

   public static final class BackpackEntry {
      public static final BackpackRegistry.BackpackEntry DEFAULT = new BackpackRegistry.BackpackEntry((String)null, "wearablebackpacks:backpack", 1000, "wearablebackpacks:backpack/default", (BackpackRegistry.ColorRange)null, false);
      public final String id;
      public final String backpack;
      public final int chance;
      public final String lootTable;
      public final BackpackRegistry.ColorRange colorRange;
      public final boolean isDefault;

      public BackpackEntry(String id, String backpack, int chance, String lootTable, BackpackRegistry.ColorRange colorRange, boolean isDefault) {
         if (backpack == null) {
            throw new NullPointerException("backpack must not be null");
         } else if (chance < 0) {
            throw new NullPointerException("chance must not be negative");
         } else {
            this.id = id;
            this.backpack = backpack;
            this.chance = chance;
            this.lootTable = lootTable;
            this.colorRange = colorRange;
            this.isDefault = isDefault;
         }
      }

      public <T extends Item & IBackpackType> T getBackpackItem() {
         Item item = Item.getByNameOrId(this.backpack);
         return (T)(item instanceof IBackpackType ? item : null);
      }

      public boolean equals(Object obj) {
         if (!(obj instanceof BackpackRegistry.BackpackEntry)) {
            return false;
         } else {
            BackpackRegistry.BackpackEntry other = (BackpackRegistry.BackpackEntry)obj;
            return Objects.equals(other.id, this.id) && Objects.equals(other.backpack, this.backpack) && other.chance == this.chance && Objects.equals(other.lootTable, this.lootTable) && Objects.equals(other.colorRange, this.colorRange);
         }
      }
   }

   public static final class ColorRange {
      public static final BackpackRegistry.ColorRange DEFAULT = new BackpackRegistry.ColorRange(2105376, 13684944);
      private static final Random rnd = new Random();
      public final int min;
      public final int max;

      public ColorRange(int min, int max) {
         this.min = min;
         this.max = max;
      }

      public boolean isValid() {
         int minR = this.min >> 16 & 255;
         int minG = this.min >> 8 & 255;
         int minB = this.min & 255;
         int maxR = this.max >> 16 & 255;
         int maxG = this.max >> 8 & 255;
         int maxB = this.max & 255;
         return minR <= maxR && minG <= maxG && minB <= maxB;
      }

      public int getRandom() {
         int minR = this.min >> 16 & 255;
         int minG = this.min >> 8 & 255;
         int minB = this.min & 255;
         int maxR = this.max >> 16 & 255;
         int maxG = this.max >> 8 & 255;
         int maxB = this.max & 255;
         int r = maxR - minR + 1 >= 0 ? minR + rnd.nextInt(maxR - minR + 1) : minR;
         int g = maxG - minG + 1 >= 0 ? minG + rnd.nextInt(maxG - minG + 1) : minG;
         int b = maxB - minB + 1 >= 0 ? minB + rnd.nextInt(maxB - minB + 1) : minB;
         return r << 16 | g << 8 | b;
      }

      public boolean equals(Object obj) {
         if (!(obj instanceof BackpackRegistry.ColorRange)) {
            return false;
         } else {
            BackpackRegistry.ColorRange other = (BackpackRegistry.ColorRange)obj;
            return other.min == this.min && other.max == this.max;
         }
      }
   }

   public static final class RenderOptions {
      public static final BackpackRegistry.RenderOptions DEFAULT = new BackpackRegistry.RenderOptions(0.0D, 2.5D, 0.0D, 0.8D);
      public final double y;
      public final double z;
      public final double rotate;
      public final double scale;

      public RenderOptions(double y, double z, double rotate, double scale) {
         this.y = y;
         this.z = z;
         this.rotate = rotate;
         this.scale = scale;
      }

      public boolean equals(Object obj) {
         if (!(obj instanceof BackpackRegistry.RenderOptions)) {
            return false;
         } else {
            BackpackRegistry.RenderOptions other = (BackpackRegistry.RenderOptions)obj;
            return other.y == this.y && other.z == this.z && other.rotate == this.rotate && other.scale == this.scale;
         }
      }
   }
}
