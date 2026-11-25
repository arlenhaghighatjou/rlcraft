package vazkii.quark.management.client.gui;

import com.google.common.collect.BiMap;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextFormatting;
import vazkii.arl.util.RenderHelper;
import vazkii.quark.base.client.IParentedGui;
import vazkii.quark.base.client.ModKeybinds;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.management.feature.FavoriteItems;
import vazkii.quark.management.feature.StoreToChests;

public class GuiButtonChest extends GuiButton implements IParentedGui {
   public final GuiButtonChest.Action action;
   public final GuiScreen parent;
   public final int shiftX;
   public final int shiftY;
   private Predicate<GuiScreen> enabledPredicate;
   private boolean ender;

   public GuiButtonChest(GuiScreen parent, GuiButtonChest.Action action, int id, int par2, int par3, int left, int top) {
      super(id, par2 + left, par3 + top, 16, 16, "");
      this.enabledPredicate = null;
      this.ender = false;
      this.action = action;
      this.parent = parent;
      this.shiftX = par2;
      this.shiftY = par3;
   }

   public GuiButtonChest(GuiScreen parent, GuiButtonChest.Action action, int id, int par2, int par3, int left, int top, Predicate<GuiScreen> enabledPredicate) {
      this(parent, action, id, par2, par3, left, top);
      this.enabledPredicate = enabledPredicate;
   }

   public void drawButton(@Nonnull Minecraft par1Minecraft, int par2, int par3, float partial) {
      if (par1Minecraft.player.isSpectator()) {
         this.enabled = false;
      }

      if (this.enabledPredicate != null) {
         this.enabled = this.enabledPredicate.test(this.parent);
      }

      if (this.enabled) {
         this.hovered = par2 >= this.x && par3 >= this.y && par2 < this.x + this.width && par3 < this.y + this.height;
         int k = this.getHoverState(this.hovered);
         int u = this.action.u;
         int v = this.action.v;
         if (this.action == GuiButtonChest.Action.DROPOFF && GuiScreen.isShiftKeyDown() != StoreToChests.invert) {
            u = 32;
         }

         if (k == 2) {
            u += 16;
         }

         par1Minecraft.renderEngine.bindTexture(LibMisc.GENERAL_ICONS_RESOURCE);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.draw(u, v);
         if (k == 2) {
            if (this.action != GuiButtonChest.Action.RESTOCK && !this.action.isSortAction()) {
               FavoriteItems.hovering = true;
            }

            GlStateManager.pushMatrix();
            String tooltip;
            if (this.action == GuiButtonChest.Action.DROPOFF && GuiScreen.isShiftKeyDown() != StoreToChests.invert) {
               tooltip = I18n.format("quarkmisc.chestButton." + this.action.name().toLowerCase() + ".shift", new Object[0]);
            } else {
               tooltip = I18n.format("quarkmisc.chestButton." + this.action.name().toLowerCase(), new Object[0]);
            }

            int len = Minecraft.getMinecraft().fontRenderer.getStringWidth(tooltip);
            int tooltipShift = this.action == GuiButtonChest.Action.DROPOFF ? 0 : -len - 24;
            List<String> tooltipList = new ArrayList();
            tooltipList.add(tooltip);
            BiMap<IParentedGui, KeyBinding> map = ModKeybinds.keyboundButtons.inverse();
            if (map.containsKey(this)) {
               KeyBinding key = (KeyBinding)map.get(this);
               if (key.getKeyCode() != 0) {
                  String press = I18n.format("quarkmisc.keyboundButton", new Object[]{TextFormatting.GRAY, GameSettings.getKeyDisplayString(key.getKeyCode())});
                  tooltipList.add(press);
                  if (this.action != GuiButtonChest.Action.DROPOFF) {
                     int len2 = par1Minecraft.fontRenderer.getStringWidth(press);
                     if (len2 > len) {
                        tooltipShift = -len2 - 24;
                     }
                  }
               }
            }

            GlStateManager.enableDepth();
            GlStateManager.translate(0.0F, 0.0F, par1Minecraft.getRenderItem().zLevel + 300.0F);
            RenderHelper.renderTooltip(par2 + tooltipShift, par3 + 8, tooltipList);
            GlStateManager.popMatrix();
            GlStateManager.disableDepth();
         }
      }

   }

   private void draw(int u, int v) {
      this.drawChest();
      this.drawIcon(u, v);
   }

   protected void drawChest() {
      this.drawIcon(this.ender ? 32 : 0, 128);
   }

   protected void drawIcon(int u, int v) {
      this.drawTexturedModalRect(this.x, this.y, u, v, 16, 16);
   }

   public void setEnder(boolean ender) {
      this.ender = ender;
   }

   public GuiScreen getParent() {
      return this.parent;
   }

   public static enum Action {
      DROPOFF(0, 0),
      DEPOSIT(0, 0),
      SMART_DEPOSIT(32, 0),
      RESTOCK(64, 0),
      EXTRACT(64, 16),
      SORT(0, 16),
      SORT_PLAYER(0, 16);

      public final int u;
      public final int v;

      private Action(int u, int v) {
         this.u = u;
         this.v = v;
      }

      public boolean isSortAction() {
         return this == SORT || this == SORT_PLAYER;
      }
   }
}
