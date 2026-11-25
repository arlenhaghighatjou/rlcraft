package net.ilexiconn.llibrary.server.animation;

public class Animation {
   /** @deprecated */
   @Deprecated
   private int id;
   private int duration;
   private boolean looping;

   protected Animation(int duration) {
      this.duration = duration;
   }

   /** @deprecated */
   @Deprecated
   public static Animation create(int id, int duration) {
      Animation animation = create(duration);
      animation.id = id;
      return animation;
   }

   public static Animation create(int duration) {
      return new Animation(duration);
   }

   /** @deprecated */
   @Deprecated
   public int getID() {
      return this.id;
   }

   public int getDuration() {
      return this.duration;
   }

   public Animation setLooping(boolean loops) {
      this.looping = loops;
      return this;
   }

   public boolean doesLoop() {
      return this.looping;
   }
}
