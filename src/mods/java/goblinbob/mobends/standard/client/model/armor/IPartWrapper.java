package goblinbob.mobends.standard.client.model.armor;

import goblinbob.mobends.core.client.model.IModelPart;
import goblinbob.mobends.core.client.model.ModelPartTransform;
import goblinbob.mobends.standard.data.BipedEntityData;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;

public interface IPartWrapper {
   void apply(ArmorWrapper var1);

   void deapply(ArmorWrapper var1);

   void syncUp(BipedEntityData<?> var1);

   IPartWrapper offsetInner(float var1, float var2, float var3);

   IPartWrapper setParent(IModelPart var1);

   @FunctionalInterface
   public interface DataPartSelector {
      ModelPartTransform selectPart(BipedEntityData<?> var1);
   }

   @FunctionalInterface
   public interface ModelPartSetter {
      void replacePart(ModelBiped var1, ModelRenderer var2);
   }
}
