package boni.dummy;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import boni.dummy.client.RenderDummy;
import boni.dummy.client.RenderFloatingNumber;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
   public ClientProxy() {
   }

   public void preinit() {
      this.register(MinecraftForge.EVENT_BUS);
      RenderingRegistry.registerEntityRenderingHandler(EntityDummy.class, new IRenderFactory<EntityDummy>() {
         public Render<? super EntityDummy> createRenderFor(RenderManager manager) {
            return new RenderDummy(manager);
         }
      });
      RenderingRegistry.registerEntityRenderingHandler(EntityFloatingNumber.class, new IRenderFactory<EntityFloatingNumber>() {
         public Render<? super EntityFloatingNumber> createRenderFor(RenderManager manager) {
            return new RenderFloatingNumber(manager);
         }
      });
   }

   public void register(EventBus bus) {
      bus.register(ModelRegistryEvent.class, this::models);
   }

   public void models(ModelRegistryEvent e) {
      String loc = "testdummy:dummy";
      ModelLoader.setCustomModelResourceLocation(TestDummyMod.itemDummy, 0, new ModelResourceLocation("testdummy:dummy", "inventory"));
   }
}
