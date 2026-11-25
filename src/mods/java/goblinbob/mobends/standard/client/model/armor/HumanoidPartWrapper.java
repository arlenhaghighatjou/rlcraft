package goblinbob.mobends.standard.client.model.armor;

import goblinbob.mobends.core.client.model.IModelPart;
import goblinbob.mobends.standard.data.BipedEntityData;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;

public class HumanoidPartWrapper implements IPartWrapper {
   protected IPartWrapper.DataPartSelector dataPartSelector;
   protected IPartWrapper.ModelPartSetter modelPartSetter;
   protected ModelRenderer vanillaPart;
   protected PartContainer partContainer;

   public HumanoidPartWrapper(ModelBiped vanillaModel, ModelRenderer vanillaPart, IPartWrapper.ModelPartSetter modelPartSetter, IPartWrapper.DataPartSelector dataPartSelector) {
      this.dataPartSelector = dataPartSelector;
      this.modelPartSetter = modelPartSetter;
      this.vanillaPart = vanillaPart;
      if (vanillaPart instanceof PartContainer) {
         throw new MalformedArmorModelException("Tried to mutate a previously mutated part. A ModelRenderer instance has to have been used between Model instances.");
      } else {
         this.partContainer = new PartContainer(vanillaModel, vanillaPart);
      }
   }

   public void syncUp(BipedEntityData<?> data) {
      this.partContainer.syncUp(this.dataPartSelector.selectPart(data));
   }

   public void apply(ArmorWrapper armorWrapper) {
      this.modelPartSetter.replacePart(armorWrapper, this.partContainer);
      this.modelPartSetter.replacePart(armorWrapper.original, this.partContainer);
      this.partContainer.isHidden = this.vanillaPart.isHidden;
      this.partContainer.showModel = this.vanillaPart.showModel;
   }

   public void deapply(ArmorWrapper armorWrapper) {
      this.modelPartSetter.replacePart(armorWrapper, this.vanillaPart);
      this.modelPartSetter.replacePart(armorWrapper.original, this.vanillaPart);
      this.vanillaPart.isHidden = this.partContainer.isHidden;
      this.vanillaPart.showModel = this.partContainer.showModel;
   }

   public IPartWrapper setParent(IModelPart parent) {
      this.partContainer.setParent(parent);
      return this;
   }

   public IPartWrapper offsetInner(float x, float y, float z) {
      this.partContainer.setInnerOffset(x, y, z);
      return this;
   }
}
