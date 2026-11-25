package com.Shultrea.Rin.Prop_Sector;

public interface IArrowProperties {
   void setArrowRapidDamage(boolean var1);

   boolean isArrowRapidDamage();

   void setFlameLevel(int var1);

   int getFlameLevel();

   void setExplosion(float var1, boolean var2);

   void setNoDrag(boolean var1);

   void setCanRecover(boolean var1);

   float getExplosionPower();

   boolean getCanDestroyBlocks();

   boolean getNoDrag();

   boolean getCanRecover();

   void setDidStarFall(boolean var1);

   boolean didStarFall();

   int setLevel(int var1);

   int getLevel();

   boolean getIsStarFallMade();

   void setIsStarFallMade(boolean var1);

   int getArmorPiercingLevel();

   void setPiercingLevel(int var1);

   float getPullPower();

   void setPullPower(float var1);
}
