package betterquesting.client.ui_builder;

import betterquesting.api.client.gui.misc.IVolatileScreen;
import betterquesting.api.misc.ICallback;
import betterquesting.api.storage.BQ_Settings;
import betterquesting.api2.client.gui.GuiScreenCanvas;
import betterquesting.api2.client.gui.controls.PanelButton;
import betterquesting.api2.client.gui.controls.PanelButtonStorage;
import betterquesting.api2.client.gui.misc.GuiAlign;
import betterquesting.api2.client.gui.misc.GuiPadding;
import betterquesting.api2.client.gui.misc.GuiRectangle;
import betterquesting.api2.client.gui.misc.GuiTransform;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.CanvasEmpty;
import betterquesting.api2.client.gui.panels.CanvasTextured;
import betterquesting.api2.client.gui.panels.IGuiCanvas;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.client.gui.panels.bars.PanelVScrollBar;
import betterquesting.api2.client.gui.panels.lists.CanvasScrolling;
import betterquesting.api2.client.gui.resources.colors.GuiColorStatic;
import betterquesting.api2.client.gui.resources.colors.IGuiColor;
import betterquesting.api2.client.gui.resources.lines.BoxLine;
import betterquesting.api2.client.gui.themes.presets.PresetIcon;
import betterquesting.api2.client.gui.themes.presets.PresetTexture;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.storage.SimpleDatabase;
import betterquesting.client.gui2.editors.nbt.PanelScrollingNBT;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Queue;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.GuiScreen;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.lax1dude.eaglercraft.v1_8.Keyboard;
import net.lax1dude.eaglercraft.v1_8.Mouse;
import org.lwjgl.opengl.GL11;
import net.lax1dude.eaglercraft.v1_8.vector.Vector4f;

public class GuiBuilderMain extends GuiScreenCanvas implements IVolatileScreen {
   private final SimpleDatabase<ComponentPanel> COM_DB = new SimpleDatabase<ComponentPanel>();
   private final SimpleDatabase<IGuiPanel> PANEL_DB = new SimpleDatabase<IGuiPanel>();
   private CanvasEmpty cvPreview;
   private IGuiCanvas cvPropTray;
   private PanelButton btnTrayToggle;
   private ResourceLocation paletteSel = null;
   private int toolMode = 0;
   private int selectedID = -1;
   private IGuiPanel selPn = null;
   private int dragID = -1;
   private int dragType = -1;
   private final GuiTransform refBounds = new GuiTransform();
   private BoxLine boxLine = new BoxLine();
   private IGuiColor parCol = new GuiColorStatic(-65536);
   private IGuiColor ancCol = new GuiColorStatic(-256);
   private IGuiColor boundsCol = new GuiColorStatic(-16776961);
   private static final GuiTransform[] PRE_TF_SEG = new GuiTransform[]{new GuiTransform(new Vector4f(0.0F, 0.0F, 0.5F, 0.5F), new GuiPadding(0, 0, 0, 0), 0), new GuiTransform(new Vector4f(0.0F, 0.0F, 1.0F, 0.5F), new GuiPadding(0, 0, 0, 0), 0), new GuiTransform(new Vector4f(0.5F, 0.0F, 1.0F, 0.5F), new GuiPadding(0, 0, 0, 0), 0), new GuiTransform(new Vector4f(0.0F, 0.0F, 0.5F, 1.0F), new GuiPadding(0, 0, 0, 0), 0), new GuiTransform(new Vector4f(0.0F, 0.0F, 1.0F, 1.0F), new GuiPadding(0, 0, 0, 0), 0), new GuiTransform(new Vector4f(0.5F, 0.0F, 1.0F, 1.0F), new GuiPadding(0, 0, 0, 0), 0), new GuiTransform(new Vector4f(0.0F, 0.5F, 0.5F, 1.0F), new GuiPadding(0, 0, 0, 0), 0), new GuiTransform(new Vector4f(0.0F, 0.5F, 1.0F, 1.0F), new GuiPadding(0, 0, 0, 0), 0), new GuiTransform(new Vector4f(0.5F, 0.5F, 1.0F, 1.0F), new GuiPadding(0, 0, 0, 0), 0)};
   private static final Map<Integer, GuiTransform> PRE_TF_EDGE;

   public GuiBuilderMain(GuiScreen parent) {
      super(parent);
      this.useMargins(false);
      this.useDefaultBG(true);
   }

   public void initPanel() {
      super.initPanel();
      int marginX = BQ_Settings.guiWidth <= 0 ? 16 : Math.max(16, (this.width - BQ_Settings.guiWidth) / 2);
      int marginY = BQ_Settings.guiHeight <= 0 ? 16 : Math.max(16, (this.height - BQ_Settings.guiHeight) / 2);
      GuiTransform pvTransform = new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(marginX, marginY, marginX, marginY), 0);
      this.cvPreview = new CanvasEmpty(pvTransform);
      this.addPanel(this.cvPreview);
      this.cvPropTray = new CanvasTextured(new GuiTransform(new Vector4f(0.5F, 0.0F, 1.0F, 1.0F), new GuiPadding(0, 32, 0, 0), 0), PresetTexture.PANEL_MAIN.getTexture());
      this.addPanel(this.cvPropTray);
      this.cvPropTray.setEnabled(false);
      this.btnTrayToggle = (new PanelButton(new GuiTransform(new Vector4f(0.5F, 1.0F, 0.5F, 1.0F), -16, -16, 16, 16, 0), -1, "")).setIcon(PresetIcon.ICON_UP.getTexture());
      this.btnTrayToggle.setClickAction((btn) -> {
         if (this.cvPropTray.isEnabled()) {
            this.closeTray();
         } else if (this.selectedID >= 0 && this.toolMode != 0 && this.toolMode != 2) {
            ComponentPanel com = this.COM_DB.getValue(this.selectedID);
            if (com != null) {
               this.openTrayNBT(com.writeToNBT(new NBTTagCompound()));
            }
         } else if (this.toolMode == 3) {
            this.openTrayPalette();
         }

      });
      this.addPanel(this.btnTrayToggle);
      PanelButton btnExit = (new PanelButton(new GuiTransform(GuiAlign.TOP_RIGHT, -16, 0, 16, 16, 0), -1, "") {
         public void onButtonClick() {
            GuiBuilderMain.this.mc.displayGuiScreen(GuiBuilderMain.this.parent);
         }
      }).setIcon(PresetIcon.ICON_CROSS.getTexture());
      this.addPanel(btnExit);
      PanelButton btnSave = (new PanelButton(new GuiRectangle(0, 0, 16, 16, 0), -1, "") {
         public void onButtonClick() {
         }
      }).setIcon(PresetIcon.ICON_TICK.getTexture());
      this.addPanel(btnSave);
      PanelButton btnLoad = (new PanelButton(new GuiRectangle(16, 0, 16, 16, 0), -1, "") {
         public void onButtonClick() {
         }
      }).setIcon(PresetIcon.ICON_FOLDER_OPEN.getTexture());
      this.addPanel(btnLoad);
      PanelButton btnRefresh = (new PanelButton(new GuiRectangle(32, 0, 16, 16, 0), -1, "") {
         public void onButtonClick() {
            GuiBuilderMain.this.refreshComponents();
         }
      }).setIcon(PresetIcon.ICON_REFRESH.getTexture());
      this.addPanel(btnRefresh);
      final List<PanelButton> toolBtns = new ArrayList();
      PanelButton btnCursor = (new PanelButton(new GuiTransform(GuiAlign.BOTTOM_LEFT, 0, -16, 16, 16, 0), -1, "") {
         public void onButtonClick() {
            toolBtns.forEach((btn) -> {
               btn.setActive(true);
            });
            this.setActive(false);
            GuiBuilderMain.this.toolMode = 0;
         }
      }).setIcon(PresetIcon.ICON_CURSOR.getTexture());
      btnCursor.setActive(this.toolMode != 0);
      this.addPanel(btnCursor);
      toolBtns.add(btnCursor);
      PanelButton btnProp = (new PanelButton(new GuiTransform(GuiAlign.BOTTOM_LEFT, 16, -16, 16, 16, 0), -1, "") {
         public void onButtonClick() {
            toolBtns.forEach((btn) -> {
               btn.setActive(true);
            });
            this.setActive(false);
            GuiBuilderMain.this.toolMode = 1;
         }
      }).setIcon(PresetIcon.ICON_PROPS.getTexture());
      btnProp.setActive(this.toolMode != 1);
      this.addPanel(btnProp);
      toolBtns.add(btnProp);
      PanelButton btnAdd = (new PanelButton(new GuiTransform(GuiAlign.BOTTOM_LEFT, 32, -16, 16, 16, 0), -1, "") {
         public void onButtonClick() {
            toolBtns.forEach((btn) -> {
               btn.setActive(true);
            });
            this.setActive(false);
            GuiBuilderMain.this.toolMode = 2;
            GuiBuilderMain.this.openTrayPalette();
         }
      }).setIcon(PresetIcon.ICON_POSITIVE.getTexture());
      btnAdd.setActive(this.toolMode != 2);
      this.addPanel(btnAdd);
      toolBtns.add(btnAdd);
      PanelButton btnSize = (new PanelButton(new GuiTransform(GuiAlign.BOTTOM_LEFT, 48, -16, 16, 16, 0), -1, "") {
         public void onButtonClick() {
            toolBtns.forEach((btn) -> {
               btn.setActive(true);
            });
            this.setActive(false);
            GuiBuilderMain.this.toolMode = 3;
         }
      }).setIcon(PresetIcon.ICON_SCALE.getTexture());
      btnSize.setActive(this.toolMode != 3);
      this.addPanel(btnSize);
      toolBtns.add(btnSize);
      PanelButton btnLink = (new PanelButton(new GuiTransform(GuiAlign.BOTTOM_LEFT, 64, -16, 16, 16, 0), -1, "") {
         public void onButtonClick() {
            toolBtns.forEach((btn) -> {
               btn.setActive(true);
            });
            this.setActive(false);
            GuiBuilderMain.this.toolMode = 4;
         }
      }).setIcon(PresetIcon.ICON_LINK.getTexture());
      btnLink.setActive(this.toolMode != 4);
      this.addPanel(btnLink);
      toolBtns.add(btnLink);
      PanelButton btnDel = (new PanelButton(new GuiTransform(GuiAlign.BOTTOM_LEFT, 80, -16, 16, 16, 0), -1, "") {
         public void onButtonClick() {
            toolBtns.forEach((btn) -> {
               btn.setActive(true);
            });
            this.setActive(false);
            GuiBuilderMain.this.toolMode = 5;
         }
      }).setIcon(PresetIcon.ICON_TRASH.getTexture());
      btnDel.setActive(this.toolMode != 5);
      this.addPanel(btnDel);
      toolBtns.add(btnDel);
      this.refreshComponents();
   }

   public void drawPanel(int mx, int my, float partialTick) {
      super.drawPanel(mx, my, partialTick);
      int cmx = MathHelper.clamp(mx, this.cvPreview.getTransform().getX(), this.cvPreview.getTransform().getX() + this.cvPreview.getTransform().getWidth());
      int cmy = MathHelper.clamp(my, this.cvPreview.getTransform().getY(), this.cvPreview.getTransform().getY() + this.cvPreview.getTransform().getHeight());
      if (this.dragID >= 0) {
         if (this.toolMode == 3 && this.selPn != null && this.dragType > 0) {
            ComponentPanel com = this.COM_DB.getValue(this.selectedID);
            if ((this.dragType & 16) == 16 && this.selPn.getTransform() instanceof GuiTransform) {
               GuiTransform trans = (GuiTransform)this.selPn.getTransform();
               int dx = cmx - trans.getParent().getX();
               int dy = cmy - trans.getParent().getY();
               int width = trans.getParent().getWidth();
               int height = trans.getParent().getHeight();
               boolean shift = Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
               if ((this.dragType & 1) == 1 && height > 0) {
                  trans.getAnchor().y = (float)dy / (float)height;
                  if (shift) {
                     Vector4f var30 = trans.getAnchor();
                     var30.y += 0.025F;
                     var30 = trans.getAnchor();
                     var30.y -= trans.getAnchor().y % 0.05F;
                  }
               }

               if ((this.dragType & 2) == 2 && height > 0) {
                  trans.getAnchor().w = (float)dy / (float)height;
                  if (shift) {
                     Vector4f var32 = trans.getAnchor();
                     var32.w += 0.025F;
                     var32 = trans.getAnchor();
                     var32.w -= trans.getAnchor().w % 0.05F;
                  }
               }

               if ((this.dragType & 4) == 4 && width > 0) {
                  trans.getAnchor().x = (float)dx / (float)width;
                  if (shift) {
                     Vector4f var34 = trans.getAnchor();
                     var34.x += 0.025F;
                     var34 = trans.getAnchor();
                     var34.x -= trans.getAnchor().x % 0.05F;
                  }
               }

               if ((this.dragType & 8) == 8 && width > 0) {
                  trans.getAnchor().z = (float)dx / (float)width;
                  if (shift) {
                     Vector4f var36 = trans.getAnchor();
                     var36.z += 0.025F;
                     var36 = trans.getAnchor();
                     var36.z -= trans.getAnchor().z % 0.05F;
                  }
               }

               if (com != null) {
                  com.setTransform(trans);
               }
            } else if ((this.dragType & 16) == 0 && this.selPn.getTransform() instanceof GuiTransform) {
               GuiTransform trans = (GuiTransform)this.selPn.getTransform();
               int width = MathHelper.ceil((float)trans.getParent().getWidth() * (trans.getAnchor().z - trans.getAnchor().x));
               int height = MathHelper.ceil((float)trans.getParent().getHeight() * (trans.getAnchor().w - trans.getAnchor().y));
               int dx = cmx - (trans.getParent().getX() + MathHelper.ceil((float)trans.getParent().getWidth() * trans.getAnchor().x));
               int dy = cmy - (trans.getParent().getY() + MathHelper.ceil((float)trans.getParent().getHeight() * trans.getAnchor().y));
               boolean shift = Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
               if ((this.dragType & 1) == 1) {
                  trans.getPadding().t = dy;
                  if (shift) {
                     GuiPadding var10000 = trans.getPadding();
                     var10000.t += 4;
                     var10000 = trans.getPadding();
                     var10000.t -= trans.getPadding().t % 8;
                  }
               }

               if ((this.dragType & 2) == 2) {
                  trans.getPadding().b = height - dy;
                  if (shift) {
                     GuiPadding var24 = trans.getPadding();
                     var24.b += 4;
                     var24 = trans.getPadding();
                     var24.b -= trans.getPadding().b % 8;
                  }
               }

               if ((this.dragType & 4) == 4) {
                  trans.getPadding().l = dx;
                  if (shift) {
                     GuiPadding var26 = trans.getPadding();
                     var26.l += 4;
                     var26 = trans.getPadding();
                     var26.l -= trans.getPadding().l % 8;
                  }
               }

               if ((this.dragType & 8) == 8) {
                  trans.getPadding().r = width - dx;
                  if (shift) {
                     GuiPadding var28 = trans.getPadding();
                     var28.r += 4;
                     var28 = trans.getPadding();
                     var28.r -= trans.getPadding().r % 8;
                  }
               }

               if (com != null) {
                  com.setTransform(trans);
               }
            }
         }

         if (!Mouse.isButtonDown(0)) {
            if (this.dragID >= 0) {
               this.refreshComponents();
            }

            this.dragID = -1;
            this.dragType = -1;
         }
      }

      if (this.toolMode == 2 && this.cvPreview.getTransform().contains(mx, my)) {
         IGuiPanel topPanel = this.getPanelUnderMouse(mx, my, 0);
         if (topPanel == null) {
            topPanel = this.cvPreview;
         }

         int edge = this.getNearbyEdges(topPanel.getTransform(), mx, my, 16, true);
         IGuiRect drawBounds;
         if (edge > 0) {
            drawBounds = (IGuiRect)PRE_TF_EDGE.get(Integer.valueOf(edge));
            drawBounds.setParent(topPanel.getTransform());
         } else {
            int segment = this.getSegmentSlice(topPanel.getTransform(), mx, my);
            drawBounds = PRE_TF_SEG[segment];
            drawBounds.setParent(topPanel.getTransform());
         }

         this.drawTransformBounds(drawBounds, false);
      } else if ((this.toolMode == 1 || this.toolMode == 3) && this.selPn != null) {
         this.drawTransformBounds(this.selPn.getTransform(), false);
      }

   }

   private void drawTransformBounds(@Nonnull IGuiRect rect, boolean showNumbers) {
      if (rect.getParent() != null) {
         this.boxLine.drawLine(rect.getParent(), rect.getParent(), 2, this.parCol, 1.0F);
      }

      this.boxLine.drawLine(rect, rect, 2, this.boundsCol, 1.0F);
      if (rect.getParent() != null) {
         int midX = rect.getX() + rect.getWidth() / 2;
         int midY = rect.getY() + rect.getHeight() / 2;
         int x1 = rect.getParent().getX();
         int x2 = rect.getX();
         int x3 = x2 + rect.getWidth();
         int x4 = rect.getParent().getX() + rect.getParent().getWidth();
         int ax1 = x1;
         int ax2 = x4;
         int y1 = rect.getParent().getY();
         int y2 = rect.getY();
         int y3 = y2 + rect.getHeight();
         int y4 = rect.getParent().getY() + rect.getParent().getHeight();
         int ay1 = y1;
         int ay2 = y4;
         if (rect instanceof GuiTransform) {
            GuiTransform trans = (GuiTransform)rect;
            ax1 = x1 + MathHelper.floor((float)trans.getParent().getWidth() * trans.getAnchor().x);
            ax2 = x1 + MathHelper.floor((float)trans.getParent().getWidth() * trans.getAnchor().z);
            ay1 = y1 + MathHelper.floor((float)trans.getParent().getHeight() * trans.getAnchor().y);
            ay2 = y1 + MathHelper.floor((float)trans.getParent().getHeight() * trans.getAnchor().w);
            this.drawSimpleLine(x1, midY, ax1, midY, 2, this.ancCol);
            this.drawSimpleLine(ax2, midY, x4, midY, 2, this.ancCol);
            this.drawSimpleLine(ax1, midY - 8, ax1, midY + 8, 2, this.ancCol);
            this.drawSimpleLine(ax2, midY - 8, ax2, midY + 8, 2, this.ancCol);
            this.drawSimpleLine(midX, y1, midX, ay1, 2, this.ancCol);
            this.drawSimpleLine(midX, ay2, midX, y4, 2, this.ancCol);
            this.drawSimpleLine(midX - 8, ay1, midX + 8, ay1, 2, this.ancCol);
            this.drawSimpleLine(midX - 8, ay2, midX + 8, ay2, 2, this.ancCol);
         }

         this.drawSimpleLine(ax1, midY, x2, midY, 2, this.parCol);
         this.drawSimpleLine(x3, midY, ax2, midY, 2, this.parCol);
         this.drawSimpleLine(midX, ay1, midX, y2, 2, this.parCol);
         this.drawSimpleLine(midX, y3, midX, ay2, 2, this.parCol);
      }
   }

   private void drawSimpleLine(int x1, int y1, int x2, int y2, int width, IGuiColor color) {
      GlStateManager.pushMatrix();
      GlStateManager.disableTexture2D();
      color.applyGlColor();
      GL11.glLineWidth((float)width);
      GL11.glBegin(1);
      GL11.glVertex2f((float)x1, (float)y1);
      GL11.glVertex2f((float)x2, (float)y2);
      GL11.glEnd();
      GL11.glLineWidth(1.0F);
      GlStateManager.enableTexture2D();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
   }

   public boolean onMouseClick(int mx, int my, int click) {
      if (this.toolMode != 0 && this.cvPreview.getTransform().contains(mx, my)) {
         if (this.toolMode == 1) {
            if (click == 0) {
               IGuiPanel topPanel = this.getPanelUnderMouse(mx, my, 0);
               this.selectedID = this.PANEL_DB.getID(topPanel);
               if (this.selectedID >= 0) {
                  this.selPn = topPanel;
               }
            } else if (click == 1) {
               this.selPn = null;
               this.selectedID = -1;
            }
         } else if (this.toolMode == 2 && click == 0 && this.paletteSel != null) {
            IGuiPanel topPanel = this.getPanelUnderMouse(mx, my, 0);
            if (topPanel == null) {
               topPanel = this.cvPreview;
            }

            int edge = this.getNearbyEdges(topPanel.getTransform(), mx, my, 16, true);
            GuiTransform drawBounds;
            if (edge > 0) {
               drawBounds = (GuiTransform)PRE_TF_EDGE.get(Integer.valueOf(edge));
               drawBounds.setParent(topPanel.getTransform());
            } else {
               int segment = this.getSegmentSlice(topPanel.getTransform(), mx, my);
               drawBounds = PRE_TF_SEG[segment];
               drawBounds.setParent(topPanel.getTransform());
            }

            drawBounds = drawBounds.copy();
            if (topPanel == this.cvPreview) {
               drawBounds.getPadding().setPadding(0, 0, 0, 0);
            } else {
               drawBounds.getPadding().setPadding(8, 8, 8, 8);
            }

            ComponentPanel com = new ComponentPanel(drawBounds);
            this.COM_DB.add(this.COM_DB.nextID(), com);
            com.panelType = this.paletteSel.toString();
            int id = this.PANEL_DB.getID(topPanel);
            this.selectedID = id;
            com.cvParentID = id;
            this.refreshComponents();
            this.selPn = this.PANEL_DB.getValue(id);
         } else if (this.toolMode == 3 && this.cvPreview.getChildren().size() > 0) {
            if (click == 0 && this.selPn == null) {
               IGuiPanel topPanel = this.getPanelUnderMouse(mx, my, 0);
               this.selectedID = this.PANEL_DB.getID(topPanel);
               if (this.selectedID >= 0) {
                  this.selPn = topPanel;
               }
            } else if (click == 0) {
               int edgePad = this.getNearbyEdges(this.selPn.getTransform(), mx, my, 4, true);
               int edgePar = 0;
               if (edgePad <= 0 && this.selPn.getTransform() instanceof GuiTransform) {
                  GuiTransform temp = new GuiTransform(((GuiTransform)this.selPn.getTransform()).getAnchor(), new GuiPadding(0, 0, 0, 0), 0);
                  temp.setParent(this.selPn.getTransform().getParent());
                  edgePar = this.selPn.getTransform().getParent() == null ? 0 : this.getNearbyEdges(temp, mx, my, 4, false);
               }

               if (edgePad > 0) {
                  this.dragID = this.selectedID;
                  this.dragType = edgePad;
               } else if (edgePar > 0) {
                  this.dragID = this.selectedID;
                  this.dragType = edgePar | 16;
               }
            } else if (click == 1) {
               this.selPn = null;
               this.selectedID = -1;
            }
         } else if (this.toolMode == 5 && this.cvPreview.getTransform().contains(mx, my)) {
            IGuiPanel topPanel = this.getPanelUnderMouse(mx, my, 0);
            int topID = topPanel == null ? -1 : this.PANEL_DB.getID(topPanel);
            if (topID >= 0) {
               this.removeComponent(topID);
               this.refreshComponents();
            }
         }

         return super.onMouseClick(mx, my, click);
      } else {
         return super.onMouseClick(mx, my, click);
      }
   }

   public boolean onKeyTyped(char c, int keycode) {
      return super.onKeyTyped(c, keycode);
   }

   private void refreshComponents() {
      this.PANEL_DB.reset();
      this.cvPreview.resetCanvas();
      this.COM_DB.getEntries().forEach((entry) -> {
         IGuiPanel pan = ((ComponentPanel)entry.getValue()).build();
         if (pan != null) {
            this.PANEL_DB.add(entry.getID(), pan);
         }

      });
      Queue<Integer> parentQueue = new ArrayDeque();
      parentQueue.add(Integer.valueOf(-1));

      while(!parentQueue.isEmpty()) {
         int pID = ((Integer)parentQueue.poll()).intValue();
         IGuiPanel pPan = this.PANEL_DB.getValue(pID);
         if (pPan == null) {
            pPan = this.cvPreview;
         }

         if (pPan instanceof IGuiCanvas) {
            IGuiCanvas pCan = (IGuiCanvas)pPan;
            this.getCanvasChildren(pID).forEach((entry) -> {
               pCan.addPanel(this.PANEL_DB.getValue(entry.getID()));
               parentQueue.add(Integer.valueOf(entry.getID()));
            });
         }

         ComponentPanel com = this.COM_DB.getValue(pID);
         IGuiPanel tPan = com == null ? null : (IGuiPanel)this.PANEL_DB.getValue(com.tfParentID);
         if (tPan != null) {
            pPan.getTransform().setParent(tPan.getTransform());
         }
      }

      if (this.selectedID >= 0) {
         this.selPn = this.PANEL_DB.getValue(this.selectedID);
         if (this.selPn == null) {
            this.selectedID = -1;
         }
      }

   }

   private void removeComponent(int comID) {
      Queue<Integer> parentQueue = new ArrayDeque();
      parentQueue.add(Integer.valueOf(comID));

      while(!parentQueue.isEmpty()) {
         int pID = ((Integer)parentQueue.poll()).intValue();
         this.getCanvasChildren(pID).forEach((entry) -> {
            parentQueue.add(Integer.valueOf(entry.getID()));
         });
         this.getTransformChildren(pID).forEach((entry) -> {
            ((ComponentPanel)entry.getValue()).tfParentID = -1;
         });
         this.COM_DB.removeID(pID);
         this.PANEL_DB.removeID(pID);
      }

   }

   @Nonnull
   private List<DBEntry<ComponentPanel>> getCanvasChildren(int panelID) {
      List<DBEntry<ComponentPanel>> list = new ArrayList();
      this.COM_DB.getEntries().forEach((entry) -> {
         if (((ComponentPanel)entry.getValue()).cvParentID == panelID) {
            list.add(entry);
         }

      });
      return list;
   }

   @Nonnull
   private List<DBEntry<ComponentPanel>> getTransformChildren(int panelID) {
      List<DBEntry<ComponentPanel>> list = new ArrayList();
      this.COM_DB.getEntries().forEach((entry) -> {
         if (((ComponentPanel)entry.getValue()).tfParentID == panelID) {
            list.add(entry);
         }

      });
      return list;
   }

   private IGuiPanel getPanelUnderMouse(int mx, int my, int range) {
      ListIterator<IGuiPanel> iter = this.cvPreview.getChildren().listIterator(this.cvPreview.getChildren().size());
      IGuiPanel topPanel = null;

      while(iter.hasPrevious()) {
         IGuiPanel pan = (IGuiPanel)iter.previous();
         if (this.containsRanged(pan.getTransform(), mx, my, range)) {
            topPanel = pan;
            if (!(pan instanceof IGuiCanvas)) {
               break;
            }

            iter = ((IGuiCanvas)pan).getChildren().listIterator(((IGuiCanvas)pan).getChildren().size());
         }
      }

      return topPanel;
   }

   private void openTrayNBT(NBTTagCompound tag) {
      this.cvPropTray.setEnabled(true);
      this.cvPropTray.resetCanvas();
      this.btnTrayToggle.setIcon(PresetIcon.ICON_DOWN.getTexture());
      PanelScrollingNBT cvNbt = new PanelScrollingNBT(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 0, 8, 0), 0), tag, 1, 2, 3, 4);
      this.cvPropTray.addPanel(cvNbt);
      PanelVScrollBar scPropTray = new PanelVScrollBar(new GuiTransform(GuiAlign.RIGHT_EDGE, new GuiPadding(-8, 0, 0, 0), 0));
      cvNbt.setScrollDriverY(scPropTray);
      this.cvPropTray.addPanel(scPropTray);
   }

   private void openTrayPalette() {
      this.cvPropTray.setEnabled(true);
      this.cvPropTray.resetCanvas();
      this.btnTrayToggle.setIcon(PresetIcon.ICON_DOWN.getTexture());
      CanvasScrolling cvScroll = new CanvasScrolling(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 0, 8, 0), 0));
      this.cvPropTray.addPanel(cvScroll);
      PanelVScrollBar scPropTray = new PanelVScrollBar(new GuiTransform(GuiAlign.RIGHT_EDGE, new GuiPadding(-8, 0, 0, 0), 0));
      cvScroll.setScrollDriverY(scPropTray);
      this.cvPropTray.addPanel(scPropTray);
      int cvWidth = cvScroll.getTransform().getWidth();
      List<ResourceLocation> resList = ComponentRegistry.INSTANCE.getRegisteredIDs();

      for(int i = 0; i < resList.size(); ++i) {
         ResourceLocation res = (ResourceLocation)resList.get(i);
         PanelButtonStorage<ResourceLocation> btnID = new PanelButtonStorage<ResourceLocation>(new GuiRectangle(0, i * 16, cvWidth, 16, 0), -1, res.toString(), res);
         btnID.setCallback((id) -> {
            this.paletteSel = id;
         });
         cvScroll.addPanel(btnID);
      }

   }

   private void closeTray() {
      this.cvPropTray.resetCanvas();
      this.cvPropTray.setEnabled(false);
      this.btnTrayToggle.setIcon(PresetIcon.ICON_UP.getTexture());
   }

   private int getNearbyEdges(IGuiRect rect, int mx, int my, int range, boolean bounded) {
      int value = 0;
      boolean boundX = mx >= rect.getX() && mx < rect.getX() + rect.getWidth();
      boolean boundY = my >= rect.getY() && my < rect.getY() + rect.getHeight();
      if (Math.abs(my - (rect.getY() + rect.getHeight())) > range || bounded && !boundX) {
         if (Math.abs(my - rect.getY()) <= range && mx >= rect.getX() && (!bounded || boundX)) {
            value |= 1;
         }
      } else {
         value |= 2;
      }

      if (Math.abs(mx - (rect.getX() + rect.getWidth())) > range || bounded && !boundY) {
         if (Math.abs(mx - rect.getX()) <= range && my >= rect.getY() && (!bounded || boundY)) {
            value |= 4;
         }
      } else {
         value |= 8;
      }

      return value;
   }

   private int getSegmentSlice(IGuiRect rect, int mx, int my) {
      if (!rect.contains(mx, my)) {
         return -1;
      } else {
         int dx = mx - rect.getX();
         int dy = my - rect.getY();
         int segX = dx / (rect.getWidth() / 3);
         int segY = dy / (rect.getHeight() / 3);
         return segY * 3 + segX;
      }
   }

   private boolean containsRanged(IGuiRect rect, int x3, int y3, int range) {
      int x1 = rect.getX();
      int y1 = rect.getY();
      int w = rect.getWidth();
      int h = rect.getHeight();
      int x2 = x1 + w;
      int y2 = y1 + h;
      return x3 >= x1 - range && x3 < x2 + range && y3 >= y1 - range && y3 < y2 + range;
   }

   static {
      HashMap<Integer, GuiTransform> tmp = new HashMap();
      tmp.put(Integer.valueOf(5), new GuiTransform(GuiAlign.TOP_LEFT, new GuiPadding(-4, -4, -4, -4), 0));
      tmp.put(Integer.valueOf(9), new GuiTransform(GuiAlign.TOP_RIGHT, new GuiPadding(-4, -4, -4, -4), 0));
      tmp.put(Integer.valueOf(10), new GuiTransform(GuiAlign.BOTTOM_RIGHT, new GuiPadding(-4, -4, -4, -4), 0));
      tmp.put(Integer.valueOf(6), new GuiTransform(GuiAlign.BOTTOM_LEFT, new GuiPadding(-4, -4, -4, -4), 0));
      tmp.put(Integer.valueOf(1), new GuiTransform(GuiAlign.TOP_EDGE, new GuiPadding(-4, -4, -4, -4), 0));
      tmp.put(Integer.valueOf(8), new GuiTransform(GuiAlign.RIGHT_EDGE, new GuiPadding(-4, -4, -4, -4), 0));
      tmp.put(Integer.valueOf(2), new GuiTransform(GuiAlign.BOTTOM_EDGE, new GuiPadding(-4, -4, -4, -4), 0));
      tmp.put(Integer.valueOf(4), new GuiTransform(GuiAlign.LEFT_EDGE, new GuiPadding(-4, -4, -4, -4), 0));
      PRE_TF_EDGE = Collections.unmodifiableMap(tmp);
   }
}
