package noppes.vc.client.fx;

import net.minecraft.client.particle.ParticlePortal;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.math.MathHelper;

public class EntityElementalStaffFX extends ParticlePortal {
   double x;
   double y;
   double z;
   EntityLivingBase player;

   public EntityElementalStaffFX(EntityLivingBase player, double d, double d1, double d2, double f1, double f2, double f3, int color) {
      super(player.world, player.posX + d, player.posY + d1, player.posZ + d2, f1, f2, f3);
      this.player = player;
      this.x = d;
      this.y = d1;
      this.z = d2;
      float[] colors;
      if (color <= 15) {
         colors = EntitySheep.getDyeRgb(EnumDyeColor.byDyeDamage(color));
      } else {
         colors = new float[]{(float)(color >> 16 & 255) / 255.0F, (float)(color >> 8 & 255) / 255.0F, (float)(color & 255) / 255.0F};
      }

      this.particleRed = colors[0];
      this.particleGreen = colors[1];
      this.particleBlue = colors[2];
      this.particleMaxAge = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
   }

   public void onUpdate() {
      if (this.player.isDead) {
         this.setExpired();
      } else {
         this.prevPosX = this.posX;
         this.prevPosY = this.posY;
         this.prevPosZ = this.posZ;
         float var1 = (float)this.particleAge / (float)this.particleMaxAge;
         float var7 = -var1 + var1 * var1 * 2.0F;
         float var8 = 1.0F - var7;
         double dx = (double)(-MathHelper.sin((float)((double)(this.player.rotationYaw / 180.0F) * 3.141592653589793D)) * MathHelper.cos((float)((double)(this.player.rotationPitch / 180.0F) * 3.141592653589793D)));
         double dz = (double)(MathHelper.cos((float)((double)(this.player.rotationYaw / 180.0F) * 3.141592653589793D)) * MathHelper.cos((float)((double)(this.player.rotationPitch / 180.0F) * 3.141592653589793D)));
         this.posX = this.player.posX + this.x + dx + this.motionX * (double)var8;
         this.posY = this.player.posY + this.y + this.motionY * (double)var8 + (double)(1.0F - var1) - (double)(this.player.rotationPitch / 40.0F);
         this.posZ = this.player.posZ + this.z + dz + this.motionZ * (double)var8;
         if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
         }

      }
   }
}
