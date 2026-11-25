package moe.plushie.armourers_workshop.api.common.capability;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import net.minecraft.inventory.EntityEquipmentSlot;

public interface IPlayerWardrobeCap extends IWardrobeCap {
   boolean getArmourOverride(EntityEquipmentSlot var1);

   void setArmourOverride(EntityEquipmentSlot var1, boolean var2);

   int getUnlockedSlotsForSkinType(ISkinType var1);

   void setUnlockedSlotsForSkinType(ISkinType var1, int var2);

   int getMaxSlotsForSkinType(ISkinType var1);
}
