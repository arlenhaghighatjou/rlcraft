package noppes.vc.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import noppes.vc.blocks.tiles.TilePedestal;
import noppes.vc.client.models.ModelPedestal;

public class BlockPedestalRenderer extends BlockRendererBasic {
   private final ModelPedestal model = new ModelPedestal();
   private static final ResourceLocation resource = new ResourceLocation("variedcommodities", "textures/models/block/pedestal.png");
   private int meta;

   public BlockPedestalRenderer(int meta) {
      this.meta = meta;
   }

   public void render(TileEntity var1, double var2, double var4, double var6, float var8, int blockDamage, float partialTick) {
      TilePedestal tile = (TilePedestal)var1;
      GlStateManager.enableAlpha();
      GlStateManager.disableBlend();
      GlStateManager.pushMatrix();
      GlStateManager.enableLighting();
      GlStateManager.translate((float)var2 + 0.5F, (float)var4 + 1.5F, (float)var6 + 0.5F);
      this.setMaterial(tile, this.meta);
      this.model.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
      GlStateManager.scale(1.0F, 0.99F, 1.0F);
      TextureManager manager = Minecraft.getMinecraft().getTextureManager();
      manager.bindTexture(resource);
      this.model.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
      if (!this.playerTooFar(tile)) {
         this.doRender(tile.getStackInSlot(0));
      }

      GlStateManager.popMatrix();
      GlStateManager.color(1.0F, 1.0F, 1.0F);
   }

   private void doRender(ItemStack itemstack) {
      if (!itemstack.isEmpty() && !(itemstack.getItem() instanceof ItemBlock)) {
         Item item = itemstack.getItem();
         GlStateManager.translate(0.0D, 0.6D, 0.0D);
         GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
         if (item instanceof ItemSword) {
            GlStateManager.rotate(180.0F, -1.0F, 0.0F, 0.0F);
         }

         Minecraft minecraft = Minecraft.getMinecraft();
         IBakedModel model = minecraft.getRenderItem().getItemModelMesher().getItemModel(itemstack);
         ItemTransformVec3f p_175034_1_ = model.getItemCameraTransforms().thirdperson_right;
         GlStateManager.scale(p_175034_1_.scale.x + ItemCameraTransforms.offsetScaleX, p_175034_1_.scale.y + ItemCameraTransforms.offsetScaleY, p_175034_1_.scale.z + ItemCameraTransforms.offsetScaleZ);
         GlStateManager.rotate(45.0F, 0.0F, 0.0F, 1.0F);
         minecraft.getRenderItem().renderItem(itemstack, TransformType.NONE);
      }
   }

   public int specialRenderDistance() {
      return 40;
   }
}
