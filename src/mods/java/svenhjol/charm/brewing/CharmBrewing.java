package svenhjol.charm.brewing;

import svenhjol.charm.brewing.feature.Coffee;
import svenhjol.charm.brewing.feature.Decay;
import svenhjol.charm.brewing.feature.EnderSight;
import svenhjol.charm.brewing.feature.FlavoredCake;
import svenhjol.charm.brewing.feature.PoisonousPotatoBrew;
import svenhjol.meson.Module;

public class CharmBrewing extends Module {
   public CharmBrewing() {
      this.features.add(new Coffee());
      this.features.add(new Decay());
      this.features.add(new EnderSight());
      this.features.add(new FlavoredCake());
      this.features.add(new PoisonousPotatoBrew());
   }
}
