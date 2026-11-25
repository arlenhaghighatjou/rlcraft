package me.ichun.mods.ichunutil.common.grab.handlers;

import me.ichun.mods.ichunutil.common.grab.GrabHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;

public class GrabbedFallingBlockHandler implements GrabHandler.GrabbedEntityHandler {
   public GrabbedFallingBlockHandler() {
   }

   public boolean eligible(Entity grabbed) {
      return grabbed instanceof EntityFallingBlock;
   }

   public void handle(GrabHandler grabHandler) {
      ((EntityFallingBlock)grabHandler.grabbed).fallTime = 2;
   }
}
