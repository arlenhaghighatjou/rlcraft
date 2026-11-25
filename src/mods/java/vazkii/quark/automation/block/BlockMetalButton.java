package vazkii.quark.automation.block;

import net.minecraft.block.SoundType;
import net.minecraft.world.World;
import vazkii.quark.base.block.BlockQuarkButton;

public class BlockMetalButton extends BlockQuarkButton {
   private final int speed;

   public BlockMetalButton(String variant, int speed) {
      super(variant + "_button", false);
      this.setSoundType(SoundType.METAL);
      this.speed = speed;
   }

   public int tickRate(World worldIn) {
      return this.speed;
   }
}
