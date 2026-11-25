package goblinbob.mobends.core.mutators;

import goblinbob.mobends.core.animation.controller.IAnimationController;
import goblinbob.mobends.core.data.EntityDatabase;
import goblinbob.mobends.core.data.IEntityDataFactory;
import goblinbob.mobends.core.data.LivingEntityData;
import goblinbob.mobends.core.kumo.variable.KumoVariableRegistry;
import goblinbob.mobends.core.math.vector.SmoothVector3f;
import goblinbob.mobends.core.network.NetworkConfiguration;
import goblinbob.mobends.core.pack.BendsPackPerformer;
import goblinbob.mobends.core.util.GUtil;
import java.util.Collection;
import java.util.List;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;

public abstract class Mutator<D extends LivingEntityData<E>, E extends EntityLivingBase, M extends ModelBase> {
   protected M vanillaModel;
   protected float headYaw;
   protected float headPitch;
   protected float limbSwing;
   protected float limbSwingAmount;
   protected float swingProgress;
   private IEntityDataFactory<E> dataFactory;
   protected List<LayerRenderer<?>> layerRenderers;

   public Mutator(IEntityDataFactory<E> dataFactory) {
      this.dataFactory = dataFactory;
   }

   public void fetchFields(RenderLivingBase<? extends E> renderer) {
      this.layerRenderers = (List)renderer.layerRenderers;
   }

   public abstract void storeVanillaModel(M var1);

   public abstract void applyVanillaModel(M var1);

   public abstract void swapLayer(RenderLivingBase<? extends E> var1, int var2, boolean var3);

   public abstract void deswapLayer(RenderLivingBase<? extends E> var1, int var2);

   public abstract boolean createParts(M var1, float var2);

   public boolean mutate(RenderLivingBase<? extends E> renderer) {
      if (renderer.getMainModel() != null && !this.shouldModelBeSkipped(renderer.getMainModel())) {
         this.fetchFields(renderer);
         M model = renderer.getMainModel();
         float scaleFactor = 0.0F;
         boolean isModelVanilla = this.isModelVanilla(model);
         if (isModelVanilla) {
            this.storeVanillaModel(model);
         }

         this.createParts(model, scaleFactor);
         if (this.layerRenderers != null) {
            for(int i = 0; i < this.layerRenderers.size(); ++i) {
               this.swapLayer(renderer, i, isModelVanilla);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public void demutate(RenderLivingBase<? extends E> renderer) {
      if (!this.shouldModelBeSkipped(renderer.getMainModel())) {
         M model = renderer.getMainModel();
         this.applyVanillaModel(model);
         if (this.layerRenderers != null) {
            for(int i = 0; i < this.layerRenderers.size(); ++i) {
               this.deswapLayer(renderer, i);
            }
         }

      }
   }

   public void updateModel(E entity, RenderLivingBase<? extends E> renderer, float partialTicks) {
      boolean shouldSit = entity.isRiding() && entity.getRidingEntity() != null && entity.getRidingEntity().shouldRiderSit();
      float f = GUtil.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
      float f1 = GUtil.interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTicks);
      float yaw = f1 - f;
      if (shouldSit && entity.getRidingEntity() instanceof EntityLivingBase) {
         EntityLivingBase entitylivingbase = (EntityLivingBase)entity.getRidingEntity();
         f = GUtil.interpolateRotation(entitylivingbase.prevRenderYawOffset, entitylivingbase.renderYawOffset, partialTicks);
         yaw = f1 - f;
         float f3 = MathHelper.wrapDegrees(yaw);
         if (f3 < -85.0F) {
            f3 = -85.0F;
         }

         if (f3 >= 85.0F) {
            f3 = 85.0F;
         }

         f = f1 - f3;
         if (f3 * f3 > 2500.0F) {
            f += f3 * 0.2F;
         }

         yaw = f1 - f;
      }

      float pitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
      float f5 = 0.0F;
      float f6 = 0.0F;
      if (!entity.isRiding()) {
         f5 = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks;
         f6 = entity.limbSwing - entity.limbSwingAmount * (1.0F - partialTicks);
         if (entity.isChild()) {
            f6 *= 3.0F;
         }

         if (f5 > 1.0F) {
            f5 = 1.0F;
         }

         yaw = f1 - f;
      }

      this.headYaw = yaw;
      this.headPitch = pitch;
      this.limbSwing = f6;
      this.limbSwingAmount = f5;
      this.swingProgress = entity.getSwingProgress(partialTicks);
   }

   public void performAnimations(D data, String animatedEntityKey, RenderLivingBase<? extends E> renderer, float partialTicks) {
      data.headYaw.set(Float.valueOf(MathHelper.wrapDegrees(this.headYaw)));
      data.headPitch.set(Float.valueOf(MathHelper.wrapDegrees(this.headPitch)));
      data.limbSwing.set(Float.valueOf(this.limbSwing));
      data.limbSwingAmount.set(Float.valueOf(this.limbSwingAmount));
      data.swingProgress.set(Float.valueOf(this.swingProgress));
      KumoVariableRegistry.instance.provideTemporaryData(data);
      IAnimationController<D> controller = data.getController();
      Collection<String> actions = controller.perform(data);
      SmoothVector3f lastGlobalOffset = new SmoothVector3f(data.globalOffset);
      SmoothVector3f lastLocalOffset = new SmoothVector3f(data.localOffset);
      if (NetworkConfiguration.instance.areBendsPacksAllowed()) {
         BendsPackPerformer.INSTANCE.performCurrentPack(data, animatedEntityKey, actions);
         if (NetworkConfiguration.instance.isMovementLimited()) {
            data.globalOffset.limitDistanceTo(lastGlobalOffset, 10.0F);
            data.localOffset.limitDistanceTo(lastLocalOffset, 10.0F);
         }
      }

   }

   public abstract void syncUpWithData(D var1);

   public D getData(E entity) {
      return (D)EntityDatabase.instance.get(entity);
   }

   public D getOrMakeData(E entity) {
      return (D)EntityDatabase.instance.getOrMake(this.dataFactory, entity);
   }

   public abstract boolean isModelVanilla(M var1);

   public abstract boolean shouldModelBeSkipped(ModelBase var1);

   public void postRefresh() {
   }
}
