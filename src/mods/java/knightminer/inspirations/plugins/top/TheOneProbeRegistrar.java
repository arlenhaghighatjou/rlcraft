package knightminer.inspirations.plugins.top;

import com.google.common.base.Function;
import javax.annotation.Nullable;
import mcjty.theoneprobe.api.ITheOneProbe;

public class TheOneProbeRegistrar implements Function<ITheOneProbe, Void> {
   public TheOneProbeRegistrar() {
   }

   @Nullable
   public Void apply(ITheOneProbe probe) {
      probe.registerProvider(new CauldronInfoProvider());
      return null;
   }
}
