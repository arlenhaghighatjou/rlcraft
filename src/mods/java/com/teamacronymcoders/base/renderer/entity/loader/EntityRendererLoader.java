package com.teamacronymcoders.base.renderer.entity.loader;

import com.teamacronymcoders.base.modulesystem.ModuleHandler;
import com.teamacronymcoders.base.util.ClassLoading;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityRendererLoader {
   public EntityRendererLoader() {
   }

   public static void loadRenderersFor(ASMDataTable table, ModuleHandler handler) {
      ClassLoading.getInstances(table, EntityRenderer.class, IEntityRenderer.class, (aClass) -> {
         EntityRenderer entityRenderer = (EntityRenderer)aClass.getAnnotation(EntityRenderer.class);
         boolean load = false;
         if (entityRenderer != null) {
            String entityRendererHandler = entityRenderer.handler().trim();
            load = entityRendererHandler.isEmpty() || entityRendererHandler.equalsIgnoreCase(handler.getName());
            load = load & handler.isModuleEnabled(entityRenderer.module());
         }

         return load;
      }).forEach((entityRender) -> {
         RenderingRegistry.registerEntityRenderingHandler(entityRender.getEntityClass(), entityRender.getRenderFactory());
      });
   }
}
