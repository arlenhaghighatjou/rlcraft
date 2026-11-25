package moe.plushie.armourers_workshop.api.common.skin;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;

public interface IEntityEquipment {
   void addEquipment(ISkinType var1, int var2, ISkinDescriptor var3);

   void removeEquipment(ISkinType var1, int var2);

   boolean haveEquipment(ISkinType var1, int var2);

   /** @deprecated */
   @Deprecated
   int getEquipmentId(ISkinType var1, int var2);

   ISkinDescriptor getSkinPointer(ISkinType var1, int var2);

   /** @deprecated */
   @Deprecated
   ISkinDye getSkinDye(ISkinType var1, int var2);

   /** @deprecated */
   @Deprecated
   int getNumberOfSlots();
}
