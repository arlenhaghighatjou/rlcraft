package dev.satyrn.wolfarmor.compatibility.dogslie;

import dev.satyrn.wolfarmor.api.compatibility.Provider;
import dev.satyrn.wolfarmor.api.compatibility.client.LayerProvider;
import dev.satyrn.wolfarmor.compatibility.dogslie.layer.DogsLieBackpackLayer;
import dev.satyrn.wolfarmor.compatibility.dogslie.layer.DogsLieWolfArmorLayer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Provider(
   value = "dogslie",
   priority = 255
)
public class LetSleepingDogsLieLayerProvider extends LayerProvider {
   public LetSleepingDogsLieLayerProvider() {
   }

   public boolean getProvidesArmorLayer() {
      return true;
   }

   public boolean getProvidesBackpackLayer() {
      return true;
   }

   public LayerRenderer<?> getArmorLayer(@Nonnull RenderLiving<?> entityRenderer) {
      return new DogsLieWolfArmorLayer(entityRenderer);
   }

   @Nullable
   public LayerRenderer<?> getBackpackLayer(@Nonnull RenderLiving<?> entityRenderer) {
      return new DogsLieBackpackLayer(entityRenderer);
   }
}
