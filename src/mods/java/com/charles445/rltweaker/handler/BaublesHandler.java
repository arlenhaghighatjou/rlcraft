package com.charles445.rltweaker.handler;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.reflect.BaublesReflect;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BaublesHandler {
   BaublesReflect reflector;

   public BaublesHandler() {
      try {
         this.reflector = new BaublesReflect();
         MinecraftForge.EVENT_BUS.register(this);
      } catch (Exception var2) {
         RLTweaker.logger.error("Failed to setup BaublesHandler!", var2);
         ErrorUtil.logSilent("Baubles Critical Setup Failure");
         if (var2 instanceof CriticalException) {
            throw new RuntimeException(var2);
         }
      }

   }

   @SubscribeEvent
   public void onRightClickItem(RightClickItem event) {
      if (event.getHand() == EnumHand.OFF_HAND && ModConfig.server.baubles.minersRingDupeFix && !event.getItemStack().isEmpty() && this.reflector.c_ItemRing.isInstance(event.getItemStack().getItem())) {
         event.setCanceled(true);
      }

   }
}
