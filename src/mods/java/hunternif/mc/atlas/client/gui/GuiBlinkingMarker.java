package hunternif.mc.atlas.client.gui;

import hunternif.mc.atlas.client.gui.core.GuiBlinkingImage;
import hunternif.mc.atlas.registry.MarkerType;

public class GuiBlinkingMarker extends GuiBlinkingImage implements GuiMarkerFinalizer.IMarkerTypeSelectListener {
   public GuiBlinkingMarker() {
   }

   public void onSelectMarkerType(MarkerType markerType) {
      this.setTexture(markerType.getIcon(), 32, 32);
   }
}
