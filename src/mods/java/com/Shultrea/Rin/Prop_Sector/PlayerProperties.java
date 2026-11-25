package com.Shultrea.Rin.Prop_Sector;

public class PlayerProperties implements IPlayerProperties {
   boolean isResurrecting;
   double moralitasDamage;

   public PlayerProperties() {
   }

   public void setResurrecting(boolean flag) {
      this.isResurrecting = flag;
   }

   public boolean isResurrecting() {
      return this.isResurrecting;
   }

   public double getMoralitasDamage() {
      return this.moralitasDamage;
   }

   public void setMoralitasDamage(double moralitasDamage) {
      this.moralitasDamage = moralitasDamage;
   }
}
