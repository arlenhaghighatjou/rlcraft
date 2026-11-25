package com.ferreusveritas.dynamictrees.systems.poissondisc;

import com.ferreusveritas.dynamictrees.api.worldgen.IPoissonDebug;
import com.ferreusveritas.dynamictrees.api.worldgen.IPoissonDiscProvider;
import com.ferreusveritas.dynamictrees.api.worldgen.IRadiusCoordinator;
import com.ferreusveritas.dynamictrees.util.CoordUtils;
import com.ferreusveritas.dynamictrees.util.RandomXOR;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.TreeMap;
import java.util.Map.Entry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;

public class PoissonDiscProvider implements IPoissonDiscProvider {
   private final IRadiusCoordinator radiusCoordinator;
   private final HashMap<ChunkPos, PoissonDiscChunkSet> chunkDiscs = new HashMap();
   private RandomXOR random = new RandomXOR();
   private IPoissonDebug debug;
   private final List<PoissonDisc> discCache1 = new ArrayList(64);
   private final List<PoissonDisc> discCache2 = new ArrayList(64);

   public PoissonDiscProvider(IRadiusCoordinator radCoord) {
      this.radiusCoordinator = radCoord;
   }

   public void setSeed(Long seed) {
      if (seed != null) {
         this.random = new RandomXOR(seed.longValue());
      }

   }

   public void setDebug(IPoissonDebug debug) {
      this.debug = debug;
   }

   public List<PoissonDisc> getPoissonDiscs(int chunkX, int chunkY, int chunkZ) {
      this.random.setXOR(new BlockPos(chunkX, chunkY, chunkZ));
      PoissonDiscChunkSet cSet = this.getChunkDiscSet(chunkX, chunkZ);
      if (cSet.generated) {
         return this.getChunkPoissonDiscs(chunkX, chunkZ);
      } else {
         int i = 0;

         List<PoissonDisc> output;
         for(output = null; this.radiusCoordinator.runPass(chunkX, chunkZ, i++); output = this.generatePoissonDiscs(this.random, chunkX, chunkZ)) {
            ;
         }

         return output;
      }
   }

   public List<PoissonDisc> generatePoissonDiscs(EaglercraftRandom random, int chunkX, int chunkZ) {
      List<PoissonDisc> allDiscs = this.discCache1;
      List<PoissonDisc> unsolvedDiscs = this.discCache2;
      allDiscs.clear();
      unsolvedDiscs.clear();
      if (this.debug != null) {
         this.debug.begin(chunkX, chunkZ);
      }

      this.getChunkPoissonDiscs(allDiscs, chunkX, chunkZ);

      for(CoordUtils.Surround surr : CoordUtils.Surround.values()) {
         Vec3i dir = surr.getOffset();
         this.getChunkPoissonDiscs(allDiscs, chunkX + dir.getX(), chunkZ + dir.getZ());
      }

      if (this.debug != null) {
         this.debug.collectSolved(allDiscs);
      }

      int chunkXStart = chunkX << 4;
      int chunkZStart = chunkZ << 4;

      for(PoissonDisc c : allDiscs) {
         c.edgeMask(chunkXStart, chunkZStart);
      }

      if (this.debug != null) {
         this.debug.doEdgeMasking(allDiscs);
      }

      for(int i = 0; i < allDiscs.size() - 1; ++i) {
         for(int j = i + 1; j < allDiscs.size(); ++j) {
            PoissonDiscHelper.maskDiscs((PoissonDisc)allDiscs.get(i), (PoissonDisc)allDiscs.get(j));
         }
      }

      if (this.debug != null) {
         this.debug.maskSolvedDiscs(allDiscs);
      }

      if (allDiscs.size() == 0) {
         int x = chunkXStart + random.nextInt(16);
         int z = chunkZStart + random.nextInt(16);
         int radius = this.radiusCoordinator.getRadiusAtCoords(x, z);
         PoissonDisc rootDisc = new PoissonDisc(x, z, radius);
         rootDisc.real = true;
         allDiscs.add(rootDisc);
         if (this.debug != null) {
            this.debug.createRootDisc(allDiscs, rootDisc);
         }
      }

      PoissonDiscHelper.gatherUnsolved(unsolvedDiscs, allDiscs);
      if (this.debug != null) {
         this.debug.gatherUnsolved(unsolvedDiscs, allDiscs);
      }

      int count = 0;

      while(!unsolvedDiscs.isEmpty()) {
         if (this.debug != null) {
            this.debug.updateCount(count, unsolvedDiscs, allDiscs);
         }

         PoissonDisc master = (PoissonDisc)unsolvedDiscs.get(0);
         if (this.debug != null) {
            this.debug.pickMasterDisc(master, unsolvedDiscs, allDiscs);
         }

         PoissonDisc slave = null;
         Vec2i slavePos = null;
         int radius = 0;

         for(int dir = 0; dir <= 1; ++dir) {
            boolean CCW = dir == 0;
            float angle = CCW ? (float)master.getFreeAngleCCW() : (float)master.getFreeAngleCW();
            double dx = (double)master.x + (double)(MathHelper.sin(angle) * (float)master.radius) * 1.5D;
            double dz = (double)master.z + (double)(MathHelper.cos(angle) * (float)master.radius) * 1.5D;
            radius = this.radiusCoordinator.getRadiusAtCoords((int)dx, (int)dz);
            if (this.debug != null) {
               this.debug.getRadius(master, radius, unsolvedDiscs, allDiscs);
            }

            slave = PoissonDiscHelper.findSecondDisc(master, radius, true, CCW);
            slavePos = new Vec2i(slave);
            if (this.debug != null) {
               this.debug.findSecondDisc(master, slave, unsolvedDiscs, allDiscs);
            }

            if (this.doesDiscIntersectWith(slave, allDiscs)) {
               break;
            }
         }

         master.arc |= 1 << master.getFreeBitCW();
         PoissonDiscHelper.maskDiscs(master, slave, true);
         if (this.debug != null) {
            this.debug.maskMasterSlave(master, slave, unsolvedDiscs, allDiscs);
         }

         int i = 0;
         Map<Integer, PoissonDisc> intersecting = new TreeMap();

         for(PoissonDisc c : allDiscs) {
            if (slave.doCirclesIntersectPadding(c)) {
               int depth = 16 + (int)c.discPenetration(slave);
               intersecting.put(Integer.valueOf(depth << 8 | i++), c);
            }
         }

         if (this.debug != null) {
            this.debug.intersectingList(slave, intersecting, allDiscs);
         }

         for(Entry<Integer, PoissonDisc> entry : intersecting.entrySet()) {
            PoissonDisc master1 = master;
            PoissonDisc master2 = (PoissonDisc)entry.getValue();
            int cross = Vec2i.crossProduct((new Vec2i(slavePos)).sub(master), (new Vec2i(master2)).sub(master));
            if (cross < 0) {
               PoissonDisc temp = master2;
               master2 = master;
               master1 = temp;
            }

            slave = PoissonDiscHelper.findThirdDisc(master1, master2, radius);
            if (this.debug != null) {
               this.debug.findThirdDiscCandidate(master1, master2, slave, unsolvedDiscs, allDiscs);
            }

            if (slave != null) {
               for(int ci = 0; ci < allDiscs.size(); ++ci) {
                  PoissonDisc c = (PoissonDisc)allDiscs.get(ci);
                  if (slave.doCirclesIntersectPadding(c)) {
                     if (this.debug != null) {
                        this.debug.thirdCircleCandidateIntersects(master1, master2, slave, c, unsolvedDiscs, allDiscs);
                     }

                     if (c.real || !c.real && !slave.isInCenterChunk(chunkXStart, chunkZStart)) {
                        slave = null;
                        break;
                     }

                     PoissonDiscHelper.fastRemove(allDiscs, ci--);
                  }
               }
            }

            if (slave != null) {
               if (this.debug != null) {
                  this.debug.findThirdDiscSolved(slave, unsolvedDiscs, allDiscs);
               }
               break;
            }
         }

         if (slave != null) {
            slave.edgeMask(chunkXStart, chunkZStart);
            slave.real = slave.isInCenterChunk(chunkXStart, chunkZStart);
            unsolvedDiscs.add(slave);
            PoissonDiscHelper.solveDiscs(unsolvedDiscs, allDiscs);
            allDiscs.add(slave);
            if (this.debug != null) {
               this.debug.solveDiscs(unsolvedDiscs, allDiscs);
            }
         }

         PoissonDiscHelper.gatherUnsolved(unsolvedDiscs, allDiscs);
         if (this.debug != null) {
            this.debug.gatherUnsolved2(unsolvedDiscs, allDiscs);
         }

         ++count;
         if (count > 64 && !unsolvedDiscs.isEmpty()) {
            if (this.debug != null) {
               this.debug.unsolvable(chunkX, chunkZ, count, unsolvedDiscs, allDiscs);
            }
            break;
         }
      }

      PoissonDiscChunkSet cSet = this.getChunkDiscSet(chunkX, chunkZ);
      cSet.generated = true;

      for(PoissonDisc c : allDiscs) {
         if (c.isInCenterChunk(chunkXStart, chunkZStart)) {
            cSet.addDisc(c);
         }
      }

      return cSet.getDiscs(new ArrayList(16), chunkX, chunkZ);
   }

   private boolean doesDiscIntersectWith(PoissonDisc disc, List<PoissonDisc> others) {
      for(PoissonDisc other : others) {
         if (disc.doCirclesIntersectPadding(other)) {
            return true;
         }
      }

      return false;
   }

   private PoissonDiscChunkSet getChunkDiscSet(int chunkX, int chunkZ) {
      ChunkPos key = new ChunkPos(chunkX, chunkZ);
      PoissonDiscChunkSet cSet;
      if (this.chunkDiscs.containsKey(key)) {
         cSet = (PoissonDiscChunkSet)this.chunkDiscs.get(key);
      } else {
         cSet = new PoissonDiscChunkSet();
         this.chunkDiscs.put(key, cSet);
      }

      return cSet;
   }

   public byte[] getChunkPoissonData(int chunkX, int chunkY, int chunkZ) {
      return this.getChunkDiscSet(chunkX, chunkZ).getDiscData();
   }

   public void setChunkPoissonData(int chunkX, int chunkY, int chunkZ, byte[] circleData) {
      this.getChunkDiscSet(chunkX, chunkZ).setDiscData(circleData);
   }

   public void unloadChunkPoissonData(int chunkX, int chunkY, int chunkZ) {
      this.chunkDiscs.remove(new ChunkPos(chunkX, chunkZ));
   }

   private List<PoissonDisc> getChunkPoissonDiscs(int chunkX, int chunkZ) {
      return this.getChunkPoissonDiscs(new ArrayList(), chunkX, chunkZ);
   }

   private List<PoissonDisc> getChunkPoissonDiscs(List<PoissonDisc> discs, int chunkX, int chunkZ) {
      PoissonDiscChunkSet cSet = this.getChunkDiscSet(chunkX, chunkZ);
      cSet.getDiscs(discs, chunkX, chunkZ);
      return discs;
   }
}
