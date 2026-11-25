package org.orecruncher.dsurround.client.aurora;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.lib.math.MathStuff;

@SideOnly(Side.CLIENT)
public class AuroraLifeTracker {
   protected final int peakAge;
   protected final int ageDelta;
   protected int timer;
   protected boolean isAlive = true;
   protected boolean isFading = false;

   public AuroraLifeTracker(int peakAge, int ageDelta) {
      this.peakAge = peakAge;
      this.ageDelta = ageDelta;
   }

   public boolean isAlive() {
      return this.isAlive;
   }

   public boolean isFading() {
      return this.isFading;
   }

   public void setFading(boolean f) {
      this.isFading = f;
   }

   public void kill() {
      this.isAlive = false;
      this.timer = 0;
   }

   public float ageRatio() {
      return (float)this.timer / (float)this.peakAge;
   }

   public void update() {
      if (this.isAlive) {
         if (this.isFading) {
            this.timer -= this.ageDelta;
         } else {
            this.timer += this.ageDelta;
         }

         this.timer = MathStuff.clamp(this.timer, 0, this.peakAge);
         if (this.timer == 0 && this.isFading) {
            this.isAlive = false;
         }

      }
   }
}
