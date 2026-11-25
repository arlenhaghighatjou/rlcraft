package ivorius.ivtoolkit.math;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import org.apache.commons.lang3.tuple.Pair;

public class MinecraftTransforms {
   public MinecraftTransforms() {
   }

   public static AxisAlignedTransform2D from(Rotation rotation, Mirror mirror) {
      return AxisAlignedTransform2D.from(from(rotation) + (mirror == Mirror.LEFT_RIGHT ? 2 : 0), mirror != Mirror.NONE);
   }

   public static Pair<Rotation, Mirror> to(AxisAlignedTransform2D transform) {
      return Pair.of(to(transform.getRotation()), transform.isMirrorX() ? Mirror.FRONT_BACK : Mirror.NONE);
   }

   public static void useAs(AxisAlignedTransform2D transform, BiConsumer<Rotation, Mirror> consumer) {
      consumer.accept(to(transform.getRotation()), transform.isMirrorX() ? Mirror.FRONT_BACK : Mirror.NONE);
   }

   public static <T> T map(AxisAlignedTransform2D transform, BiFunction<Rotation, Mirror, T> consumer) {
      return (T)consumer.apply(to(transform.getRotation()), transform.isMirrorX() ? Mirror.FRONT_BACK : Mirror.NONE);
   }

   public static Rotation to(int rotation) {
      switch(rotation) {
      case 0:
         return Rotation.NONE;
      case 1:
         return Rotation.CLOCKWISE_90;
      case 2:
         return Rotation.CLOCKWISE_180;
      case 3:
      default:
         return Rotation.COUNTERCLOCKWISE_90;
      }
   }

   public static int from(Rotation rotation) {
      switch(rotation) {
      case NONE:
         return 0;
      case CLOCKWISE_90:
         return 1;
      case CLOCKWISE_180:
         return 2;
      case COUNTERCLOCKWISE_90:
      default:
         return 3;
      }
   }
}
