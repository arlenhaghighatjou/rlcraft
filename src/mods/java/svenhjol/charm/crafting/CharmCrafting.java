package svenhjol.charm.crafting;

import svenhjol.charm.crafting.feature.Barrel;
import svenhjol.charm.crafting.feature.BookshelfChest;
import svenhjol.charm.crafting.feature.Composter;
import svenhjol.charm.crafting.feature.Crate;
import svenhjol.charm.crafting.feature.EnderPearlBlock;
import svenhjol.charm.crafting.feature.Lantern;
import svenhjol.charm.crafting.feature.RottenFleshBlock;
import svenhjol.charm.crafting.feature.SmoothGlowstone;
import svenhjol.charm.crafting.feature.SuspiciousSoup;
import svenhjol.meson.Module;

public class CharmCrafting extends Module {
   public CharmCrafting() {
      this.features.add(new Barrel());
      this.features.add(new BookshelfChest());
      this.features.add(new Composter());
      this.features.add(new Crate());
      this.features.add(new EnderPearlBlock());
      this.features.add(new Lantern());
      this.features.add(new RottenFleshBlock());
      this.features.add(new SmoothGlowstone());
      this.features.add(new SuspiciousSoup());
   }
}
