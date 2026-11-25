package melonslise.locks.client.gui.sprite.action;

import melonslise.locks.client.gui.sprite.Sprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class TimedAction<S extends Sprite> extends SingleCallbackAction<S> {
   public int ticks;

   public TimedAction() {
   }

   public TimedAction<S> time(int ticks) {
      this.ticks = ticks;
      return this;
   }

   public boolean isFinished(S sprite) {
      return this.ticks == 0;
   }

   public void update(S sprite) {
      --this.ticks;
   }
}
