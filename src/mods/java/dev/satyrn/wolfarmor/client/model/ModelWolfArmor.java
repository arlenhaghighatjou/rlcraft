package dev.satyrn.wolfarmor.client.model;

import dev.satyrn.wolfarmor.mixin.accessors.ModelWolfAccessor;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelWolf;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelWolfArmor extends ModelWolf {
   public ModelWolfArmor(float scale) {
      this.wolfHeadMain = new ModelRenderer(this, 0, 0);
      this.wolfHeadMain.addBox(-2.0F, -3.0F, -2.0F, 6, 6, 4, scale);
      this.wolfHeadMain.setRotationPoint(-1.0F, 13.5F, -7.0F);
      this.wolfBody = new ModelRenderer(this, 18, 14);
      this.wolfBody.addBox(-3.0F, -2.0F, -3.0F, 6, 9, 6, scale);
      this.wolfBody.setRotationPoint(0.0F, 14.0F, 2.0F);
      this.wolfLeg1 = new ModelRenderer(this, 0, 18);
      this.wolfLeg1.addBox(0.0F, 0.0F, -1.0F, 2, 8, 2, scale);
      this.wolfLeg1.setRotationPoint(-2.5F, 16.0F, 7.0F);
      this.wolfLeg2 = new ModelRenderer(this, 0, 18);
      this.wolfLeg2.addBox(0.0F, 0.0F, -1.0F, 2, 8, 2, scale);
      this.wolfLeg2.setRotationPoint(0.5F, 16.0F, 7.0F);
      this.wolfLeg3 = new ModelRenderer(this, 0, 18);
      this.wolfLeg3.addBox(0.0F, 0.0F, -1.0F, 2, 8, 2, scale);
      this.wolfLeg3.setRotationPoint(-2.5F, 16.0F, -4.0F);
      this.wolfLeg4 = new ModelRenderer(this, 0, 18);
      this.wolfLeg4.addBox(0.0F, 0.0F, -1.0F, 2, 8, 2, scale);
      this.wolfLeg4.setRotationPoint(0.5F, 16.0F, -4.0F);
      this.wolfHeadMain.setTextureOffset(16, 14).addBox(-2.0F, -5.0F, 0.0F, 2, 2, 1, scale);
      this.wolfHeadMain.setTextureOffset(16, 14).addBox(2.0F, -5.0F, 0.0F, 2, 2, 1, scale);
      this.wolfHeadMain.setTextureOffset(0, 10).addBox(-0.5F, 0.0F, -5.0F, 3, 3, 4, scale);
      ModelRenderer wolfMane = new ModelRenderer(this, 21, 0);
      wolfMane.addBox(-3.0F, -3.0F, -3.0F, 8, 6, 7, scale);
      wolfMane.setRotationPoint(-1.0F, 14.0F, 2.0F);
      ModelRenderer wolfTail = new ModelRenderer(this, 9, 18);
      wolfTail.addBox(0.0F, 0.0F, -1.0F, 2, 8, 2, scale);
      wolfTail.setRotationPoint(-1.0F, 12.0F, 8.0F);
      ModelWolfAccessor accessor = (ModelWolfAccessor)this;
      accessor.setWolfTail(wolfTail);
      accessor.setWolfMane(wolfMane);
   }
}
