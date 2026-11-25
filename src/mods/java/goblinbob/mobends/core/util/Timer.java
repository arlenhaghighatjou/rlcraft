package goblinbob.mobends.core.util;

public class Timer {
   private long time = System.nanoTime();
   private long lastTime;
   private float delta;

   public Timer() {
      this.lastTime = this.time;
   }

   public float tick() {
      this.time = System.nanoTime();
      this.delta = (float)(this.time - this.lastTime) / 1000000.0F;
      this.lastTime = this.time;
      return this.delta;
   }

   public float getDelta() {
      return this.delta;
   }
}
