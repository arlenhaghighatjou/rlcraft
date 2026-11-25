package xzeroair.trinkets.races;

public class RaceAttributesWrapper {
   protected int size = 100;
   protected int width = 100;
   protected int height = 100;
   protected int color1 = 11107684;
   protected int color2 = 16374701;
   protected int color3 = 16374701;
   protected float opacity = 1.0F;
   protected float trait_opacity = 1.0F;
   protected String[] attributes = new String[0];

   public RaceAttributesWrapper() {
   }

   public final int getSize() {
      return this.size;
   }

   public final int getWidth() {
      return this.width;
   }

   public final int getHeight() {
      return this.height;
   }

   public final int getPrimaryColor() {
      return this.color1;
   }

   public final int getSecondaryColor() {
      return this.color2;
   }

   public final int getOptionalColor() {
      return this.color3;
   }

   public final float getOpacity() {
      return this.opacity;
   }

   public final float getTraitOpacity() {
      return this.trait_opacity;
   }

   public String[] getAttributes() {
      return this.attributes;
   }
}
