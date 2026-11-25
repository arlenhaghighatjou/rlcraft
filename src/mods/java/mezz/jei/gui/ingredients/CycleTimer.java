package mezz.jei.gui.ingredients;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;

public class CycleTimer {
   private static final int cycleTime = 1000;
   private long startTime;
   private long drawTime;
   private long pausedDuration = 0L;

   public CycleTimer(int offset) {
      long time = System.currentTimeMillis();
      this.startTime = time - (long)(offset * 1000);
      this.drawTime = time;
   }

   @Nullable
   public <T> T getCycledItem(List<T> list) {
      if (list.isEmpty()) {
         return (T)null;
      } else {
         long index = (this.drawTime - this.startTime) / 1000L % (long)list.size();
         return (T)list.get(Math.toIntExact(index));
      }
   }

   public void onDraw() {
      if (!GuiScreen.isShiftKeyDown()) {
         if (this.pausedDuration > 0L) {
            this.startTime += this.pausedDuration;
            this.pausedDuration = 0L;
         }

         this.drawTime = System.currentTimeMillis();
      } else {
         this.pausedDuration = System.currentTimeMillis() - this.drawTime;
      }

   }
}
