package com.ferreusveritas.dynamictrees.api.cells;

import com.ferreusveritas.dynamictrees.util.SimpleVoxmap;

public interface ICellKit {
   ICell getCellForLeaves(int var1);

   ICell getCellForBranch(int var1, int var2);

   ICellSolver getCellSolver();

   SimpleVoxmap getLeafCluster();

   int getDefaultHydration();
}
