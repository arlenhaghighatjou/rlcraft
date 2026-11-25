package me.ichun.mods.ichunutil.client.entity.head;

import me.ichun.mods.ichunutil.api.client.head.HeadBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.EnumDyeColor;

public class HeadSheep extends HeadBase<EntitySheep> {
   public HeadSheep() {
      this.eyeOffset = new float[]{0.0F, 0.09375F, 0.375F};
      this.eyeScale = 0.65F;
   }

   public float getPupilScale(EntitySheep living, float partialTick, int eye) {
      return living.getSheared() ? 0.5F : super.getPupilScale(living, partialTick, eye);
   }

   public float[] getIrisColours(EntitySheep living, float partialTick, int eye) {
      if (living.hasCustomName() && "jeb_".equals(living.getCustomNameTag())) {
         this.livingRand.setSeed((long)(Math.abs(living.hashCode()) * 9823 * (eye + 1)));
         int i = living.ticksExisted / 25 + living.getEntityId() + this.livingRand.nextInt(25);
         int j = EnumDyeColor.values().length;
         int k = i % j;
         int l = (i + 1) % j;
         float f = ((float)(living.ticksExisted % 25) + partialTick) / 25.0F;
         float[] afloat1 = EntitySheep.getDyeRgb(EnumDyeColor.byMetadata(k));
         float[] afloat2 = EntitySheep.getDyeRgb(EnumDyeColor.byMetadata(l));
         return new float[]{afloat1[0] * (1.0F - f) + afloat2[0] * f, afloat1[1] * (1.0F - f) + afloat2[1] * f, afloat1[2] * (1.0F - f) + afloat2[2] * f};
      } else if (living.getSheared()) {
         float[] afloat = EntitySheep.getDyeRgb(living.getFleeceColor());
         return afloat;
      } else {
         return this.irisColour;
      }
   }

   public float[] getPupilColours(EntitySheep living, float partialTick, int eye) {
      if (living.hasCustomName() && "jeb_".equals(living.getCustomNameTag())) {
         this.livingRand.setSeed((long)(Math.abs(living.hashCode()) * 2145 * (eye + 1)));
         int i = living.ticksExisted / 25 + living.getEntityId() + this.livingRand.nextInt(25);
         int j = EnumDyeColor.values().length;
         int k = i % j;
         int l = (i + 1) % j;
         float f = ((float)(living.ticksExisted % 25) + partialTick) / 25.0F;
         float[] afloat1 = EntitySheep.getDyeRgb(EnumDyeColor.byMetadata(k));
         float[] afloat2 = EntitySheep.getDyeRgb(EnumDyeColor.byMetadata(l));
         return new float[]{afloat1[0] * (1.0F - f) + afloat2[0] * f, afloat1[1] * (1.0F - f) + afloat2[1] * f, afloat1[2] * (1.0F - f) + afloat2[2] * f};
      } else if (living.getSheared()) {
         return this.irisColour;
      } else {
         EnumDyeColor clr = living.getFleeceColor();
         if (clr != EnumDyeColor.WHITE) {
            float[] afloat = EntitySheep.getDyeRgb(clr);
            return afloat;
         } else {
            return this.pupilColour;
         }
      }
   }
}
