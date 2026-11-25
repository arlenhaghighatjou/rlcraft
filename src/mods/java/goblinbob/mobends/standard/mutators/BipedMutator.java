package goblinbob.mobends.standard.mutators;

import goblinbob.mobends.core.client.model.BoxSide;
import goblinbob.mobends.core.client.model.IModelPart;
import goblinbob.mobends.core.client.model.ModelPart;
import goblinbob.mobends.core.client.model.ModelPartExtended;
import goblinbob.mobends.core.client.model.ModelPartPostOffset;
import goblinbob.mobends.core.data.IEntityDataFactory;
import goblinbob.mobends.core.mutators.Mutator;
import goblinbob.mobends.standard.client.renderer.entity.layers.LayerCustomBipedArmor;
import goblinbob.mobends.standard.client.renderer.entity.layers.LayerCustomHeldItem;
import goblinbob.mobends.standard.data.BipedEntityData;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;

public abstract class BipedMutator<D extends BipedEntityData<E>, E extends EntityLivingBase, M extends ModelBiped> extends Mutator<D, E, M> {
   protected ModelPartPostOffset body;
   protected ModelPart head;
   protected ModelPart headwear;
   protected ModelPartExtended leftArm;
   protected ModelPartExtended rightArm;
   protected ModelPartPostOffset leftForeArm;
   protected ModelPartPostOffset rightForeArm;
   protected ModelPartExtended leftLeg;
   protected ModelPartExtended rightLeg;
   protected ModelPart leftForeLeg;
   protected ModelPart rightForeLeg;
   protected LayerCustomBipedArmor layerArmor;
   protected LayerBipedArmor layerArmorVanilla;
   protected LayerCustomHeldItem layerHeldItem;
   protected LayerHeldItem layerHeldItemVanilla;
   protected LayerCustomHead layerCustomHead;
   protected LayerCustomHead layerCustomHeadVanilla;

   public BipedMutator(IEntityDataFactory<E> dataFactory) {
      super(dataFactory);
   }

   public void storeVanillaModel(M model) {
      ((ModelBiped)this.vanillaModel).bipedBody = model.bipedBody;
      ((ModelBiped)this.vanillaModel).bipedHead = model.bipedHead;
      ((ModelBiped)this.vanillaModel).bipedHeadwear = model.bipedHeadwear;
      ((ModelBiped)this.vanillaModel).bipedLeftArm = model.bipedLeftArm;
      ((ModelBiped)this.vanillaModel).bipedLeftLeg = model.bipedLeftLeg;
      ((ModelBiped)this.vanillaModel).bipedRightArm = model.bipedRightArm;
      ((ModelBiped)this.vanillaModel).bipedRightLeg = model.bipedRightLeg;
   }

   public void applyVanillaModel(M model) {
      model.bipedBody = ((ModelBiped)this.vanillaModel).bipedBody;
      model.bipedHead = ((ModelBiped)this.vanillaModel).bipedHead;
      model.bipedHeadwear = ((ModelBiped)this.vanillaModel).bipedHeadwear;
      model.bipedLeftArm = ((ModelBiped)this.vanillaModel).bipedLeftArm;
      model.bipedLeftLeg = ((ModelBiped)this.vanillaModel).bipedLeftLeg;
      model.bipedRightArm = ((ModelBiped)this.vanillaModel).bipedRightArm;
      model.bipedRightLeg = ((ModelBiped)this.vanillaModel).bipedRightLeg;
   }

   public void swapLayer(RenderLivingBase<? extends E> renderer, int index, boolean isModelVanilla) {
      LayerRenderer<?> layer = (LayerRenderer)this.layerRenderers.get(index);
      if (layer instanceof LayerBipedArmor) {
         this.layerArmor = new LayerCustomBipedArmor(renderer);
         if (isModelVanilla) {
            this.layerArmorVanilla = (LayerBipedArmor)layer;
         }

         this.layerRenderers.set(index, this.layerArmor);
      } else if (layer instanceof LayerHeldItem) {
         this.layerHeldItem = new LayerCustomHeldItem(renderer);
         if (isModelVanilla) {
            this.layerHeldItemVanilla = (LayerHeldItem)layer;
         }

         this.layerRenderers.set(index, this.layerHeldItem);
      } else if (layer instanceof LayerCustomHead) {
         this.layerCustomHead = new LayerCustomHead(this.head);
         if (isModelVanilla) {
            this.layerCustomHeadVanilla = (LayerCustomHead)layer;
         }

         this.layerRenderers.set(index, this.layerCustomHead);
      }

   }

   public void deswapLayer(RenderLivingBase<? extends E> renderer, int index) {
      LayerRenderer<? extends EntityLivingBase> layer = (LayerRenderer)this.layerRenderers.get(index);
      if (layer instanceof LayerCustomBipedArmor) {
         this.layerRenderers.set(index, this.layerArmorVanilla);
      } else if (layer instanceof LayerCustomHeldItem) {
         this.layerRenderers.set(index, this.layerHeldItemVanilla);
      } else if (layer instanceof LayerCustomHead) {
         this.layerRenderers.set(index, this.layerCustomHeadVanilla);
      }

   }

   public boolean createParts(M original, float scaleFactor) {
      original.bipedBody = this.body = (ModelPartPostOffset)(new ModelPartPostOffset(original, 16, 16)).setPostOffset(0.0F, -12.0F, 0.0F).setPosition(0.0F, 12.0F, 0.0F);
      this.body.addBox(-4.0F, -12.0F, -2.0F, 8, 12, 4, scaleFactor);
      original.bipedHead = this.head = (new ModelPart(original, 0, 0)).setParent(this.body).setPosition(0.0F, -12.0F, 0.0F);
      this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, scaleFactor);
      int armWidth = 4;
      float armY = -10.0F;
      original.bipedLeftArm = this.leftArm = (ModelPartExtended)(new ModelPartExtended(original, 40, 16)).setParent(this.body).setPosition(5.0F, armY, 0.0F).setMirror(true);
      this.leftArm.developBox(-1.0F, -2.0F, -2.0F, armWidth, 6, 4, scaleFactor).inflate(0.01F, 0.0F, 0.01F).hideFace(BoxSide.BOTTOM).create();
      original.bipedRightArm = this.rightArm = (ModelPartExtended)(new ModelPartExtended(original, 40, 16)).setParent(this.body).setPosition(-5.0F, armY, 0.0F);
      this.rightArm.developBox((float)(-armWidth + 1), -2.0F, -2.0F, armWidth, 6, 4, scaleFactor).inflate(0.01F, 0.0F, 0.1F).hideFace(BoxSide.BOTTOM).create();
      this.leftForeArm = (ModelPartPostOffset)(new ModelPartPostOffset(original, 40, 22)).setPostOffset(0.0F, -4.0F, -2.0F).setParent(this.leftArm).setPosition(0.0F, 4.0F, 2.0F).setMirror(true);
      this.leftForeArm.developBox(-1.0F, 0.0F, -4.0F, armWidth, 6, 4, scaleFactor).hideFace(BoxSide.TOP).offsetTextureQuad(BoxSide.BOTTOM, 0.0F, -6.0F).create();
      this.leftArm.setExtension(this.leftForeArm);
      this.rightForeArm = (ModelPartPostOffset)(new ModelPartPostOffset(original, 40, 22)).setPostOffset(0.0F, -4.0F, -2.0F).setParent(this.rightArm).setPosition(0.0F, 4.0F, 2.0F);
      this.rightForeArm.developBox((float)(-armWidth + 1), 0.0F, -4.0F, armWidth, 6, 4, scaleFactor).hideFace(BoxSide.TOP).offsetTextureQuad(BoxSide.BOTTOM, 0.0F, -6.0F).create();
      this.rightArm.setExtension(this.rightForeArm);
      original.bipedRightLeg = this.rightLeg = (ModelPartExtended)(new ModelPartExtended(original, 0, 16)).setPosition(0.0F, 12.0F, 0.0F);
      this.rightLeg.addBox(-3.9F, 0.0F, -2.0F, 4, 6, 4, scaleFactor);
      original.bipedLeftLeg = this.leftLeg = (ModelPartExtended)(new ModelPartExtended(original, 0, 16)).setPosition(0.0F, 12.0F, 0.0F).setMirror(true);
      this.leftLeg.addBox(-0.1F, 0.0F, -2.0F, 4, 6, 4, scaleFactor);
      this.leftForeLeg = (new ModelPart(original, 0, 22)).setParent(this.leftLeg).setPosition(0.0F, 6.0F, -2.0F).setMirror(true);
      this.leftForeLeg.developBox(-0.1F, 0.0F, 0.0F, 4, 6, 4, scaleFactor).inflate(0.01F, 0.0F, 0.01F).offsetTextureQuad(BoxSide.BOTTOM, 0.0F, -6.0F).create();
      this.leftLeg.setExtension(this.leftForeLeg);
      this.rightForeLeg = (new ModelPart(original, 0, 22)).setParent(this.rightLeg).setPosition(0.0F, 6.0F, -2.0F);
      this.rightForeLeg.developBox(-3.9F, 0.0F, 0.0F, 4, 6, 4, scaleFactor).inflate(0.01F, 0.0F, 0.01F).offsetTextureQuad(BoxSide.BOTTOM, 0.0F, -6.0F).create();
      this.rightLeg.setExtension(this.rightForeLeg);
      original.bipedHeadwear = this.headwear = (new ModelPart(original, 32, 0)).setParent(this.head);
      this.headwear.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, scaleFactor + 0.5F);
      return true;
   }

   public void syncUpWithData(D data) {
      this.head.syncUp(data.head);
      this.body.syncUp(data.body);
      this.leftArm.syncUp(data.leftArm);
      this.rightArm.syncUp(data.rightArm);
      this.leftLeg.syncUp(data.leftLeg);
      this.rightLeg.syncUp(data.rightLeg);
      this.leftForeArm.syncUp(data.leftForeArm);
      this.rightForeArm.syncUp(data.rightForeArm);
      this.leftForeLeg.syncUp(data.leftForeLeg);
      this.rightForeLeg.syncUp(data.rightForeLeg);
   }

   public boolean isModelVanilla(M model) {
      return !(model.bipedBody instanceof IModelPart);
   }
}
