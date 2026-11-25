package bq_standard.client.gui.tasks;

import betterquesting.api.api.QuestingAPI;
import betterquesting.api.misc.ICallback;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api2.client.gui.misc.GuiAlign;
import betterquesting.api2.client.gui.misc.GuiPadding;
import betterquesting.api2.client.gui.misc.GuiRectangle;
import betterquesting.api2.client.gui.misc.GuiTransform;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.CanvasEmpty;
import betterquesting.api2.client.gui.panels.bars.PanelVScrollBar;
import betterquesting.api2.client.gui.panels.content.PanelGeneric;
import betterquesting.api2.client.gui.panels.content.PanelItemSlot;
import betterquesting.api2.client.gui.panels.content.PanelTextBox;
import betterquesting.api2.client.gui.panels.lists.CanvasScrolling;
import betterquesting.api2.client.gui.resources.colors.GuiColorStatic;
import betterquesting.api2.client.gui.resources.textures.GuiTextureColored;
import betterquesting.api2.client.gui.resources.textures.IGuiTexture;
import betterquesting.api2.client.gui.resources.textures.ItemTexture;
import betterquesting.api2.client.gui.themes.presets.PresetColor;
import betterquesting.api2.client.gui.themes.presets.PresetIcon;
import betterquesting.api2.utils.QuestTranslation;
import bq_standard.core.BQ_Standard;
import bq_standard.tasks.TaskCrafting;
import java.util.UUID;
import mezz.jei.Internal;
import mezz.jei.api.recipe.IFocus.Mode;
import mezz.jei.gui.Focus;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Optional.Method;

public class PanelTaskCrafting extends CanvasEmpty {
   private final TaskCrafting task;

   public PanelTaskCrafting(IGuiRect rect, TaskCrafting task) {
      super(rect);
      this.task = task;
   }

   public void initPanel() {
      super.initPanel();
      UUID uuid = QuestingAPI.getQuestingUUID(Minecraft.getMinecraft().player);
      int[] progress = this.task.getUsersProgress(uuid);
      boolean isComplete = this.task.isComplete(uuid);
      IGuiTexture txTick = new GuiTextureColored(PresetIcon.ICON_TICK.getTexture(), new GuiColorStatic(-16711936));
      IGuiTexture txCross = new GuiTextureColored(PresetIcon.ICON_CROSS.getTexture(), new GuiColorStatic(-65536));
      this.addPanel(new PanelGeneric(new GuiRectangle(0, 0, 24, 24, 0), new ItemTexture(new BigItemStack(Blocks.CRAFTING_TABLE))));
      this.addPanel(new PanelGeneric(new GuiRectangle(16, 16, 8, 8, 0), this.task.allowCraft ? txTick : txCross));
      this.addPanel(new PanelGeneric(new GuiRectangle(32, 0, 24, 24, 0), new ItemTexture(new BigItemStack(Blocks.FURNACE))));
      this.addPanel(new PanelGeneric(new GuiRectangle(48, 16, 8, 8, 0), this.task.allowSmelt ? txTick : txCross));
      this.addPanel(new PanelGeneric(new GuiRectangle(64, 0, 24, 24, 0), new ItemTexture(new BigItemStack(Blocks.ANVIL))));
      this.addPanel(new PanelGeneric(new GuiRectangle(80, 16, 8, 8, 0), this.task.allowAnvil ? txTick : txCross));
      CanvasScrolling cvList = new CanvasScrolling(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 32, 8, 0), 0));
      this.addPanel(cvList);
      PanelVScrollBar scList = new PanelVScrollBar(new GuiTransform(GuiAlign.RIGHT_EDGE, new GuiPadding(-8, 32, 0, 0), 0));
      this.addPanel(scList);
      cvList.setScrollDriverY(scList);
      int listW = cvList.getTransform().getWidth();

      for(int i = 0; i < this.task.requiredItems.size(); ++i) {
         BigItemStack stack = (BigItemStack)this.task.requiredItems.get(i);
         PanelItemSlot slot = new PanelItemSlot(new GuiRectangle(0, i * 36, 36, 36, 0), -1, stack, false, true);
         if (BQ_Standard.hasJEI) {
            slot.setCallback((value) -> {
               this.lookupRecipe(value.getBaseStack());
            });
         }

         cvList.addPanel(slot);
         StringBuilder sb = new StringBuilder();
         sb.append(stack.getBaseStack().getDisplayName());
         if (stack.hasOreDict()) {
            sb.append(" (").append(stack.getOreDict()).append(")");
         }

         sb.append("\n").append(progress[i]).append("/").append(stack.stackSize).append("\n");
         if (!isComplete && progress[i] < stack.stackSize) {
            sb.append(TextFormatting.RED).append(QuestTranslation.translate("betterquesting.tooltip.incomplete", new Object[0]));
         } else {
            sb.append(TextFormatting.GREEN).append(QuestTranslation.translate("betterquesting.tooltip.complete", new Object[0]));
         }

         PanelTextBox text = new PanelTextBox(new GuiRectangle(40, i * 36, listW - 40, 36, 0), sb.toString());
         text.setColor(PresetColor.TEXT_MAIN.getColor());
         cvList.addPanel(text);
      }

   }

   @Method(
      modid = "jei"
   )
   private void lookupRecipe(ItemStack stack) {
      if (stack != null && !stack.isEmpty() && Internal.getRuntime() != null) {
         Internal.getRuntime().getRecipesGui().show(new Focus(Mode.OUTPUT, stack));
      }
   }
}
