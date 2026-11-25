package betterquesting.api2.client.gui.events.types;

import betterquesting.api2.client.gui.controls.IPanelButton;
import betterquesting.api2.client.gui.events.PanelEvent;

public class PEventButton extends PanelEvent {
   private final IPanelButton btn;

   public PEventButton(IPanelButton btn) {
      this.btn = btn;
   }

   public IPanelButton getButton() {
      return this.btn;
   }

   public boolean canCancel() {
      return true;
   }
}
