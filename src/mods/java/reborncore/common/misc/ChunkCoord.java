package reborncore.common.misc;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.ChunkEvent;
import org.apache.commons.lang3.tuple.ImmutablePair;
import reborncore.common.util.WorldUtils;

public class ChunkCoord implements Comparable<ChunkCoord> {
   private final ImmutablePair<Integer, Integer> data;

   private ChunkCoord(int x, int z) {
      this.data = ImmutablePair.of(Integer.valueOf(x), Integer.valueOf(z));
   }

   private ChunkCoord(ChunkEvent event) {
      this(event.getChunk().x, event.getChunk().z);
   }

   public static ChunkCoord of(int x, int z) {
      return new ChunkCoord(x, z);
   }

   public static ChunkCoord of(BlockPos coord) {
      return new ChunkCoord(coord.getX() >> 4, coord.getZ() >> 4);
   }

   public static ChunkCoord of(ChunkEvent event) {
      return new ChunkCoord(event);
   }

   public boolean exists(World world) {
      return WorldUtils.chunkExists(world, ((Integer)this.data.getLeft()).intValue(), ((Integer)this.data.getRight()).intValue());
   }

   public boolean containsWorldCoord(BlockPos coord) {
      return this.equals(of(coord));
   }

   public int getX() {
      return ((Integer)this.data.left).intValue();
   }

   public int getZ() {
      return ((Integer)this.data.right).intValue();
   }

   public BlockPos localToWorldCoords(BlockPos coord) {
      return new BlockPos((((Integer)this.data.left).intValue() << 4) + coord.getX(), coord.getY(), (((Integer)this.data.right).intValue() << 4) + coord.getZ());
   }

   public int hashCode() {
      return Objects.hashCode(new Object[]{this.data.left, this.data.right});
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ChunkCoord that = (ChunkCoord)o;
         return ((Integer)this.data.left).equals(that.data.left) && ((Integer)this.data.right).equals(that.data.right);
      } else {
         return false;
      }
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("X", this.data.left).add("Z", this.data.right).toString();
   }

   public int compareTo(ChunkCoord o) {
      return ((Integer)this.data.left).equals(o.data.left) ? ((Integer)this.data.right).compareTo((Integer)o.data.right) : ((Integer)this.data.left).compareTo((Integer)o.data.left);
   }
}
