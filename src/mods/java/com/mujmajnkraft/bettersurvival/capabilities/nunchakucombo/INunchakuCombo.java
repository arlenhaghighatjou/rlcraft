package com.mujmajnkraft.bettersurvival.capabilities.nunchakucombo;

public interface INunchakuCombo {
   float getComboPower();

   int getComboTime();

   void setComboPower(float var1);

   void countDown();

   void setComboTime(int var1);

   boolean isSpinning();

   void setSpinning(boolean var1);
}
