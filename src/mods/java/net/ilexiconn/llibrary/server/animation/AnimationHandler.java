package net.ilexiconn.llibrary.server.animation;

import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.server.event.AnimationEvent;
import net.ilexiconn.llibrary.server.network.AnimationMessage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.lang3.ArrayUtils;

public enum AnimationHandler {
   INSTANCE;

   private AnimationHandler() {
   }

   public <T extends Entity & IAnimatedEntity> void sendAnimationMessage(T entity, Animation animation) {
      if (!entity.world.isRemote) {
         ((IAnimatedEntity)entity).setAnimation(animation);

         for(EntityPlayer trackingPlayer : ((WorldServer)entity.world).getEntityTracker().getTrackingPlayers(entity)) {
            LLibrary.NETWORK_WRAPPER.sendTo(new AnimationMessage(entity.getEntityId(), ArrayUtils.indexOf(((IAnimatedEntity)entity).getAnimations(), animation)), (EntityPlayerMP)trackingPlayer);
         }

      }
   }

   public <T extends Entity & IAnimatedEntity> void updateAnimations(T entity) {
      if (((IAnimatedEntity)entity).getAnimation() == null) {
         ((IAnimatedEntity)entity).setAnimation(IAnimatedEntity.NO_ANIMATION);
      } else if (((IAnimatedEntity)entity).getAnimation() != IAnimatedEntity.NO_ANIMATION) {
         if (((IAnimatedEntity)entity).getAnimationTick() == 0) {
            AnimationEvent event = new AnimationEvent.Start(entity, ((IAnimatedEntity)entity).getAnimation());
            if (!MinecraftForge.EVENT_BUS.post(event)) {
               this.sendAnimationMessage(entity, event.getAnimation());
            }
         }

         if (((IAnimatedEntity)entity).getAnimationTick() < ((IAnimatedEntity)entity).getAnimation().getDuration()) {
            ((IAnimatedEntity)entity).setAnimationTick(((IAnimatedEntity)entity).getAnimationTick() + 1);
            MinecraftForge.EVENT_BUS.post(new AnimationEvent.Tick(entity, ((IAnimatedEntity)entity).getAnimation(), ((IAnimatedEntity)entity).getAnimationTick()));
         }

         if (((IAnimatedEntity)entity).getAnimationTick() == ((IAnimatedEntity)entity).getAnimation().getDuration()) {
            if (!((IAnimatedEntity)entity).getAnimation().doesLoop()) {
               ((IAnimatedEntity)entity).setAnimation(IAnimatedEntity.NO_ANIMATION);
            }

            ((IAnimatedEntity)entity).setAnimationTick(0);
         }
      }

   }
}
