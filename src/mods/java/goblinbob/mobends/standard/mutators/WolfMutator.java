package goblinbob.mobends.standard.mutators;

import goblinbob.mobends.core.client.model.BoxSide;
import goblinbob.mobends.core.client.model.FaceRotation;
import goblinbob.mobends.core.client.model.IModelPart;
import goblinbob.mobends.core.client.model.ModelPart;
import goblinbob.mobends.core.client.model.ModelPartExtended;
import goblinbob.mobends.core.data.IEntityDataFactory;
import goblinbob.mobends.core.mutators.Mutator;
import goblinbob.mobends.standard.client.renderer.entity.layers.LayerWolfMisc;
import goblinbob.mobends.standard.data.WolfData;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelWolf;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.passive.EntityWolf;

public class WolfMutator extends Mutator<WolfData, EntityWolf, ModelWolf> {
   public ModelPart wolfHeadMain;
   public ModelPart wolfBody;
   public ModelPartExtended wolfLeg1;
   public ModelPartExtended wolfLeg2;
   public ModelPartExtended wolfLeg3;
   public ModelPartExtended wolfLeg4;
   public ModelPart wolfTail;
   public ModelPart wolfMane;
   public ModelPart nose;
   public ModelPart mouth;
   public ModelPart leftEar;
   public ModelPart rightEar;
   public ModelPart foreLeg1;
   public ModelPart foreLeg2;
   public ModelPart foreLeg3;
   public ModelPart foreLeg4;
   protected LayerWolfMisc layerMisc;

   public WolfMutator(IEntityDataFactory<EntityWolf> dataFactory) {
      super(dataFactory);
   }

   public void storeVanillaModel(ModelWolf model) {
      this.vanillaModel = (M)(new ModelWolf());
      ((ModelWolf)this.vanillaModel).wolfHeadMain = model.wolfHeadMain;
      ((ModelWolf)this.vanillaModel).wolfBody = model.wolfBody;
      ((ModelWolf)this.vanillaModel).wolfLeg1 = model.wolfLeg1;
      ((ModelWolf)this.vanillaModel).wolfLeg2 = model.wolfLeg2;
      ((ModelWolf)this.vanillaModel).wolfLeg3 = model.wolfLeg3;
      ((ModelWolf)this.vanillaModel).wolfLeg4 = model.wolfLeg4;
      ((ModelWolf)this.vanillaModel).wolfTail = model.wolfTail;
      ((ModelWolf)this.vanillaModel).wolfMane = model.wolfMane;
   }

   public void applyVanillaModel(ModelWolf model) {
      model.wolfHeadMain = ((ModelWolf)this.vanillaModel).wolfHeadMain;
      model.wolfBody = ((ModelWolf)this.vanillaModel).wolfBody;
      model.wolfLeg1 = ((ModelWolf)this.vanillaModel).wolfLeg1;
      model.wolfLeg2 = ((ModelWolf)this.vanillaModel).wolfLeg2;
      model.wolfLeg3 = ((ModelWolf)this.vanillaModel).wolfLeg3;
      model.wolfLeg4 = ((ModelWolf)this.vanillaModel).wolfLeg4;
      model.wolfTail = ((ModelWolf)this.vanillaModel).wolfTail;
      model.wolfMane = ((ModelWolf)this.vanillaModel).wolfMane;
      this.layerRenderers.remove(this.layerMisc);
   }

   public void swapLayer(RenderLivingBase<? extends EntityWolf> renderer, int index, boolean isModelVanilla) {
   }

   public void deswapLayer(RenderLivingBase<? extends EntityWolf> renderer, int index) {
   }

   public boolean createParts(ModelWolf original, float scaleFactor) {
      this.layerRenderers.remove(this.layerMisc);
      this.layerRenderers.add(this.layerMisc = new LayerWolfMisc());
      original.wolfBody = this.wolfBody = (new ModelPart(original, 18, 14)).setPosition(0.0F, 13.0F, 8.0F);
      this.wolfBody.developBox(-3.0F, -3.0F, -8.0F, 6, 6, 9, scaleFactor).offsetTextureQuad(BoxSide.TOP, 9.0F, 6.0F).rotateTextureQuad(BoxSide.TOP, FaceRotation.HALF_TURN).offsetTextureQuad(BoxSide.BACK, -12.0F, -9.0F).rotateTextureQuad(BoxSide.BOTTOM, FaceRotation.HALF_TURN).offsetTextureQuad(BoxSide.BOTTOM, -8.0F, 6.0F).rotateTextureQuad(BoxSide.LEFT, FaceRotation.CLOCKWISE).offsetTextureQuad(BoxSide.LEFT, -3.0F, -3.0F).rotateTextureQuad(BoxSide.RIGHT, FaceRotation.COUNTER_CLOCKWISE).offsetTextureQuad(BoxSide.RIGHT, 0.0F, -3.0F).create();
      original.wolfHeadMain = this.wolfHeadMain = (new ModelPart(original, 0, 0)).setParent(this.wolfBody).setPosition(0.0F, 0.0F, -7.0F);
      this.wolfHeadMain.addBox(-3.0F, -3.0F, -4.0F, 6, 6, 4, scaleFactor);
      original.wolfMane = this.wolfMane = (new ModelPart(original, 21, 0)).setParent(this.wolfBody).setPosition(0.0F, 0.0F, -7.0F);
      this.wolfMane.developBox(-4.0F, -3.5F, -2.0F, 8, 7, 6, scaleFactor).offsetTextureQuad(BoxSide.TOP, 1.0F, 7.0F).rotateTextureQuad(BoxSide.TOP, FaceRotation.HALF_TURN).offsetTextureQuad(BoxSide.BACK, -5.0F, -6.0F).offsetTextureQuad(BoxSide.BOTTOM, 8.0F, 7.0F).rotateTextureQuad(BoxSide.BOTTOM, FaceRotation.HALF_TURN).rotateTextureQuad(BoxSide.LEFT, FaceRotation.CLOCKWISE).offsetTextureQuad(BoxSide.LEFT, -14.0F, 1.0F).rotateTextureQuad(BoxSide.RIGHT, FaceRotation.COUNTER_CLOCKWISE).offsetTextureQuad(BoxSide.RIGHT, 15.0F, 1.0F).offsetTextureQuad(BoxSide.FRONT, 1.0F, -6.0F).create();
      original.wolfLeg1 = this.wolfLeg1 = (ModelPartExtended)(new ModelPartExtended(original, 0, 18)).setParent(this.wolfBody).setPosition(-2.5F, 16.0F, 7.0F);
      this.wolfLeg1.addBox(-1.0F, 0.0F, -1.0F, 2, 4, 2, scaleFactor);
      original.wolfLeg2 = this.wolfLeg2 = (ModelPartExtended)(new ModelPartExtended(original, 0, 18)).setParent(this.wolfBody).setPosition(0.5F, 16.0F, 7.0F);
      this.wolfLeg2.addBox(-1.0F, 0.0F, -1.0F, 2, 4, 2, scaleFactor);
      original.wolfLeg3 = this.wolfLeg3 = (ModelPartExtended)(new ModelPartExtended(original, 0, 18)).setParent(this.wolfBody).setPosition(-2.5F, 0.0F, -4.0F);
      this.wolfLeg3.addBox(-1.0F, 0.0F, -1.0F, 2, 4, 2, scaleFactor);
      original.wolfLeg4 = this.wolfLeg4 = (ModelPartExtended)(new ModelPartExtended(original, 0, 18)).setParent(this.wolfBody).setPosition(0.5F, 0.0F, -4.0F);
      this.wolfLeg4.addBox(-1.0F, 0.0F, -1.0F, 2, 4, 2, scaleFactor);
      original.wolfTail = this.wolfTail = (new ModelPart(original, 9, 18)).setParent(this.wolfBody).setPosition(-1.0F, 0.0F, 8.0F);
      this.wolfTail.addBox(-1.0F, 0.0F, -2.0F, 2, 8, 2, scaleFactor);
      this.nose = (new ModelPart(original, 0, 10)).setPosition(0.0F, 1.0F, -4.0F);
      this.nose.developBox(-1.5F, -1.0F, -4.0F, 3, 2, 4, 0.0F).hideFace(BoxSide.BOTTOM).create();
      this.wolfHeadMain.addChild(this.nose);
      this.mouth = (new ModelPart(original, 0, 12)).setPosition(0.0F, 2.0F, -4.0F);
      this.mouth.developBox(-1.5F, 0.0F, -4.0F, 3, 1, 4, 0.0F).hideFace(BoxSide.TOP).create();
      this.wolfHeadMain.addChild(this.mouth);
      this.leftEar = (new ModelPart(original, 16, 14)).setPosition(0.0F, 1.0F, -4.0F);
      this.leftEar.addBox(-1.0F, -2.0F, -1.0F, 2, 2, 1, 0.0F);
      this.wolfHeadMain.addChild(this.leftEar);
      this.rightEar = (new ModelPart(original, 16, 14)).setPosition(0.0F, 1.0F, -4.0F);
      this.rightEar.addBox(-1.0F, -2.0F, -1.0F, 2, 2, 1, 0.0F);
      this.wolfHeadMain.addChild(this.rightEar);
      this.foreLeg1 = (new ModelPart(original, 0, 18)).setParent(this.wolfLeg1).setPosition(0.0F, -4.0F, -1.0F);
      this.foreLeg1.addBox(-1.0F, 0.0F, 0.0F, 2, 4, 2, scaleFactor);
      this.wolfLeg1.setExtension(this.foreLeg1);
      this.foreLeg2 = (new ModelPart(original, 0, 18)).setParent(this.wolfLeg2).setPosition(0.0F, -4.0F, -1.0F);
      this.foreLeg2.addBox(-1.0F, 0.0F, 0.0F, 2, 4, 2, scaleFactor);
      this.wolfLeg2.setExtension(this.foreLeg2);
      this.foreLeg3 = (new ModelPart(original, 0, 18)).setParent(this.wolfLeg3).setPosition(0.0F, -4.0F, 1.0F);
      this.foreLeg3.addBox(-1.0F, 0.0F, -2.0F, 2, 4, 2, scaleFactor);
      this.wolfLeg3.setExtension(this.foreLeg3);
      this.foreLeg4 = (new ModelPart(original, 0, 18)).setParent(this.wolfLeg4).setPosition(0.0F, -4.0F, 1.0F);
      this.foreLeg4.addBox(-1.0F, 0.0F, -2.0F, 2, 4, 2, scaleFactor);
      this.wolfLeg4.setExtension(this.foreLeg4);
      return true;
   }

   public void syncUpWithData(WolfData data) {
      this.wolfHeadMain.syncUp(data.head);
      this.wolfBody.syncUp(data.body);
      this.wolfLeg1.syncUp(data.leg1);
      this.wolfLeg2.syncUp(data.leg2);
      this.wolfLeg3.syncUp(data.leg3);
      this.wolfLeg4.syncUp(data.leg4);
      this.wolfTail.syncUp(data.tail);
      this.wolfMane.syncUp(data.mane);
      this.nose.syncUp(data.nose);
      this.mouth.syncUp(data.mouth);
      this.leftEar.syncUp(data.leftEar);
      this.rightEar.syncUp(data.rightEar);
      this.foreLeg1.syncUp(data.foreLeg1);
      this.foreLeg2.syncUp(data.foreLeg2);
      this.foreLeg3.syncUp(data.foreLeg3);
      this.foreLeg4.syncUp(data.foreLeg4);
   }

   public boolean isModelVanilla(ModelWolf model) {
      return !(model.wolfBody instanceof IModelPart);
   }

   public boolean shouldModelBeSkipped(ModelBase model) {
      return !(model instanceof ModelWolf);
   }
}
