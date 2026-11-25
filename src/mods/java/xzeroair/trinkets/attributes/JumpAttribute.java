package xzeroair.trinkets.attributes;

import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;

public class JumpAttribute {
   public static final IAttribute Jump = (new RangedAttribute((IAttribute)null, "xat.jump", 0.41999998688697815D, 0.0D, 256.0D)).setDescription("Jump Height").setShouldWatch(true);
   public static final IAttribute stepHeight = (new RangedAttribute((IAttribute)null, "xat.stepheight", 0.6000000238418579D, 0.0D, 256.0D)).setDescription("Step Height").setShouldWatch(true);

   public JumpAttribute() {
   }
}
