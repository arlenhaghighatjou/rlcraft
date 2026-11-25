package goblinbob.mobends.standard.client;

import goblinbob.mobends.core.Core;
import goblinbob.mobends.core.addon.AddonHelper;
import goblinbob.mobends.standard.DefaultAddon;
import goblinbob.mobends.standard.client.event.RenderingEventHandler;
import goblinbob.mobends.standard.client.renderer.entity.RenderBendsSpectralArrow;
import goblinbob.mobends.standard.client.renderer.entity.RenderBendsTippedArrow;
import goblinbob.mobends.standard.main.CommonProxy;
import net.minecraft.entity.projectile.EntitySpectralArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
   public ClientProxy() {
   }

   public void preInit() {
      RenderingRegistry.registerEntityRenderingHandler(EntitySpectralArrow.class, RenderBendsSpectralArrow::new);
      RenderingRegistry.registerEntityRenderingHandler(EntityTippedArrow.class, RenderBendsTippedArrow::new);
   }

   public void init() {
      MinecraftForge.EVENT_BUS.register(new RenderingEventHandler());
      AddonHelper.registerAddon("mobends", new DefaultAddon());
   }

   public void postInit() {
   }

   public void createCore() {
      Core.createAsClient();
   }
}
