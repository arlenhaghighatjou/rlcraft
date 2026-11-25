package goblinbob.mobends.core.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.util.vector.Vector3f;

public class ModelPartPostOffset extends ModelPart {
   protected Vector3f postOffset = new Vector3f(0.0F, 0.0F, 0.0F);

   public ModelPartPostOffset(ModelBase model, boolean register, int texOffsetX, int texOffsetY) {
      super(model, register, texOffsetY, texOffsetY);
   }

   public ModelPartPostOffset(ModelBase model, boolean register) {
      super(model, register);
   }

   public ModelPartPostOffset(ModelBase model, int texOffsetX, int texOffsetY) {
      super(model, texOffsetX, texOffsetY);
   }

   public ModelPartPostOffset setPostOffset(float x, float y, float z) {
      this.postOffset.set(x, y, z);
      return this;
   }

   public void propagateTransform(float scale) {
      super.propagateTransform(scale);
   }

   public void applyPostTransform(float scale) {
      GlStateManager.translate(this.postOffset.x * scale, this.postOffset.y * scale, this.postOffset.z * scale);
   }
}
