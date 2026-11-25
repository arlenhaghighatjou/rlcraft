package gigaherz.toolbelt.client;

import gigaherz.toolbelt.BeltFinder;
import gigaherz.toolbelt.Config;
import gigaherz.toolbelt.ToolBelt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class LayerToolBelt implements LayerRenderer<EntityPlayer> {
   private static final ResourceLocation TEXTURE_BELT = ToolBelt.location("textures/entity/belt.png");
   private final RenderLivingBase<?> livingEntityRenderer;
   private final ModelBase beltModel = new ModelBase() {
      final ModelRenderer belt = new ModelRenderer(this);
      final ModelRenderer buckle = new ModelRenderer(this, 10, 10);
      final ModelRenderer pocketL = new ModelRenderer(this, 0, 10);
      final ModelRenderer pocketR = new ModelRenderer(this, 0, 10);

      {
         this.belt.addBox(-5.0F, 10.0F, -3.0F, 10, 4, 6);
         this.buckle.addBox(-2.5F, 9.5F, -3.5F, 5, 5, 1);
         this.pocketL.addBox(-2.0F, 12.0F, 5.0F, 4, 4, 1);
         this.pocketL.rotateAngleY = (float)Math.toRadians(-90.0D);
         this.pocketR.addBox(-2.0F, 12.0F, 5.0F, 4, 4, 1);
         this.pocketR.rotateAngleY = (float)Math.toRadians(90.0D);
      }

      public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
         GlStateManager.disableRescaleNormal();
         GlStateManager.disableCull();
         this.belt.render(scale);
         this.pocketL.render(scale);
         this.pocketR.render(scale);
         GlStateManager.pushMatrix();
         GlStateManager.scale(0.8F, 1.0F, 1.0F);
         this.buckle.render(scale);
         GlStateManager.popMatrix();
      }
   };

   public LayerToolBelt(RenderLivingBase<?> livingEntityRendererIn) {
      this.livingEntityRenderer = livingEntityRendererIn;
   }

   public void doRenderLayer(EntityPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      boolean flag = player.getPrimaryHand() == EnumHandSide.RIGHT;
      if (Config.showBeltOnPlayers) {
         BeltFinder.BeltGetter getter = BeltFinder.findBelt(player);
         if (getter != null) {
            ItemStack stack = getter.getBelt();
            if (stack.getCount() > 0) {
               IItemHandler cap = (IItemHandler)stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null);
               ItemStack firstItem = cap.getStackInSlot(0);
               ItemStack secondItem = cap.getStackInSlot(1);
               ItemStack leftItem = flag ? firstItem : secondItem;
               ItemStack rightItem = flag ? secondItem : firstItem;
               GlStateManager.pushMatrix();
               if (player.isSneaking()) {
                  GlStateManager.translate(0.0F, 0.2F, 0.0F);
               }

               this.translateToBody();
               if (!leftItem.isEmpty() || !rightItem.isEmpty()) {
                  GlStateManager.pushMatrix();
                  if (this.livingEntityRenderer.getMainModel().isChild) {
                     GlStateManager.translate(0.0F, 0.75F, 0.0F);
                     GlStateManager.scale(0.5F, 0.5F, 0.5F);
                  }

                  this.renderHeldItem(player, rightItem, TransformType.THIRD_PERSON_RIGHT_HAND, EnumHandSide.RIGHT);
                  this.renderHeldItem(player, leftItem, TransformType.THIRD_PERSON_LEFT_HAND, EnumHandSide.LEFT);
                  GlStateManager.popMatrix();
               }

               GlStateManager.translate(0.0F, 0.19F, 0.0F);
               GlStateManager.scale(0.85F, 0.6F, 0.78F);
               this.livingEntityRenderer.bindTexture(TEXTURE_BELT);
               this.beltModel.render(player, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, scale);
               GlStateManager.popMatrix();
            }
         }
      }
   }

   private void translateToBody() {
      ((ModelBiped)this.livingEntityRenderer.getMainModel()).bipedBody.postRender(0.0625F);
   }

   private void renderHeldItem(EntityLivingBase player, ItemStack stack, TransformType cameraTransform, EnumHandSide handSide) {
      if (!stack.isEmpty()) {
         GlStateManager.pushMatrix();
         if (handSide == EnumHandSide.LEFT) {
            GlStateManager.translate(-0.271875F, 0.7F, -0.1F);
         } else {
            GlStateManager.translate(0.271875F, 0.7F, -0.1F);
         }

         GlStateManager.rotate(40.0F, 1.0F, 0.0F, 0.0F);
         float scale = Config.beltItemScale;
         GlStateManager.scale(scale, scale, scale);
         Minecraft.getMinecraft().getItemRenderer().renderItemSide(player, stack, cameraTransform, handSide == EnumHandSide.LEFT);
         GlStateManager.popMatrix();
      }
   }

   public boolean shouldCombineTextures() {
      return false;
   }
}
