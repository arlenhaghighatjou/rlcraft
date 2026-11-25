package melonslise.locks.client.gui.sprite.action;

import melonslise.locks.client.gui.sprite.Sprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class AccelerateAction<S extends Sprite> extends MoveAction<S> {
   public float accelX;
   public float accelY;

   public AccelerateAction(float speedX, float speedY, float accelX, float accelY) {
      super(speedX, speedY);
      this.accelX = accelX;
      this.accelY = accelY;
   }

   public static <Z extends Sprite> AccelerateAction<Z> at(float speedX, float speedY, float accelX, float accelY) {
      return new AccelerateAction<Z>(speedX, speedY, accelX, accelY);
   }

   public static <Z extends Sprite> AccelerateAction<Z> to(float shiftX, float shiftY, int ticks, boolean accel) {
      float speedX = 2.0F * shiftX / (float)ticks / (accel ? 3.0F : 1.0F);
      float speedY = 2.0F * shiftY / (float)ticks / (accel ? 3.0F : 1.0F);
      float accelX = speedX / (float)ticks * (accel ? 1.0F : -1.0F);
      float accelY = speedY / (float)ticks * (accel ? 1.0F : -1.0F);
      return (AccelerateAction)at(speedX, speedY, accelX, accelY).time(ticks);
   }

   public static <Z extends Sprite> AccelerateAction<Z> to(Sprite sprite, float posX, float posY, int ticks, boolean accel) {
      return to(posX - sprite.posX, posY - sprite.posY, ticks, accel);
   }

   public void update(S sprite) {
      super.update(sprite);
      sprite.posX += this.accelX * 0.5F;
      sprite.posY += this.accelY * 0.5F;
      this.speedX += this.accelX;
      this.speedY += this.accelY;
   }
}
