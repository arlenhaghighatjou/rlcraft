package dev.satyrn.wolfarmor.compatibility.ebwizardry;

import dev.satyrn.wolfarmor.api.compatibility.Compatibility;
import dev.satyrn.wolfarmor.api.compatibility.CompatibilityProvider;
import dev.satyrn.wolfarmor.api.compatibility.Provider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Provider("ebwizardry")
public class WizardryProvider extends CompatibilityProvider {
   public WizardryProvider() {
   }

   @SideOnly(Side.CLIENT)
   public void loadComplete_Client(FMLLoadCompleteEvent event) {
      RenderManager manager = Minecraft.getMinecraft().getRenderManager();
      String className = "electroblob.wizardry.entity.living.EntitySpiritWolf";

      try {
         Class<?> spiritWolfClass = WizardryProvider.class.getClassLoader().loadClass("electroblob.wizardry.entity.living.EntitySpiritWolf");
         RenderLiving<?> renderWolf = (RenderLiving)manager.entityRenderMap.get(spiritWolfClass);
         if (renderWolf == null) {
            logger.warn("Renderer for {} could not be found.", "electroblob.wizardry.entity.living.EntitySpiritWolf");
         } else {
            renderWolf.addLayer(Compatibility.getArmorLayer(renderWolf, "dogslie"));
            renderWolf.addLayer(Compatibility.getBackpackLayer(renderWolf, "dogslie"));
         }
      } catch (ClassNotFoundException var6) {
         logger.warn("Failed to load class {}: {}", "electroblob.wizardry.entity.living.EntitySpiritWolf", var6.toString());
      }

   }
}
