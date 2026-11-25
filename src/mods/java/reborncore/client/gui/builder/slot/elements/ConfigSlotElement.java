package reborncore.client.gui.builder.slot.elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import reborncore.api.recipe.IRecipeCrafterProvider;
import reborncore.client.gui.builder.GuiBase;
import reborncore.client.gui.builder.slot.GuiSlotConfiguration;
import reborncore.client.gui.slots.BaseSlot;
import reborncore.common.recipes.RecipeCrafter;

public class ConfigSlotElement extends ElementBase {
   SlotType type;
   IInventory inventory;
   int id;
   public List<ElementBase> elements = new ArrayList();
   boolean filter = false;

   public ConfigSlotElement(IInventory slotInventory, int slotId, SlotType type, int x, int y, GuiBase gui) {
      super(x, y, type.getButtonSprite());
      this.type = type;
      this.inventory = slotInventory;
      this.id = slotId;
      boolean inputEnabled = gui.container.inventorySlots.stream().filter(Objects::nonNull).filter((slot) -> {
         return slot.inventory == this.inventory;
      }).filter((slot) -> {
         return slot instanceof BaseSlot;
      }).map((slot) -> {
         return (BaseSlot)slot;
      }).filter((baseSlot) -> {
         return baseSlot.getSlotIndex() == slotId;
      }).allMatch(BaseSlot::canWorldBlockInsert);
      SlotConfigPopupElement popupElement;
      this.elements.add(popupElement = new SlotConfigPopupElement(this.id, x - 22, y - 22, this, inputEnabled));
      this.elements.add((new ButtonElement(x + 37, y - 25, Sprite.EXIT_BUTTON)).addReleaseAction((element, gui1, provider, mouseX, mouseY) -> {
         GuiSlotConfiguration.selectedSlot = -1;
         GuiBase.slotConfigType = GuiBase.SlotConfigType.NONE;
         return true;
      }));
      if (inputEnabled) {
         this.elements.add((new CheckBoxElement("Auto Input", -1, x - 26, y + 42, "input", slotId, Sprite.LIGHT_CHECK_BOX, gui.getMachine(), (checkBoxElement) -> {
            return checkBoxElement.machineBase.slotConfiguration.getSlotDetails(checkBoxElement.slotID).autoInput();
         })).addPressAction((element, gui12, provider, mouseX, mouseY) -> {
            popupElement.updateCheckBox((CheckBoxElement)element, "input", gui12);
            return true;
         }));
      }

      this.elements.add((new CheckBoxElement("Auto Output", -1, x - 26, y + (inputEnabled ? 57 : 42), "output", slotId, Sprite.LIGHT_CHECK_BOX, gui.getMachine(), (checkBoxElement) -> {
         return checkBoxElement.machineBase.slotConfiguration.getSlotDetails(checkBoxElement.slotID).autoOutput();
      })).addPressAction((element, gui13, provider, mouseX, mouseY) -> {
         popupElement.updateCheckBox((CheckBoxElement)element, "output", gui13);
         return true;
      }));
      if (gui.getMachine() instanceof IRecipeCrafterProvider) {
         RecipeCrafter recipeCrafter = ((IRecipeCrafterProvider)gui.getMachine()).getRecipeCrafter();
         if (Arrays.stream(recipeCrafter.inputSlots).anyMatch((value) -> {
            return value == slotId;
         })) {
            this.elements.add((new CheckBoxElement("Filter Input", -1, x - 26, y + 72, "filter", slotId, Sprite.LIGHT_CHECK_BOX, gui.getMachine(), (checkBoxElement) -> {
               return checkBoxElement.machineBase.slotConfiguration.getSlotDetails(checkBoxElement.slotID).filter();
            })).addPressAction((element, gui13, provider, mouseX, mouseY) -> {
               popupElement.updateCheckBox((CheckBoxElement)element, "filter", gui13);
               return true;
            }));
            this.filter = true;
            popupElement.filter = true;
         }
      }

      this.setWidth(85);
      this.setHeight(105 + (this.filter ? 15 : 0));
   }

   public void draw(GuiBase gui) {
      super.draw(gui);
      ItemStack stack = this.inventory.getStackInSlot(this.id);
      int xPos = this.x + 1 + gui.guiLeft;
      int yPos = this.y + 1 + gui.guiTop;
      GlStateManager.enableDepth();
      GlStateManager.pushMatrix();
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
      RenderHelper.enableGUIStandardItemLighting();
      RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
      renderItem.renderItemAndEffectIntoGUI(gui.mc.player, stack, xPos, yPos);
      renderItem.renderItemOverlayIntoGUI(gui.mc.fontRenderer, stack, xPos, yPos, (String)null);
      GlStateManager.disableDepth();
      GlStateManager.disableLighting();
      GlStateManager.popMatrix();
      if (this.isHovering) {
         this.drawSprite(gui, this.type.getButtonHoverOverlay(), this.x, this.y);
      }

      this.elements.forEach((elementBase) -> {
         elementBase.draw(gui);
      });
   }

   public SlotType getType() {
      return this.type;
   }

   public int getId() {
      return this.id;
   }
}
