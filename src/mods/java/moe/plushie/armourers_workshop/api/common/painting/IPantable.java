package moe.plushie.armourers_workshop.api.common.painting;

import moe.plushie.armourers_workshop.api.common.skin.cubes.ICubeColour;

public interface IPantable {
   /** @deprecated */
   @Deprecated
   void setColour(int var1);

   /** @deprecated */
   @Deprecated
   void setColour(int var1, int var2);

   void setColour(byte[] var1, int var2);

   void setColour(ICubeColour var1);

   int getColour(int var1);

   void setPaintType(IPaintType var1, int var2);

   IPaintType getPaintType(int var1);

   ICubeColour getColour();
}
