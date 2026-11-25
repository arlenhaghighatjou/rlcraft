package pl.asie.foamfix.coremod;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import java.util.List;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;

public class FoamFixCoreContainer extends DummyModContainer {
   private static final ModMetadata md = new ModMetadata();

   public FoamFixCoreContainer() {
      super(md);
   }

   public boolean registerBus(EventBus bus, LoadController controller) {
      return true;
   }

   public List<String> getOwnedPackages() {
      return ImmutableList.of("pl.asie.foamfix.coremod");
   }

   static {
      md.modId = "foamfixcore";
      md.name = "FoamFixCore";
      md.description = "I'm actually just an optional part of FoamFix, available exclusively as part of the Anarchy version!";
      md.authorList = ImmutableList.of("asie");
      md.version = "7.7.4";
   }
}
