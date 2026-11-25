package goblinbob.mobends.core.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;

public class ModelPartExtended extends ModelPart {
   protected IModelPart extension;

   public ModelPartExtended(ModelBase model, boolean register, int texOffsetX, int texOffsetY) {
      super(model, register, texOffsetX, texOffsetY);
   }

   public ModelPartExtended(ModelBase model, boolean register) {
      super(model, register);
   }

   public ModelPartExtended(ModelBase model, int texOffsetX, int texOffsetY) {
      super(model, texOffsetX, texOffsetY);
   }

   public ModelPartExtended setExtension(IModelPart modelPart) {
      this.extension = modelPart;
      return this;
   }

   public void renderPart(float scale) {
      if (this.isShowing()) {
         if (!this.compiled) {
            this.compileDisplayList(scale);
         }

         GlStateManager.pushMatrix();
         this.applyCharacterTransform(scale);
         GlStateManager.callList(this.displayList);
         if (this.extension != null) {
            this.extension.renderJustPart(scale);
         }

         if (this.childModels != null) {
            for(ModelRenderer childModel : this.childModels) {
               childModel.render(scale);
            }
         }

         GlStateManager.popMatrix();
      }
   }

   public void renderJustPart(float scale) {
      if (this.isShowing()) {
         if (!this.compiled) {
            this.compileDisplayList(scale);
         }

         GlStateManager.pushMatrix();
         this.applyLocalTransform(scale);
         GlStateManager.callList(this.displayList);
         if (this.extension != null) {
            this.extension.renderJustPart(scale);
         }

         if (this.childModels != null) {
            for(ModelRenderer childModel : this.childModels) {
               childModel.render(scale);
            }
         }

         GlStateManager.popMatrix();
      }
   }

   public void applyCharacterTransform(float scale) {
      super.applyCharacterTransform(scale);
   }

   public void applyPostTransform(float scale) {
      if (this.extension != null) {
         this.extension.propagateTransform(scale);
      }

   }

   public void propagateTransform(float scale) {
      super.propagateTransform(scale);
      this.applyPostTransform(scale);
   }
}
