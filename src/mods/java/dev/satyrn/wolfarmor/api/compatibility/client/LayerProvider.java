package dev.satyrn.wolfarmor.api.compatibility.client;

import dev.satyrn.wolfarmor.client.renderer.entity.layer.LayerWolfBackpack;
import dev.satyrn.wolfarmor.client.renderer.entity.layer.LayerWolfArmor;
import dev.satyrn.wolfarmor.api.compatibility.Provider;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerProvider extends Provider.ProviderType {
   public LayerProvider() {
   }

   public boolean getProvidesArmorLayer() {
      return true;
   }

   public boolean getProvidesBackpackLayer() {
      return true;
   }

   @Nullable
   public LayerRenderer<?> getArmorLayer(@Nonnull RenderLiving<?> entityRenderer) {
      return new LayerWolfArmor(entityRenderer);
   }

   @Nullable
   public LayerRenderer<?> getBackpackLayer(@Nonnull RenderLiving<?> entityRenderer) {
      return new LayerWolfBackpack(entityRenderer);
   }
}
