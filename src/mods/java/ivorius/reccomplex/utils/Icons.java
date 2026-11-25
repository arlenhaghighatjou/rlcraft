package ivorius.reccomplex.utils;

import ivorius.ivtoolkit.rendering.Icon;
import java.util.List;
import net.minecraft.util.math.MathHelper;

public class Icons {
   public static final Icon WHOLE_TEXTURE = Icon.fromCoords(0.0F, 1.0F, 0.0F, 1.0F);

   public Icons() {
   }

   public static Icon from(float minU, float minV, float maxU, float maxV) {
      return Icon.fromCoords(minU, maxU, minV, maxV);
   }

   public static <T> T frame(T[] icons, float ticks) {
      return (T)icons[(MathHelper.floor(ticks) % icons.length + icons.length) % icons.length];
   }

   public static <T> T frame(List<T> icons, float ticks) {
      return (T)icons.get((MathHelper.floor(ticks) % icons.size() + icons.size()) % icons.size());
   }
}
