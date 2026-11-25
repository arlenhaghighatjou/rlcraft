package vazkii.quark.decoration.block;

import net.minecraft.block.SoundType;
import vazkii.quark.base.block.BlockQuarkFenceGate;

public class BlockNetherBrickFenceGate extends BlockQuarkFenceGate {
   public BlockNetherBrickFenceGate() {
      super("nether_brick_fence_gate");
      this.setHardness(2.0F);
      this.setResistance(10.0F);
      this.setSoundType(SoundType.STONE);
   }
}
