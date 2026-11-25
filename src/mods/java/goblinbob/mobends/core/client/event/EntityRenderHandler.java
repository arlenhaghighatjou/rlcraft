package goblinbob.mobends.core.client.event;

import goblinbob.mobends.core.bender.EntityBender;
import goblinbob.mobends.core.bender.EntityBenderRegistry;
import goblinbob.mobends.core.data.LivingEntityData;
import goblinbob.mobends.core.mutators.Mutator;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderLivingEvent.Post;
import net.minecraftforge.client.event.RenderLivingEvent.Pre;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EntityRenderHandler {
   public EntityRenderHandler() {
   }

   @SubscribeEvent
   public void beforeLivingRender(Pre<? extends EntityLivingBase> event) {
      EntityLivingBase living = event.getEntity();
      EntityBender<EntityLivingBase> entityBender = EntityBenderRegistry.instance.<EntityLivingBase>getForEntity(living);
      if (entityBender != null) {
         RenderLivingBase<?> renderer = event.getRenderer();
         float pt = event.getPartialRenderTick();
         GlStateManager.pushMatrix();
         if (entityBender.isAnimated()) {
            if (entityBender.applyMutation(renderer, living, pt)) {
               Mutator mutator = entityBender.getMutator(renderer);
               LivingEntityData<EntityLivingBase> data = mutator.getData(living);
               entityBender.beforeRender(data, living, pt);
            }
         } else {
            entityBender.deapplyMutation(event.getRenderer(), living);
         }

      }
   }

   @SubscribeEvent
   public void afterLivingRender(Post<? extends EntityLivingBase> event) {
      EntityBender<EntityLivingBase> entityBender = EntityBenderRegistry.instance.<EntityLivingBase>getForEntity(event.getEntity());
      if (entityBender != null) {
         entityBender.afterRender(event.getEntity(), event.getPartialRenderTick());
         GlStateManager.popMatrix();
      }
   }
}
