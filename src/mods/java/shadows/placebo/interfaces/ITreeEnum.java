package shadows.placebo.interfaces;

import net.minecraft.world.gen.feature.WorldGenerator;

public interface ITreeEnum extends IPropertyEnum {
   WorldGenerator getTreeGen();

   void setTreeGen(WorldGenerator var1);

   default int getPredicateIndex() {
      return ((Enum)this).ordinal() / 4;
   }

   default int getMetadata() {
      return ((Enum)this).ordinal() % 4;
   }
}
