package com.jamieswhiteshirt.trumpetskeleton.client;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.jamieswhiteshirt.trumpetskeleton.client.renderer.entity.RenderTrumpetSkeleton;
import com.jamieswhiteshirt.trumpetskeleton.common.CommonProxy;
import com.jamieswhiteshirt.trumpetskeleton.common.TrumpetSkeletonItems;
import com.jamieswhiteshirt.trumpetskeleton.common.entity.EntityTrumpetSkeleton;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
   public ClientProxy() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      this.register(MinecraftForge.EVENT_BUS);
      RenderingRegistry.registerEntityRenderingHandler(EntityTrumpetSkeleton.class, RenderTrumpetSkeleton::new);
   }

   public void register(EventBus bus) {
      bus.register(ModelRegistryEvent.class, this::registerModels);
   }

   public void registerModels(ModelRegistryEvent event) {
      ModelLoader.setCustomModelResourceLocation(TrumpetSkeletonItems.TRUMPET, 0, new ModelResourceLocation(TrumpetSkeletonItems.TRUMPET.getRegistryName(), "inventory"));
   }
}
