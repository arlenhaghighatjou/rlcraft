package net.ilexiconn.llibrary.server.world;

import net.ilexiconn.llibrary.LLibrary;

public enum TickRateHandler {
   INSTANCE;

   public static final float DEFAULT_TPS = 20.0F;
   private float tps = 20.0F;

   private TickRateHandler() {
   }

   public float getTPS() {
      return this.tps;
   }

   public void setTPS(float tps) {
      if (this.tps != tps) {
         LLibrary.PROXY.setTPS(tps);
      }

      this.tps = tps;
   }

   public void resetTPS() {
      this.setTPS(20.0F);
   }

   public long getTickRate() {
      return (long)(this.tps / 20.0F * 50.0F);
   }
}
