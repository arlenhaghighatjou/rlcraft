package goblinbob.mobends.standard.client.model.armor;

import goblinbob.mobends.core.bender.EntityBender;
import goblinbob.mobends.core.bender.EntityBenderRegistry;
import goblinbob.mobends.core.client.model.ModelPartTransform;
import goblinbob.mobends.core.data.EntityData;
import goblinbob.mobends.core.data.EntityDatabase;
import goblinbob.mobends.standard.data.BipedEntityData;
import goblinbob.mobends.standard.data.PlayerData;
import goblinbob.mobends.standard.previewer.PlayerPreviewer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class ArmorWrapper extends ModelBiped {
   protected ModelBiped original;
   protected boolean mutated = true;
   protected boolean applied = false;
   protected List<IPartWrapper> partWrappers;
   protected IPartWrapper bodyParts;
   protected IPartWrapper headParts;
   protected IPartWrapper headwearParts;
   protected IPartWrapper leftArmParts;
   protected IPartWrapper rightArmParts;
   protected IPartWrapper leftLegParts;
   protected IPartWrapper rightLegParts;
   protected ModelPartTransform bodyTransform;
   private static final IPartWrapper.ModelPartSetter bodySetter = (model, part) -> {
      model.bipedBody = part;
   };
   private static final IPartWrapper.ModelPartSetter headSetter = (model, part) -> {
      model.bipedHead = part;
   };
   private static final IPartWrapper.ModelPartSetter headwearSetter = (model, part) -> {
      model.bipedHeadwear = part;
   };
   private static final IPartWrapper.ModelPartSetter leftArmSetter = (model, part) -> {
      model.bipedLeftArm = part;
   };
   private static final IPartWrapper.ModelPartSetter rightArmSetter = (model, part) -> {
      model.bipedRightArm = part;
   };
   private static final IPartWrapper.ModelPartSetter leftLegSetter = (model, part) -> {
      model.bipedLeftLeg = part;
   };
   private static final IPartWrapper.ModelPartSetter rightLegSetter = (model, part) -> {
      model.bipedRightLeg = part;
   };

   private ArmorWrapper(ModelBiped original) {
      this.original = original;
      this.bipedBody = original.bipedBody;
      this.bipedHead = original.bipedHead;
      this.bipedLeftArm = original.bipedLeftArm;
      this.bipedRightArm = original.bipedRightArm;
      this.bipedLeftLeg = original.bipedLeftLeg;
      this.bipedRightLeg = original.bipedRightLeg;
      this.bipedHeadwear = original.bipedHeadwear;
      this.bodyTransform = new ModelPartTransform();
      this.partWrappers = new ArrayList();
      this.bodyParts = this.registerWrapper(original, this.bipedBody, bodySetter, (data) -> {
         return data.body;
      }).offsetInner(0.0F, -12.0F, 0.0F);
      this.headParts = this.registerWrapper(original, this.bipedHead, headSetter, (data) -> {
         return data.head;
      }).setParent(this.bodyTransform);
      this.headwearParts = this.registerWrapper(original, this.bipedHeadwear, headwearSetter, (data) -> {
         return data.head;
      }).setParent(this.bodyTransform);
      this.leftArmParts = this.registerWrapper(original, this.bipedLeftArm, leftArmSetter, (data) -> {
         return data.leftArm;
      }, (data) -> {
         return data.leftForeArm;
      }, 4.0F, 0.001F).offsetLower(0.0F, -4.0F, -2.0F).setParent(this.bodyTransform);
      this.rightArmParts = this.registerWrapper(original, this.bipedRightArm, rightArmSetter, (data) -> {
         return data.rightArm;
      }, (data) -> {
         return data.rightForeArm;
      }, 4.0F, 0.001F).offsetLower(0.0F, -4.0F, -2.0F).setParent(this.bodyTransform);
      this.leftLegParts = this.registerWrapper(original, this.bipedLeftLeg, leftLegSetter, (data) -> {
         return data.leftLeg;
      }, (data) -> {
         return data.leftForeLeg;
      }, 6.0F, 0.0F).offsetLower(1.9F, -6.0F, 2.0F).offsetInner(1.9F, 0.0F, 0.0F);
      this.rightLegParts = this.registerWrapper(original, this.bipedRightLeg, rightLegSetter, (data) -> {
         return data.rightLeg;
      }, (data) -> {
         return data.rightForeLeg;
      }, 6.0F, 0.0F).offsetLower(-1.9F, -6.0F, 2.0F).offsetInner(-1.9F, 0.0F, 0.0F);
   }

   private HumanoidPartWrapper registerWrapper(ModelBiped vanillaModel, ModelRenderer vanillaPart, IPartWrapper.ModelPartSetter setter, IPartWrapper.DataPartSelector dataSelector) {
      HumanoidPartWrapper wrapper = new HumanoidPartWrapper(vanillaModel, vanillaPart, setter, dataSelector);
      this.partWrappers.add(wrapper);
      return wrapper;
   }

   private HumanoidLimbWrapper registerWrapper(ModelBiped vanillaModel, ModelRenderer vanillaPart, IPartWrapper.ModelPartSetter setter, IPartWrapper.DataPartSelector data, IPartWrapper.DataPartSelector lowerData, float cutPlane, float inflation) {
      HumanoidLimbWrapper wrapper = new HumanoidLimbWrapper(vanillaModel, vanillaPart, setter, data, lowerData, cutPlane, inflation);
      this.partWrappers.add(wrapper);
      return wrapper;
   }

   public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      if (!this.mutated) {
         throw new MalformedArmorModelException("Operating on a demutated armor wrapper.");
      } else if (entityIn instanceof EntityLivingBase) {
         EntityLivingBase entityLiving = (EntityLivingBase)entityIn;
         EntityBender<EntityLivingBase> entityBender = EntityBenderRegistry.instance.<EntityLivingBase>getForEntity(entityLiving);
         if (entityBender != null) {
            EntityData<?> entityData = EntityDatabase.instance.get(entityLiving);
            if (entityData instanceof BipedEntityData) {
               if (entityData instanceof PlayerData && PlayerPreviewer.isPreviewInProgress()) {
                  entityData = PlayerPreviewer.getPreviewData();
               }

               BipedEntityData<?> dataBiped = (BipedEntityData)entityData;
               this.bodyTransform.syncUp(dataBiped.body);
               this.partWrappers.forEach((group) -> {
                  group.syncUp(dataBiped);
               });
               this.apply();
               this.original.setModelAttributes(this);
               this.original.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
               this.deapply();
            }
         }
      }
   }

   public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
   }

   public void demutate() {
      this.deapply();
      this.partWrappers.clear();
      this.mutated = false;
   }

   public void apply() {
      if (!this.applied) {
         for(IPartWrapper wrapper : this.partWrappers) {
            wrapper.apply(this);
         }

         this.applied = true;
      }
   }

   public void deapply() {
      if (this.applied) {
         for(IPartWrapper wrapper : this.partWrappers) {
            wrapper.deapply(this);
         }

         this.applied = false;
      }
   }

   public static ArmorWrapper createFor(ModelBiped src) {
      return new ArmorWrapper(src);
   }
}
