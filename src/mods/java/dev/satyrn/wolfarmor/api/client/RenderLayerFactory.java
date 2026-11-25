package dev.satyrn.wolfarmor.api.client;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** @deprecated */
@SideOnly(Side.CLIENT)
@Deprecated
public abstract class RenderLayerFactory {
   public RenderLayerFactory() {
   }

   /** @deprecated */
   @Nullable
   public static LayerRenderer<?> createArmorLayer(@Nonnull RenderLiving<?> renderLiving) {
      try {
         Class<?> clazz = RenderLayerFactory.class.getClassLoader().loadClass("dev.satyrn.wolfarmor.client.renderer.entity.layer.LayerWolfArmor");
         return (LayerRenderer)clazz.getConstructor(RenderLiving.class).newInstance(renderLiving);
      } catch (Exception var2) {
         return null;
      }
   }

   /** @deprecated */
   @Nullable
   public static LayerRenderer<?> createBackpackLayer(@Nonnull RenderLiving<?> renderLiving) {
      try {
         Class<?> clazz = RenderLayerFactory.class.getClassLoader().loadClass("dev.satyrn.wolfarmor.client.renderer.entity.layer.LayerWolfBackpack");
         return (LayerRenderer)clazz.getConstructor(RenderLiving.class).newInstance(renderLiving);
      } catch (Exception var2) {
         return null;
      }
   }
}
