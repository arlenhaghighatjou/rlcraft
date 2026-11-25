package com.oblivioussp.spartanweaponry.client.model;

import net.minecraft.client.model.ModelBase;

public abstract class ModelQuiverBase extends ModelBase {
   protected int arrowsToRender = 0;

   public ModelQuiverBase() {
   }

   public abstract void render(float var1);

   public void setArrowsToRender(int arrowsToRender) {
      this.arrowsToRender = arrowsToRender;
   }
}
