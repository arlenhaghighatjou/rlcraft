package pl.asie.foamfix.coremod.injections;

import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pl.asie.foamfix.api.IFoamFixMobSpawnerLogic;
import pl.asie.foamfix.shared.FoamFixShared;

public class CachingMobSpawnerLogicInject extends MobSpawnerBaseLogic implements IFoamFixMobSpawnerLogic {
   private boolean foamfix_activatedCache;
   private int foamfix_activatedCachePESize;
   private long foamfix_activatedCacheTime;
   private boolean foamfix_forcedCache;
   private long foamfix_forcedCacheTime;

   public CachingMobSpawnerLogicInject() {
   }

   public boolean isActivated() {
      World world = this.getSpawnerWorld();
      int peSize = world.playerEntities.size();
      long time = world.getTotalWorldTime();
      if (time == this.foamfix_forcedCacheTime) {
         return this.foamfix_forcedCache;
      } else {
         if (peSize != this.foamfix_activatedCachePESize) {
            this.foamfix_activatedCacheTime = 0L;
         }

         if (time < this.foamfix_activatedCacheTime) {
            return this.foamfix_activatedCache;
         } else {
            this.foamfix_activatedCacheTime = time + (long)(world.rand.nextInt() & 1) + (long)FoamFixShared.config.geMobSpawnerCheckSpeed;
            this.foamfix_activatedCachePESize = peSize;
            return this.foamfix_activatedCache = this.isActivated_foamfix_old();
         }
      }
   }

   public boolean isActivated_foamfix_old() {
      return false;
   }

   public void broadcastEvent(int id) {
   }

   public World getSpawnerWorld() {
      return null;
   }

   public BlockPos getSpawnerPosition() {
      return null;
   }

   public boolean forceSpawnActivationFlag(boolean value) {
      World world = this.getSpawnerWorld();
      long time = world.getTotalWorldTime();
      this.foamfix_forcedCacheTime = time;
      this.foamfix_forcedCache = value;
      return true;
   }
}
