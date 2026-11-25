package knightminer.inspirations.building.block;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockGlassTrapdoor extends BlockTrapDoor {
   public BlockGlassTrapdoor() {
      super(Material.GLASS);
      this.setHardness(0.3F);
      this.setSoundType(SoundType.GLASS);
      this.disableStats();
   }

   public int quantityDropped(EaglercraftRandom random) {
      return 0;
   }

   /** @deprecated */
   @Deprecated
   protected boolean canSilkHarvest() {
      return true;
   }
}
