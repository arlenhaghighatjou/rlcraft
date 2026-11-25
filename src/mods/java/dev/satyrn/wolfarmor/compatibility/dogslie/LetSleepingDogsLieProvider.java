package dev.satyrn.wolfarmor.compatibility.dogslie;

import dev.satyrn.wolfarmor.api.compatibility.CompatibilityProvider;
import dev.satyrn.wolfarmor.api.compatibility.Provider;
import dev.satyrn.wolfarmor.api.compatibility.client.LayerProvider;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Provider("dogslie")
public class LetSleepingDogsLieProvider extends CompatibilityProvider {
   public LetSleepingDogsLieProvider() {
   }

   @Nonnull
   @SideOnly(Side.CLIENT)
   public List<LayerProvider> getLayerProviders() {
      LayerProvider letSleepingDogsLie = new LetSleepingDogsLieLayerProvider();
      List<LayerProvider> providers = new ArrayList();
      providers.add(letSleepingDogsLie);
      return providers;
   }
}
