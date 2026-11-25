package prospector.traverse.util;

import net.minecraft.block.Block;
import prospector.traverse.shadow.shootingstar.ShootingStar;

public class TUtils {
   public TUtils() {
   }

   public static Block getBlock(String name) {
      return ShootingStar.getBlock("traverse", name);
   }
}
