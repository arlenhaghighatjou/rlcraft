package mezz.jei.gui.overlay;

import com.arlenh7.eaglercraft.awt.Rectangle;
import java.util.Collection;
import javax.annotation.Nullable;
import mezz.jei.Internal;
import mezz.jei.JustEnoughItems;
import mezz.jei.config.Config;
import mezz.jei.gui.TooltipRenderer;
import mezz.jei.gui.ingredients.GuiItemStackGroup;
import mezz.jei.gui.ingredients.IIngredientListElement;
import mezz.jei.input.ClickedIngredient;
import mezz.jei.input.IClickedIngredient;
import mezz.jei.input.IShowsRecipeFocuses;
import mezz.jei.input.MouseHelper;
import mezz.jei.network.packets.PacketDeletePlayerItem;
import mezz.jei.network.packets.PacketJei;
import mezz.jei.render.IngredientListBatchRenderer;
import mezz.jei.render.IngredientListSlot;
import mezz.jei.render.IngredientRenderer;
import mezz.jei.runtime.JeiRuntime;
import mezz.jei.util.GiveMode;
import mezz.jei.util.MathUtil;
import mezz.jei.util.Translator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

public class IngredientGrid implements IShowsRecipeFocuses {
   private static final int INGREDIENT_PADDING = 1;
   public static final int INGREDIENT_WIDTH = GuiItemStackGroup.getWidth(1);
   public static final int INGREDIENT_HEIGHT = GuiItemStackGroup.getHeight(1);
   private final GridAlignment alignment;
   private Rectangle area = new Rectangle();
   protected final IngredientListBatchRenderer guiIngredientSlots;

   public IngredientGrid(GridAlignment alignment) {
      this.alignment = alignment;
      this.guiIngredientSlots = new IngredientListBatchRenderer();
   }

   public int size() {
      return this.guiIngredientSlots.size();
   }

   public boolean updateBounds(Rectangle availableArea, int minWidth, Collection<Rectangle> exclusionAreas) {
      int columns = Math.min(availableArea.width / INGREDIENT_WIDTH, Config.getMaxColumns());
      int rows = availableArea.height / INGREDIENT_HEIGHT;
      int ingredientsWidth = columns * INGREDIENT_WIDTH;
      int width = Math.max(ingredientsWidth, minWidth);
      int height = rows * INGREDIENT_HEIGHT;
      int x;
      if (this.alignment == GridAlignment.LEFT) {
         x = availableArea.x + (availableArea.width - width);
      } else {
         x = availableArea.x;
      }

      int y = availableArea.y + (availableArea.height - height) / 2;
      int xOffset = x + Math.max(0, (width - ingredientsWidth) / 2);
      this.area = new Rectangle(x, y, width, height);
      this.guiIngredientSlots.clear();
      if (rows != 0 && columns >= 4) {
         for(int row = 0; row < rows; ++row) {
            int y1 = y + row * INGREDIENT_HEIGHT;

            for(int column = 0; column < columns; ++column) {
               int x1 = xOffset + column * INGREDIENT_WIDTH;
               IngredientListSlot ingredientListSlot = new IngredientListSlot(x1, y1, 1);
               Rectangle stackArea = ingredientListSlot.getArea();
               boolean blocked = MathUtil.intersects(exclusionAreas, stackArea);
               ingredientListSlot.setBlocked(blocked);
               this.guiIngredientSlots.add(ingredientListSlot);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public Rectangle getArea() {
      return this.area;
   }

   public void draw(Minecraft minecraft, int mouseX, int mouseY) {
      GlStateManager.disableBlend();
      this.guiIngredientSlots.render(minecraft);
      if (!this.shouldDeleteItemOnClick(minecraft, mouseX, mouseY) && this.isMouseOver(mouseX, mouseY)) {
         IngredientRenderer hovered = this.guiIngredientSlots.getHovered(mouseX, mouseY);
         if (hovered != null) {
            hovered.drawHighlight();
         }
      }

      GlStateManager.enableAlpha();
   }

   public void drawTooltips(Minecraft minecraft, int mouseX, int mouseY) {
      if (this.isMouseOver(mouseX, mouseY)) {
         if (this.shouldDeleteItemOnClick(minecraft, mouseX, mouseY)) {
            String deleteItem = Translator.translateToLocal("jei.tooltip.delete.item");
            TooltipRenderer.drawHoveringText(minecraft, deleteItem, mouseX, mouseY);
         } else {
            IngredientRenderer hovered = this.guiIngredientSlots.getHovered(mouseX, mouseY);
            if (hovered != null) {
               hovered.drawTooltip(minecraft, mouseX, mouseY);
            }
         }
      }

   }

   private boolean shouldDeleteItemOnClick(Minecraft minecraft, int mouseX, int mouseY) {
      if (Config.isDeleteItemsInCheatModeActive()) {
         EntityPlayer player = minecraft.player;
         if (player != null) {
            ItemStack itemStack = player.inventory.getItemStack();
            if (!itemStack.isEmpty()) {
               JeiRuntime runtime = Internal.getRuntime();
               if (runtime == null || !runtime.getRecipesGui().isOpen()) {
                  GiveMode giveMode = Config.getGiveMode();
                  if (giveMode == GiveMode.MOUSE_PICKUP) {
                     IClickedIngredient<?> ingredientUnderMouse = this.getIngredientUnderMouse(mouseX, mouseY);
                     if (ingredientUnderMouse != null && ingredientUnderMouse.getValue() instanceof ItemStack) {
                        ItemStack value = (ItemStack)ingredientUnderMouse.getValue();
                        if (ItemHandlerHelper.canItemStacksStack(itemStack, value)) {
                           return false;
                        }
                     }
                  }

                  return true;
               }
            }
         }
      }

      return false;
   }

   public boolean isMouseOver(int mouseX, int mouseY) {
      return this.area.contains(mouseX, mouseY);
   }

   public boolean handleMouseClicked(int mouseX, int mouseY) {
      if (this.isMouseOver(mouseX, mouseY)) {
         Minecraft minecraft = Minecraft.getMinecraft();
         if (this.shouldDeleteItemOnClick(minecraft, mouseX, mouseY)) {
            EntityPlayerSP player = minecraft.player;
            if (player != null) {
               ItemStack itemStack = player.inventory.getItemStack();
               if (!itemStack.isEmpty()) {
                  player.inventory.setItemStack(ItemStack.EMPTY);
                  PacketJei packet = new PacketDeletePlayerItem(itemStack);
                  JustEnoughItems.getProxy().sendPacketToServer(packet);
                  return true;
               }
            }
         }
      }

      return false;
   }

   @Nullable
   public IIngredientListElement getElementUnderMouse() {
      IngredientRenderer hovered = this.guiIngredientSlots.getHovered(MouseHelper.getX(), MouseHelper.getY());
      return hovered != null ? hovered.getElement() : null;
   }

   @Nullable
   public IClickedIngredient<?> getIngredientUnderMouse(int mouseX, int mouseY) {
      if (this.isMouseOver(mouseX, mouseY)) {
         ClickedIngredient<?> clicked = this.guiIngredientSlots.getIngredientUnderMouse(mouseX, mouseY);
         if (clicked != null) {
            clicked.setAllowsCheating();
         }

         return clicked;
      } else {
         return null;
      }
   }

   public boolean canSetFocusWithMouse() {
      return true;
   }
}
