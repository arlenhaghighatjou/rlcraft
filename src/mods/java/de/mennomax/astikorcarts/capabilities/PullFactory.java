package de.mennomax.astikorcarts.capabilities;

import de.mennomax.astikorcarts.entity.AbstractDrawn;

public class PullFactory implements IPull {
   private AbstractDrawn drawn = null;

   public PullFactory() {
   }

   public void setDrawn(AbstractDrawn drawnIn) {
      this.drawn = drawnIn;
   }

   public AbstractDrawn getDrawn() {
      return this.drawn;
   }
}
