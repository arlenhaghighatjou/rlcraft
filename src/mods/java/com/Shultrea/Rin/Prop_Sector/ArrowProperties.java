package com.Shultrea.Rin.Prop_Sector;

public class ArrowProperties implements IArrowProperties {
   private float ExplosionPower;
   private boolean CanDestroyBLocks;
   private boolean NoDrag;
   private boolean CanRecover;
   private boolean didStarFall;
   private int level;
   private boolean isStarFallMade;
   private int flameLevel;
   private boolean pierceInvuln;
   private int piercingLevel;
   private float pullPower;

   public ArrowProperties() {
   }

   public boolean getIsStarFallMade() {
      return this.isStarFallMade;
   }

   public void setIsStarFallMade(boolean isStarFall) {
      this.isStarFallMade = isStarFall;
   }

   public int getLevel() {
      return this.level;
   }

   public int setLevel(int level) {
      return this.level = level;
   }

   public void setDidStarFall(boolean didStarFall) {
      this.didStarFall = didStarFall;
   }

   public boolean didStarFall() {
      return this.didStarFall;
   }

   public void setExplosion(float power, boolean canDestroyBlocks) {
      this.ExplosionPower = power;
      this.CanDestroyBLocks = canDestroyBlocks;
   }

   public void setNoDrag(boolean noDrag) {
      this.NoDrag = noDrag;
   }

   public float getExplosionPower() {
      return this.ExplosionPower;
   }

   public boolean getCanDestroyBlocks() {
      return this.CanDestroyBLocks;
   }

   public boolean getNoDrag() {
      return this.NoDrag;
   }

   public void setCanRecover(boolean canRecover) {
      this.CanRecover = canRecover;
   }

   public boolean getCanRecover() {
      return this.CanRecover;
   }

   public void setFlameLevel(int flameLevel) {
      this.flameLevel = flameLevel;
   }

   public int getFlameLevel() {
      return this.flameLevel;
   }

   public void setArrowRapidDamage(boolean flag) {
      this.pierceInvuln = flag;
   }

   public boolean isArrowRapidDamage() {
      return this.pierceInvuln;
   }

   public int getArmorPiercingLevel() {
      return this.piercingLevel;
   }

   public void setPiercingLevel(int level) {
      this.piercingLevel = level;
   }

   public float getPullPower() {
      return this.pullPower;
   }

   public void setPullPower(float p) {
      this.pullPower = p;
   }
}
