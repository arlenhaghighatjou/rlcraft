package goblinbob.mobends.standard.client.model.armor;

import goblinbob.mobends.core.client.model.BoxFactory;
import goblinbob.mobends.core.client.model.BoxMutator;
import goblinbob.mobends.core.client.model.IModelPart;
import goblinbob.mobends.core.client.model.ModelPart;
import goblinbob.mobends.standard.data.BipedEntityData;
import java.util.List;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;

public class HumanoidLimbWrapper implements IPartWrapper {
   private final ModelRenderer vanillaPart;
   protected IPartWrapper.DataPartSelector upperPartDataSelector;
   protected IPartWrapper.ModelPartSetter modelPartSetter;
   private final IPartWrapper.DataPartSelector lowerPartDataSelector;
   private final float inflation;
   private ModelPart upperPart;
   private ModelPart upperPartAnchor;
   private ModelPart lowerPart;
   private ModelPart lowerPartAnchor;

   public HumanoidLimbWrapper(ModelBiped vanillaModel, ModelRenderer vanillaPart, IPartWrapper.ModelPartSetter modelPartSetter, IPartWrapper.DataPartSelector upperPartDataSelector, IPartWrapper.DataPartSelector lowerPartDataSelector, float cutPlane, float inflation) {
      this.vanillaPart = vanillaPart;
      this.upperPartDataSelector = upperPartDataSelector;
      this.lowerPartDataSelector = lowerPartDataSelector;
      this.modelPartSetter = modelPartSetter;
      this.inflation = inflation;
      if (vanillaPart instanceof PartContainer) {
         throw new MalformedArmorModelException("Tried to mutate a previously mutated part. A ModelRenderer instance has to have been used between Model instances.");
      } else {
         this.upperPart = new ModelPart(vanillaModel, false);
         this.upperPartAnchor = new ModelPart(vanillaModel, false);
         this.lowerPart = new ModelPart(vanillaModel, false);
         this.lowerPartAnchor = new ModelPart(vanillaModel, false);
         this.upperPart.addChild(this.upperPartAnchor);
         this.upperPart.addChild(this.lowerPart);
         this.lowerPart.addChild(this.lowerPartAnchor);
         this.upperPart.mirror = this.upperPartAnchor.mirror = this.lowerPart.mirror = this.lowerPartAnchor.mirror = vanillaPart.mirror;
         this.sliceAppendage(vanillaModel, vanillaPart, cutPlane);
      }
   }

   private void sliceAppendage(ModelBiped vanillaModel, ModelRenderer vanillaPart, float cutPlane) {
      for(ModelBox box : vanillaPart.cubeList) {
         BoxMutator mutator = BoxMutator.createFrom(vanillaModel, vanillaPart, box);
         if (mutator != null) {
            if (mutator.getFactory().min.y < cutPlane) {
               BoxFactory lowerPartFactory = mutator.sliceFromBottom(cutPlane);
               this.upperPartAnchor.addBox(mutator.getFactory().inflate(this.inflation, 0.0F, this.inflation).create(this.upperPart));
               if (lowerPartFactory != null) {
                  float lowerInflation = this.inflation + 0.001F;
                  this.lowerPartAnchor.addBox(lowerPartFactory.inflate(lowerInflation, 0.0F, lowerInflation).create(this.upperPart));
               }
            } else {
               this.lowerPartAnchor.addVanillaBox(box);
            }
         }
      }

      List<ModelRenderer> vanillaChildren = vanillaPart.childModels;
      if (vanillaChildren != null) {
         for(ModelRenderer child : vanillaChildren) {
            if (child != null) {
               if (child.rotationPointY < cutPlane) {
                  this.upperPartAnchor.addChild(child);
               } else {
                  this.lowerPartAnchor.addChild(child);
               }
            }
         }
      }

   }

   public void syncUp(BipedEntityData<?> data) {
      this.upperPart.syncUp(this.upperPartDataSelector.selectPart(data));
      if (this.lowerPart != null) {
         this.lowerPart.syncUp(this.lowerPartDataSelector.selectPart(data));
      }

   }

   public void apply(ArmorWrapper armorWrapper) {
      this.modelPartSetter.replacePart(armorWrapper, this.upperPart);
      this.modelPartSetter.replacePart(armorWrapper.original, this.upperPart);
      this.upperPart.isHidden = this.vanillaPart.isHidden;
      this.upperPart.showModel = this.vanillaPart.showModel;
   }

   public void deapply(ArmorWrapper armorWrapper) {
      this.modelPartSetter.replacePart(armorWrapper, this.vanillaPart);
      this.modelPartSetter.replacePart(armorWrapper.original, this.vanillaPart);
      this.vanillaPart.isHidden = this.upperPart.isHidden;
      this.vanillaPart.showModel = this.upperPart.showModel;
   }

   public IPartWrapper setParent(IModelPart parent) {
      this.upperPart.setParent(parent);
      return this;
   }

   public IPartWrapper offsetInner(float x, float y, float z) {
      this.upperPartAnchor.setPosition(x, y, z);
      return this;
   }

   public HumanoidLimbWrapper offsetLower(float x, float y, float z) {
      this.lowerPartAnchor.setPosition(x, y, z);
      return this;
   }
}
