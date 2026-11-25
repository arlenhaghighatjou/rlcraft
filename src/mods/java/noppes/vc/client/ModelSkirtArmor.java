package noppes.vc.client;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.Loader;

public class ModelSkirtArmor extends ModelBiped {
   private ModelPlaneRenderer Shape1 = new ModelPlaneRenderer(this, 4, 20);
   private final boolean moBends;

   public ModelSkirtArmor() {
      this.Shape1.addSidePlane(-2.6F, 9.0F, -2.6F, 9, 2);
      ModelPlaneRenderer part1 = new ModelPlaneRenderer(this, 6, 20);
      part1.addSidePlane(-0.6F, 9.0F, 2.6F, 9, 2);
      part1.rotateAngleY = -1.5707964F;
      this.Shape1.addChild(part1);
      this.Shape1.setRotationPoint(2.4F, 0.0F, 0.0F);
      this.setRotation(this.Shape1, 0.3F, -0.2F, -0.2F);
      this.moBends = Loader.isModLoaded("mobends");
   }

   public void setRotation(ModelRenderer model, float x, float y, float z) {
      model.rotateAngleX = x;
      model.rotateAngleY = y;
      model.rotateAngleZ = z;
   }

   public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7) {
      GlStateManager.pushMatrix();
      this.setRotationAngles(par2, par3, par4, par5, par6, par7, par1Entity);
      if (par1Entity.isSneaking()) {
         GlStateManager.translate(0.0D, 0.20000000298023224D, -0.2D);
         if (!this.moBends) {
            GlStateManager.translate(0.0D, -0.10000000149011612D, 0.35D);
         }

         GlStateManager.rotate(12.0F, 1.0F, 0.0F, 0.0F);
      }

      GlStateManager.scale(2.1F, 1.04F, 2.1F);

      for(int i = 0; i < 10; ++i) {
         GlStateManager.rotate(36.0F, 0.0F, 1.0F, 0.0F);
         this.Shape1.render(par7);
      }

      GlStateManager.popMatrix();
   }

   public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity) {
      super.setRotationAngles(par1, par2, par3, par4, par5, par6, par7Entity);
      this.setRotation(this.Shape1, 0.3F, -0.2F, -0.2F);
      this.Shape1.rotateAngleX += this.bipedLeftArm.rotateAngleX * 0.01F;
      this.Shape1.rotateAngleZ += this.bipedLeftArm.rotateAngleX * 0.03F;
      this.Shape1.rotateAngleZ -= MathHelper.cos(par3 * 0.06F) * 0.01F + 0.02F;
   }
}
