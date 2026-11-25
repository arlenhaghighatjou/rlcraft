package org.orecruncher.lib.compat;

import javax.annotation.Nonnull;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.lib.ReflectedField;

@SideOnly(Side.CLIENT)
public final class EntityRendererUtil {
   private static final ReflectedField.BooleanField<EntityRenderer> drawblockOutline = new ReflectedField.BooleanField<EntityRenderer>(EntityRenderer.class, "drawBlockOutline", "drawBlockOutline");

   public EntityRendererUtil() {
   }

   public static boolean getDrawBlockOutline(@Nonnull EntityRenderer renderer) {
      return drawblockOutline.get(renderer);
   }

   public static void setDrawBlockOutline(@Nonnull EntityRenderer renderer, boolean value) {
      drawblockOutline.set(renderer, value);
   }
}
