package team.creative.enhancedvisuals.api.event;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class VisualExplosionEvent extends Event {
   public final float damage;
   protected float newDamage;
   protected boolean disableBlur = false;

   public VisualExplosionEvent(float damage) {
      this.damage = this.newDamage = damage;
   }

   public boolean isBlurDisabled() {
      return this.disableBlur;
   }

   public void setDisableBlur(boolean disabled) {
      this.disableBlur = disabled;
   }

   public void setNewDamage(float damage) {
      this.newDamage = damage;
   }

   public float getNewDamage() {
      return this.newDamage;
   }
}
