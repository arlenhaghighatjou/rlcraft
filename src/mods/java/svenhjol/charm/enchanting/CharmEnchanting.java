package svenhjol.charm.enchanting;

import svenhjol.charm.enchanting.feature.CurseBreak;
import svenhjol.charm.enchanting.feature.ExtraCurses;
import svenhjol.charm.enchanting.feature.Homing;
import svenhjol.charm.enchanting.feature.Magnetic;
import svenhjol.charm.enchanting.feature.Salvage;
import svenhjol.meson.Module;

public class CharmEnchanting extends Module {
   public CharmEnchanting() {
      this.features.add(new CurseBreak());
      this.features.add(new ExtraCurses());
      this.features.add(new Homing());
      this.features.add(new Magnetic());
      this.features.add(new Salvage());
   }
}
