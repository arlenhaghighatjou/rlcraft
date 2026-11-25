package slimeknights.mantle.client.gui.book.element;

import java.util.stream.IntStream;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.lax1dude.eaglercraft.v1_8.Mouse;
import slimeknights.mantle.client.book.StructureBlockAccess;
import slimeknights.mantle.client.book.StructureInfo;
import slimeknights.mantle.client.book.data.element.BlockData;
import slimeknights.mantle.client.gui.book.GuiBook;

public class ElementStructure extends SizedBookElement implements IButtonClickHandler {
   public static final int BUTTON_ID_LAYER_UP = 0;
   public static final int BUTTON_ID_LAYER_DOWN = 1;
   public static final int BUTTON_ID_ANIMATE = 2;
   private float scale = 50.0F;
   private float xTranslate = 0.0F;
   private float yTranslate = 0.0F;
   private float w = 0.0F;
   private float h = 0.0F;
   boolean canTick = false;
   int tick = 0;
   float rotX = 0.0F;
   float rotY = 0.0F;
   float rotZ = 0.0F;
   public StructureInfo structureData;
   StructureBlockAccess blockAccess;
   int[] lastClick = null;
   private int fullStructureSteps = 5;
   private int lastX;
   private int lastY;

   public ElementStructure(int x, int y, int width, int height, int[] size, BlockData[] structure) {
      super(x, y, width, height);
      if (size.length == 3) {
         this.scale = 100.0F / (float)IntStream.of(size).max().getAsInt();
         float sx = (float)width / (float)GuiBook.PAGE_WIDTH;
         float sy = (float)height / (float)GuiBook.PAGE_HEIGHT;
         this.scale *= Math.min(sx, sy);
         this.xTranslate = (float)(x + width / 2);
         this.yTranslate = (float)(y + height / 2);
         this.w = (float)size[0] * this.scale;
         this.h = (float)size[1] * this.scale;
      }

      this.init(size, structure);
   }

   public void init(int[] size, BlockData[] data) {
      int yOff = 0;
      this.structureData = new StructureInfo(size[0], size[1], size[2], data);
      this.blockAccess = new StructureBlockAccess(this.structureData);
      this.rotX = 25.0F;
      this.rotY = -45.0F;
   }

   public void draw(int mouseX, int mouseY, float partialTicks, FontRenderer fontRenderer) {
      if (this.lastClick != null) {
         if (!Mouse.isButtonDown(0) && !Mouse.isButtonDown(1)) {
            this.lastClick = null;
         } else {
            int dx = mouseX - this.lastClick[0];
            int dy = mouseY - this.lastClick[1];
            float maxSpeed = 10.0F;
            float changeX = Math.min(maxSpeed, (float)dx / 10.0F);
            float changeY = Math.min(maxSpeed, (float)dy / 10.0F);
            this.rotY += changeX;
            this.rotX += changeY;
         }
      }

      if (this.canTick) {
         if (++this.tick % 20 == 0 && (this.structureData.canStep() || ++this.fullStructureSteps >= 5)) {
            this.structureData.step();
            this.fullStructureSteps = 0;
         }
      } else {
         this.structureData.reset();
         this.structureData.setShowLayer(9);
      }

      int structureLength = this.structureData.structureLength;
      int structureWidth = this.structureData.structureWidth;
      int structureHeight = this.structureData.structureHeight;
      int xHalf = structureWidth * 5 - structureLength * 5;
      int yOffPartial = (structureHeight - 1) * 16 + structureWidth * 8 + structureLength * 8;
      int yOffTotal = Math.max(52, yOffPartial + 16);
      GlStateManager.enableRescaleNormal();
      GlStateManager.pushMatrix();
      RenderHelper.disableStandardItemLighting();
      int i = 0;
      ItemStack highlighted = null;
      BlockRendererDispatcher blockRender = Minecraft.getMinecraft().getBlockRendererDispatcher();
      float f = (float)Math.sqrt((double)(structureHeight * structureHeight + structureWidth * structureWidth + structureLength * structureLength));
      yOffTotal = 10 + Math.max(10 + (structureHeight > 1 ? 36 : 0), (int)(f * this.scale));
      GlStateManager.translate(this.xTranslate, this.yTranslate, (float)Math.max(structureHeight, Math.max(structureWidth, structureLength)));
      GlStateManager.scale(this.scale, -this.scale, 1.0F);
      GlStateManager.rotate(this.rotX, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(this.rotY, 0.0F, 1.0F, 0.0F);
      GlStateManager.translate((float)structureLength / -2.0F, (float)structureHeight / -2.0F, (float)structureWidth / -2.0F);
      GlStateManager.disableLighting();
      if (Minecraft.isAmbientOcclusionEnabled()) {
         GlStateManager.shadeModel(7425);
      } else {
         GlStateManager.shadeModel(7424);
      }

      if (structureWidth % 2 == 1) {
         ;
      }

      int iterator = 0;
      this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

      for(int h = 0; h < this.structureData.structureHeight; ++h) {
         for(int l = 0; l < this.structureData.structureLength; ++l) {
            for(int w = 0; w < this.structureData.structureWidth; ++w) {
               BlockPos pos = new BlockPos(l, h, w);
               if (!this.blockAccess.isAirBlock(pos)) {
                  IBlockState state = this.blockAccess.getBlockState(pos);
                  Tessellator tessellator = Tessellator.getInstance();
                  WorldRenderer buffer = tessellator.getBuffer();
                  buffer.begin(7, DefaultVertexFormats.BLOCK);
                  blockRender.renderBlock(state, pos, this.blockAccess, buffer);
                  tessellator.draw();
               }
            }
         }
      }

      GlStateManager.popMatrix();
      RenderHelper.disableStandardItemLighting();
      GlStateManager.disableRescaleNormal();
      GlStateManager.shadeModel(7424);
      GlStateManager.enableBlend();
      RenderHelper.disableStandardItemLighting();
   }

   public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      this.lastClick = new int[]{mouseX, mouseY};
   }

   public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton) {
      int dx = mouseX - this.lastX;
      int dy = mouseX - this.lastY;
      float maxSpeed = 1.0F;
      Math.min(maxSpeed, (float)dx / 100.0F);
      Math.min(maxSpeed, (float)dy / 100.0F);
   }

   public void mouseReleased(int mouseX, int mouseY, int clickedMouseButton) {
      super.mouseReleased(mouseX, mouseY, clickedMouseButton);
      this.lastClick = null;
   }

   public void mouseDragged(int clickX, int clickY, int mx, int my, int lastX, int lastY, int button) {
      int dx = mx - lastX;
      int dy = my - lastY;
      this.rotY += (float)dx / 104.0F;
      this.rotX += (float)dy / 100.0F;
   }

   public void changeActiveLayer(int direction) {
   }

   public boolean onButtonClick(int buttonId, ElementButton button) {
      switch(buttonId) {
      case 2:
         this.canTick = !this.canTick;
         return true;
      default:
         return false;
      }
   }
}
