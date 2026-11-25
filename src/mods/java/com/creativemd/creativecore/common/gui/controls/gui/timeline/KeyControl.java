package com.creativemd.creativecore.common.gui.controls.gui.timeline;

import com.creativemd.creativecore.common.gui.GuiControl;
import com.creativemd.creativecore.common.gui.GuiRenderHelper;
import com.creativemd.creativecore.common.gui.client.style.ColoredDisplayStyle;
import com.creativemd.creativecore.common.gui.client.style.DisplayStyle;
import com.creativemd.creativecore.common.gui.client.style.Style;
import java.util.ArrayList;
import java.util.List;

public class KeyControl<T> extends GuiControl implements Comparable<KeyControl> {
   public TimelineChannel channel;
   public boolean modifiable = true;
   public int tick;
   public boolean selected = false;
   public T value;

   public KeyControl(TimelineChannel channel, int index, int tick, T value) {
      super("" + index + ".", 0, 0, 0, 0);
      this.channel = channel;
      this.rotation = 45.0F;
      this.tick = tick;
      this.value = value;
   }

   public DisplayStyle getBorderDisplay(DisplayStyle display) {
      if (!this.modifiable) {
         return this.selected ? new ColoredDisplayStyle(140, 140, 40) : new ColoredDisplayStyle(200, 200, 100);
      } else if (this.selected) {
         return new ColoredDisplayStyle(40, 40, 140);
      } else {
         return (DisplayStyle)(this.isMouseOver() ? new ColoredDisplayStyle(20, 20, 20) : super.getBorderDisplay(display));
      }
   }

   protected void renderContent(GuiRenderHelper helper, Style style, int width, int height) {
   }

   public boolean mousePressed(int x, int y, int button) {
      return true;
   }

   public List<String> getTooltip() {
      List<String> tooltip = new ArrayList();
      tooltip.add("" + this.value);
      return tooltip;
   }

   public void removeKey() {
      this.channel.removeKey(this);
      this.getParent().removeControl(this);
   }

   public int compareTo(KeyControl o) {
      return Integer.compare(this.tick, o.tick);
   }
}
