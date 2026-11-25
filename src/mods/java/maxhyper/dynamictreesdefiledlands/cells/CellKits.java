package maxhyper.dynamictreesdefiledlands.cells;

import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import com.ferreusveritas.dynamictrees.api.cells.CellNull;
import com.ferreusveritas.dynamictrees.api.cells.ICell;
import com.ferreusveritas.dynamictrees.api.cells.ICellKit;
import com.ferreusveritas.dynamictrees.api.cells.ICellSolver;
import com.ferreusveritas.dynamictrees.cells.CellNormal;
import com.ferreusveritas.dynamictrees.cells.CellKits.BasicSolver;
import com.ferreusveritas.dynamictrees.util.SimpleVoxmap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class CellKits {
   private final ICellKit sparse = new ICellKit() {
      private final ICell sparseBranch = new CellSparseBranch();
      private final ICell sparseLeaves = new CellNormal(1);
      private final ICellSolver solver = new BasicSolver(new short[]{529});

      public ICell getCellForLeaves(int hydro) {
         return (ICell)(hydro > 0 ? this.sparseLeaves : CellNull.NULLCELL);
      }

      public ICell getCellForBranch(int radius, int meta) {
         return (ICell)(radius == 1 ? this.sparseBranch : CellNull.NULLCELL);
      }

      public SimpleVoxmap getLeafCluster() {
         return CellKits.sparseCluster;
      }

      public ICellSolver getCellSolver() {
         return this.solver;
      }

      public int getDefaultHydration() {
         return 1;
      }
   };
   public static final SimpleVoxmap sparseCluster = (new SimpleVoxmap(3, 2, 3, new byte[]{0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0})).setCenter(new BlockPos(1, 0, 1));

   public static void init() {
      new CellKits();
   }

   public CellKits() {
      TreeRegistry.registerCellKit(new ResourceLocation("dynamictreesdefiledlands", "sparse"), this.sparse);
   }
}
