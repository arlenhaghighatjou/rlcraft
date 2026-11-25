package com.ferreusveritas.dynamictrees.api.worldgen;

import com.ferreusveritas.dynamictrees.systems.poissondisc.PoissonDisc;
import java.util.List;
import java.util.Map;

public interface IPoissonDebug {
   void begin(int var1, int var2);

   void collectSolved(List<PoissonDisc> var1);

   void doEdgeMasking(List<PoissonDisc> var1);

   void maskSolvedDiscs(List<PoissonDisc> var1);

   void createRootDisc(List<PoissonDisc> var1, PoissonDisc var2);

   void gatherUnsolved(List<PoissonDisc> var1, List<PoissonDisc> var2);

   void updateCount(int var1, List<PoissonDisc> var2, List<PoissonDisc> var3);

   void pickMasterDisc(PoissonDisc var1, List<PoissonDisc> var2, List<PoissonDisc> var3);

   void getRadius(PoissonDisc var1, int var2, List<PoissonDisc> var3, List<PoissonDisc> var4);

   void findSecondDisc(PoissonDisc var1, PoissonDisc var2, List<PoissonDisc> var3, List<PoissonDisc> var4);

   void maskMasterSlave(PoissonDisc var1, PoissonDisc var2, List<PoissonDisc> var3, List<PoissonDisc> var4);

   void intersectingList(PoissonDisc var1, Map<Integer, PoissonDisc> var2, List<PoissonDisc> var3);

   void findThirdDiscCandidate(PoissonDisc var1, PoissonDisc var2, PoissonDisc var3, List<PoissonDisc> var4, List<PoissonDisc> var5);

   void thirdCircleCandidateIntersects(PoissonDisc var1, PoissonDisc var2, PoissonDisc var3, PoissonDisc var4, List<PoissonDisc> var5, List<PoissonDisc> var6);

   void findThirdDiscSolved(PoissonDisc var1, List<PoissonDisc> var2, List<PoissonDisc> var3);

   void solveDiscs(List<PoissonDisc> var1, List<PoissonDisc> var2);

   void gatherUnsolved2(List<PoissonDisc> var1, List<PoissonDisc> var2);

   default void unsolvable(int chunkX, int chunkZ, int interations, List<PoissonDisc> unsolvedDiscs, List<PoissonDisc> discs) {
      System.err.println("-----" + unsolvedDiscs.size() + " unsolved circles-----");
      System.err.println("@ chunk x:" + chunkX + ", z:" + chunkZ);
      System.err.println("after " + interations + " iterations");

      for(PoissonDisc c : discs) {
         System.err.println((c.hasFreeAngles() ? "->" : "  ") + c);
      }

   }
}
