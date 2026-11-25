package svenhjol.charm.world;

import svenhjol.charm.world.feature.AbandonedCrates;
import svenhjol.charm.world.feature.BatBucket;
import svenhjol.charm.world.feature.ChargedEmeralds;
import svenhjol.charm.world.feature.EndPortalRunes;
import svenhjol.charm.world.feature.EndermitePowder;
import svenhjol.charm.world.feature.IllusionersInRoofedForest;
import svenhjol.charm.world.feature.Moonstone;
import svenhjol.charm.world.feature.MoreVillageBiomes;
import svenhjol.charm.world.feature.NetherGoldDeposits;
import svenhjol.charm.world.feature.Spectre;
import svenhjol.charm.world.feature.SpectreHaunting;
import svenhjol.charm.world.feature.StructureMaps;
import svenhjol.charm.world.feature.SwampHutDecorations;
import svenhjol.charm.world.feature.TotemOfReturning;
import svenhjol.charm.world.feature.TotemOfShielding;
import svenhjol.charm.world.feature.VillageDecorations;
import svenhjol.charm.world.feature.VillagerTrades;
import svenhjol.charm.world.feature.VindicatorsInRoofedForest;
import svenhjol.meson.Module;

public class CharmWorld extends Module {
   public CharmWorld() {
      this.features.add(new AbandonedCrates());
      this.features.add(new BatBucket());
      this.features.add(new ChargedEmeralds());
      this.features.add(new EndermitePowder());
      this.features.add(new EndPortalRunes());
      this.features.add(new IllusionersInRoofedForest());
      this.features.add(new Moonstone());
      this.features.add(new MoreVillageBiomes());
      this.features.add(new NetherGoldDeposits());
      this.features.add(new Spectre());
      this.features.add(new SpectreHaunting());
      this.features.add(new StructureMaps());
      this.features.add(new SwampHutDecorations());
      this.features.add(new TotemOfReturning());
      this.features.add(new TotemOfShielding());
      this.features.add(new VillageDecorations());
      this.features.add(new VillagerTrades());
      this.features.add(new VindicatorsInRoofedForest());
   }
}
