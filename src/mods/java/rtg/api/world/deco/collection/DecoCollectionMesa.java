package rtg.api.world.deco.collection;

import net.minecraft.block.BlockSand.EnumType;
import net.minecraft.init.Blocks;
import rtg.api.config.BiomeConfig;
import rtg.api.util.BlockUtil;
import rtg.api.world.deco.DecoBoulder;
import rtg.api.world.deco.DecoCactus;
import rtg.api.world.deco.DecoDeadBush;
import rtg.api.world.deco.DecoShrub;

public class DecoCollectionMesa extends DecoCollectionBase {
   public DecoCollectionMesa(BiomeConfig config) {
      super(config);
      DecoBoulder decoBoulder = new DecoBoulder();
      decoBoulder.setBoulderBlock(Blocks.COBBLESTONE.getDefaultState());
      decoBoulder.setMaxY(83);
      this.addDeco(decoBoulder);
      DecoShrub decoShrub = new DecoShrub();
      decoShrub.setLoops(2);
      decoShrub.setChance(4);
      decoShrub.setMaxY(90);
      this.addDeco(decoShrub);
      DecoDeadBush decoDeadBush = new DecoDeadBush();
      decoDeadBush.setMaxY(100);
      decoDeadBush.setLoops(3);
      this.addDeco(decoDeadBush);
      DecoCactus decoCactus = new DecoCactus();
      decoCactus.setSoilBlock(BlockUtil.getStateSand(EnumType.RED_SAND));
      decoCactus.setLoops(10);
      decoCactus.setMaxY(100);
      this.addDeco(decoCactus, config.ALLOW_CACTUS.get());
   }
}
