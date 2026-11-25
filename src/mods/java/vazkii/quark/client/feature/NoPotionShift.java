package vazkii.quark.client.feature;

import net.minecraftforge.client.event.GuiScreenEvent.PotionShiftEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;

public class NoPotionShift extends Feature {
   public NoPotionShift() {
   }

   public boolean hasSubscriptions() {
      return this.isClient();
   }

   @SubscribeEvent
   public void onPotionShiftEvent(PotionShiftEvent event) {
      event.setCanceled(true);
   }
}
