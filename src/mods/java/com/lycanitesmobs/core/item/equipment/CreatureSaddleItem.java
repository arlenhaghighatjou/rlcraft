package com.lycanitesmobs.core.item.equipment;

import com.lycanitesmobs.core.info.CreatureType;
import com.lycanitesmobs.core.item.ItemBase;

public class CreatureSaddleItem extends ItemBase {
   protected CreatureType creatureType;

   public CreatureSaddleItem(CreatureType creatureType) {
      this.itemName = creatureType.getSaddleName();
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
