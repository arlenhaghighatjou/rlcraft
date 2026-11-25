package svenhjol.charm.world.feature;

import net.minecraft.world.gen.structure.ComponentScatteredFeaturePieces.SwampHut;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import svenhjol.meson.Feature;
import svenhjol.meson.event.StructureEventBase;

public class SwampHutDecorations extends Feature {
   public SwampHutDecorations() {
   }

   public String getDescription() {
      return "Swamp huts may generate with a filled cauldron, a chest of loot and a black cat.";
   }

   @SubscribeEvent
   public void onAddComponentParts(StructureEventBase.Post event) {
      if (event.getComponent() instanceof SwampHut && !event.getWorld().isRemote) {
         svenhjol.charm.world.decorator.inner.SwampHut hut = new svenhjol.charm.world.decorator.inner.SwampHut((SwampHut)event.getComponent(), event.getWorld(), event.getBox());
         hut.generate();
      }

   }

   public boolean hasSubscriptions() {
      return true;
   }
}
