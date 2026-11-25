package ivorius.reccomplex.gui.table;

import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TIntObjectProcedure;
import ivorius.ivtoolkit.math.IvMathHelper;
import ivorius.reccomplex.gui.table.cell.TableCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSource;
import ivorius.reccomplex.utils.RCStrings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import net.lax1dude.eaglercraft.v1_8.Mouse;

@SideOnly(Side.CLIENT)
public class GuiTable extends Gui {
   private static final ResourceLocation CREATIVE_INVENTORY_TABS = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
   public static final int TITLE_HEIGHT = 25;
   public static final int HEIGHT_PER_SLOT = 22;
   public static final int SCROLL_BAR_WIDTH = 19;
   public static final int SCROLL_BAR_MARGIN = 4;
   public static final float SCROLL_SPEED = 0.005F;
   private TableDelegate delegate;
   private TableDataSource dataSource;
   private Bounds bounds;
   private Bounds tableBounds;
   private boolean allowsNegativeScroll = false;
   private boolean startCentered = true;
   private boolean firstTime = true;
   private boolean showsScrollBar;
   private boolean dragsScrollBar;
   private float currentScroll;
   private boolean hideScrollbarIfUnnecessary;
   private final TIntObjectHashMap<TableCell> cachedCells = new TIntObjectHashMap();
   private final List<TableCell> currentCells = new ArrayList();
   private final Set<String> lockedCells = new HashSet();
   private Map<GuiButton, Pair<TableCell, Integer>> buttonMap = new HashMap();
   private GuiButton scrollUpButton;
   private GuiButton scrollDownButton;

   public GuiTable(TableDelegate delegate, TableDataSource dataSource) {
      this.delegate = delegate;
      this.dataSource = dataSource;
   }

   public TableDelegate getDelegate() {
      return this.delegate;
   }

   public void setDelegate(TableDelegate delegate) {
      this.delegate = delegate;
   }

   public TableDataSource getDataSource() {
      return this.dataSource;
   }

   public void setDataSource(TableDataSource dataSource) {
      this.dataSource = dataSource;
   }

   public boolean hidesScrollbarIfUnnecessary() {
      return this.hideScrollbarIfUnnecessary;
   }

   public void setHideScrollbarIfUnnecessary(boolean hideScrollbarIfUnnecessary) {
      this.hideScrollbarIfUnnecessary = hideScrollbarIfUnnecessary;
   }

   public boolean isAllowsNegativeScroll() {
      return this.allowsNegativeScroll;
   }

   public void setAllowsNegativeScroll(boolean allowsNegativeScroll) {
      this.allowsNegativeScroll = allowsNegativeScroll;
   }

   public void initGui() {
      this.buttonMap.clear();

      for(TableCell cell : this.currentCells) {
         cell.setHidden(true);
      }

      this.currentCells.clear();
      int numberOfCells = this.dataSource.numberOfCells();
      int supportedSlotNumber = this.tableBounds.getHeight() / 22;
      if (this.firstTime) {
         if (supportedSlotNumber > numberOfCells && this.allowsNegativeScroll && this.startCentered) {
            this.currentScroll = (this.getMinScroll() + this.getMaxScroll()) / 2.0F;
         }

         this.firstTime = false;
      } else {
         this.updateScrollUpwards(0.0F);
      }

      int roundedScrollIndex = MathHelper.floor(this.currentScroll + 0.5F);
      this.scrollUpButton = new GuiButton(-1, this.tableBounds.getMaxX() + 4, this.tableBounds.getMinY(), 15, 20, TextFormatting.BOLD + "↑");
      this.delegate.addButtonToTable(this.scrollUpButton);
      this.scrollDownButton = new GuiButton(-1, this.tableBounds.getMaxX() + 4, this.tableBounds.getMaxY() - 20, 15, 20, TextFormatting.BOLD + "↓");
      this.delegate.addButtonToTable(this.scrollDownButton);
      boolean needsUpScroll = this.canScrollUp(numberOfCells);
      boolean needsDownScroll = this.canScrollDown(numberOfCells);
      this.scrollUpButton.enabled = needsUpScroll;
      this.scrollDownButton.enabled = needsDownScroll;
      this.showsScrollBar = needsUpScroll || needsDownScroll || !this.hideScrollbarIfUnnecessary;
      this.scrollUpButton.visible = this.showsScrollBar;
      this.scrollDownButton.visible = this.showsScrollBar;
      int baseY = this.tableBounds.getMinY() + (this.tableBounds.getHeight() - (!this.allowsNegativeScroll && this.startCentered ? Math.min(numberOfCells, supportedSlotNumber) : supportedSlotNumber) * 22) / 2;

      for(int index = 0; index < supportedSlotNumber && roundedScrollIndex + index < numberOfCells; ++index) {
         int cellIndex = roundedScrollIndex + index;
         if (cellIndex >= 0) {
            TableCell cell = (TableCell)this.cachedCells.get(cellIndex);
            boolean initCell = cell == null;
            if (initCell) {
               cell = this.dataSource.cellForIndex(this, cellIndex);
            }

            if (cell == null) {
               throw new NullPointerException("Cell not initialized: at " + cellIndex);
            }

            int cellY = index * 22 + 1;
            cell.setBounds(Bounds.fromAxes(this.tableBounds.getMinX(), this.tableBounds.getWidth(), baseY + cellY, 20));
            cell.setHidden(false);
            cell.initGui(this);
            if (initCell) {
               this.cachedCells.put(cellIndex, cell);
            }

            this.currentCells.add(cell);
         }
      }

   }

   public void drawScreen(GuiScreen screen, int mouseX, int mouseY, float partialTicks) {
      GlStateManager.color(1.0F, 1.0F, 1.0F);
      GlStateManager.scale(2.0F, 2.0F, 2.0F);
      this.drawString(Minecraft.getMinecraft().fontRenderer, RCStrings.abbreviateFormatted(this.dataSource.title(), 35), this.bounds.getMinX() / 2, this.bounds.getMinY() / 2 + 2, -1);
      GlStateManager.scale(0.5F, 0.5F, 0.5F);
      this.currentCells.stream().filter((cell) -> {
         return !cell.isHidden();
      }).forEach((cell) -> {
         cell.draw(this, mouseX, mouseY, partialTicks);
      });
      this.currentCells.stream().filter((cell) -> {
         return !cell.isHidden();
      }).forEach((cell) -> {
         cell.drawFloating(this, mouseX, mouseY, partialTicks);
      });
      if (this.showsScrollBar && this.getMaxScroll() != this.getMinScroll()) {
         screen.mc.getTextureManager().bindTexture(CREATIVE_INVENTORY_TABS);
         GlStateManager.color(1.0F, 1.0F, 1.0F);
         this.drawTexturedModalRect((float)(this.tableBounds.getMaxX() + 4 + 1), (float)(this.tableBounds.getMinY() + 20) + (this.currentScroll - this.getMinScroll()) / (this.getMaxScroll() - this.getMinScroll()) * (float)(this.tableBounds.getHeight() - 40 - 15), 232, 0, 12, 15);
      }

   }

   public void updateScreen() {
      this.currentCells.forEach((cell) -> {
         cell.update(this);
      });
   }

   public void actionPerformed(GuiButton button) {
      if (button == this.scrollDownButton) {
         this.tryScrollDown();
      } else if (button == this.scrollUpButton) {
         this.tryScrollUp();
      } else {
         Pair<TableCell, Integer> propertyPair = (Pair)this.buttonMap.get(button);
         if (propertyPair != null) {
            ((TableCell)propertyPair.getLeft()).buttonClicked(((Integer)propertyPair.getRight()).intValue());
         }
      }

   }

   public void handleMouseInput() {
      int i = Mouse.getEventDWheel();
      if (i != 0) {
         this.tryScrollUp((float)i * 0.005F);
      }

   }

   public boolean keyTyped(char keyChar, int keyCode) {
      for(TableCell cell : this.currentCells) {
         if (cell.keyTyped(keyChar, keyCode)) {
            return true;
         }
      }

      return false;
   }

   public void mouseReleased(int x, int y, int button) {
      this.dragsScrollBar = false;
   }

   public void mouseClicked(int x, int y, int button) {
      for(TableCell cell : this.currentCells) {
         cell.mouseClicked(button, x, y);
      }

      if (this.scrollBarBounds().contains(x, y)) {
         this.dragsScrollBar = true;
         this.dragScrollBar(x, y);
      }

   }

   public void mouseClickMove(int x, int y, int button, long timeSinceLastClick) {
      if (this.dragsScrollBar) {
         this.dragScrollBar(x, y);
      }

   }

   public void dragScrollBar(int x, int y) {
      Bounds scrollBarBounds = this.scrollBarBounds();
      float scrollHeight = (float)(y - scrollBarBounds.getMinY()) / (float)scrollBarBounds.getHeight();
      this.setScroll(this.getMinScroll() + scrollHeight * (this.getMaxScroll() - this.getMinScroll()));
      this.delegate.redrawTable();
   }

   public void addButton(TableCell property, int id, GuiButton button) {
      this.delegate.addButtonToTable(button);
      this.buttonMap.put(button, new ImmutablePair(property, id));
   }

   public Bounds getTableBounds() {
      return this.tableBounds;
   }

   public Bounds getBounds() {
      return this.bounds;
   }

   public void setBounds(Bounds bounds) {
      this.bounds = bounds;
      this.tableBounds = Bounds.fromSize(bounds.getMinX(), bounds.getMinY() + 25, bounds.getWidth() - 19, bounds.getHeight() - 25);
   }

   public void tryScrollUp() {
      this.tryScrollUp(1.0F);
   }

   public void tryScrollDown() {
      this.tryScrollUp(-1.0F);
   }

   public float getMinScroll() {
      return this.getMinScroll(this.dataSource.numberOfCells());
   }

   public float getMinScroll(int numberOfCells) {
      if (!this.hideScrollbarIfUnnecessary && this.allowsNegativeScroll) {
         int supportedSlots = this.tableBounds.getHeight() / 22;
         return (float)Math.min(0, numberOfCells - supportedSlots);
      } else {
         return 0.0F;
      }
   }

   public float getMaxScroll() {
      return this.getMaxScroll(this.dataSource.numberOfCells());
   }

   protected float getMaxScroll(int numberOfCells) {
      int supportedSlots = this.tableBounds.getHeight() / 22;
      return (float)Math.max(0, numberOfCells - supportedSlots);
   }

   public void tryScrollUp(float dist) {
      this.updateScrollUpwards(dist);
      this.delegate.redrawTable();
   }

   protected void updateScrollUpwards(float dist) {
      this.setScroll(this.currentScroll - dist);
   }

   private void setScroll(float value) {
      this.currentScroll = IvMathHelper.clamp(this.getMinScroll(), value, this.getMaxScroll());
   }

   public boolean canScrollUp(int numberOfCells) {
      return this.currentScroll > this.getMinScroll(numberOfCells);
   }

   public boolean canScrollDown() {
      return this.canScrollDown(this.dataSource.numberOfCells());
   }

   protected boolean canScrollDown(int numberOfCells) {
      return this.currentScroll < this.getMaxScroll(numberOfCells);
   }

   public Bounds scrollBarBounds() {
      return Bounds.fromSize(this.tableBounds.getMaxX() + 4, this.tableBounds.getMinY() + 20, 15, this.tableBounds.getHeight() - 40);
   }

   public void clearCellCache() {
      this.cachedCells.retainEntries((key, cell) -> {
         return this.lockedCells.contains(cell.getID());
      });
   }

   public void setLocked(String cell, boolean lock) {
      if (lock) {
         this.lockedCells.add(cell);
      } else {
         this.lockedCells.remove(cell);
      }

   }

   public void drawTooltipRect(List<String> lines, Bounds bounds, int mouseX, int mouseY, FontRenderer font) {
      if (bounds.contains(mouseX, mouseY)) {
         this.drawTooltip(lines, mouseX, mouseY, font);
      }

   }

   public void drawTooltip(List<String> lines, int x, int y, FontRenderer font) {
      if (!lines.isEmpty()) {
         GlStateManager.disableDepth();
         int k = 0;

         for(String s : lines) {
            int l = font.getStringWidth(s);
            if (l > k) {
               k = l;
            }
         }

         int j2 = x + 12;
         int k2 = y - 12;
         int i1 = 8;
         if (lines.size() > 1) {
            i1 += 2 + (lines.size() - 1) * 10;
         }

         if (j2 + k > this.bounds.getWidth()) {
            j2 -= 28 + k;
         }

         if (k2 + i1 + 6 > this.bounds.getHeight()) {
            k2 = this.bounds.getHeight() - i1 - 6;
         }

         this.zLevel = 300.0F;
         int j1 = -267386864;
         this.drawGradientRect(j2 - 3, k2 - 4, j2 + k + 3, k2 - 3, j1, j1);
         this.drawGradientRect(j2 - 3, k2 + i1 + 3, j2 + k + 3, k2 + i1 + 4, j1, j1);
         this.drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 + i1 + 3, j1, j1);
         this.drawGradientRect(j2 - 4, k2 - 3, j2 - 3, k2 + i1 + 3, j1, j1);
         this.drawGradientRect(j2 + k + 3, k2 - 3, j2 + k + 4, k2 + i1 + 3, j1, j1);
         int k1 = 1347420415;
         int l1 = (k1 & 16711422) >> 1 | k1 & -16777216;
         this.drawGradientRect(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k1, l1);
         this.drawGradientRect(j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k1, l1);
         this.drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k1, k1);
         this.drawGradientRect(j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l1, l1);

         for(int i2 = 0; i2 < lines.size(); ++i2) {
            String s1 = (String)lines.get(i2);
            font.drawStringWithShadow(s1, (float)j2, (float)k2, -1);
            if (i2 == 0) {
               k2 += 2;
            }

            k2 += 10;
         }

         this.zLevel = 0.0F;
      }

   }
}
