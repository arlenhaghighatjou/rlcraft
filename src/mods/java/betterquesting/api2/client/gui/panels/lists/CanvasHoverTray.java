package betterquesting.api2.client.gui.panels.lists;

import betterquesting.api2.client.gui.misc.GuiAlign;
import betterquesting.api2.client.gui.misc.GuiTransform;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.CanvasEmpty;
import betterquesting.api2.client.gui.panels.CanvasResizeable;
import betterquesting.api2.client.gui.panels.IGuiCanvas;
import betterquesting.api2.client.gui.resources.textures.IGuiTexture;

public class CanvasHoverTray extends CanvasResizeable {
   private boolean manualOpen = false;
   private final IGuiRect rectClosed;
   private final IGuiRect rectOpen;
   private final IGuiCanvas cvOpen;
   private final IGuiCanvas cvClosed;
   private Runnable actionOpen;
   private Runnable actionClose;

   public CanvasHoverTray(IGuiRect rectClosed, IGuiRect rectOpen, IGuiTexture texture) {
      super(rectClosed, texture);
      this.rectClosed = rectClosed;
      this.rectOpen = rectOpen;
      GuiTransform trans = new GuiTransform(GuiAlign.FULL_BOX);
      trans.setParent(this.getRectLerp());
      this.cvOpen = new CanvasEmpty(trans);
      this.cvClosed = new CanvasEmpty(trans);
   }

   public IGuiCanvas getCanvasOpen() {
      return this.cvOpen;
   }

   public IGuiCanvas getCanvasClosed() {
      return this.cvClosed;
   }

   public boolean isTrayOpen() {
      return this.cvOpen.isEnabled();
   }

   public CanvasHoverTray setManualOpen(boolean state) {
      this.manualOpen = state;
      return this;
   }

   public CanvasHoverTray setCloseAction(Runnable action) {
      this.actionClose = action;
      return this;
   }

   public CanvasHoverTray setOpenAction(Runnable action) {
      this.actionOpen = action;
      return this;
   }

   public void setTrayState(boolean open, long time) {
      if (!open && this.isTrayOpen()) {
         this.lerpToRect(this.rectClosed, time, true);
         this.cvOpen.setEnabled(false);
         this.cvClosed.setEnabled(true);
         if (this.actionClose != null) {
            this.actionClose.run();
         }
      } else if (open && !this.isTrayOpen()) {
         this.lerpToRect(this.rectOpen, time, true);
         this.cvOpen.setEnabled(true);
         this.cvClosed.setEnabled(false);
         if (this.actionOpen != null) {
            this.actionOpen.run();
         }
      }

   }

   public void drawPanel(int mx, int my, float partialTick) {
      if (!this.manualOpen) {
         if (this.isTrayOpen() && !this.rectOpen.contains(mx, my)) {
            this.setTrayState(false, 200L);
         } else if (!this.isTrayOpen() && this.rectClosed.contains(mx, my)) {
            this.setTrayState(true, 200L);
         }
      }

      super.drawPanel(mx, my, partialTick);
   }

   public void resetCanvas() {
      super.resetCanvas();
      this.cvOpen.resetCanvas();
      this.cvClosed.resetCanvas();
      this.addPanel(this.cvOpen);
      this.addPanel(this.cvClosed);
      this.cvOpen.setEnabled(false);
      this.cvClosed.setEnabled(true);
   }

   public void initPanel() {
      super.initPanel();
      this.addPanel(this.cvOpen);
      this.addPanel(this.cvClosed);
      this.cvOpen.setEnabled(false);
      this.cvClosed.setEnabled(true);
      this.rectOpen.setParent(this.rectClosed.getParent());
   }
}
