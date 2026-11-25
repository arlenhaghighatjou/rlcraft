package svenhjol.charm.tweaks.feature;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import svenhjol.charm.tweaks.compat.QuarkWitchHat;
import svenhjol.charm.world.event.SpectreAttackEvent;
import svenhjol.meson.Feature;
import svenhjol.meson.Meson;
import svenhjol.meson.helper.ForgeHelper;

public class WitchHatProtection extends Feature {
   private QuarkWitchHat quarkWitchHat;

   public WitchHatProtection() {
   }

   public String getDescription() {
      return "Wearing a Witch Hat protects your items from being cursed when touched by a Spectre.\nNOTE: Quark must be installed for this feature to be enabled.";
   }

   public void configure() {
      super.configure();

      try {
         this.quarkWitchHat = (QuarkWitchHat)QuarkWitchHat.class.newInstance();
      } catch (Exception var2) {
         Meson.runtimeException("Error loading QuarkWitchHat");
      }

   }

   @SubscribeEvent
   public void onSpectreAttack(SpectreAttackEvent event) {
      if (this.quarkWitchHat != null && this.quarkWitchHat.isWearingWitchHat(event.getAttacked())) {
         event.setCanceled(true);
      }

   }

   public boolean isEnabled() {
      return this.enabled && ForgeHelper.areModsLoaded("quark");
   }

   public boolean hasSubscriptions() {
      return true;
   }
}
