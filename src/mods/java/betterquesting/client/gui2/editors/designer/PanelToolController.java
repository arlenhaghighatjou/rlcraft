package betterquesting.client.gui2.editors.designer;

import betterquesting.api.client.toolbox.IToolboxTool;
import betterquesting.api.utils.RenderUtils;
import betterquesting.api2.client.gui.controls.IValueIO;
import betterquesting.api2.client.gui.controls.PanelButtonQuest;
import betterquesting.api2.client.gui.controls.io.FloatSimpleIO;
import betterquesting.api2.client.gui.misc.GuiRectangle;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.client.gui.panels.content.PanelGeneric;
import betterquesting.api2.client.gui.panels.lists.CanvasQuestLine;
import betterquesting.api2.client.gui.resources.colors.GuiColorPulse;
import betterquesting.api2.client.gui.resources.colors.IGuiColor;
import betterquesting.api2.client.gui.resources.lines.BoxLine;
import betterquesting.api2.client.gui.resources.lines.IGuiLine;
import betterquesting.api2.client.gui.resources.textures.ColorTexture;
import betterquesting.api2.client.gui.resources.textures.IGuiTexture;
import betterquesting.api2.storage.DBEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.lax1dude.eaglercraft.v1_8.Keyboard;

public class PanelToolController implements IGuiPanel {
   private CanvasQuestLine questLine;
   private final IGuiRect transform;
   private boolean enabled = true;
   private final IValueIO<Float> scDriverX;
   private final IValueIO<Float> scDriverY;
   private IToolboxTool activeTool;
   public static final NonNullList<PanelButtonQuest> selected = NonNullList.create();
   public static final List<IGuiPanel> highlights = new ArrayList();
   private GuiRectangle selBounds;
   private IGuiLine selLine = new BoxLine();
   private IGuiColor selCol = new GuiColorPulse(-1, -16777216, 2.0D, 0.0F);
   private IGuiTexture hTex = new ColorTexture(new GuiColorPulse(587202559, 2013265919, 2.0D, 0.0F));

   public PanelToolController(IGuiRect rect, CanvasQuestLine questLine) {
      this.transform = rect;
      this.questLine = questLine;
      this.scDriverX = (new FloatSimpleIO() {
         public void writeValue(Float value) {
            if (PanelToolController.this.activeTool != null && !PanelToolController.this.activeTool.clampScrolling()) {
               this.v = value.floatValue();
            } else {
               this.v = MathHelper.clamp(value.floatValue(), 0.0F, 1.0F);
            }

         }
      }).setLerp(true, 0.02F);
      this.scDriverY = (new FloatSimpleIO() {
         public void writeValue(Float value) {
            if (PanelToolController.this.activeTool != null && !PanelToolController.this.activeTool.clampScrolling()) {
               this.v = value.floatValue();
            } else {
               this.v = MathHelper.clamp(value.floatValue(), 0.0F, 1.0F);
            }

         }
      }).setLerp(true, 0.02F);
   }

   public void setActiveTool(IToolboxTool tool) {
      if (this.activeTool != null) {
         this.activeTool.disableTool();
      }

      if (tool != null) {
         this.activeTool = tool;
         tool.initTool(this.questLine);
      }
   }

   public IToolboxTool getActiveTool() {
      return this.activeTool;
   }

   public void changeCanvas(@Nonnull CanvasQuestLine canvas) {
      this.questLine = canvas;
      this.refreshCanvas();
      this.setActiveTool(this.getActiveTool());
   }

   public void refreshCanvas() {
      List<PanelButtonQuest> tmp = new ArrayList();

      for(PanelButtonQuest b1 : selected) {
         for(PanelButtonQuest b2 : this.questLine.getQuestButtons()) {
            if (((DBEntry)b1.getStoredValue()).getID() == ((DBEntry)b2.getStoredValue()).getID()) {
               tmp.add(b2);
            }
         }
      }

      selected.clear();
      selected.addAll(tmp);
      highlights.clear();

      for(PanelButtonQuest btn : selected) {
         highlights.add(new PanelGeneric(btn.rect, this.hTex));
      }

      if (this.activeTool != null) {
         this.activeTool.refresh(this.questLine);
      }

   }

   public CanvasQuestLine getCanvas() {
      return this.getCanvas();
   }

   public IValueIO<Float> getScrollX() {
      return this.scDriverX;
   }

   public IValueIO<Float> getScrollY() {
      return this.scDriverY;
   }

   public IGuiRect getTransform() {
      return this.transform;
   }

   public void initPanel() {
   }

   public void setEnabled(boolean state) {
      this.enabled = state;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void drawPanel(int mx, int my, float partialTick) {
      if (this.enabled) {
         if (this.activeTool != null) {
            float zs = this.questLine.getZoom();
            int lsx = this.questLine.getScrollX();
            int lsy = this.questLine.getScrollY();
            int tx = this.getTransform().getX();
            int ty = this.getTransform().getY();
            int smx = (int)((float)(mx - tx) / zs) + lsx;
            int smy = (int)((float)(my - ty) / zs) + lsy;
            GlStateManager.pushMatrix();
            RenderUtils.startScissor(this.transform);
            GlStateManager.translate((float)tx - (float)lsx * zs, (float)ty - (float)lsy * zs, 0.0F);
            GlStateManager.scale(zs, zs, zs);
            if (this.selBounds != null) {
               this.selBounds.w = smx - this.selBounds.x;
               this.selBounds.h = smy - this.selBounds.y;
               this.selLine.drawLine(this.selBounds, this.selBounds, 2, this.selCol, partialTick);
            }

            for(IGuiPanel pn : highlights) {
               pn.drawPanel(smx, smy, partialTick);
            }

            this.activeTool.drawCanvas(smx, smy, partialTick);
            RenderUtils.endScissor();
            GlStateManager.popMatrix();
            this.activeTool.drawOverlay(mx, my, partialTick);
         }

      }
   }

   public boolean onMouseClick(int mx, int my, int button) {
      if (this.activeTool != null && this.getTransform().contains(mx, my)) {
         if (this.activeTool.onMouseClick(mx, my, button)) {
            return true;
         }

         if (this.activeTool.useSelection()) {
            if (button == 1) {
               this.selBounds = null;
               selected.clear();
               highlights.clear();
               this.activeTool.onSelection(selected);
               return true;
            }

            if (button == 0) {
               float zs = this.questLine.getZoom();
               int lsx = this.questLine.getScrollX();
               int lsy = this.questLine.getScrollY();
               int tx = this.questLine.getTransform().getX();
               int ty = this.questLine.getTransform().getY();
               int smx = (int)((float)(mx - tx) / zs) + lsx;
               int smy = (int)((float)(my - ty) / zs) + lsy;
               this.selBounds = new GuiRectangle(smx, smy, 0, 0);
               return true;
            }
         }
      }

      return false;
   }

   public boolean onMouseRelease(int mx, int my, int button) {
      if (this.selBounds != null) {
         if (this.selBounds.w < 0) {
            this.selBounds.x += this.selBounds.w;
            this.selBounds.w *= -1;
         }

         if (this.selBounds.h < 0) {
            this.selBounds.y += this.selBounds.h;
            this.selBounds.h *= -1;
         }

         boolean append = Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
         boolean subtract = Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157);
         if (!append && !subtract) {
            selected.clear();
         }

         for(PanelButtonQuest btn : this.questLine.getQuestButtons()) {
            if (this.selBounds.contains(btn.rect.x + btn.rect.w / 2, btn.rect.y + btn.rect.h / 2) || btn.rect.contains(this.selBounds.x, this.selBounds.y) && Math.max(this.selBounds.w, this.selBounds.h) < 4) {
               if (subtract) {
                  selected.remove(btn);
               } else if (!append || !selected.contains(btn)) {
                  selected.add(btn);
               }
            }
         }

         highlights.clear();

         for(PanelButtonQuest btn : selected) {
            highlights.add(new PanelGeneric(btn.rect, this.hTex));
         }

         this.selBounds = null;
         if (this.activeTool != null) {
            this.activeTool.onSelection(selected);
         }
      }

      return this.activeTool != null ? this.activeTool.onMouseRelease(mx, my, button) : false;
   }

   public boolean onMouseScroll(int mx, int my, int scroll) {
      return this.activeTool != null ? this.activeTool.onMouseScroll(mx, my, scroll) : false;
   }

   public boolean onKeyTyped(char c, int keycode) {
      if (this.activeTool != null) {
         if (this.activeTool.onKeyPressed(c, keycode)) {
            return true;
         }

         if (this.activeTool.useSelection() && keycode == 30) {
            boolean append = Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157);
            boolean subtract = append && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
            if (subtract) {
               this.selBounds = null;
               selected.clear();
               highlights.clear();
               this.activeTool.onSelection(selected);
               return true;
            }

            if (append) {
               selected.clear();
               highlights.clear();

               for(PanelButtonQuest btn : this.questLine.getQuestButtons()) {
                  selected.add(btn);
                  highlights.add(new PanelGeneric(btn.rect, this.hTex));
               }

               this.selBounds = null;
               this.activeTool.onSelection(selected);
               return true;
            }
         }
      }

      return false;
   }

   public List<String> getTooltip(int mx, int my) {
      if (this.selBounds != null) {
         return Collections.emptyList();
      } else {
         return this.activeTool != null ? this.activeTool.getTooltip(mx, my) : null;
      }
   }
}
