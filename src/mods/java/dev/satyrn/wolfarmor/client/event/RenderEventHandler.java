package dev.satyrn.wolfarmor.client.event;

import dev.satyrn.wolfarmor.WolfArmorMod;
import dev.satyrn.wolfarmor.api.entity.passive.IArmoredWolf;
import dev.satyrn.wolfarmor.client.renderer.WolfRenderHelper;
import dev.satyrn.wolfarmor.mixin.accessors.RenderAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraftforge.client.event.RenderLivingEvent.Post;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@EventBusSubscriber({Side.CLIENT})
public class RenderEventHandler {
   public RenderEventHandler() {
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public void onRenderLiving(Post<EntityWolf> event) {
      Minecraft mc = Minecraft.getMinecraft();
      if (!mc.gameSettings.hideGUI) {
         if (WolfArmorMod.getConfig().getStatsRendered() && event != null && event.getEntity() instanceof EntityWolf && event.getEntity() instanceof IArmoredWolf) {
            EntityWolf wolf = (EntityWolf)event.getEntity();
            RenderLivingBase<EntityWolf> renderer = event.getRenderer();
            if (renderer.getRenderManager().pointedEntity == wolf) {
               double distanceFromViewport = wolf.getDistanceSq(renderer.getRenderManager().renderViewEntity);
               if (distanceFromViewport <= 4096.0D) {
                  boolean isSneaking = wolf.isSneaking();
                  float playerViewX = renderer.getRenderManager().playerViewX;
                  float playerViewY = renderer.getRenderManager().playerViewY;
                  boolean isThirdPerson = renderer.getRenderManager().options.thirdPersonView == 2;
                  float heightOffset = wolf.height + 0.5F - (isSneaking ? 0.25F : 0.0F);
                  boolean showNameplate = ((RenderAccessor)renderer).callCanRenderName(wolf);
                  WolfRenderHelper.drawStats(wolf, showNameplate, event.getX(), event.getY() + (double)heightOffset, event.getZ(), playerViewX, playerViewY, isThirdPerson, isSneaking);
               }
            }

         }
      }
   }
}
