package com.ferreusveritas.dynamictrees.cells;

import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import com.ferreusveritas.dynamictrees.api.cells.CellNull;
import com.ferreusveritas.dynamictrees.api.cells.ICell;
import com.ferreusveritas.dynamictrees.api.cells.ICellKit;
import com.ferreusveritas.dynamictrees.api.cells.ICellSolver;
import com.ferreusveritas.dynamictrees.util.SimpleVoxmap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class CellKits {
   private static final ICellSolver NULLCELLSOLVER = new ICellSolver() {
      public int solve(ICell[] cells) {
         return 0;
      }
   };
   public static final ICellKit NULLCELLKIT = new ICellKit() {
      public ICell getCellForLeaves(int hydro) {
         return CellNull.NULLCELL;
      }

      public ICell getCellForBranch(int radius, int meta) {
         return CellNull.NULLCELL;
      }

      public ICellSolver getCellSolver() {
         return CellKits.NULLCELLSOLVER;
      }

      public SimpleVoxmap getLeafCluster() {
         return LeafClusters.NULLMAP;
      }

      public int getDefaultHydration() {
         return 0;
      }
   };
   private final ICellKit deciduous = new ICellKit() {
      private final ICell[] normalCells = new ICell[]{CellNull.NULLCELL, new CellNormal(1), new CellNormal(2), new CellNormal(3), new CellNormal(4), new CellNormal(5), new CellNormal(6), new CellNormal(7)};
      private final ICell branchCell = new CellNormal(5);
      private final CellKits.BasicSolver deciduousSolver = new CellKits.BasicSolver(new short[]{1300, 1059, 802, 1041, 785, 529});

      public ICell getCellForLeaves(int hydro) {
         return this.normalCells[hydro];
      }

      public ICell getCellForBranch(int radius, int meta) {
         return (ICell)(radius == 1 ? this.branchCell : CellNull.NULLCELL);
      }

      public SimpleVoxmap getLeafCluster() {
         return LeafClusters.deciduous;
      }

      public ICellSolver getCellSolver() {
         return this.deciduousSolver;
      }

      public int getDefaultHydration() {
         return 4;
      }
   };
   private final ICellKit conifer = new ICellKit() {
      private final ICell coniferBranch = new CellConiferBranch();
      private final ICell coniferTopBranch = new CellConiferTopBranch();
      private final ICell[] coniferLeafCells = new ICell[]{CellNull.NULLCELL, new CellConiferLeaf(1), new CellConiferLeaf(2), new CellConiferLeaf(3), new CellConiferLeaf(4)};
      private final CellKits.BasicSolver coniferSolver = new CellKits.BasicSolver(new short[]{1300, 1043, 786, 529});

      public ICell getCellForLeaves(int hydro) {
         return this.coniferLeafCells[hydro];
      }

      public ICell getCellForBranch(int radius, int meta) {
         if (meta == 1) {
            return this.coniferTopBranch;
         } else {
            return (ICell)(radius == 1 ? this.coniferBranch : CellNull.NULLCELL);
         }
      }

      public SimpleVoxmap getLeafCluster() {
         return LeafClusters.conifer;
      }

      public ICellSolver getCellSolver() {
         return this.coniferSolver;
      }

      public int getDefaultHydration() {
         return 4;
      }
   };
   private final ICellKit acacia = new ICellKit() {
      private final ICell acaciaBranch = new ICell() {
         final int[] map = new int[]{0, 3, 5, 5, 5, 5};

         public int getValue() {
            return 5;
         }

         public int getValueFromSide(EnumFacing side) {
            return this.map[side.ordinal()];
         }
      };
      private final ICell[] acaciaLeafCells = new ICell[]{CellNull.NULLCELL, new CellAcaciaLeaf(1), new CellAcaciaLeaf(2), new CellAcaciaLeaf(3), new CellAcaciaLeaf(4)};
      private final CellKits.BasicSolver acaciaSolver = new CellKits.BasicSolver(new short[]{1300, 1059, 1042, 786, 529});

      public ICell getCellForLeaves(int hydro) {
         return this.acaciaLeafCells[hydro];
      }

      public ICell getCellForBranch(int radius, int meta) {
         return (ICell)(radius == 1 ? this.acaciaBranch : CellNull.NULLCELL);
      }

      public SimpleVoxmap getLeafCluster() {
         return LeafClusters.acacia;
      }

      public ICellSolver getCellSolver() {
         return this.acaciaSolver;
      }

      public int getDefaultHydration() {
         return 4;
      }
   };
   private final ICellKit darkoak = new ICellKit() {
      private final ICell branchCell = new CellNormal(5);
      private final ICell[] darkOakLeafCells = new ICell[]{CellNull.NULLCELL, new CellDarkOakLeaf(1), new CellDarkOakLeaf(2), new CellDarkOakLeaf(3), new CellDarkOakLeaf(4)};
      private final CellKits.BasicSolver darkOakSolver = new CellKits.BasicSolver(new short[]{1300, 1059, 1042, 786, 529});

      public ICell getCellForLeaves(int hydro) {
         return this.darkOakLeafCells[hydro];
      }

      public ICell getCellForBranch(int radius, int meta) {
         return (ICell)(radius == 1 ? this.branchCell : CellNull.NULLCELL);
      }

      public SimpleVoxmap getLeafCluster() {
         return LeafClusters.darkoak;
      }

      public ICellSolver getCellSolver() {
         return this.darkOakSolver;
      }

      public int getDefaultHydration() {
         return 4;
      }
   };
   private final ICellKit bare = new ICellKit() {
      private final ICellSolver solver = new CellKits.BasicSolver(new short[0]);

      public ICell getCellForLeaves(int hydro) {
         return CellNull.NULLCELL;
      }

      public ICell getCellForBranch(int radius, int meta) {
         return CellNull.NULLCELL;
      }

      public SimpleVoxmap getLeafCluster() {
         return LeafClusters.bare;
      }

      public ICellSolver getCellSolver() {
         return this.solver;
      }

      public int getDefaultHydration() {
         return 0;
      }
   };
   private final ICellKit palm = new ICellKit() {
      private final ICell palmBranch = new ICell() {
         public int getValue() {
            return 5;
         }

         public int getValueFromSide(EnumFacing side) {
            return side == EnumFacing.UP ? this.getValue() : 0;
         }
      };
      private final ICell[] palmFrondCells = new ICell[]{CellNull.NULLCELL, new CellPalmFrond(1), new CellPalmFrond(2), new CellPalmFrond(3), new CellPalmFrond(4)};
      private final CellKits.BasicSolver palmSolver = new CellKits.BasicSolver(new short[]{1300, 1043, 786, 545});

      public ICell getCellForLeaves(int hydro) {
         return this.palmFrondCells[hydro];
      }

      public ICell getCellForBranch(int radius, int meta) {
         return this.palmBranch;
      }

      public SimpleVoxmap getLeafCluster() {
         return LeafClusters.palm;
      }

      public ICellSolver getCellSolver() {
         return this.palmSolver;
      }

      public int getDefaultHydration() {
         return 4;
      }
   };

   public static void preInit() {
      new CellKits();
   }

   public CellKits() {
      TreeRegistry.registerCellKit(new ResourceLocation("dynamictrees", "deciduous"), this.deciduous);
      TreeRegistry.registerCellKit(new ResourceLocation("dynamictrees", "conifer"), this.conifer);
      TreeRegistry.registerCellKit(new ResourceLocation("dynamictrees", "acacia"), this.acacia);
      TreeRegistry.registerCellKit(new ResourceLocation("dynamictrees", "darkoak"), this.darkoak);
      TreeRegistry.registerCellKit(new ResourceLocation("dynamictrees", "bare"), this.bare);
      TreeRegistry.registerCellKit(new ResourceLocation("dynamictrees", "palm"), this.palm);
   }

   public static int solveCell(int[] nv, short[] solution) {
      for(int d : solution) {
         if (nv[d >> 8 & 15] >= (d >> 4 & 15)) {
            return d & 15;
         }
      }

      return 0;
   }

   public static class BasicSolver implements ICellSolver {
      private final short[] codes;

      public BasicSolver(short[] codes) {
         this.codes = codes;
      }

      public int solve(ICell[] cells) {
         int[] nv = new int[16];

         for(EnumFacing dir : EnumFacing.VALUES) {
            ++nv[cells[dir.ordinal()].getValueFromSide(dir.getOpposite())];
         }

         return CellKits.solveCell(nv, this.codes);
      }
   }
}
