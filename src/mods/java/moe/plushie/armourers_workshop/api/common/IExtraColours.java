package moe.plushie.armourers_workshop.api.common;

public interface IExtraColours {
   int getColour(IExtraColours.ExtraColourType var1);

   byte[] getColourBytes(IExtraColours.ExtraColourType var1);

   void setColour(IExtraColours.ExtraColourType var1, int var2);

   void setColourBytes(IExtraColours.ExtraColourType var1, byte[] var2);

   public static enum ExtraColourType {
      SKIN,
      HAIR,
      EYE,
      MISC_1,
      MISC_2,
      MISC_3,
      MISC_4;

      private ExtraColourType() {
      }
   }
}
