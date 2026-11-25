package svenhjol.charm.smithing;

import svenhjol.charm.smithing.feature.DecreaseRepairCost;
import svenhjol.charm.smithing.feature.ExtendTotemOfReturning;
import svenhjol.charm.smithing.feature.ExtractBookXP;
import svenhjol.charm.smithing.feature.ExtractEnchantments;
import svenhjol.charm.smithing.feature.TallowIncreasesDurability;
import svenhjol.meson.Module;

public class CharmSmithing extends Module {
   public CharmSmithing() {
      this.features.add(new DecreaseRepairCost());
      this.features.add(new ExtendTotemOfReturning());
      this.features.add(new ExtractBookXP());
      this.features.add(new ExtractEnchantments());
      this.features.add(new TallowIncreasesDurability());
   }
}
