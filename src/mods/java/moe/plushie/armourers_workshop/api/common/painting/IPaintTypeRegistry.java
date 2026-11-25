package moe.plushie.armourers_workshop.api.common.painting;

import java.util.ArrayList;

public interface IPaintTypeRegistry {
   boolean registerPaintType(IPaintType var1);

   int getExtraChannels();

   IPaintType getPaintTypeFromColour(int var1);

   int setPaintTypeOnColour(IPaintType var1, int var2);

   IPaintType getPaintTypeFormByte(byte var1);

   IPaintType getPaintTypeFormName(String var1);

   ArrayList<IPaintType> getRegisteredTypes();

   IPaintType getPaintTypeFromIndex(int var1);
}
