package mcjty.incontrol.rules;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import mcjty.incontrol.InControl;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class RuleCache {
   private Map<Integer, RuleCache.CachePerWorld> caches = new HashMap();

   public RuleCache() {
   }

   public void reset(World world) {
      RuleCache.CachePerWorld cache = (RuleCache.CachePerWorld)this.caches.get(Integer.valueOf(world.provider.getDimension()));
      if (cache != null) {
         cache.reset();
      }

   }

   public int getValidSpawnChunks(World world) {
      RuleCache.CachePerWorld cache = this.getOrCreateCache(world);
      return cache.getValidSpawnChunks(world);
   }

   public int getValidPlayers(World world) {
      RuleCache.CachePerWorld cache = this.getOrCreateCache(world);
      return cache.getValidPlayers(world);
   }

   public int getCountPassive(World world) {
      RuleCache.CachePerWorld cache = this.getOrCreateCache(world);
      return cache.getCountPassive(world);
   }

   public int getCountHostile(World world) {
      RuleCache.CachePerWorld cache = this.getOrCreateCache(world);
      return cache.getCountHostile(world);
   }

   public int getCount(World world, Class<? extends Entity> entityType) {
      RuleCache.CachePerWorld cache = this.getOrCreateCache(world);
      int count = cache.getCount(world, entityType);
      return count;
   }

   public int getCountPerMod(World world, String mod) {
      RuleCache.CachePerWorld cache = this.getOrCreateCache(world);
      RuleCache.CountPerMod countPerMod = cache.getCountPerMod(world, mod);
      return countPerMod == null ? 0 : countPerMod.total;
   }

   public int getCountPerModHostile(World world, String mod) {
      RuleCache.CachePerWorld cache = this.getOrCreateCache(world);
      RuleCache.CountPerMod countPerMod = cache.getCountPerMod(world, mod);
      return countPerMod == null ? 0 : countPerMod.hostile;
   }

   public int getCountPerModPassive(World world, String mod) {
      RuleCache.CachePerWorld cache = this.getOrCreateCache(world);
      RuleCache.CountPerMod countPerMod = cache.getCountPerMod(world, mod);
      return countPerMod == null ? 0 : countPerMod.passive;
   }

   public void registerSpawn(World world, Class<? extends Entity> entityType) {
      RuleCache.CachePerWorld cache = this.getOrCreateCache(world);
      cache.registerSpawn(world, entityType);
   }

   public void registerDespawn(World world, Class<? extends Entity> entityType) {
      RuleCache.CachePerWorld cache = this.getOrCreateCache(world);
      cache.registerDespawn(world, entityType);
   }

   private RuleCache.CachePerWorld getOrCreateCache(World world) {
      RuleCache.CachePerWorld cache = (RuleCache.CachePerWorld)this.caches.get(Integer.valueOf(world.provider.getDimension()));
      if (cache == null) {
         cache = new RuleCache.CachePerWorld();
         this.caches.put(Integer.valueOf(world.provider.getDimension()), cache);
      }

      return cache;
   }

   private class CachePerWorld {
      private Map<Class, Integer> cachedCounters;
      private Map<String, RuleCache.CountPerMod> countPerMod;
      private int countPassive;
      private int countHostile;
      private int validSpawnChunks;
      private int validPlayers;
      private boolean countDone;

      private CachePerWorld() {
         this.cachedCounters = new HashMap();
         this.countPerMod = new HashMap();
         this.countPassive = -1;
         this.countHostile = -1;
         this.validSpawnChunks = -1;
         this.validPlayers = -1;
         this.countDone = false;
      }

      public void reset() {
         this.cachedCounters.clear();
         this.countPerMod.clear();
         this.countPassive = -1;
         this.countHostile = -1;
         this.validSpawnChunks = -1;
         this.validPlayers = -1;
         this.countDone = false;
      }

      public int getValidSpawnChunks(World world) {
         if (this.validSpawnChunks == -1) {
            this.validSpawnChunks = this.countValidSpawnChunks((WorldServer)world);
         }

         return this.validSpawnChunks;
      }

      public int getValidPlayers(World world) {
         if (this.validPlayers == -1) {
            this.validPlayers = this.countValidPlayers(world);
         }

         return this.validPlayers;
      }

      private int countValidPlayers(World world) {
         int cnt = 0;

         for(EntityPlayer entityplayer : world.playerEntities) {
            if (!entityplayer.isSpectator()) {
               ++cnt;
            }
         }

         return cnt;
      }

      private int countValidSpawnChunks(WorldServer world) {
         Set<ChunkPos> eligibleChunksForSpawning = new HashSet();

         for(EntityPlayer entityplayer : world.playerEntities) {
            if (!entityplayer.isSpectator()) {
               int chunkX = MathHelper.floor(entityplayer.posX / 16.0D);
               int chunkZ = MathHelper.floor(entityplayer.posZ / 16.0D);

               for(int dx = -8; dx <= 8; ++dx) {
                  for(int dz = -8; dz <= 8; ++dz) {
                     boolean flag = dx == -8 || dx == 8 || dz == -8 || dz == 8;
                     ChunkPos chunkpos = new ChunkPos(dx + chunkX, dz + chunkZ);
                     if (!eligibleChunksForSpawning.contains(chunkpos) && !flag && world.getWorldBorder().contains(chunkpos)) {
                        PlayerChunkMapEntry entry = world.getPlayerChunkMap().getEntry(chunkpos.x, chunkpos.z);
                        if (entry != null && entry.isSentToPlayers()) {
                           eligibleChunksForSpawning.add(chunkpos);
                        }
                     }
                  }
               }
            }
         }

         return eligibleChunksForSpawning.size();
      }

      public int getCountPassive(World world) {
         this.count(world);
         return this.countPassive;
      }

      public int getCountHostile(World world) {
         this.count(world);
         return this.countHostile;
      }

      private void count(World world) {
         if (!this.countDone) {
            this.countDone = true;
            this.cachedCounters.clear();
            this.countPerMod.clear();
            this.countPassive = 0;
            this.countHostile = 0;

            for(Entity entity : world.loadedEntityList) {
               if (entity instanceof EntityLiving) {
                  int cnt = ((Integer)this.cachedCounters.getOrDefault(entity.getClass(), Integer.valueOf(0))).intValue() + 1;
                  this.cachedCounters.put(entity.getClass(), Integer.valueOf(cnt));
                  String mod = InControl.instance.modCache.getMod(entity);
                  RuleCache.CountPerMod count = (RuleCache.CountPerMod)this.countPerMod.computeIfAbsent(mod, (s) -> {
                     return new RuleCache.CountPerMod();
                  });
                  count.total++;
                  if (entity instanceof IMob) {
                     count.hostile++;
                     ++this.countHostile;
                  } else if (entity instanceof IAnimals) {
                     count.passive++;
                     ++this.countPassive;
                  }
               }
            }

         }
      }

      public int getCount(World world, Class<? extends Entity> entityType) {
         this.count(world);
         return ((Integer)this.cachedCounters.getOrDefault(entityType, Integer.valueOf(0))).intValue();
      }

      public RuleCache.CountPerMod getCountPerMod(World world, String mod) {
         this.count(world);
         return (RuleCache.CountPerMod)this.countPerMod.get(mod);
      }

      public void registerSpawn(World world, Class<? extends Entity> entityType) {
         this.count(world);
         this.cachedCounters.put(entityType, Integer.valueOf(((Integer)this.cachedCounters.getOrDefault(entityType, Integer.valueOf(0))).intValue() + 1));
      }

      public void registerDespawn(World world, Class<? extends Entity> entityType) {
         this.count(world);
         Integer cnt = (Integer)this.cachedCounters.getOrDefault(entityType, Integer.valueOf(0));
         if (cnt.intValue() > 0) {
            this.cachedCounters.put(entityType, Integer.valueOf(cnt.intValue() - 1));
         }

      }
   }

   private static class CountPerMod {
      private int hostile;
      private int passive;
      private int total;

      private CountPerMod() {
      }
   }
}
