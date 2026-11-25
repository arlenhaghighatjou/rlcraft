package com.lycanitesmobs.core.item.consumable;

import com.lycanitesmobs.core.info.CreatureType;
import com.lycanitesmobs.core.item.ItemBase;

public class CreatureTreatItem extends ItemBase {
   protected CreatureType creatureType;

   public CreatureTreatItem(CreatureType creatureType) {
      this.itemName = creatureType.getTreatName();
      this.modInfo = creatureType.modInfo;
      this.creatureType = creatureType;
      this.setMaxStackSize(16);
      this.setUnlocalizedName(this.itemName);
      this.setup();
   }

   public CreatureType getCreatureType() {
      return this.creatureType;
   }
}
