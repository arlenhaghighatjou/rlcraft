package net.ilexiconn.llibrary.server.structure;

import java.util.Random;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.ArrayUtils;

public abstract class StructureGenerator {
   public static final EnumFacing[] CLOCKWISE_FACINGS = new EnumFacing[]{EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST};

   public StructureGenerator() {
   }

   public static EnumFacing getNextClockwise(EnumFacing facing) {
      int index = ArrayUtils.indexOf(CLOCKWISE_FACINGS, facing);
      if (index < 0) {
         throw new IllegalArgumentException();
      } else {
         return CLOCKWISE_FACINGS[(index + 1) % CLOCKWISE_FACINGS.length];
      }
   }

   public abstract void generate(World var1, BlockPos var2, Random var3);

   public abstract StructureGenerator rotate(EnumFacing var1, EnumFacing var2);

   public abstract StructureGenerator rotateTowards(EnumFacing var1);
}
