package pl.asie.foamfix.coremod;

import java.io.IOException;
import net.minecraftforge.fml.common.asm.transformers.AccessTransformer;
import pl.asie.foamfix.shared.FoamFixShared;

public class FoamFixAT extends AccessTransformer {
   public FoamFixAT() throws IOException {
      super("foamfix_runtime_at.cfg");
   }

   public byte[] transform(String name, String transformedName, byte[] bytes) {
      return !FoamFixShared.isCoremod ? bytes : super.transform(name, transformedName, bytes);
   }
}
