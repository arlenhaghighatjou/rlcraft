package ivorius.reccomplex.world.gen.feature.structure.generic.transformers;

public class RunTransformer {
   public final TransformerMulti transformer;
   public final TransformerMulti.InstanceData instanceData;

   public RunTransformer(TransformerMulti transformer, TransformerMulti.InstanceData instanceData) {
      this.transformer = transformer;
      this.instanceData = instanceData;
   }
}
