package goblinbob.mobends.standard.mutators;

import goblinbob.mobends.core.client.model.BoxSide;
import goblinbob.mobends.core.client.model.ModelPart;
import goblinbob.mobends.core.client.model.ModelPartExtended;
import goblinbob.mobends.core.client.model.ModelPartPostOffset;
import goblinbob.mobends.core.data.IEntityDataFactory;
import goblinbob.mobends.standard.data.SkeletonData;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelSkeleton;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;

public class SkeletonMutator extends BipedMutator<SkeletonData, EntitySkeleton, ModelSkeleton> {
   protected boolean boneLimbs = false;

   public SkeletonMutator(IEntityDataFactory<EntitySkeleton> dataCreationFunction) {
      super(dataCreationFunction);
   }

   public void fetchFields(RenderLivingBase<? extends EntitySkeleton> renderer) {
      super.fetchFields(renderer);
      if (renderer.getMainModel() instanceof ModelSkeleton) {
         ModelSkeleton model = (ModelSkeleton)renderer.getMainModel();
         this.boneLimbs = (int)((ModelBox)model.bipedRightArm.cubeList.get(0)).posX1 == -1;
      }

   }

   public void storeVanillaModel(ModelSkeleton model) {
      this.vanillaModel = (M)(new ModelSkeleton(0.0F, !this.boneLimbs));
      super.storeVanillaModel(model);
   }

   public boolean createParts(ModelSkeleton original, float scaleFactor) {
      super.createParts(original, scaleFactor);
      if (this.boneLimbs) {
         original.bipedRightArm = this.rightArm = new ModelPartExtended(original, 40, 16);
         this.rightArm.setParent(this.body).setPosition(-5.0F, 2.0F, 0.0F).developBox(-1.0F, -2.0F, -1.0F, 2, 6, 2, scaleFactor).inflate(0.01F, 0.0F, 0.01F).hideFace(BoxSide.BOTTOM).create();
         original.bipedLeftArm = this.leftArm = new ModelPartExtended(original, 40, 16);
         this.leftArm.setParent(this.body).setPosition(5.0F, 2.0F, 0.0F).developBox(-1.0F, -2.0F, -1.0F, 2, 6, 2, scaleFactor).inflate(0.01F, 0.0F, 0.01F).hideFace(BoxSide.BOTTOM).create();
         this.rightForeArm = (new ModelPartPostOffset(original, 40, 22)).setPostOffset(0.0F, -4.0F, -1.0F);
         this.rightForeArm.setPosition(0.0F, 4.0F, 1.0F).setParent(this.rightArm).developBox(-1.0F, 0.0F, -2.0F, 2, 6, 2, scaleFactor).hideFace(BoxSide.TOP).offsetTextureQuad(BoxSide.BOTTOM, 0.0F, -6.0F).create();
         this.rightArm.setExtension(this.rightForeArm);
         this.leftForeArm = (new ModelPartPostOffset(original, 40, 22)).setPostOffset(0.0F, -4.0F, -1.0F);
         this.leftForeArm.setPosition(0.0F, 4.0F, 1.0F).setParent(this.leftArm).developBox(-1.0F, 0.0F, -2.0F, 2, 6, 2, scaleFactor).hideFace(BoxSide.TOP).offsetTextureQuad(BoxSide.BOTTOM, 0.0F, -6.0F).create();
         this.leftArm.setExtension(this.leftForeArm);
         original.bipedRightLeg = this.rightLeg = (ModelPartExtended)(new ModelPartExtended(original, 0, 16)).setPosition(-2.0F, 12.0F, 0.0F);
         this.rightLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 6, 2, scaleFactor);
         original.bipedLeftLeg = this.leftLeg = (ModelPartExtended)(new ModelPartExtended(original, 0, 16)).setPosition(2.0F, 12.0F, 0.0F).setMirror(true);
         this.leftLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 6, 2, scaleFactor);
         this.leftForeLeg = (new ModelPart(original, 0, 22)).setParent(this.leftLeg).setPosition(0.0F, 6.0F, -2.0F).setMirror(true);
         this.leftForeLeg.developBox(-1.0F, 0.0F, 0.0F, 2, 6, 2, scaleFactor).inflate(0.01F, 0.0F, 0.01F).offsetTextureQuad(BoxSide.BOTTOM, 0.0F, -6.0F).create();
         this.leftLeg.setExtension(this.leftForeLeg);
         this.rightForeLeg = (new ModelPart(original, 0, 22)).setParent(this.rightLeg).setPosition(0.0F, 6.0F, -2.0F);
         this.rightForeLeg.developBox(-1.0F, 0.0F, 0.0F, 2, 6, 2, scaleFactor).inflate(0.01F, 0.0F, 0.01F).offsetTextureQuad(BoxSide.BOTTOM, 0.0F, -6.0F).create();
         this.rightLeg.setExtension(this.rightForeLeg);
      }

      return true;
   }

   public boolean shouldModelBeSkipped(ModelBase model) {
      return !(model instanceof ModelSkeleton);
   }
}
