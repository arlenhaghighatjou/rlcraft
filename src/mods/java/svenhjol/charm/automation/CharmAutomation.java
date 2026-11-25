package svenhjol.charm.automation;

import svenhjol.charm.automation.feature.GunpowderBlock;
import svenhjol.charm.automation.feature.RedstoneSand;
import svenhjol.charm.automation.feature.VariableRedstoneLamp;
import svenhjol.meson.Module;

public class CharmAutomation extends Module {
   public CharmAutomation() {
      this.features.add(new GunpowderBlock());
      this.features.add(new RedstoneSand());
      this.features.add(new VariableRedstoneLamp());
   }
}
