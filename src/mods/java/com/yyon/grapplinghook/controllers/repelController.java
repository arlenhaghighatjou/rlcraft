package com.yyon.grapplinghook.controllers;

import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.vec;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class repelController extends grappleController {
   public repelController(int arrowId, int entityId, World world, vec pos, int id) {
      super(arrowId, entityId, world, pos, id, (GrappleCustomization)null);
      this.playermovementmult = 1.0D;
   }

   public void updatePlayerPos() {
      Entity entity = this.entity;
      if (this.attached && entity != null) {
         this.normalGround(false);
         this.normalCollisions(false);
         vec playerpos = vec.positionvec(entity);
         if (entity instanceof EntityPlayer) {
            if (this.playersneak) {
               this.motion.mult_ip(0.95D);
            }

            this.applyPlayerMovement();
         }

         vec blockpush = this.check_repel(playerpos, entity.world);
         blockpush.mult_ip(0.5D);
         blockpush = new vec(blockpush.x * 0.5D, blockpush.y * 2.0D, blockpush.z * 0.5D);
         this.motion.add_ip(blockpush);
         if (!entity.onGround) {
            this.motion.add_ip(0.0D, -0.05D, 0.0D);
         }

         entity.motionX = this.motion.x;
         entity.motionY = this.motion.y;
         entity.motionZ = this.motion.z;
         this.updateServerPos();
      }

   }
}
