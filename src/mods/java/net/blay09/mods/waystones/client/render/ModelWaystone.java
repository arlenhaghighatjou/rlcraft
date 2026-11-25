package net.blay09.mods.waystones.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelWaystone extends ModelBase {
   private ModelRenderer top;
   private ModelRenderer topMidTop;
   private ModelRenderer pillar;
   private ModelRenderer topBottom;
   private ModelRenderer baseTop;
   private ModelRenderer topMidBottom;
   private ModelRenderer baseMid;
   private ModelRenderer baseBottom;

   public ModelWaystone() {
      this.textureWidth = 256;
      this.textureHeight = 256;
      this.top = new ModelRenderer(this, 0, 0);
      this.top.addBox(-8.0F, -64.0F, -8.0F, 16, 4, 16);
      this.topMidTop = new ModelRenderer(this, 64, 0);
      this.topMidTop.addBox(-10.0F, -60.0F, -10.0F, 20, 4, 20);
      this.topMidBottom = new ModelRenderer(this, 0, 76);
      this.topMidBottom.addBox(-14.0F, -56.0F, -14.0F, 28, 4, 28);
      this.topBottom = new ModelRenderer(this, 0, 24);
      this.topBottom.addBox(-12.0F, -52.0F, -12.0F, 24, 4, 24);
      this.pillar = new ModelRenderer(this, 144, 0);
      this.pillar.addBox(-10.0F, -48.0F, -10.0F, 20, 28, 20);
      this.baseTop = new ModelRenderer(this, 96, 48);
      this.baseTop.addBox(-12.0F, -20.0F, -12.0F, 24, 4, 24);
      this.baseMid = new ModelRenderer(this, 112, 76);
      this.baseMid.addBox(-14.0F, -16.0F, -14.0F, 28, 8, 28);
      this.baseBottom = new ModelRenderer(this, 0, 112);
      this.baseBottom.addBox(-16.0F, -8.0F, -16.0F, 32, 8, 32);
   }

   public void renderAll() {
      float f = 0.0625F;
      this.top.render(f);
      this.topMidTop.render(f);
      this.topMidBottom.render(f);
      this.topBottom.render(f);
      this.pillar.render(f);
      this.baseTop.render(f);
      this.baseMid.render(f);
      this.baseBottom.render(f);
   }

   public void renderPillar() {
      float f = 0.0625F;
      this.pillar.render(f);
   }
}
