package xzeroair.trinkets.client.events;

import java.util.function.Consumer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent.Post;
import net.minecraftforge.client.event.RenderPlayerEvent.Pre;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xzeroair.trinkets.capabilities.Capabilities;

public class RenderEntitiesEvent {
   public RenderEntitiesEvent() {
   }

   @SubscribeEvent
   public void renderPlayerPre(Pre event) {
      EntityPlayer player = event.getEntityPlayer();
      Capabilities.getEntityProperties(player, (cap) -> {
         GlStateManager.pushMatrix();
         cap.getRaceHandler().doRenderPlayerPre(player, event.getX(), event.getY(), event.getZ(), event.getRenderer(), event.getPartialRenderTick());
      });
   }

   @SubscribeEvent
   public void renderPlayerPost(Post event) {
      EntityPlayer player = event.getEntityPlayer();
      Capabilities.getEntityProperties(player, (cap) -> {
         cap.getRaceHandler().doRenderPlayerPost(player, event.getX(), event.getY(), event.getZ(), event.getRenderer(), event.getPartialRenderTick());
         GlStateManager.popMatrix();
      });
   }

   @SubscribeEvent
   public void onRenderSpecialPre(net.minecraftforge.client.event.RenderLivingEvent.Specials.Pre event) {
      EntityLivingBase entity = event.getEntity();
      Capabilities.getEntityProperties(entity, (cap) -> {
         cap.getRaceHandler().doRenderLivingSpecialsPre(entity, event.getX(), event.getY(), event.getZ(), event.getRenderer(), event.getPartialRenderTick());
      });
   }

   @SubscribeEvent
   public void onRenderSpecialPost(net.minecraftforge.client.event.RenderLivingEvent.Specials.Post event) {
      EntityLivingBase entity = event.getEntity();
      Capabilities.getEntityProperties(entity, (cap) -> {
         cap.getRaceHandler().doRenderLivingSpecialsPost(entity, event.getX(), event.getY(), event.getZ(), event.getRenderer(), event.getPartialRenderTick());
      });
   }

   @SubscribeEvent
   public void onRenderLivingPre(net.minecraftforge.client.event.RenderLivingEvent.Pre event) {
      EntityLivingBase entity = event.getEntity();
      if (!(entity instanceof EntityPlayer)) {
         Capabilities.getEntityProperties(entity, (cap) -> {
            cap.getRaceHandler().doRenderLivingPre(entity, event.getX(), event.getY(), event.getZ(), event.getRenderer(), event.getPartialRenderTick());
         });
      }

   }

   @SubscribeEvent
   public void onRenderLivingPost(net.minecraftforge.client.event.RenderLivingEvent.Post event) {
      EntityLivingBase entity = event.getEntity();
      if (!(entity instanceof EntityPlayer)) {
         Capabilities.getEntityProperties(entity, (cap) -> {
            cap.getRaceHandler().doRenderLivingPost(entity, event.getX(), event.getY(), event.getZ(), event.getRenderer(), event.getPartialRenderTick());
         });
      }

   }
}
