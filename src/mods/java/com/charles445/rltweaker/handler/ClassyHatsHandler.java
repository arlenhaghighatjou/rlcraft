package com.charles445.rltweaker.handler;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.reflect.ClassyHatsReflect;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClassyHatsHandler {
   ClassyHatsReflect reflector;

   public ClassyHatsHandler() {
      try {
         this.reflector = new ClassyHatsReflect();
         MinecraftForge.EVENT_BUS.register(this);
      } catch (Exception var2) {
         RLTweaker.logger.error("Failed to setup ClassyHatsHandler!", var2);
         ErrorUtil.logSilent("ClassyHats Critical Setup Failure");
         if (var2 instanceof CriticalException) {
            throw new RuntimeException(var2);
         }
      }

   }

   @SubscribeEvent
   public void onRightClickItem(RightClickItem event) {
      if (event.getHand() == EnumHand.OFF_HAND && ModConfig.server.classyhats.hatDupeFix && !event.getItemStack().isEmpty() && this.reflector.c_ItemHat.isInstance(event.getItemStack().getItem())) {
         event.setCanceled(true);
      }

   }
}
