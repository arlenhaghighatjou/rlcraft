package com.creativemd.creativecore.client.sound;

import com.creativemd.creativecore.common.utils.mc.TickUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntitySound extends MovingSound {
   public Entity entity;

   public EntitySound(SoundEvent soundIn, Entity entity, float volume, float pitch, SoundCategory categoryIn) {
      super(soundIn, categoryIn);
      this.entity = entity;
      this.volume = volume;
      this.pitch = pitch;
   }

   public void update() {
      Entity view = Minecraft.getMinecraft().getRenderViewEntity();
      float partialTicks = TickUtils.getPartialTickTime();
      Vec3d viewVec = view.getPositionEyes(partialTicks);
      AxisAlignedBB bb = this.entity.getEntityBoundingBox();
      if (bb.contains(viewVec)) {
         this.xPosF = (float)viewVec.x;
         this.yPosF = (float)viewVec.y;
         this.zPosF = (float)viewVec.z;
      } else {
         if (viewVec.x > bb.minX) {
            if (viewVec.x > bb.maxX) {
               this.xPosF = (float)bb.maxX;
            } else {
               this.xPosF = (float)viewVec.x;
            }
         } else {
            this.xPosF = (float)bb.minX;
         }

         if (viewVec.y > bb.minY) {
            if (viewVec.y > bb.maxY) {
               this.yPosF = (float)bb.maxY;
            } else {
               this.yPosF = (float)viewVec.y;
            }
         } else {
            this.yPosF = (float)bb.minY;
         }

         if (viewVec.z > bb.minZ) {
            if (viewVec.z > bb.maxZ) {
               this.zPosF = (float)bb.maxZ;
            } else {
               this.zPosF = (float)viewVec.z;
            }
         } else {
            this.zPosF = (float)bb.minZ;
         }

      }
   }
}
