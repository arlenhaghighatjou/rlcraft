package hunternif.mc.atlas.ext.watcher;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.util.math.BlockPos;

public class WatcherPos {
   public static final Pattern POS_PATTERN = Pattern.compile("\\[([-\\d]+),([-\\d]+)\\]");
   private final int x;
   private final int z;

   public WatcherPos(int x, int z) {
      this.x = x;
      this.z = z;
   }

   public WatcherPos(BlockPos pos) {
      this(pos.getX(), pos.getY());
   }

   public WatcherPos(String coords) {
      Matcher matcher = POS_PATTERN.matcher(coords);
      if (!matcher.matches()) {
         throw new IllegalArgumentException("Improper coordinate format provided: " + coords);
      } else {
         this.x = Integer.parseInt(matcher.group(1));
         this.z = Integer.parseInt(matcher.group(2));
      }
   }

   public int getX() {
      return this.x;
   }

   public int getZ() {
      return this.z;
   }

   public String toString() {
      return "[" + this.x + ", " + this.z + "]";
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof WatcherPos)) {
         return false;
      } else {
         WatcherPos that = (WatcherPos)o;
         if (this.x != that.x) {
            return false;
         } else {
            return this.z == that.z;
         }
      }
   }

   public int hashCode() {
      int result = this.x;
      result = 31 * result + this.z;
      return result;
   }
}
