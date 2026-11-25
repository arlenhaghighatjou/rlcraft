package codersafterdark.reskillable.client.gui.button;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ToolTip {
   private final List<String> lines = new ArrayList();
   private final long delay;
   private long mouseOverStart;

   public ToolTip() {
      this.delay = 0L;
   }

   public ToolTip(int delay) {
      this.delay = (long)delay;
   }

   public void clear() {
      this.lines.clear();
   }

   public boolean add(String line) {
      return this.add(line, (TextFormatting)null);
   }

   public boolean add(String line, @Nullable TextFormatting formatting) {
      return this.lines.add(formatting != null ? formatting + line : line);
   }

   public boolean add(List lines) {
      boolean changed = false;

      for(Object line : lines) {
         if (line instanceof String) {
            changed |= this.add((String)line);
         }
      }

      return changed;
   }

   public List<String> getLines() {
      return Collections.unmodifiableList(this.lines);
   }

   public void onTick(boolean mouseOver) {
      if (this.delay != 0L) {
         if (mouseOver) {
            if (this.mouseOverStart == 0L) {
               this.mouseOverStart = System.currentTimeMillis();
            }
         } else {
            this.mouseOverStart = 0L;
         }

      }
   }

   public boolean isReady() {
      return this.delay == 0L || this.mouseOverStart != 0L && System.currentTimeMillis() - this.mouseOverStart >= this.delay;
   }

   public void refresh() {
   }
}
