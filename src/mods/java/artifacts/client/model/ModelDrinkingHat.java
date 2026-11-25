package artifacts.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.ResourceLocation;

public class ModelDrinkingHat extends ModelBase {
   private static final ResourceLocation TEXTURES = new ResourceLocation("artifacts", "textures/entity/layer/drinking_hat.png");
   private static final ResourceLocation SPECIAL_TEXTURES = new ResourceLocation("artifacts", "textures/entity/layer/drinking_hat_special.png");
   protected ModelRenderer hat;
   protected ModelRenderer canLeft;
   protected ModelRenderer canRight;
   protected ModelRenderer strawLeft;
   protected ModelRenderer strawRight;
   protected ModelRenderer strawMiddle;
   protected ModelRenderer hatShade;

   public ModelDrinkingHat() {
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.hat = new ModelRenderer(this, 26, 41);
      this.hat.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 1.0F);
      this.hatShade = new ModelRenderer(this, 0, 52);
      this.hatShade.addBox(-4.0F, -6.0F, -8.0F, 8, 1, 4, 0.0F);
      this.hat.addChild(this.hatShade);
      this.canLeft = new ModelRenderer(this, 0, 41);
      this.canLeft.addBox(4.0F, -11.0F, -1.0F, 3, 6, 3);
      this.hat.addChild(this.canLeft);
      this.canRight = new ModelRenderer(this, 12, 41);
      this.canRight.addBox(-7.0F, -11.0F, -1.0F, 3, 6, 3);
      this.hat.addChild(this.canRight);
      this.strawLeft = new ModelRenderer(this, 0, 32);
      this.strawLeft.addBox(5.0F, -4.0F, -3.0F, 1, 1, 8);
      this.strawLeft.rotateAngleX = 0.7853F;
      this.hat.addChild(this.strawLeft);
      this.strawRight = new ModelRenderer(this, 17, 32);
      this.strawRight.addBox(-6.0F, -4.0F, -3.0F, 1, 1, 8);
      this.strawRight.rotateAngleX = 0.7853F;
      this.hat.addChild(this.strawRight);
      this.strawMiddle = new ModelRenderer(this, 0, 50);
      this.strawMiddle.addBox(-6.0F, -1.0F, -5.0F, 12, 1, 1);
      this.hat.addChild(this.strawMiddle);
   }

   public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      if (entity instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entity;
         if (player.getName().equals("wouterke")) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(SPECIAL_TEXTURES);
            this.hatShade.showModel = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).isEmpty();
         } else {
            Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURES);
            this.hatShade.showModel = false;
         }

         this.hat.render(scale);
      }
   }
}
