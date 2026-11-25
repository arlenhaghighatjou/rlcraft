package artifacts.client.model.layer;

import artifacts.client.model.ModelAmulet;
import artifacts.client.model.ModelPanicNecklace;
import artifacts.client.model.ModelUltimatePendant;
import artifacts.common.init.ModItems;
import artifacts.common.util.RenderHelper;
import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class LayerAmulet extends LayerBauble {
   private static final ResourceLocation SHOCK_TEXTURE = new ResourceLocation("artifacts", "textures/entity/layer/shock_pendant.png");
   private static final ResourceLocation FLAME_TEXTURE = new ResourceLocation("artifacts", "textures/entity/layer/flame_pendant.png");
   private static final ResourceLocation THORN_TEXTURE = new ResourceLocation("artifacts", "textures/entity/layer/thorn_pendant.png");
   private static final ResourceLocation PANIC_TEXTURE = new ResourceLocation("artifacts", "textures/entity/layer/panic_necklace.png");
   private static final ResourceLocation ULTIMATE_TEXTURE = new ResourceLocation("artifacts", "textures/entity/layer/ultimate_pendant.png");
   private static final ResourceLocation SACRIFICIAL_TEXTURE = new ResourceLocation("artifacts", "textures/entity/layer/sacrificial_amulet.png");
   private static final ModelBase AMULET_MODEL = new ModelAmulet();
   private static final ModelBase PANIC_MODEL = new ModelPanicNecklace();
   private static final ModelBase ULTIMATE_MODEL = new ModelUltimatePendant();

   public LayerAmulet(RenderPlayer renderPlayer) {
      super(renderPlayer);
   }

   protected void renderLayer(@Nonnull EntityPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.renderChest(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
   }

   private void renderChest(@Nonnull EntityPlayer player, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      ModelBase amulet = this.setTexturesGetModel(player);
      if (amulet != null) {
         if (player.isSneaking()) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
         }

         this.modelPlayer.bipedBody.postRender(scale);
         amulet.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      }
   }

   private ModelBase setTexturesGetModel(EntityPlayer player) {
      ItemStack stack = BaublesApi.getBaublesHandler(player).getStackInSlot(BaubleType.AMULET.getValidSlots()[0]);
      if (!RenderHelper.shouldItemStackRender(player, stack)) {
         return null;
      } else {
         ResourceLocation textures = this.getTextures(stack);
         if (textures != null) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(textures);
            if (stack.getItem() == ModItems.PANIC_NECKLACE) {
               return PANIC_MODEL;
            } else {
               return stack.getItem() == ModItems.ULTIMATE_PENDANT ? ULTIMATE_MODEL : AMULET_MODEL;
            }
         } else {
            return null;
         }
      }
   }

   @Nullable
   private ResourceLocation getTextures(ItemStack stack) {
      if (stack.getItem() == ModItems.SHOCK_PENDANT) {
         return SHOCK_TEXTURE;
      } else if (stack.getItem() == ModItems.FLAME_PENDANT) {
         return FLAME_TEXTURE;
      } else if (stack.getItem() == ModItems.THORN_PENDANT) {
         return THORN_TEXTURE;
      } else if (stack.getItem() == ModItems.PANIC_NECKLACE) {
         return PANIC_TEXTURE;
      } else if (stack.getItem() == ModItems.ULTIMATE_PENDANT) {
         return ULTIMATE_TEXTURE;
      } else {
         return stack.getItem() == ModItems.SACRIFICIAL_AMULET ? SACRIFICIAL_TEXTURE : null;
      }
   }
}
