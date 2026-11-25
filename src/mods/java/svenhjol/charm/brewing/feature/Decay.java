package svenhjol.charm.brewing.feature;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import svenhjol.charm.brewing.potion.DecayPotion;
import svenhjol.meson.Feature;
import svenhjol.meson.helper.ForgeHelper;

public class Decay extends Feature {
   public static DecayPotion decay;
   public static int duration;
   public static int strength;

   public Decay() {
   }

   public String getDescription() {
      return "A nasty potion that withers living things.";
   }

   public void configure() {
      super.configure();
      duration = this.propInt("Decay duration", "Duration (in seconds) of decay effect when consumed.", 5);
      strength = this.propInt("Wither strength", "Strength of the Wither effect that accompanies the decay effect.", 2);
   }

   public boolean isEnabled() {
      return this.enabled && !ForgeHelper.areModsLoaded("inspirations");
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      decay = new DecayPotion();
   }

   public boolean hasSubscriptions() {
      return true;
   }
}
