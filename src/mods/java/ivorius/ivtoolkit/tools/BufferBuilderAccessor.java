package ivorius.ivtoolkit.tools;

import java.lang.reflect.Field;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class BufferBuilderAccessor {
   protected static Field xOffset;
   protected static Field yOffset;
   protected static Field zOffset;

   public BufferBuilderAccessor() {
   }

   protected static Field getXOffset() {
      return xOffset != null ? xOffset : (xOffset = ReflectionHelper.findField(BufferBuilder.class, new String[]{"xOffset", "xOffset"}));
   }

   protected static Field getYOffset() {
      return yOffset != null ? yOffset : (yOffset = ReflectionHelper.findField(BufferBuilder.class, new String[]{"yOffset", "yOffset"}));
   }

   protected static Field getZOffset() {
      return zOffset != null ? zOffset : (zOffset = ReflectionHelper.findField(BufferBuilder.class, new String[]{"zOffset", "zOffset"}));
   }

   public static void addTranslation(BufferBuilder renderer, double x, double y, double z) {
      try {
         renderer.setTranslation(getXOffset().getDouble(renderer) + x, getYOffset().getDouble(renderer) + y, getZOffset().getDouble(renderer) + z);
      } catch (IllegalAccessException var8) {
         var8.printStackTrace();
      }

   }
}
