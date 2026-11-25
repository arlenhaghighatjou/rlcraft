package betterquesting.api2.client.gui.controls;

import betterquesting.api.utils.RenderUtils;
import betterquesting.api2.client.gui.events.PEventBroadcaster;
import betterquesting.api2.client.gui.events.types.PEventButton;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.client.gui.resources.colors.GuiColorStatic;
import betterquesting.api2.client.gui.resources.colors.IGuiColor;
import betterquesting.api2.client.gui.resources.textures.IGuiTexture;
import betterquesting.api2.client.gui.themes.presets.PresetColor;
import betterquesting.api2.client.gui.themes.presets.PresetTexture;
import betterquesting.api2.storage.INBTSaveLoad;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.lax1dude.eaglercraft.v1_8.Mouse;

public class PanelButton implements IPanelButton, IGuiPanel, INBTSaveLoad<NBTTagCompound> {
   private final IGuiRect transform;
   private boolean enabled = true;
   private final IGuiTexture[] texStates = new IGuiTexture[3];
   private IGuiColor[] colStates = new IGuiColor[]{new GuiColorStatic(128, 128, 128, 255), new GuiColorStatic(255, 255, 255, 255), new GuiColorStatic(16777120)};
   private IGuiTexture texIcon = null;
   private IGuiColor colIcon = null;
   private int icoPadding = 0;
   private List<String> tooltip = null;
   private boolean txtShadow = true;
   private String btnText;
   private int textAlign = 1;
   private boolean isActive = true;
   private final int btnID;
   private boolean pendingRelease = false;
   private Consumer<PanelButton> clickAction = null;

   public PanelButton(IGuiRect rect, int id, String txt) {
      this.transform = rect;
      this.btnText = txt;
      this.btnID = id;
      this.setTextures(PresetTexture.BTN_NORMAL_0.getTexture(), PresetTexture.BTN_NORMAL_1.getTexture(), PresetTexture.BTN_NORMAL_2.getTexture());
      this.setTextHighlight(PresetColor.BTN_DISABLED.getColor(), PresetColor.BTN_IDLE.getColor(), PresetColor.BTN_HOVER.getColor());
   }

   public PanelButton setClickAction(Consumer<PanelButton> action) {
      this.clickAction = action;
      return this;
   }

   public PanelButton setTextHighlight(IGuiColor disabled, IGuiColor idle, IGuiColor hover) {
      this.colStates[0] = disabled;
      this.colStates[1] = idle;
      this.colStates[2] = hover;
      return this;
   }

   public PanelButton setTextShadow(boolean enabled) {
      this.txtShadow = enabled;
      return this;
   }

   public PanelButton setTextAlignment(int align) {
      this.textAlign = MathHelper.clamp(align, 0, 2);
      return this;
   }

   public PanelButton setTextures(IGuiTexture disabled, IGuiTexture idle, IGuiTexture hover) {
      this.texStates[0] = disabled;
      this.texStates[1] = idle;
      this.texStates[2] = hover;
      return this;
   }

   public PanelButton setIcon(IGuiTexture icon) {
      return this.setIcon(icon, 0);
   }

   public PanelButton setIcon(IGuiTexture icon, int padding) {
      return this.setIcon(icon, (IGuiColor)null, padding);
   }

   public PanelButton setIcon(IGuiTexture icon, IGuiColor color, int padding) {
      this.texIcon = icon;
      this.colIcon = color;
      this.icoPadding = padding * 2;
      return this;
   }

   public PanelButton setTooltip(List<String> tooltip) {
      this.tooltip = tooltip;
      return this;
   }

   public void setText(String text) {
      this.btnText = text;
   }

   public String getText() {
      return this.btnText;
   }

   public int getButtonID() {
      return this.btnID;
   }

   public boolean isActive() {
      return this.isActive;
   }

   public void setActive(boolean state) {
      this.isActive = state;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean state) {
      this.enabled = state;
   }

   public IGuiRect getTransform() {
      return this.transform;
   }

   public void initPanel() {
   }

   public void drawPanel(int mx, int my, float partialTick) {
      IGuiRect bounds = this.getTransform();
      GlStateManager.pushMatrix();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      int curState = !this.isActive() ? 0 : (bounds.contains(mx, my) ? 2 : 1);
      if (curState == 2 && this.pendingRelease && Mouse.isButtonDown(0)) {
         curState = 0;
      }

      IGuiTexture t = this.texStates[curState];
      if (t != null) {
         t.drawTexture(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), 0.0F, partialTick);
      }

      if (this.texIcon != null) {
         int isz = Math.min(bounds.getHeight() - this.icoPadding, bounds.getWidth() - this.icoPadding);
         if (isz > 0) {
            if (this.colIcon != null) {
               this.texIcon.drawTexture(bounds.getX() + bounds.getWidth() / 2 - isz / 2, bounds.getY() + bounds.getHeight() / 2 - isz / 2, isz, isz, 0.0F, partialTick, this.colIcon);
            } else {
               this.texIcon.drawTexture(bounds.getX() + bounds.getWidth() / 2 - isz / 2, bounds.getY() + bounds.getHeight() / 2 - isz / 2, isz, isz, 0.0F, partialTick);
            }
         }
      }

      if (this.btnText != null && this.btnText.length() > 0) {
         drawCenteredString(Minecraft.getMinecraft().fontRenderer, this.btnText, bounds.getX(), bounds.getY() + bounds.getHeight() / 2 - 4, bounds.getWidth(), this.colStates[curState].getRGB(), this.txtShadow, this.textAlign);
      }

      GlStateManager.popMatrix();
   }

   private static void drawCenteredString(FontRenderer font, String text, int x, int y, int width, int color, boolean shadow, int align) {
      switch(align) {
      case 0:
         font.drawString(text, (float)(x + 4), (float)y, color, shadow);
         break;
      case 2:
         font.drawString(text, (float)(x + width) - (float)RenderUtils.getStringWidth(text, font) / 2.0F - 4.0F, (float)y, color, shadow);
         break;
      default:
         font.drawString(text, (float)(x + Math.floorDiv(width, 2)) - (float)RenderUtils.getStringWidth(text, font) / 2.0F, (float)y, color, shadow);
      }

   }

   public boolean onMouseClick(int mx, int my, int click) {
      boolean contains = this.getTransform().contains(mx, my);
      this.pendingRelease = this.isActive() && click == 0 && contains;
      return (click == 0 || click == 1) && contains;
   }

   public boolean onMouseRelease(int mx, int my, int click) {
      if (!this.pendingRelease) {
         return false;
      } else {
         this.pendingRelease = false;
         IGuiRect bounds = this.getTransform();
         boolean clicked = this.isActive() && click == 0 && bounds.contains(mx, my) && !PEventBroadcaster.INSTANCE.postEvent(new PEventButton(this));
         if (clicked) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.onButtonClick();
         }

         return clicked;
      }
   }

   public boolean onMouseScroll(int mx, int my, int scroll) {
      return false;
   }

   public boolean onKeyTyped(char c, int keycode) {
      return false;
   }

   public List<String> getTooltip(int mx, int my) {
      return this.getTransform().contains(mx, my) ? this.tooltip : null;
   }

   public void onButtonClick() {
      if (this.clickAction != null) {
         this.clickAction.accept(this);
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
      return nbt;
   }

   public void readFromNBT(NBTTagCompound nbt) {
   }
}
