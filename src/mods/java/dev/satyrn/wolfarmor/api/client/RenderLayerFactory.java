package dev.satyrn.wolfarmor.api.client;

import dev.satyrn.wolfarmor.client.renderer.entity.layer.LayerWolfBackpack;
import dev.satyrn.wolfarmor.client.renderer.entity.layer.LayerWolfArmor;
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
      return new LayerWolfArmor(renderLiving);
   }

   /** @deprecated */
   @Nullable
   public static LayerRenderer<?> createBackpackLayer(@Nonnull RenderLiving<?> renderLiving) {
      return new LayerWolfBackpack(renderLiving);
   }
}
