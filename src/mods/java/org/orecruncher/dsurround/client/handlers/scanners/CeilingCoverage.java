package org.orecruncher.dsurround.client.handlers.scanners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;
import org.orecruncher.lib.chunk.ClientChunkCache;
import org.orecruncher.lib.math.MathStuff;

@SideOnly(Side.CLIENT)
public final class CeilingCoverage implements ITickable {
   private static final int SURVEY_INTERVAL = 4;
   private static final int INSIDE_SURVEY_RANGE = 3;
   private static final float INSIDE_THRESHOLD = 0.6306818F;
   private static final CeilingCoverage.Cell[] cells;
   private static final float TOTAL_POINTS;
   private boolean reallyInside = false;

   public CeilingCoverage() {
   }

   public void update() {
      if (EnvironStateHandler.EnvironState.getTickCounter() % 4 == 0) {
         if (EnvironStateHandler.EnvironState.getDimensionId() != -1 && !EnvironStateHandler.EnvironState.getDimensionInfo().alwaysOutside()) {
            BlockPos pos = EnvironStateHandler.EnvironState.getPlayerPosition();
            float score = 0.0F;

            for(CeilingCoverage.Cell cell : cells) {
               score += cell.score(pos);
            }

            float ceilingCoverageRatio = 1.0F - score / TOTAL_POINTS;
            this.reallyInside = ceilingCoverageRatio > 0.6306818F;
         } else {
            this.reallyInside = false;
         }
      }

   }

   public boolean isReallyInside() {
      return this.reallyInside;
   }

   static {
      List<CeilingCoverage.Cell> cellList = new ArrayList();

      for(int x = -3; x <= 3; ++x) {
         for(int z = -3; z <= 3; ++z) {
            cellList.add(new CeilingCoverage.Cell(new Vec3i(x, 0, z), 3));
         }
      }

      Collections.sort(cellList);
      cells = (CeilingCoverage.Cell[])cellList.toArray(new CeilingCoverage.Cell[0]);
      float totalPoints = 0.0F;

      for(CeilingCoverage.Cell c : cellList) {
         totalPoints += c.potentialPoints();
      }

      TOTAL_POINTS = totalPoints;
   }

   private static final class Cell implements Comparable<CeilingCoverage.Cell> {
      private final Vec3i offset;
      private final float points;
      private final MutableBlockPos working;

      public Cell(@Nonnull Vec3i offset, int range) {
         this.offset = offset;
         float xV = (float)(range - MathStuff.abs(offset.getX()) + 1);
         float zV = (float)(range - MathStuff.abs(offset.getZ()) + 1);
         float candidate = Math.min(xV, zV);
         this.points = candidate * candidate;
         this.working = new MutableBlockPos();
      }

      public float potentialPoints() {
         return this.points;
      }

      public float score(@Nonnull BlockPos playerPos) {
         this.working.setPos(playerPos.getX() + this.offset.getX(), playerPos.getY() + this.offset.getY(), playerPos.getZ() + this.offset.getZ());
         World world = EnvironStateHandler.EnvironState.getWorld();
         int playerHeight = Math.max(playerPos.getY() + 1, 0);
         this.working.setPos(ClientChunkCache.instance().getPrecipitationHeight(this.working));

         while(this.working.getY() > playerHeight) {
            IBlockState state = ClientChunkCache.instance().getBlockState(this.working);
            if (state.getMaterial().blocksMovement() && !state.getBlock().isLeaves(state, world, this.working) && !state.getBlock().isFoliage(world, this.working)) {
               return 0.0F;
            }

            this.working.setY(this.working.getY() - 1);
         }

         return this.points;
      }

      public int compareTo(@Nonnull CeilingCoverage.Cell cell) {
         return -Float.compare(this.potentialPoints(), cell.potentialPoints());
      }

      @Nonnull
      public String toString() {
         StringBuilder builder = new StringBuilder();
         builder.append(this.offset.toString());
         builder.append(" points: ").append(this.points);
         return builder.toString();
      }
   }
}
