package dev.satyrn.wolfarmor.client.gui;

import dev.satyrn.wolfarmor.WolfArmorMod;
import dev.satyrn.wolfarmor.api.entity.passive.IArmoredWolf;
import dev.satyrn.wolfarmor.client.renderer.WolfRenderHelper;
import dev.satyrn.wolfarmor.common.inventory.ContainerWolfInventory;
import dev.satyrn.wolfarmor.common.network.packets.WolfDropChestMessage;
import dev.satyrn.wolfarmor.config.WolfArmorConfig;
import dev.satyrn.wolfarmor.util.WolfFoodStatsLevel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.Point;
import org.lwjgl.util.Rectangle;

@SideOnly(Side.CLIENT)
public class GuiWolfInventory extends GuiContainer {
   private final IInventory wolfInventory;
   private final IInventory playerInventory;
   private final EntityWolf wolf;
   private final IArmoredWolf armoredWolf;
   private final WolfArmorConfig config = WolfArmorMod.getConfig();
   private float lastMouseX;
   private float lastMouseY;
   private static final int Y_SIZE_INITIAL = 169;
   private static final Rectangle EMPTY_SLOT = new Rectangle(176, 72, 18, 18);
   private static final Rectangle BUTTON_AREA = new Rectangle(7, 35, 18, 18);
   private static final Rectangle BUTTON_TEXTURE = new Rectangle(194, 72, 18, 18);
   private static final Rectangle EMPTY_CHEST_BG = new Rectangle(176, 0, 18, 18);
   private static final Rectangle STATS_BG_TEXTURE = new Rectangle(0, 169, 176, 10);
   private static final Rectangle UI_BOTTOM_TEXTURE = new Rectangle(0, 165, 176, 4);
   private static final Rectangle WOLF_RENDER_AREA = new Rectangle(31, 28, 54, 54);
   private static final Rectangle INVENTORY_TEXTURE = new Rectangle(0, 0, 176, 169);
   private static final ResourceLocation TEXTURE_GUI_WOLF_INVENTORY = new ResourceLocation("wolfarmor", "textures/gui/wolf.png");

   public GuiWolfInventory(@Nonnull IInventory playerInventory, @Nonnull IInventory wolfInventory, @Nonnull EntityWolf wolf, @Nonnull EntityPlayer player) {
      super(new ContainerWolfInventory(playerInventory, wolfInventory, wolf, player));
      this.armoredWolf = (IArmoredWolf)wolf;
      this.wolfInventory = wolfInventory;
      this.playerInventory = playerInventory;
      this.wolf = wolf;
      this.allowUserInput = false;
      this.ySize = 169;
      if (this.config.getStatsInGui()) {
         this.ySize += 4;
         if (this.config.getFoodStatsLevel() != WolfFoodStatsLevel.DISABLED) {
            this.ySize += 10;
         }
      }

   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      if (this.isChestButtonHovered(mouseX, mouseY)) {
         WolfArmorMod.getNetworkChannel().sendToServer(new WolfDropChestMessage(this.wolf.getEntityId()));
         this.mc.player.closeScreen();
      }

   }

   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      String wolfName = this.wolf.getName();
      this.fontRenderer.drawString(this.wolfInventory.hasCustomName() ? this.wolfInventory.getName() : I18n.format(this.wolfInventory.getName(), new Object[]{wolfName}), 8, 6, 4210752);
      this.fontRenderer.drawString(this.playerInventory.hasCustomName() ? this.playerInventory.getName() : I18n.format(this.playerInventory.getName(), new Object[0]), 8, 73, 4210752);
      this.drawWolfHealthAndArmor();
      super.drawGuiContainerForegroundLayer(mouseX, mouseY);
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.lastMouseX = (float)mouseX;
      this.lastMouseY = (float)mouseY;
      super.drawScreen(mouseX, mouseY, partialTicks);
      this.renderHoveredToolTip(mouseX, mouseY);
   }

   protected void renderHoveredToolTip(int mouseX, int mouseY) {
      super.renderHoveredToolTip(mouseX, mouseY);
      if (this.armoredWolf.getHasChest() && this.isChestButtonHovered(mouseX, mouseY)) {
         List<String> displayText = new ArrayList();
         displayText.add(I18n.format("gui.wolfarmor.inventory.remove_chest", new Object[0]));
         this.drawHoveringText(displayText, mouseX, mouseY, this.fontRenderer);
      }

   }

   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      Point origin = this.getOrigin();
      GlStateManager.pushMatrix();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(TEXTURE_GUI_WOLF_INVENTORY);
      this.drawTexturedModalRect(origin.getX(), origin.getY(), INVENTORY_TEXTURE.getX(), INVENTORY_TEXTURE.getY(), INVENTORY_TEXTURE.getWidth(), INVENTORY_TEXTURE.getHeight());
      if (this.armoredWolf.getHasArmor()) {
         this.drawTexturedModalRect(origin.getX() + 7, origin.getY() + 17, EMPTY_SLOT.getX(), EMPTY_SLOT.getY(), EMPTY_SLOT.getWidth(), EMPTY_SLOT.getHeight());
      }

      int columns = WolfArmorMod.getConfig().getChestSize().getColumns();
      int rows = WolfArmorMod.getConfig().getChestSize().getRows();
      Point chestOrigin = new Point((5 - columns) * 9 + 79, (3 - rows) * 9 + 17);
      if (this.armoredWolf.getHasChest()) {
         for(int xOffset = 0; xOffset < columns; ++xOffset) {
            for(int yOffset = 0; yOffset < rows; ++yOffset) {
               this.drawTexturedModalRect(origin.getX() + chestOrigin.getX() + EMPTY_SLOT.getWidth() * xOffset, origin.getY() + chestOrigin.getY() + EMPTY_SLOT.getHeight() * yOffset, EMPTY_SLOT.getX(), EMPTY_SLOT.getY(), EMPTY_SLOT.getWidth(), EMPTY_SLOT.getHeight());
            }
         }
      } else {
         for(int xSlot = 0; xSlot < columns; ++xSlot) {
            for(int ySlot = 0; ySlot < rows; ++ySlot) {
               int textureXOffset = (columns > 1 ? 1 : 0) + (xSlot > 0 ? (xSlot == columns - 1 ? 2 : 1) : 0);
               int textureYOffset = (rows > 1 ? 1 : 0) + (ySlot > 0 ? (ySlot == rows - 1 ? 2 : 1) : 0);
               this.drawTexturedModalRect(origin.getX() + chestOrigin.getX() + EMPTY_CHEST_BG.getWidth() * xSlot, origin.getY() + chestOrigin.getY() + EMPTY_CHEST_BG.getHeight() * ySlot, textureXOffset * EMPTY_CHEST_BG.getWidth() + EMPTY_CHEST_BG.getX(), textureYOffset * EMPTY_CHEST_BG.getHeight() + EMPTY_CHEST_BG.getY(), EMPTY_CHEST_BG.getWidth(), EMPTY_CHEST_BG.getHeight());
            }
         }
      }

      if (this.config.getStatsInGui()) {
         int yOffset = this.getOrigin().getY() + 159;
         this.drawTexturedModalRect(origin.getX(), yOffset, STATS_BG_TEXTURE.getX(), STATS_BG_TEXTURE.getY(), STATS_BG_TEXTURE.getWidth(), STATS_BG_TEXTURE.getHeight());
         yOffset = yOffset + STATS_BG_TEXTURE.getHeight();
         if (this.config.getFoodStatsLevel() != WolfFoodStatsLevel.DISABLED && this.armoredWolf.getHasArmor()) {
            this.drawTexturedModalRect(origin.getX(), yOffset, STATS_BG_TEXTURE.getX(), STATS_BG_TEXTURE.getY(), STATS_BG_TEXTURE.getWidth(), STATS_BG_TEXTURE.getHeight());
            yOffset += STATS_BG_TEXTURE.getHeight();
         }

         this.drawTexturedModalRect(origin.getX(), yOffset, UI_BOTTOM_TEXTURE.getX(), UI_BOTTOM_TEXTURE.getY(), UI_BOTTOM_TEXTURE.getWidth(), UI_BOTTOM_TEXTURE.getHeight());
      }

      float entityPosX = (float)(this.guiLeft + 52);
      float entityPosY = (float)(this.guiTop + 62);
      float entityMouseX = entityPosX - this.lastMouseX;
      float entityMouseY = entityPosY - 16.0F - this.lastMouseY;
      GuiInventory.drawEntityOnScreen(MathHelper.fastFloor((double)entityPosX), MathHelper.fastFloor((double)entityPosY), 30, entityMouseX, entityMouseY, this.wolf);
      GlStateManager.popMatrix();
      if (this.armoredWolf.getHasChest()) {
         GlStateManager.pushMatrix();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.mc.getTextureManager().bindTexture(TEXTURE_GUI_WOLF_INVENTORY);
         this.drawTexturedModalRect(origin.getX() + BUTTON_AREA.getX(), origin.getY() + BUTTON_AREA.getY(), BUTTON_TEXTURE.getX() + (this.isChestButtonHovered(mouseX, mouseY) ? BUTTON_TEXTURE.getWidth() : 0), BUTTON_TEXTURE.getY(), BUTTON_TEXTURE.getWidth(), BUTTON_TEXTURE.getHeight());
         GlStateManager.popMatrix();
      }

   }

   private void drawWolfHealthAndArmor() {
      if (this.config.getStatsInGui()) {
         Point origin = this.getOrigin();
         GlStateManager.pushMatrix();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         int yPosition = 160;
         if (this.config.getFoodStatsLevel() != WolfFoodStatsLevel.DISABLED && this.armoredWolf.getHasArmor()) {
            Point renderPos = WolfRenderHelper.renderArmor(this.wolf, new Point(5, yPosition), false);
            yPosition = renderPos.getY();
         }

         Point renderPos = WolfRenderHelper.renderHealth(this.wolf, new Point(5, yPosition), false);
         if (this.config.getFoodStatsLevel() != WolfFoodStatsLevel.DISABLED) {
            WolfRenderHelper.renderHunger(this.wolf, new Point(this.xSize - 95, yPosition), true);
         } else if (this.armoredWolf.getHasArmor()) {
            WolfRenderHelper.renderArmor(this.wolf, new Point(this.xSize - 95, yPosition), true);
         }

         GlStateManager.popMatrix();
      }
   }

   private Point getOrigin() {
      return new Point((this.width - this.xSize) / 2, (this.height - this.ySize) / 2);
   }

   private Point getLocalFromScreen(int x, int y) {
      Point origin = this.getOrigin();
      int mouseLocX = x - origin.getX();
      int mouseLocY = y - origin.getY();
      return new Point(mouseLocX, mouseLocY);
   }

   private boolean isChestButtonHovered(int mouseX, int mouseY) {
      Point mouseLoc = this.getLocalFromScreen(mouseX, mouseY);
      return mouseLoc.getX() >= BUTTON_AREA.getX() && mouseLoc.getY() >= BUTTON_AREA.getY() && mouseLoc.getX() < BUTTON_AREA.getX() + BUTTON_AREA.getWidth() && mouseLoc.getY() < BUTTON_AREA.getY() + BUTTON_AREA.getHeight();
   }
}
