package com.ferreusveritas.dynamictrees.systems.poissondisc;

import java.util.List;

public class PoissonDiscHelper {
   private static final int[] singleSearchOrder = new int[]{0, 1, -1};
   private static final int[] pairSearchOrder = new int[]{34, 33, 35, 18, 50, 17, 17, 19, 49, 51, 32, 36, 2, 66, 1, 3, 65, 67, 16, 20, 48, 52};

   public PoissonDiscHelper() {
   }

   public static PoissonDisc findSecondDisc(PoissonDisc cA, int cBrad, boolean onlyTight, boolean CCW) {
      return findSecondDisc(cA, cBrad, CCW ? cA.getFreeAngleCCW() : cA.getFreeAngleCW(), onlyTight);
   }

   public static PoissonDisc findSecondDisc(PoissonDisc cA, int cBrad, double angle, boolean onlyTight) {
      PoissonDiscPairData pd = new PoissonDiscPairData(cA.radius, cBrad);
      int sector = pd.getSector(angle);
      if (onlyTight) {
         for(int i : singleSearchOrder) {
            Vec2i c = pd.getCoords(sector + i);
            if (c.isTight()) {
               return (PoissonDisc)(new PoissonDisc(c, cBrad)).add(cA.x, cA.z);
            }
         }
      }

      return (PoissonDisc)(new PoissonDisc(pd.getCoords(sector), cBrad)).add(cA.x, cA.z);
   }

   public static PoissonDisc findThirdDisc(PoissonDisc cA, PoissonDisc cB, int cCrad) {
      if (cA != null && cB != null && cCrad >= 2 && cCrad <= 8) {
         Vec2i delta = new Vec2i(cB.x - cA.x, cB.z - cA.z);
         double lenAB = delta.len();
         int lenAC = cA.radius + cCrad;
         int lenBC = cB.radius + cCrad;
         double angA = Math.acos(((double)(lenAC * lenAC) + lenAB * lenAB - (double)(lenBC * lenBC)) / ((double)(2 * lenAC) * lenAB));
         double angB = Math.acos(((double)(lenBC * lenBC) + lenAB * lenAB - (double)(lenAC * lenAC)) / ((double)(2 * lenBC) * lenAB));
         double angAB = delta.angle();
         double angBAC = PoissonDiscMathHelper.wrapAngle(angAB - angA);
         double angABC = PoissonDiscMathHelper.wrapAngle(-3.141592653589793D + angAB + angB);
         PoissonDiscPairData pdAC = new PoissonDiscPairData(cA.radius, cCrad);
         PoissonDiscPairData pdBC = new PoissonDiscPairData(cB.radius, cCrad);
         int sectorAC = pdAC.getSector(angBAC);
         int sectorBC = pdBC.getSector(angABC);
         Vec2i[] aCoords = new Vec2i[5];
         Vec2i[] bCoords = new Vec2i[5];
         Vec2i halftight = null;
         Vec2i loose = null;

         for(int i : pairSearchOrder) {
            int aDeltaSector = i >> 4;
            int bDeltaSector = i & 15;
            if (aCoords[aDeltaSector] == null) {
               aCoords[aDeltaSector] = pdAC.getCoords(sectorAC + aDeltaSector - 2).add(cA.x, cA.z);
            }

            if (bCoords[bDeltaSector] == null) {
               bCoords[bDeltaSector] = pdBC.getCoords(sectorBC + bDeltaSector - 2).add(cB.x, cB.z);
            }

            Vec2i a = aCoords[aDeltaSector];
            Vec2i b = bCoords[bDeltaSector];
            if (a.x == b.x && a.z == b.z) {
               if (a.tight && b.tight) {
                  return new PoissonDisc(a, cCrad);
               }

               if (halftight == null) {
                  if (!a.tight && !b.tight) {
                     if (loose == null) {
                        loose = new Vec2i(a);
                     }
                  } else {
                     halftight = new Vec2i(a);
                  }
               }
            }
         }

         if (halftight != null) {
            return new PoissonDisc(halftight, cCrad);
         } else if (loose != null) {
            return new PoissonDisc(loose, cCrad);
         } else {
            return findSecondDisc(cA, cCrad, angAB, true);
         }
      } else {
         System.err.println("3rd circle condition: Radius out of bounds or null circles");
         return null;
      }
   }

   public static void maskDiscs(PoissonDisc c1, PoissonDisc c2) {
      maskDiscs(c1, c2, false);
   }

   public static void maskDiscs(PoissonDisc c1, PoissonDisc c2, boolean force) {
      if (c1 != c2) {
         Vec2i delta = new Vec2i(c2.x - c1.x, c2.z - c1.z);
         double angle = delta.angle();
         double dist = delta.len();
         if (force || c2.isInside(c1.x + (int)((double)(delta.x * (c1.radius + 2)) / dist), c1.z + (int)((double)(delta.z * (c1.radius + 2)) / dist))) {
            if (c1.hasFreeAngles()) {
               double ang = Math.asin(((double)c2.radius + 1.5D) / dist);
               c1.maskArc(angle - ang, angle + ang);
            }

            if (c2.hasFreeAngles()) {
               double ang = Math.asin(((double)c1.radius + 1.5D) / dist);
               c2.maskArc(angle - ang + 3.141592653589793D, angle + ang + 3.141592653589793D);
            }
         }

      }
   }

   public static void solveDiscs(List<PoissonDisc> unsolved, List<PoissonDisc> allDiscs) {
      for(PoissonDisc u : unsolved) {
         for(PoissonDisc c : allDiscs) {
            maskDiscs(u, c);
         }
      }

   }

   public static List<PoissonDisc> gatherUnsolved(List<PoissonDisc> unsolved, List<PoissonDisc> allDiscs) {
      unsolved.clear();

      for(int ci = 0; ci < allDiscs.size(); ++ci) {
         PoissonDisc c = (PoissonDisc)allDiscs.get(ci);
         if (!c.isSolved()) {
            unsolved.add(c);
         }
      }

      return unsolved;
   }

   public static void fastRemove(List<PoissonDisc> discs, int index) {
      PoissonDisc c = (PoissonDisc)discs.remove(discs.size() - 1);
      if (index < discs.size()) {
         discs.set(index, c);
      }

   }
}
