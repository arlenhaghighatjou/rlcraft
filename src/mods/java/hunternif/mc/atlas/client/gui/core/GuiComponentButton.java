package hunternif.mc.atlas.client.gui.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;

public class GuiComponentButton extends GuiComponent {
   private final List<IButtonListener> listeners = new ArrayList();
   private boolean enabled = true;
   private SoundEvent clickSound = SoundEvents.UI_BUTTON_CLICK;

   public GuiComponentButton() {
   }

   public void setEnabled(boolean value) {
      this.enabled = value;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setClickSound(SoundEvent clickSound) {
      this.clickSound = clickSound;
   }

   public void mute() {
      this.clickSound = null;
   }

   protected void mouseClicked(int x, int y, int mouseButton) throws IOException {
      super.mouseClicked(x, y, mouseButton);
      if (mouseButton == 0 && this.enabled && this.isMouseOver) {
         this.onClick();
         this.mouseHasBeenHandled();
      }

   }

   void onClick() {
      if (this.clickSound != null) {
         this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(this.clickSound, 1.0F));
      }

      for(IButtonListener listener : this.listeners) {
         listener.onClick(this);
      }

   }

   public void addListener(IButtonListener listener) {
      this.listeners.add(listener);
   }

   public void removeListener(IButtonListener listener) {
      this.listeners.remove(listener);
   }
}
