package ivorius.reccomplex.client.rendering;

import ivorius.ivtoolkit.blocks.BlockArea;
import ivorius.ivtoolkit.rendering.grid.AreaRenderer;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.capability.SelectionOwner;
import ivorius.reccomplex.item.ItemBlockSelector;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.OpenGlHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class SelectionRenderer {
   public static ResourceLocation[] TEXTURE = new ResourceLocation[100];
   public static ResourceLocation[] LATTICE_TEXTURE;

   public SelectionRenderer() {
   }

   public static void renderSelection(EntityLivingBase entity, int ticks, float partialTicks) {
      Minecraft mc = Minecraft.getMinecraft();
      BlockPos selPoint1 = null;
      BlockPos selPoint2 = null;
      SelectionOwner owner = SelectionOwner.getOwner(entity, (EnumFacing)null);
      if (owner != null) {
         selPoint1 = owner.getSelectedPoint1();
         selPoint2 = owner.getSelectedPoint2();
      }

      GL11.glLineWidth(3.0F);
      if (selPoint1 != null) {
         GlStateManager.color(0.6F, 0.8F, 0.95F);
         AreaRenderer.renderAreaLined(new BlockArea(selPoint1, selPoint1), 0.03F);
      }

      if (selPoint2 != null) {
         GlStateManager.color(0.2F, 0.45F, 0.65F);
         AreaRenderer.renderAreaLined(new BlockArea(selPoint2, selPoint2), 0.04F);
      }

      for(EnumHand enumHand : EnumHand.values()) {
         ItemStack heldItem = entity.getHeldItem(enumHand);
         if (heldItem != null && heldItem.getItem() instanceof ItemBlockSelector) {
            ItemBlockSelector selector = (ItemBlockSelector)heldItem.getItem();
            BlockPos hoverPoint = selector.hoveredBlock(heldItem, entity);
            GlStateManager.color(0.6F, 0.6F, 1.0F);
            AreaRenderer.renderAreaLined(new BlockArea(hoverPoint, hoverPoint), 0.05F);
         }
      }

      if (selPoint1 != null && selPoint2 != null) {
         BlockArea selArea = new BlockArea(selPoint1, selPoint2);
         GlStateManager.color(0.4F, 0.65F, 0.8F);
         AreaRenderer.renderAreaLined(selArea, 0.02F);
         GlStateManager.enableBlend();
         OpenGlHelper.glBlendFunc(770, 771, 1, 0);
         GlStateManager.alphaFunc(516, 1.0E-4F);
         ResourceLocation curTex = TEXTURE[MathHelper.floor(((float)ticks + partialTicks) * 0.75F) % TEXTURE.length];
         mc.renderEngine.bindTexture(curTex);
         GlStateManager.color(0.2F, 0.5F, 0.6F, 0.5F);
         AreaRenderer.renderArea(selArea, false, true, 0.01F);
         GlStateManager.color(0.4F, 0.65F, 0.8F, 0.75F);
         AreaRenderer.renderArea(selArea, false, false, 0.01F);
         GlStateManager.alphaFunc(516, 0.002F);
         GlStateManager.disableBlend();
      }

   }

   static {
      for(int i = 0; i < TEXTURE.length; ++i) {
         TEXTURE[i] = new ResourceLocation("reccomplex", String.format("%sselection/selection_%05d.png", RecurrentComplex.filePathTextures, i));
      }

      LATTICE_TEXTURE = new ResourceLocation[100];

      for(int i = 0; i < LATTICE_TEXTURE.length; ++i) {
         LATTICE_TEXTURE[i] = new ResourceLocation("reccomplex", String.format("%sselection-lattice/lattice_%05d.png", RecurrentComplex.filePathTextures, i));
      }

   }
}
