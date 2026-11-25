package vazkii.quark.oddities.client.gui;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.GuiScrollingList;
import org.lwjgl.input.Mouse;
import vazkii.arl.network.NetworkHandler;
import vazkii.arl.util.ClientTicker;
import vazkii.quark.base.network.message.MessageMatrixEnchanterOperation;
import vazkii.quark.oddities.client.gui.button.GuiButtonMatrixEnchantingPlus;
import vazkii.quark.oddities.feature.MatrixEnchanting;
import vazkii.quark.oddities.inventory.ContainerMatrixEnchanting;
import vazkii.quark.oddities.inventory.EnchantmentMatrix;
import vazkii.quark.oddities.tile.TileMatrixEnchanter;

public class GuiMatrixEnchanting extends GuiContainer {
   public static final ResourceLocation BACKGROUND = new ResourceLocation("quark", "textures/misc/matrix_enchanting.png");
   protected final InventoryPlayer playerInv;
   protected final TileMatrixEnchanter enchanter;
   protected GuiButton plusButton;
   protected GuiMatrixEnchanting.PieceList pieceList;
   protected EnchantmentMatrix.Piece hoveredPiece;
   protected int selectedPiece = -1;
   protected int gridHoverX;
   protected int gridHoverY;
   protected List<Integer> listPieces = null;

   public GuiMatrixEnchanting(InventoryPlayer playerInv, TileMatrixEnchanter enchanter) {
      super(new ContainerMatrixEnchanting(playerInv, enchanter));
      this.playerInv = playerInv;
      this.enchanter = enchanter;
   }

   public void initGui() {
      super.initGui();
      this.selectedPiece = -1;
      this.addButton(this.plusButton = new GuiButtonMatrixEnchantingPlus(this.guiLeft + 86, this.guiTop + 63));
      this.pieceList = new GuiMatrixEnchanting.PieceList(this, 29, 64, this.guiTop + 11, this.guiLeft + 139, 22);
      this.updateButtonStatus();
   }

   public void updateScreen() {
      super.updateScreen();
      this.updateButtonStatus();
      if (this.enchanter.matrix == null) {
         this.selectedPiece = -1;
      }

   }

   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(BACKGROUND);
      int i = this.guiLeft;
      int j = this.guiTop;
      this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
      if (this.enchanter.charge > 0 && MatrixEnchanting.chargePerLapis > 0) {
         int maxHeight = 18;
         int barHeight = (int)((float)this.enchanter.charge / (float)MatrixEnchanting.chargePerLapis * (float)maxHeight);
         this.drawTexturedModalRect(i + 7, j + 32 + maxHeight - barHeight, 50, 176 + maxHeight - barHeight, 4, barHeight);
      }

      if (this.enchanter.matrix != null && this.enchanter.matrix.canGeneratePiece(this.enchanter.bookshelfPower, this.enchanter.enchantability)) {
         int x = i + 74;
         int y = j + 58;
         int xpCost = this.enchanter.matrix.getNewPiecePrice();
         int xpMin = this.enchanter.matrix.getMinXpLevel(this.enchanter.bookshelfPower);
         boolean has = this.enchanter.matrix.validateXp(this.mc.player, this.enchanter.bookshelfPower);
         this.drawTexturedModalRect(x, y, 0, this.ySize, 10, 10);
         String text = String.valueOf(xpCost);
         if (!has && this.mc.player.experienceLevel < xpMin) {
            this.fontRenderer.drawStringWithShadow("!", (float)(x + 6), (float)(y + 3), 16711680);
            text = I18n.format("quarkmisc.matrixMin", new Object[]{xpMin});
         }

         x = x - (this.fontRenderer.getStringWidth(text) - 5);
         y = y + 3;
         this.fontRenderer.drawString(text, x - 1, y, 0);
         this.fontRenderer.drawString(text, x + 1, y, 0);
         this.fontRenderer.drawString(text, x, y + 1, 0);
         this.fontRenderer.drawString(text, x, y - 1, 0);
         this.fontRenderer.drawString(text, x, y, has ? 13172623 : 16748431);
      }

   }

   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      this.fontRenderer.drawString(this.enchanter.getDisplayName().getUnformattedText(), 12, 5, 4210752);
      this.fontRenderer.drawString(this.playerInv.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
      if (this.enchanter.matrix != null) {
         this.listPieces = this.enchanter.matrix.benchedPieces;
         this.renderMatrixGrid(this.enchanter.matrix);
      }

   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      super.drawScreen(mouseX, mouseY, partialTicks);
      if (this.enchanter.matrix != null) {
         RenderHelper.disableStandardItemLighting();
         this.pieceList.drawScreen(mouseX, mouseY, ClientTicker.partialTicks);
      }

      if (this.hoveredPiece != null) {
         List<String> tooltip = new LinkedList();
         tooltip.add(this.hoveredPiece.enchant.getTranslatedName(this.hoveredPiece.level));
         if (this.hoveredPiece.influence > 0) {
            tooltip.add(TextFormatting.GRAY + I18n.format("quarkmisc.matrixInfluence", new Object[]{(int)((float)this.hoveredPiece.influence * MatrixEnchanting.influencePower * 100.0F)}));
         }

         int max = this.hoveredPiece.getMaxXP();
         if (max > 0) {
            tooltip.add(TextFormatting.GRAY + I18n.format("quarkmisc.matrixUpgrade", new Object[]{this.hoveredPiece.xp, max}));
         }

         if (this.gridHoverX == -1) {
            tooltip.add("");
            tooltip.add(TextFormatting.GRAY + I18n.format("quarkmisc.matrixLeftClick", new Object[0]));
            tooltip.add(TextFormatting.GRAY + I18n.format("quarkmisc.matrixRightClick", new Object[0]));
         } else if (this.selectedPiece != -1) {
            EnchantmentMatrix.Piece p = this.getPiece(this.selectedPiece);
            if (p != null && p.enchant == this.hoveredPiece.enchant && this.hoveredPiece.level < this.hoveredPiece.enchant.getMaxLevel()) {
               tooltip.add("");
               tooltip.add(TextFormatting.GRAY + I18n.format("quarkmisc.matrixMerge", new Object[0]));
            }
         }

         this.drawHoveringText(tooltip, mouseX, mouseY);
      } else {
         this.renderHoveredToolTip(mouseX, mouseY);
      }

   }

   public void handleMouseInput() throws IOException {
      int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
      int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
      if (this.enchanter.matrix != null) {
         this.pieceList.handleMouseInput(mouseX, mouseY);
      }

      int gridMouseX = mouseX - this.guiLeft - 86;
      int gridMouseY = mouseY - this.guiTop - 11;
      this.gridHoverX = gridMouseX < 0 ? -1 : gridMouseX / 10;
      this.gridHoverY = gridMouseY < 0 ? -1 : gridMouseY / 10;
      if (this.gridHoverX >= 0 && this.gridHoverX <= 4 && this.gridHoverY >= 0 && this.gridHoverY <= 4) {
         if (this.enchanter.matrix != null) {
            int hover = this.enchanter.matrix.matrix[this.gridHoverX][this.gridHoverY];
            this.hoveredPiece = this.getPiece(hover);
         }
      } else {
         this.gridHoverX = -1;
         this.gridHoverY = -1;
         this.hoveredPiece = null;
      }

      super.handleMouseInput();
   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      if (this.enchanter.matrix != null) {
         if (mouseButton == 0 && this.gridHoverX != -1) {
            int hover = this.enchanter.matrix.matrix[this.gridHoverX][this.gridHoverY];
            if (this.selectedPiece != -1) {
               if (hover == -1) {
                  this.place(this.selectedPiece, this.gridHoverX, this.gridHoverY);
               } else {
                  this.merge(this.selectedPiece);
               }
            } else {
               this.remove(hover);
               if (!isShiftKeyDown()) {
                  this.selectedPiece = hover;
               }
            }
         } else if (mouseButton == 1 && this.selectedPiece != -1) {
            this.rotate(this.selectedPiece);
         }

      }
   }

   private void renderMatrixGrid(EnchantmentMatrix matrix) {
      this.mc.getTextureManager().bindTexture(BACKGROUND);
      GlStateManager.pushMatrix();
      GlStateManager.translate(86.0F, 11.0F, 0.0F);
      Iterator piece = matrix.placedPieces.iterator();

      while(piece.hasNext()) {
         int i = ((Integer)piece.next()).intValue();
         EnchantmentMatrix.Piece piece = this.getPiece(i);
         if (piece != null) {
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)(piece.x * 10), (float)(piece.y * 10), 0.0F);
            this.renderPiece(piece, 1.0F);
            GlStateManager.popMatrix();
         }
      }

      if (this.selectedPiece != -1 && this.gridHoverX != -1) {
         EnchantmentMatrix.Piece piece = this.getPiece(this.selectedPiece);
         if (piece != null && (this.hoveredPiece == null || piece.enchant != this.hoveredPiece.enchant || this.hoveredPiece.level >= this.hoveredPiece.enchant.getMaxLevel())) {
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)(this.gridHoverX * 10), (float)(this.gridHoverY * 10), 0.0F);
            float a = 0.2F;
            if (matrix.canPlace(piece, this.gridHoverX, this.gridHoverY)) {
               a = (float)((Math.sin((double)ClientTicker.total * 0.2D) + 1.0D) * 0.4D + 0.4D);
            }

            this.renderPiece(piece, a);
            GlStateManager.popMatrix();
         }
      }

      if (this.hoveredPiece == null && this.gridHoverX != -1) {
         this.renderHover(this.gridHoverX, this.gridHoverY);
      }

      GlStateManager.popMatrix();
   }

   private void renderPiece(EnchantmentMatrix.Piece piece, float a) {
      float r = (float)(piece.color >> 16 & 255) / 255.0F;
      float g = (float)(piece.color >> 8 & 255) / 255.0F;
      float b = (float)(piece.color & 255) / 255.0F;
      boolean hovered = this.hoveredPiece == piece;

      for(int[] block : piece.blocks) {
         this.renderBlock(block[0], block[1], piece.type, r, g, b, a, hovered);
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F);
   }

   private void renderBlock(int x, int y, int type, float r, float g, float b, float a, boolean hovered) {
      GlStateManager.color(r, g, b, a);
      this.drawTexturedModalRect(x * 10, y * 10, 11 + type * 10, this.ySize, 10, 10);
      if (hovered) {
         this.renderHover(x, y);
      }

   }

   private void renderHover(int x, int y) {
      drawRect(x * 10, y * 10, x * 10 + 10, y * 10 + 10, 1728053247);
   }

   protected void actionPerformed(GuiButton button) {
      if (button == this.plusButton) {
         this.add();
      }

   }

   public void add() {
      this.send(0, 0, 0, 0);
   }

   public void place(int id, int x, int y) {
      this.send(1, id, x, y);
      this.selectedPiece = -1;
      this.click();
   }

   public void remove(int id) {
      this.send(2, id, 0, 0);
   }

   public void rotate(int id) {
      this.send(3, id, 0, 0);
   }

   public void merge(int id) {
      int hover = this.enchanter.matrix.matrix[this.gridHoverX][this.gridHoverY];
      EnchantmentMatrix.Piece p = this.getPiece(hover);
      EnchantmentMatrix.Piece p1 = this.getPiece(this.selectedPiece);
      if (p != null && p1 != null && p.enchant == p1.enchant && p.level < p.enchant.getMaxLevel()) {
         this.send(4, hover, id, 0);
         this.selectedPiece = -1;
         this.click();
      }

   }

   private void send(int operation, int arg0, int arg1, int arg2) {
      MessageMatrixEnchanterOperation message = new MessageMatrixEnchanterOperation(operation, arg0, arg1, arg2);
      NetworkHandler.INSTANCE.sendToServer(message);
   }

   private void click() {
      this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
   }

   private void updateButtonStatus() {
      this.plusButton.enabled = this.enchanter.matrix != null && this.enchanter.charge > 0 && this.enchanter.matrix.validateXp(this.mc.player, this.enchanter.bookshelfPower) && this.enchanter.matrix.canGeneratePiece(this.enchanter.bookshelfPower, this.enchanter.enchantability);
   }

   private EnchantmentMatrix.Piece getPiece(int id) {
      EnchantmentMatrix matrix = this.enchanter.matrix;
      return matrix != null ? (EnchantmentMatrix.Piece)matrix.pieces.get(Integer.valueOf(id)) : null;
   }

   public static class PieceList extends GuiScrollingList {
      private final GuiMatrixEnchanting parent;
      private int mouseX;
      private int mouseY;

      public PieceList(GuiMatrixEnchanting parent, int width, int height, int top, int left, int entryHeight) {
         super(parent.mc, width, height, top, top + height, left, entryHeight, parent.width, parent.height);
         this.parent = parent;
      }

      protected int getSize() {
         return this.parent.listPieces == null ? 0 : this.parent.listPieces.size();
      }

      protected void elementClicked(int index, boolean doubleClick) {
         int id = ((Integer)this.parent.listPieces.get(index)).intValue();
         if (this.parent.selectedPiece == id) {
            this.parent.selectedPiece = -1;
         } else {
            this.parent.selectedPiece = id;
         }

      }

      protected boolean isSelected(int index) {
         int id = ((Integer)this.parent.listPieces.get(index)).intValue();
         return this.parent.selectedPiece == id;
      }

      protected void drawBackground() {
      }

      public void handleMouseInput(int mouseX, int mouseY) throws IOException {
         super.handleMouseInput(mouseX, mouseY);
         this.mouseX = mouseX;
         this.mouseY = mouseY;
      }

      protected void drawSlot(int slotIdx, int entryRight, int slotTop, int slotBuffer, Tessellator tess) {
         int id = ((Integer)this.parent.listPieces.get(slotIdx)).intValue();
         EnchantmentMatrix.Piece piece = this.parent.getPiece(id);
         if (piece != null) {
            if (this.mouseX >= this.left && this.mouseX < this.left + this.listWidth - 7 && this.mouseY >= slotTop && this.mouseY <= slotTop + this.slotHeight && this.mouseY < this.bottom) {
               this.parent.hoveredPiece = piece;
            }

            this.parent.mc.getTextureManager().bindTexture(GuiMatrixEnchanting.BACKGROUND);
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)this.left + (float)(this.listWidth - 7) / 2.0F, (float)slotTop + (float)this.slotHeight / 2.0F, 0.0F);
            GlStateManager.scale(0.5D, 0.5D, 0.5D);
            GlStateManager.translate(-4.0F, -8.0F, 0.0F);
            this.parent.renderPiece(piece, 1.0F);
            GlStateManager.popMatrix();
         }

      }
   }
}
