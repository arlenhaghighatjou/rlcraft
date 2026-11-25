package bq_standard.client.gui.tasks;

import betterquesting.api.api.QuestingAPI;
import betterquesting.api.utils.RenderUtils;
import betterquesting.api2.client.gui.misc.GuiAlign;
import betterquesting.api2.client.gui.misc.GuiPadding;
import betterquesting.api2.client.gui.misc.GuiTransform;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.CanvasEmpty;
import betterquesting.api2.client.gui.panels.content.PanelGeneric;
import betterquesting.api2.client.gui.panels.content.PanelTextBox;
import betterquesting.api2.client.gui.resources.colors.IGuiColor;
import betterquesting.api2.client.gui.resources.textures.IGuiTexture;
import betterquesting.api2.client.gui.themes.presets.PresetColor;
import betterquesting.api2.utils.QuestTranslation;
import bq_standard.tasks.TaskLocation;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;

public class PanelTaskLocation extends CanvasEmpty {
   private final TaskLocation task;

   public PanelTaskLocation(IGuiRect rect, TaskLocation task) {
      super(rect);
      this.task = task;
   }

   public void initPanel() {
      super.initPanel();
      String desc = QuestTranslation.translate(this.task.name, new Object[0]);
      if (!this.task.hideInfo) {
         desc = desc + " (" + getDimName(this.task.dim) + ")";
         if (this.task.range >= 0) {
            desc = desc + "\n" + QuestTranslation.translate("bq_standard.gui.location", new Object[]{"(" + this.task.x + ", " + this.task.y + ", " + this.task.z + ")"});
            StringBuilder var10000 = (new StringBuilder()).append(desc).append("\n");
            Object[] var10002 = new Object[1];
            StringBuilder var10005 = new StringBuilder();
            double var10007 = (double)this.task.x;
            double var10008 = (double)this.task.y;
            var10002[0] = var10005.append((int)Minecraft.getMinecraft().player.getDistance(var10007, var10008, (double)this.task.z)).append("m").toString();
            desc = var10000.append(QuestTranslation.translate("bq_standard.gui.distance", var10002)).toString();
         }

         if (!StringUtils.isNullOrEmpty(this.task.biome)) {
            Biome biome = (Biome)Biome.REGISTRY.getObject(new ResourceLocation(this.task.biome));
            desc = desc + "\n" + QuestTranslation.translate("bq_standard.gui.biome", new Object[]{biome == null ? "?" : biome.getBiomeName()});
         }

         if (!StringUtils.isNullOrEmpty(this.task.structure)) {
            desc = desc + "\n" + QuestTranslation.translate("bq_standard.gui.structure", new Object[]{this.task.structure});
         }
      }

      if (this.task.isComplete(QuestingAPI.getQuestingUUID(Minecraft.getMinecraft().player))) {
         desc = desc + "\n" + TextFormatting.BOLD + TextFormatting.GREEN + QuestTranslation.translate("bq_standard.gui.found", new Object[0]);
      } else {
         desc = desc + "\n" + TextFormatting.BOLD + TextFormatting.RED + QuestTranslation.translate("bq_standard.gui.undiscovered", new Object[0]);
      }

      this.addPanel((new PanelTextBox(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 0, 0, 0), 0), desc)).setColor(PresetColor.TEXT_MAIN.getColor()));
      IGuiTexture texCompass = new IGuiTexture() {
         public void drawTexture(int x, int y, int width, int height, float zDepth, float partialTick) {
            this.drawTexture(x, y, width, height, zDepth, partialTick, (IGuiColor)null);
         }

         public void drawTexture(int x, int y, int width, int height, float zDepth, float partialTick, IGuiColor color) {
            Minecraft mc = Minecraft.getMinecraft();
            double la = Math.atan2((double)PanelTaskLocation.this.task.z - mc.player.posZ, (double)PanelTaskLocation.this.task.x - mc.player.posX);
            int radius = width / 2 - 12;
            int cx = x + width / 2;
            int cy = y + height / 2;
            int dx = (int)(Math.cos(la) * (double)radius);
            int dy = (int)(Math.sin(la) * (double)(-radius));
            int txtClr = color == null ? -1 : color.getRGB();
            Gui.drawRect(cx - radius, cy - radius, cx + radius, cy + radius, Color.BLACK.getRGB());
            RenderUtils.DrawLine(cx - radius, cy - radius, cx + radius, cy - radius, 4.0F, txtClr);
            RenderUtils.DrawLine(cx - radius, cy - radius, cx - radius, cy + radius, 4.0F, txtClr);
            RenderUtils.DrawLine(cx + radius, cy + radius, cx + radius, cy - radius, 4.0F, txtClr);
            RenderUtils.DrawLine(cx + radius, cy + radius, cx - radius, cy + radius, 4.0F, txtClr);
            mc.fontRenderer.drawString(TextFormatting.BOLD + "N", cx - 4, cy - radius - 9, txtClr);
            mc.fontRenderer.drawString(TextFormatting.BOLD + "S", cx - 4, cy + radius + 2, txtClr);
            mc.fontRenderer.drawString(TextFormatting.BOLD + "E", cx + radius + 2, cy - 4, txtClr);
            mc.fontRenderer.drawString(TextFormatting.BOLD + "W", cx - radius - 8, cy - 4, txtClr);
            if (!PanelTaskLocation.this.task.hideInfo && PanelTaskLocation.this.task.range >= 0 && mc.player.dimension == PanelTaskLocation.this.task.dim) {
               RenderUtils.DrawLine(cx, cy, cx + dx, cy - dy, 4.0F, Color.RED.getRGB());
            } else {
               GlStateManager.pushMatrix();
               GlStateManager.scale(2.0F, 2.0F, 2.0F);
               mc.fontRenderer.drawString(TextFormatting.BOLD + "?", cx / 2 - 4, cy / 2 - 4, Color.RED.getRGB());
               GlStateManager.popMatrix();
            }

         }

         public ResourceLocation getTexture() {
            return null;
         }

         public IGuiRect getBounds() {
            return null;
         }
      };
      int innerSize = Math.min(this.getTransform().getWidth(), (int)Math.floor((double)this.getTransform().getHeight() * 0.5D));
      PanelGeneric panelCompass = new PanelGeneric(new GuiTransform(GuiAlign.BOTTOM_RIGHT, -innerSize, -innerSize, innerSize, innerSize, -1), texCompass, PresetColor.TEXT_MAIN.getColor());
      this.addPanel(panelCompass);
      panelCompass.setEnabled(this.task.range >= 0 && !this.task.hideInfo);
   }

   private static String getDimName(int dim) {
      try {
         return DimensionType.getById(dim).getName();
      } catch (Exception var2) {
         return "?";
      }
   }
}
