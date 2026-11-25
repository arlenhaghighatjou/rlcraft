package xzeroair.trinkets.util.handlers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.capabilities.race.EntityProperties;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.helpers.StringUtils;
import xzeroair.trinkets.util.helpers.TrinketReflectionHelper;

public class SizeHandler {
   public SizeHandler() {
   }

   public static void setSize(EntityLivingBase entity, float TLHeight, float TLWidth) {
      if (!Trinkets.ArtemisLib || !TrinketsConfig.compat.artemislib) {
         if (!entity.isChild()) {
            boolean flying = entity instanceof EntityPlayer && ((EntityPlayer)entity).capabilities.isFlying;
            float width = entity.width;
            float height = entity.height;
            if (entity.isSneaking()) {
               width = TLWidth;
               height = !flying ? TLHeight * 0.92F : TLHeight;
            } else if (entity.isElytraFlying()) {
               width = TLWidth;
               height = TLHeight * 0.2F;
            } else if (entity.isPlayerSleeping()) {
               width = 0.2F;
               height = 0.2F;
            } else if (entity.isRiding()) {
               width = TLWidth;
               height = TLHeight;
            } else {
               width = TLWidth;
               height = TLHeight;
            }

            float Wclamp = 0.252F;
            float Hclamp = 0.45F;
            if (entity instanceof EntityPlayer) {
               Wclamp = 0.3F;
               Hclamp = 0.45F;
            } else {
               Wclamp = 0.3F;
               Hclamp = 0.45F;
            }

            width = (float)MathHelper.clamp(StringUtils.getAccurateDouble((double)width), (double)Wclamp, 5.400000095367432D);
            height = (float)MathHelper.clamp(StringUtils.getAccurateDouble((double)height), (double)Hclamp, 5.400000095367432D);
            if (width != entity.width || height != entity.height) {
               entity.width = width;
               entity.height = height;

               try {
                  TrinketReflectionHelper.ENTITY_SETSIZE.invoke(entity, width, height);
               } catch (Exception var20) {
                  ;
               }
            }

            double d0 = (double)entity.width / 2.0D;
            double x1 = entity.posX - d0;
            double z1 = entity.posZ - d0;
            double x2 = entity.posX + d0;
            double z2 = entity.posZ + d0;
            double y = entity.posY + (double)entity.height;
            entity.setEntityBoundingBox(new AxisAlignedBB(x1, entity.posY, z1, x2, y, z2));
         }
      }
   }

   public static void setSize(EntityLivingBase entity, EntityProperties properties) {
      if (!Trinkets.ArtemisLib || !TrinketsConfig.compat.artemislib) {
         float height = properties.getRaceHandler().getHeight();
         float width = properties.getRaceHandler().getWidth();
         if (!entity.isChild()) {
            setSize(entity, height, width);
         }
      }
   }
}
