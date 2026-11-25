package yalter.mousetweaks.handlers;

public class GuiContainerCreativeHandler extends GuiContainerHandler {
   public GuiContainerCreativeHandler(bmp guiContainerCreative) {
      super(guiContainerCreative);
   }

   public boolean isIgnored(agr slot) {
      return super.isIgnored(slot) || slot.d != this.mc.h.bv;
   }
}
