package dev.satyrn.wolfarmor.compatibility.mobends.mutators;

import dev.satyrn.wolfarmor.client.model.ModelWolfBackpack;
import dev.satyrn.wolfarmor.client.renderer.entity.layer.LayerWolfArmor;
import dev.satyrn.wolfarmor.client.renderer.entity.layer.LayerWolfBackpack;
import dev.satyrn.wolfarmor.mixin.accessors.ModelWolfAccessor;
import goblinbob.mobends.core.client.model.BoxSide;
import goblinbob.mobends.core.client.model.FaceRotation;
import goblinbob.mobends.core.client.model.IModelPart;
import goblinbob.mobends.core.client.model.ModelPart;
import goblinbob.mobends.core.client.model.ModelPartExtended;
import goblinbob.mobends.core.data.IEntityDataFactory;
import goblinbob.mobends.standard.data.WolfData;
import goblinbob.mobends.standard.mutators.WolfMutator;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelWolf;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ArmoredWolfMutator extends WolfMutator {
   public ModelPart wolfHeadMainArmorOuter;
   public ModelPart wolfBodyArmorOuter;
   public ModelPartExtended wolfLeg1ArmorOuter;
   public ModelPartExtended wolfLeg2ArmorOuter;
   public ModelPartExtended wolfLeg3ArmorOuter;
   public ModelPartExtended wolfLeg4ArmorOuter;
   public ModelPart wolfTailArmorOuter;
   public ModelPart wolfManeArmorOuter;
   public ModelPart noseArmorOuter;
   public ModelPart mouthArmorOuter;
   public ModelPart leftEarArmorOuter;
   public ModelPart rightEarArmorOuter;
   public ModelPart foreLeg1ArmorOuter;
   public ModelPart foreLeg2ArmorOuter;
   public ModelPart foreLeg3ArmorOuter;
   public ModelPart foreLeg4ArmorOuter;
   public ModelPart wolfHeadMainArmorInner;
   public ModelPart wolfBodyArmorInner;
   public ModelPartExtended wolfLeg1ArmorInner;
   public ModelPartExtended wolfLeg2ArmorInner;
   public ModelPartExtended wolfLeg3ArmorInner;
   public ModelPartExtended wolfLeg4ArmorInner;
   public ModelPart wolfTailArmorInner;
   public ModelPart wolfManeArmorInner;
   public ModelPart noseArmorInner;
   public ModelPart mouthArmorInner;
   public ModelPart leftEarArmorInner;
   public ModelPart rightEarArmorInner;
   public ModelPart foreLeg1ArmorInner;
   public ModelPart foreLeg2ArmorInner;
   public ModelPart foreLeg3ArmorInner;
   public ModelPart foreLeg4ArmorInner;
   public ModelPart backpackLeftTop;
   public ModelPart backpackRightTop;
   public ModelPart backpackLeftBottom;
   public ModelPart backpackRightBottom;
   public ModelWolf vanillaOuterArmorModel;
   public ModelWolf vanillaInnerArmorModel;
   public ModelWolfBackpack vanillaWolfBackpackModel;
   private boolean isCompatibleWolf;

   public ArmoredWolfMutator(IEntityDataFactory<EntityWolf> dataFactory) {
      super(dataFactory);
   }

   public void swapLayer(RenderLivingBase<? extends EntityWolf> renderer, int index, boolean isModelVanilla) {
      super.swapLayer(renderer, index, isModelVanilla);
      LayerRenderer<?> layerRenderer = (LayerRenderer)this.layerRenderers.get(index);
      if (layerRenderer instanceof LayerWolfArmor) {
         LayerWolfArmor layerWolfArmor = (LayerWolfArmor)layerRenderer;
         ModelBase innerArmorModel = layerWolfArmor.getArmorInnerLayer();
         ModelBase outerArmorModel = layerWolfArmor.getArmorOuterLayer();
         if (innerArmorModel instanceof ModelWolf) {
            ModelWolf innerArmorModelWolf = (ModelWolf)innerArmorModel;
            if (this.isModelVanilla(innerArmorModelWolf)) {
               this.storeVanillaInnerArmorModel(innerArmorModelWolf);
            }

            this.createInnerArmorLayerParts(innerArmorModelWolf, 0.1F);
         }

         if (outerArmorModel instanceof ModelWolf) {
            ModelWolf outerArmorModelWolf = (ModelWolf)outerArmorModel;
            if (this.isModelVanilla(outerArmorModelWolf)) {
               this.storeVanillaOuterArmorModel(outerArmorModelWolf);
            }

            this.createOuterArmorLayerParts(outerArmorModelWolf, 0.2F);
         }
      } else if (layerRenderer instanceof LayerWolfBackpack) {
         LayerWolfBackpack layerWolfBackpack = (LayerWolfBackpack)layerRenderer;
         ModelBase modelBase = layerWolfBackpack.getModelWolfBackpack();
         if (modelBase instanceof ModelWolfBackpack) {
            ModelWolfBackpack modelWolfBackpack = (ModelWolfBackpack)modelBase;
            if (this.isModelVanilla(modelWolfBackpack)) {
               this.storeVanillaWolfBackpackModel(modelWolfBackpack);
            }

            this.createBackpackLayerParts(modelWolfBackpack, 0.0F);
         }
      }

   }

   public void deswapLayer(RenderLivingBase<? extends EntityWolf> renderer, int index) {
      super.deswapLayer(renderer, index);
      LayerRenderer<?> layerRenderer = (LayerRenderer)this.layerRenderers.get(index);
      if (layerRenderer instanceof LayerWolfArmor) {
         LayerWolfArmor layerWolfArmor = (LayerWolfArmor)layerRenderer;
         ModelBase innerArmorModel = layerWolfArmor.getArmorInnerLayer();
         ModelBase outerArmorModel = layerWolfArmor.getArmorOuterLayer();
         if (innerArmorModel instanceof ModelWolf) {
            ModelWolf innerArmorModelWolf = (ModelWolf)innerArmorModel;
            if (!this.isModelVanilla(innerArmorModelWolf)) {
               this.applyVanillaInnerArmorModel(innerArmorModelWolf);
            }
         }

         if (outerArmorModel instanceof ModelWolf) {
            ModelWolf outerArmorModelWolf = (ModelWolf)outerArmorModel;
            if (!this.isModelVanilla(outerArmorModelWolf)) {
               this.applyVanillaOuterArmorModel(outerArmorModelWolf);
            }
         }
      } else if (layerRenderer instanceof LayerWolfBackpack) {
         LayerWolfBackpack layerWolfBackpack = (LayerWolfBackpack)layerRenderer;
         ModelBase modelBase = layerWolfBackpack.getModelWolfBackpack();
         if (modelBase instanceof ModelWolfBackpack) {
            ModelWolfBackpack modelWolfBackpack = (ModelWolfBackpack)modelBase;
            if (!this.isModelVanilla(modelWolfBackpack)) {
               this.applyVanillaWolfBackpackModel(modelWolfBackpack);
            }
         }
      }

   }

   private void storeVanillaWolfBackpackModel(ModelWolfBackpack model) {
      this.vanillaWolfBackpackModel = new ModelWolfBackpack(0.0F);
      this.vanillaWolfBackpackModel.backpackLeftBottom = model.backpackLeftBottom;
      this.vanillaWolfBackpackModel.backpackLeftTop = model.backpackLeftTop;
      this.vanillaWolfBackpackModel.backpackRightBottom = model.backpackRightBottom;
      this.vanillaWolfBackpackModel.backpackRightTop = model.backpackRightTop;
   }

   private void storeVanillaInnerArmorModel(ModelWolf model) {
      this.vanillaInnerArmorModel = new ModelWolf();
      this.vanillaInnerArmorModel.wolfHeadMain = model.wolfHeadMain;
      this.vanillaInnerArmorModel.wolfBody = model.wolfBody;
      this.vanillaInnerArmorModel.wolfLeg1 = model.wolfLeg1;
      this.vanillaInnerArmorModel.wolfLeg2 = model.wolfLeg2;
      this.vanillaInnerArmorModel.wolfLeg3 = model.wolfLeg3;
      this.vanillaInnerArmorModel.wolfLeg4 = model.wolfLeg4;
      ((ModelWolfAccessor)this.vanillaInnerArmorModel).setWolfMane(((ModelWolfAccessor)model).getWolfMane());
      ((ModelWolfAccessor)this.vanillaInnerArmorModel).setWolfTail(((ModelWolfAccessor)model).getWolfTail());
   }

   private void storeVanillaOuterArmorModel(ModelWolf model) {
      this.vanillaOuterArmorModel = new ModelWolf();
      this.vanillaOuterArmorModel.wolfHeadMain = model.wolfHeadMain;
      this.vanillaOuterArmorModel.wolfBody = model.wolfBody;
      this.vanillaOuterArmorModel.wolfLeg1 = model.wolfLeg1;
      this.vanillaOuterArmorModel.wolfLeg2 = model.wolfLeg2;
      this.vanillaOuterArmorModel.wolfLeg3 = model.wolfLeg3;
      this.vanillaOuterArmorModel.wolfLeg4 = model.wolfLeg4;
      ((ModelWolfAccessor)this.vanillaOuterArmorModel).setWolfMane(((ModelWolfAccessor)model).getWolfMane());
      ((ModelWolfAccessor)this.vanillaOuterArmorModel).setWolfTail(((ModelWolfAccessor)model).getWolfTail());
   }

   private void applyVanillaWolfBackpackModel(ModelWolfBackpack model) {
      model.backpackLeftBottom = this.vanillaWolfBackpackModel.backpackLeftBottom;
      model.backpackLeftTop = this.vanillaWolfBackpackModel.backpackLeftTop;
      model.backpackRightBottom = this.vanillaWolfBackpackModel.backpackRightBottom;
      model.backpackRightTop = this.vanillaWolfBackpackModel.backpackRightTop;
   }

   private void applyVanillaInnerArmorModel(ModelWolf model) {
      model.wolfHeadMain = this.vanillaInnerArmorModel.wolfHeadMain;
      model.wolfBody = this.vanillaInnerArmorModel.wolfBody;
      model.wolfLeg1 = this.vanillaInnerArmorModel.wolfLeg1;
      model.wolfLeg2 = this.vanillaInnerArmorModel.wolfLeg2;
      model.wolfLeg3 = this.vanillaInnerArmorModel.wolfLeg3;
      model.wolfLeg4 = this.vanillaInnerArmorModel.wolfLeg4;
      ((ModelWolfAccessor)model).setWolfMane(((ModelWolfAccessor)this.vanillaInnerArmorModel).getWolfMane());
      ((ModelWolfAccessor)model).setWolfTail(((ModelWolfAccessor)this.vanillaInnerArmorModel).getWolfTail());
   }

   private void applyVanillaOuterArmorModel(ModelWolf model) {
      model.wolfHeadMain = this.vanillaOuterArmorModel.wolfHeadMain;
      model.wolfBody = this.vanillaOuterArmorModel.wolfBody;
      model.wolfLeg1 = this.vanillaOuterArmorModel.wolfLeg1;
      model.wolfLeg2 = this.vanillaOuterArmorModel.wolfLeg2;
      model.wolfLeg3 = this.vanillaOuterArmorModel.wolfLeg3;
      model.wolfLeg4 = this.vanillaOuterArmorModel.wolfLeg4;
      ((ModelWolfAccessor)model).setWolfMane(((ModelWolfAccessor)this.vanillaOuterArmorModel).getWolfMane());
      ((ModelWolfAccessor)model).setWolfTail(((ModelWolfAccessor)this.vanillaOuterArmorModel).getWolfTail());
   }

   public void createOuterArmorLayerParts(ModelWolf original, float scaleFactor) {
      original.wolfBody = this.wolfBodyArmorOuter = (new ModelPart(original, 18, 14)).setPosition(0.0F, 13.0F, 8.0F);
      this.wolfBodyArmorOuter.developBox(-3.0F, -3.0F, -8.0F, 6, 6, 9, scaleFactor).offsetTextureQuad(BoxSide.TOP, 9.0F, 6.0F).rotateTextureQuad(BoxSide.TOP, FaceRotation.HALF_TURN).offsetTextureQuad(BoxSide.BACK, -12.0F, -9.0F).rotateTextureQuad(BoxSide.BOTTOM, FaceRotation.HALF_TURN).offsetTextureQuad(BoxSide.BOTTOM, -8.0F, 6.0F).rotateTextureQuad(BoxSide.LEFT, FaceRotation.CLOCKWISE).offsetTextureQuad(BoxSide.LEFT, -3.0F, -3.0F).rotateTextureQuad(BoxSide.RIGHT, FaceRotation.COUNTER_CLOCKWISE).offsetTextureQuad(BoxSide.RIGHT, 0.0F, -3.0F).create();
      original.wolfHeadMain = this.wolfHeadMainArmorOuter = (new ModelPart(original, 0, 0)).setParent(this.wolfBodyArmorOuter).setPosition(0.0F, 0.0F, -7.0F);
      this.wolfHeadMainArmorOuter.addBox(-3.0F, -3.0F, -4.0F, 6, 6, 4, scaleFactor);
      ((ModelWolfAccessor)original).setWolfMane(this.wolfManeArmorOuter = (new ModelPart(original, 21, 0)).setParent(this.wolfBodyArmorOuter).setPosition(0.0F, 0.0F, -7.0F));
      this.wolfManeArmorOuter.developBox(-4.0F, -3.5F, -2.0F, 8, 7, 6, scaleFactor).offsetTextureQuad(BoxSide.TOP, 16.0F, 7.0F).rotateTextureQuad(BoxSide.TOP, FaceRotation.HALF_TURN).offsetTextureQuad(BoxSide.BACK, -5.0F, -6.0F).offsetTextureQuad(BoxSide.BOTTOM, -7.0F, 7.0F).rotateTextureQuad(BoxSide.BOTTOM, FaceRotation.HALF_TURN).rotateTextureQuad(BoxSide.LEFT, FaceRotation.CLOCKWISE).offsetTextureQuad(BoxSide.LEFT, 1.0F, 1.0F).rotateTextureQuad(BoxSide.RIGHT, FaceRotation.COUNTER_CLOCKWISE).offsetTextureQuad(BoxSide.RIGHT, 0.0F, 1.0F).offsetTextureQuad(BoxSide.FRONT, 1.0F, -6.0F).create();
      original.wolfLeg1 = this.wolfLeg1ArmorOuter = (ModelPartExtended)(new ModelPartExtended(original, 0, 18)).setParent(this.wolfBodyArmorOuter).setPosition(-2.5F, 16.0F, 7.0F);
      this.wolfLeg1ArmorOuter.addBox(-1.0F, 0.0F, -1.0F, 2, 4, 2, scaleFactor);
      original.wolfLeg2 = this.wolfLeg2ArmorOuter = (ModelPartExtended)(new ModelPartExtended(original, 0, 18)).setParent(this.wolfBodyArmorOuter).setPosition(0.5F, 16.0F, 7.0F);
      this.wolfLeg2ArmorOuter.addBox(-1.0F, 0.0F, -1.0F, 2, 4, 2, scaleFactor);
      original.wolfLeg3 = this.wolfLeg3ArmorOuter = (ModelPartExtended)(new ModelPartExtended(original, 0, 18)).setParent(this.wolfBodyArmorOuter).setPosition(-2.5F, 0.0F, -4.0F);
      this.wolfLeg3ArmorOuter.addBox(-1.0F, 0.0F, -1.0F, 2, 4, 2, scaleFactor);
      original.wolfLeg4 = this.wolfLeg4ArmorOuter = (ModelPartExtended)(new ModelPartExtended(original, 0, 18)).setParent(this.wolfBodyArmorOuter).setPosition(0.5F, 0.0F, -4.0F);
      this.wolfLeg4ArmorOuter.addBox(-1.0F, 0.0F, -1.0F, 2, 4, 2, scaleFactor);
      ((ModelWolfAccessor)original).setWolfTail(this.wolfTailArmorOuter = (new ModelPart(original, 9, 18)).setParent(this.wolfBodyArmorOuter).setPosition(-1.0F, 0.0F, 8.0F));
      this.wolfTailArmorOuter.addBox(-1.0F, 0.0F, -2.0F, 2, 8, 2, scaleFactor);
      this.noseArmorOuter = (new ModelPart(original, 0, 10)).setPosition(0.0F, 1.0F, -4.0F);
      this.noseArmorOuter.developBox(-1.5F, -1.0F, -4.0F, 3, 2, 4, scaleFactor).hideFace(BoxSide.BOTTOM).create();
      this.wolfHeadMainArmorOuter.addChild(this.noseArmorOuter);
      this.mouthArmorOuter = (new ModelPart(original, 0, 12)).setPosition(0.0F, 2.0F, -4.0F);
      this.mouthArmorOuter.developBox(-1.5F, 0.0F, -4.0F, 3, 1, 4, scaleFactor).hideFace(BoxSide.TOP).create();
      this.wolfHeadMainArmorOuter.addChild(this.mouthArmorOuter);
      this.leftEarArmorOuter = (new ModelPart(original, 16, 14)).setPosition(0.0F, 1.0F, -4.0F);
      this.leftEarArmorOuter.addBox(-1.0F, -2.0F, -1.0F, 2, 2, 1, scaleFactor);
      this.wolfHeadMainArmorOuter.addChild(this.leftEarArmorOuter);
      this.rightEarArmorOuter = (new ModelPart(original, 16, 14)).setPosition(0.0F, 1.0F, -4.0F);
      this.rightEarArmorOuter.addBox(-1.0F, -2.0F, -1.0F, 2, 2, 1, scaleFactor);
      this.wolfHeadMainArmorOuter.addChild(this.rightEarArmorOuter);
      this.foreLeg1ArmorOuter = (new ModelPart(original, 0, 22)).setParent(this.wolfLeg1).setPosition(0.0F, -4.0F, -1.0F);
      this.foreLeg1ArmorOuter.addBox(-1.0F, 0.0F, 0.0F, 2, 4, 2, scaleFactor);
      this.wolfLeg1ArmorOuter.setExtension(this.foreLeg1ArmorOuter);
      this.foreLeg2ArmorOuter = (new ModelPart(original, 0, 22)).setParent(this.wolfLeg2).setPosition(0.0F, -4.0F, -1.0F);
      this.foreLeg2ArmorOuter.addBox(-1.0F, 0.0F, 0.0F, 2, 4, 2, scaleFactor);
      this.wolfLeg2ArmorOuter.setExtension(this.foreLeg2ArmorOuter);
      this.foreLeg3ArmorOuter = (new ModelPart(original, 0, 22)).setParent(this.wolfLeg3).setPosition(0.0F, -4.0F, 1.0F);
      this.foreLeg3ArmorOuter.addBox(-1.0F, 0.0F, -2.0F, 2, 4, 2, scaleFactor);
      this.wolfLeg3ArmorOuter.setExtension(this.foreLeg3ArmorOuter);
      this.foreLeg4ArmorOuter = (new ModelPart(original, 0, 22)).setParent(this.wolfLeg4).setPosition(0.0F, -4.0F, 1.0F);
      this.foreLeg4ArmorOuter.addBox(-1.0F, 0.0F, -2.0F, 2, 4, 2, scaleFactor);
      this.wolfLeg4ArmorOuter.setExtension(this.foreLeg4ArmorOuter);
   }

   public void createInnerArmorLayerParts(ModelWolf original, float scaleFactor) {
      original.wolfBody = this.wolfBodyArmorInner = (new ModelPart(original, 18, 14)).setPosition(0.0F, 13.0F, 8.0F);
      this.wolfBodyArmorInner.developBox(-3.0F, -3.0F, -8.0F, 6, 6, 9, scaleFactor).offsetTextureQuad(BoxSide.TOP, 9.0F, 6.0F).rotateTextureQuad(BoxSide.TOP, FaceRotation.HALF_TURN).offsetTextureQuad(BoxSide.BACK, -12.0F, -9.0F).rotateTextureQuad(BoxSide.BOTTOM, FaceRotation.HALF_TURN).offsetTextureQuad(BoxSide.BOTTOM, -8.0F, 6.0F).rotateTextureQuad(BoxSide.LEFT, FaceRotation.CLOCKWISE).offsetTextureQuad(BoxSide.LEFT, -3.0F, -3.0F).rotateTextureQuad(BoxSide.RIGHT, FaceRotation.COUNTER_CLOCKWISE).offsetTextureQuad(BoxSide.RIGHT, 0.0F, -3.0F).create();
      original.wolfHeadMain = this.wolfHeadMainArmorInner = (new ModelPart(original, 0, 0)).setParent(this.wolfBodyArmorInner).setPosition(0.0F, 0.0F, -7.0F);
      this.wolfHeadMainArmorInner.addBox(-3.0F, -3.0F, -4.0F, 6, 6, 4, scaleFactor);
      ((ModelWolfAccessor)original).setWolfMane(this.wolfManeArmorInner = (new ModelPart(original, 21, 0)).setParent(this.wolfBodyArmorInner).setPosition(0.0F, 0.0F, -7.0F));
      this.wolfManeArmorInner.developBox(-4.0F, -3.5F, -2.0F, 8, 7, 6, scaleFactor).offsetTextureQuad(BoxSide.TOP, 16.0F, 7.0F).rotateTextureQuad(BoxSide.TOP, FaceRotation.HALF_TURN).offsetTextureQuad(BoxSide.BACK, -5.0F, -6.0F).offsetTextureQuad(BoxSide.BOTTOM, -7.0F, 7.0F).rotateTextureQuad(BoxSide.BOTTOM, FaceRotation.HALF_TURN).rotateTextureQuad(BoxSide.LEFT, FaceRotation.CLOCKWISE).offsetTextureQuad(BoxSide.LEFT, 1.0F, 1.0F).rotateTextureQuad(BoxSide.RIGHT, FaceRotation.COUNTER_CLOCKWISE).offsetTextureQuad(BoxSide.RIGHT, 0.0F, 1.0F).offsetTextureQuad(BoxSide.FRONT, 1.0F, -6.0F).create();
      original.wolfLeg1 = this.wolfLeg1ArmorInner = (ModelPartExtended)(new ModelPartExtended(original, 0, 18)).setParent(this.wolfBodyArmorInner).setPosition(-2.5F, 16.0F, 7.0F);
      this.wolfLeg1ArmorInner.addBox(-1.0F, 0.0F, -1.0F, 2, 4, 2, scaleFactor);
      original.wolfLeg2 = this.wolfLeg2ArmorInner = (ModelPartExtended)(new ModelPartExtended(original, 0, 18)).setParent(this.wolfBodyArmorInner).setPosition(0.5F, 16.0F, 7.0F);
      this.wolfLeg2ArmorInner.addBox(-1.0F, 0.0F, -1.0F, 2, 4, 2, scaleFactor);
      original.wolfLeg3 = this.wolfLeg3ArmorInner = (ModelPartExtended)(new ModelPartExtended(original, 0, 18)).setParent(this.wolfBodyArmorInner).setPosition(-2.5F, 0.0F, -4.0F);
      this.wolfLeg3ArmorInner.addBox(-1.0F, 0.0F, -1.0F, 2, 4, 2, scaleFactor);
      original.wolfLeg4 = this.wolfLeg4ArmorInner = (ModelPartExtended)(new ModelPartExtended(original, 0, 18)).setParent(this.wolfBodyArmorInner).setPosition(0.5F, 0.0F, -4.0F);
      this.wolfLeg4ArmorInner.addBox(-1.0F, 0.0F, -1.0F, 2, 4, 2, scaleFactor);
      ((ModelWolfAccessor)original).setWolfTail(this.wolfTailArmorInner = (new ModelPart(original, 9, 18)).setParent(this.wolfBodyArmorInner).setPosition(-1.0F, 0.0F, 8.0F));
      this.wolfTailArmorInner.addBox(-1.0F, 0.0F, -2.0F, 2, 8, 2, scaleFactor);
      this.noseArmorInner = (new ModelPart(original, 0, 10)).setPosition(0.0F, 1.0F, -4.0F);
      this.noseArmorInner.developBox(-1.5F, -1.0F, -4.0F, 3, 2, 4, scaleFactor).hideFace(BoxSide.BOTTOM).create();
      this.wolfHeadMainArmorInner.addChild(this.noseArmorInner);
      this.mouthArmorInner = (new ModelPart(original, 0, 12)).setPosition(0.0F, 2.0F, -4.0F);
      this.mouthArmorInner.developBox(-1.5F, 0.0F, -4.0F, 3, 1, 4, scaleFactor).hideFace(BoxSide.TOP).create();
      this.wolfHeadMainArmorInner.addChild(this.mouthArmorInner);
      this.leftEarArmorInner = (new ModelPart(original, 16, 14)).setPosition(0.0F, 1.0F, -4.0F);
      this.leftEarArmorInner.addBox(-1.0F, -2.0F, -1.0F, 2, 2, 1, scaleFactor);
      this.wolfHeadMainArmorInner.addChild(this.leftEarArmorInner);
      this.rightEarArmorInner = (new ModelPart(original, 16, 14)).setPosition(0.0F, 1.0F, -4.0F);
      this.rightEarArmorInner.addBox(-1.0F, -2.0F, -1.0F, 2, 2, 1, scaleFactor);
      this.wolfHeadMainArmorInner.addChild(this.rightEarArmorInner);
      this.foreLeg1ArmorInner = (new ModelPart(original, 0, 22)).setParent(this.wolfLeg1).setPosition(0.0F, -4.0F, -1.0F);
      this.foreLeg1ArmorInner.addBox(-1.0F, 0.0F, 0.0F, 2, 4, 2, scaleFactor);
      this.wolfLeg1ArmorInner.setExtension(this.foreLeg1ArmorInner);
      this.foreLeg2ArmorInner = (new ModelPart(original, 0, 22)).setParent(this.wolfLeg2).setPosition(0.0F, -4.0F, -1.0F);
      this.foreLeg2ArmorInner.addBox(-1.0F, 0.0F, 0.0F, 2, 4, 2, scaleFactor);
      this.wolfLeg2ArmorInner.setExtension(this.foreLeg2ArmorInner);
      this.foreLeg3ArmorInner = (new ModelPart(original, 0, 22)).setParent(this.wolfLeg3).setPosition(0.0F, -4.0F, 1.0F);
      this.foreLeg3ArmorInner.addBox(-1.0F, 0.0F, -2.0F, 2, 4, 2, scaleFactor);
      this.wolfLeg3ArmorInner.setExtension(this.foreLeg3ArmorInner);
      this.foreLeg4ArmorInner = (new ModelPart(original, 0, 22)).setParent(this.wolfLeg4).setPosition(0.0F, -4.0F, 1.0F);
      this.foreLeg4ArmorInner.addBox(-1.0F, 0.0F, -2.0F, 2, 4, 2, scaleFactor);
      this.wolfLeg4ArmorInner.setExtension(this.foreLeg4ArmorInner);
   }

   private void createBackpackLayerParts(ModelWolfBackpack original, float scale) {
      original.backpackLeftTop = this.backpackLeftTop = (new ModelPart(original, 0, 16)).setPosition(0.0F, 13.0F, 8.0F);
      this.backpackLeftTop.setTextureSize(16, 32);
      this.backpackLeftTop.addBox(-5.0F, -2.0F, -4.0F, 2, 2, 5, scale);
      original.backpackRightTop = this.backpackRightTop = (new ModelPart(original, 0, 0)).setPosition(0.0F, 13.0F, 8.0F);
      this.backpackRightTop.setTextureSize(16, 32);
      this.backpackRightTop.addBox(3.0F, -2.0F, -4.0F, 2, 2, 5, scale);
      original.backpackLeftBottom = this.backpackLeftBottom = (new ModelPart(original, 0, 23)).setPosition(0.0F, 13.0F, 8.0F);
      this.backpackLeftBottom.setTextureSize(16, 32);
      this.backpackLeftBottom.addBox(-6.0F, 0.0F, -4.0F, 3, 4, 5, scale);
      original.backpackRightBottom = this.backpackRightBottom = (new ModelPart(original, 0, 7)).setPosition(0.0F, 13.0F, 8.0F);
      this.backpackRightBottom.setTextureSize(16, 32);
      this.backpackRightBottom.addBox(3.0F, 0.0F, -4.0F, 3, 4, 5, scale);
   }

   public void syncUpWithData(WolfData data) {
      super.syncUpWithData(data);
      if (this.wolfHeadMainArmorOuter != null) {
         this.wolfHeadMainArmorOuter.syncUp(data.head);
         this.wolfBodyArmorOuter.syncUp(data.body);
         this.wolfLeg1ArmorOuter.syncUp(data.leg1);
         this.wolfLeg2ArmorOuter.syncUp(data.leg2);
         this.wolfLeg3ArmorOuter.syncUp(data.leg3);
         this.wolfLeg4ArmorOuter.syncUp(data.leg4);
         this.wolfTailArmorOuter.syncUp(data.tail);
         this.wolfManeArmorOuter.syncUp(data.mane);
         this.noseArmorOuter.syncUp(data.nose);
         this.mouthArmorOuter.syncUp(data.mouth);
         this.leftEarArmorOuter.syncUp(data.leftEar);
         this.rightEarArmorOuter.syncUp(data.rightEar);
         this.foreLeg1ArmorOuter.syncUp(data.foreLeg1);
         this.foreLeg2ArmorOuter.syncUp(data.foreLeg2);
         this.foreLeg3ArmorOuter.syncUp(data.foreLeg3);
         this.foreLeg4ArmorOuter.syncUp(data.foreLeg4);
      }

      if (this.wolfHeadMainArmorInner != null) {
         this.wolfHeadMainArmorInner.syncUp(data.head);
         this.wolfBodyArmorInner.syncUp(data.body);
         this.wolfLeg1ArmorInner.syncUp(data.leg1);
         this.wolfLeg2ArmorInner.syncUp(data.leg2);
         this.wolfLeg3ArmorInner.syncUp(data.leg3);
         this.wolfLeg4ArmorInner.syncUp(data.leg4);
         this.wolfTailArmorInner.syncUp(data.tail);
         this.wolfManeArmorInner.syncUp(data.mane);
         this.noseArmorInner.syncUp(data.nose);
         this.mouthArmorInner.syncUp(data.mouth);
         this.leftEarArmorInner.syncUp(data.leftEar);
         this.rightEarArmorInner.syncUp(data.rightEar);
         this.foreLeg1ArmorInner.syncUp(data.foreLeg1);
         this.foreLeg2ArmorInner.syncUp(data.foreLeg2);
         this.foreLeg3ArmorInner.syncUp(data.foreLeg3);
         this.foreLeg4ArmorInner.syncUp(data.foreLeg4);
      }

      if (this.backpackLeftTop != null) {
         this.backpackLeftTop.syncUp(data.body);
         this.backpackRightTop.syncUp(data.body);
         this.backpackLeftBottom.syncUp(data.body);
         this.backpackRightBottom.syncUp(data.body);
      }

   }

   private boolean isModelVanilla(ModelWolfBackpack model) {
      return !(model.backpackRightTop instanceof IModelPart);
   }
}
