package com.fantasticsource.setbonus.common.bonuselements;

import com.fantasticsource.setbonus.common.Bonus;
import net.minecraft.entity.player.EntityPlayer;

public abstract class ABonusElement {
   public String parsedString;
   public Bonus bonus;

   protected ABonusElement(String parsableBonusElement, Bonus bonus) {
      this.parsedString = parsableBonusElement;
      this.bonus = bonus;
      bonus.bonusElements.add(this);
   }

   public abstract void activate(EntityPlayer var1);

   public abstract void deactivate(EntityPlayer var1);

   public abstract void updateActive(EntityPlayer var1);
}
