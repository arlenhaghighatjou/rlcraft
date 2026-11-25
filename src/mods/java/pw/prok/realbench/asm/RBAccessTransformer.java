package pw.prok.realbench.asm;

import java.io.IOException;
import net.minecraftforge.fml.common.asm.transformers.AccessTransformer;

public class RBAccessTransformer extends AccessTransformer {
   public RBAccessTransformer() throws IOException {
      super("realbench_at.cfg");
   }
}
