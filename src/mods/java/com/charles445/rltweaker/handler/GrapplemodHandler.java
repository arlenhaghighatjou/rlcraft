package com.charles445.rltweaker.handler;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.reflect.GrapplemodReflect;
import com.charles445.rltweaker.util.CompatUtil;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;
import java.util.function.Consumer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class GrapplemodHandler {
   private GrapplemodReflect reflector;

   public GrapplemodHandler() {
      try {
         this.reflector = new GrapplemodReflect();
         CompatUtil.wrapSpecificHandler("GMLivingFallEvent", (x$0) -> {
            new GrapplemodHandler.GMLivingFallEvent(x$0);
         }, "com.yyon.grapplinghook.CommonProxyClass", "onLivingFallEvent");
         CompatUtil.wrapSpecificHandler("GMLivingFallEvent", (x$0) -> {
            new GrapplemodHandler.GMLivingFallEvent(x$0);
         }, "com.yyon.grapplinghook.ServerProxyClass", "onLivingFallEvent");
         CompatUtil.wrapSpecificHandler("GMLivingFallEvent", (x$0) -> {
            new GrapplemodHandler.GMLivingFallEvent(x$0);
         }, "com.yyon.grapplinghook.ClientProxyClass", "onLivingFallEvent");
         MinecraftForge.EVENT_BUS.register(this);
      } catch (Exception var2) {
         RLTweaker.logger.error("Failed to setup GrapplemodHandler!", var2);
         ErrorUtil.logSilent("GrapplemodHandler Critical Setup Failure");
         if (var2 instanceof CriticalException) {
            throw new RuntimeException(var2);
         }
      }

   }

   @SubscribeEvent
   public void onEntityJoinWorld(EntityJoinWorldEvent event) {
      if (ModConfig.server.grapplemod.hinderGrapplingWhileFalling) {
         if (this.reflector.c_grappleArrow.isInstance(event.getEntity()) && event.getEntity() instanceof EntityThrowable) {
            EntityThrowable throwable = (EntityThrowable)event.getEntity();
            if (throwable.motionY >= 0.0D) {
               return;
            }

            Entity shooting = null;

            try {
               shooting = (Entity)this.reflector.f_grappleArrow_shootingEntity.get(this.reflector.c_grappleArrow.cast(throwable));
            } catch (IllegalAccessException | ClassCastException | IllegalArgumentException var5) {
               ErrorUtil.logSilent("GrapplemodHandler shootingEntity Failure");
               return;
            }

            if (shooting == null) {
               return;
            }

            if (shooting.motionY >= -0.10000000149011612D) {
               return;
            }

            throwable.motionY = Math.min(shooting.motionY + 0.5D, 0.0D);
         }

      }
   }

   @SubscribeEvent
   public void onPlayerTick(PlayerTickEvent event) {
      if (event.phase == Phase.START && ModConfig.server.grapplemod.grapplingHookFallDamage && event.player != null && !event.player.world.isRemote && this.reflector.getIsAttached(event.player.getEntityId()) && event.player.fallDistance > 3.0F && !event.player.onGround && event.player.motionY >= -0.6D) {
         event.player.fallDistance = 0.0F;
      }

   }

   public class GMLivingFallEvent {
      private IEventListener handler;

      public GMLivingFallEvent(IEventListener handler) {
         this.handler = handler;
         MinecraftForge.EVENT_BUS.register(this);
      }

      @SubscribeEvent
      public void onLivingFallEvent(LivingFallEvent event) {
         this.handler.invoke(event);
         if (event.isCanceled() && ModConfig.server.grapplemod.grapplingHookFallDamage) {
            event.setCanceled(false);
         }

      }
   }
}
