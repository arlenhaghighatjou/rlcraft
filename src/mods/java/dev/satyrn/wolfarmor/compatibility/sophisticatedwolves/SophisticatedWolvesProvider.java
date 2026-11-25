package dev.satyrn.wolfarmor.compatibility.sophisticatedwolves;

import dev.satyrn.wolfarmor.api.compatibility.Compatibility;
import dev.satyrn.wolfarmor.api.compatibility.CompatibilityProvider;
import dev.satyrn.wolfarmor.api.compatibility.Provider;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Provider("sophisticatedwolves")
public class SophisticatedWolvesProvider extends CompatibilityProvider {
   public SophisticatedWolvesProvider() {
   }

   @SideOnly(Side.CLIENT)
   public void loadComplete_Client(FMLLoadCompleteEvent event) {
      String className = "sophisticated_wolves.entity.EntitySophisticatedWolf";
      Map<Class<? extends Entity>, Render<? extends Entity>> pendingRenderers = new HashMap();
      RenderingRegistry.loadEntityRenderers(pendingRenderers);

      try {
         Class<?> entitySophisticatedWolfClass = SophisticatedWolvesProvider.class.getClassLoader().loadClass("sophisticated_wolves.entity.EntitySophisticatedWolf");
         RenderLiving<?> render = (RenderLiving)pendingRenderers.get(entitySophisticatedWolfClass);
         if (render == null) {
            logger.warn("Renderer for {} could not be found.", "sophisticated_wolves.entity.EntitySophisticatedWolf");
         } else {
            render.addLayer(Compatibility.getArmorLayer(render, "!dogslie"));
            render.addLayer(Compatibility.getBackpackLayer(render, "!dogslie"));
         }
      } catch (ClassNotFoundException var6) {
         logger.warn("Failed to load class {}: {}", "sophisticated_wolves.entity.EntitySophisticatedWolf", var6.toString());
      }

   }
}
