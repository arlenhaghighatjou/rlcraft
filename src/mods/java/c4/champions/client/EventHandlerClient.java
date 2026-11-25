package c4.champions.client;

import c4.champions.common.capability.CapabilityChampionship;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.config.ConfigHandler;
import c4.champions.common.init.ChampionsRegistry;
import c4.champions.common.util.ChampionHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandlerClient {
   public EventHandlerClient() {
   }

   @SubscribeEvent
   public void onMovementInput(InputUpdateEvent evt) {
      if (evt.getEntityLiving().isPotionActive(ChampionsRegistry.jailed)) {
         MovementInput input = evt.getMovementInput();
         input.sneak = false;
         input.jump = false;
         input.moveForward = 0.0F;
         input.moveStrafe = 0.0F;
      }

   }

   @SubscribeEvent
   public void renderChampionHealth(Pre evt) {
      if (evt.getType() == ElementType.BOSSHEALTH) {
         RayTraceResult mouseOver = ClientUtil.getMouseOver(evt.getPartialTicks(), (float)ConfigHandler.client.healthVisibility);
         if (mouseOver != null && mouseOver.typeOfHit == Type.ENTITY) {
            Entity entity = mouseOver.entityHit;
            if (ChampionHelper.isValidChampion(entity)) {
               EntityLiving living = (EntityLiving)entity;
               IChampionship chp = CapabilityChampionship.getChampionship(living);
               if (chp != null && ChampionHelper.isElite(chp.getRank()) && !ConfigHandler.hideEffects) {
                  ClientUtil.renderChampionHealth(living, chp);
                  evt.setCanceled(true);
               }
            }
         }
      }

   }
}
