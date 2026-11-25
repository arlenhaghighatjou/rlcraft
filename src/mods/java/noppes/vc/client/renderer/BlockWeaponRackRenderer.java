package noppes.vc.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import noppes.vc.blocks.tiles.TileWeaponRack;
import noppes.vc.client.models.ModelWeaponRack;

public class BlockWeaponRackRenderer extends BlockRendererBasic {
   private final ModelWeaponRack model = new ModelWeaponRack();
   private int meta;

   public BlockWeaponRackRenderer(int meta) {
      this.meta = meta;
   }

   public void render(TileEntity var1, double var2, double var4, double var6, float var8, int blockDamage, float partialTick) {
      TileWeaponRack tile = (TileWeaponRack)var1;
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)var2 + 0.5F, (float)var4 + 1.34F, (float)var6 + 0.5F);
      GlStateManager.scale(0.9F, 0.9F, 0.9F);
      if (tile == null) {
         GlStateManager.translate(-0.3D, -0.8D, 0.0D);
         GlStateManager.scale(1.0D, 0.7D, 1.0D);
      }

      this.setWood(tile, this.meta, 90);
      this.model.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
      if (!this.playerTooFar(tile)) {
         for(int i = 0; i < 3; ++i) {
            this.doRender(tile.getStackInSlot(i), i);
         }
      }

      GlStateManager.popMatrix();
   }

   private void doRender(ItemStack itemstack, int pos) {
      if (!itemstack.isEmpty() && !(itemstack.getItem() instanceof ItemBlock)) {
         GlStateManager.pushMatrix();
         GlStateManager.translate(-0.37F + (float)pos * 0.37F, 0.6F, 0.33F);
         Minecraft minecraft = Minecraft.getMinecraft();
         IBakedModel model = minecraft.getRenderItem().getItemModelMesher().getItemModel(itemstack);
         ItemTransformVec3f p_175034_1_ = model.getItemCameraTransforms().thirdperson_right;
         Item item = itemstack.getItem();
         GlStateManager.rotate(p_175034_1_.rotation.x, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.scale(p_175034_1_.scale.x + ItemCameraTransforms.offsetScaleX, p_175034_1_.scale.y + ItemCameraTransforms.offsetScaleY, p_175034_1_.scale.z + ItemCameraTransforms.offsetScaleZ);
         GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(45.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         minecraft.getRenderItem().renderItem(itemstack, TransformType.NONE);
         GlStateManager.popMatrix();
      }
   }

   public int specialRenderDistance() {
      return 26;
   }
}
