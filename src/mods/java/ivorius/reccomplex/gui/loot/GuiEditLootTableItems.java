package ivorius.reccomplex.gui.loot;

import ivorius.ivtoolkit.gui.FloatRange;
import ivorius.ivtoolkit.gui.GuiControlListener;
import ivorius.ivtoolkit.gui.GuiSlider;
import ivorius.ivtoolkit.gui.GuiSliderRange;
import ivorius.ivtoolkit.gui.IntegerRange;
import ivorius.ivtoolkit.gui.Ranges;
import ivorius.ivtoolkit.network.PacketGuiAction;
import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.gui.GuiHider;
import ivorius.reccomplex.gui.InventoryWatcher;
import ivorius.reccomplex.gui.RCGuiHandler;
import ivorius.reccomplex.utils.SaveDirectoryData;
import ivorius.reccomplex.utils.scale.Scale;
import ivorius.reccomplex.utils.scale.Scales;
import ivorius.reccomplex.world.storage.loot.GenericLootTable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class GuiEditLootTableItems extends GuiContainer implements InventoryWatcher {
   public static ResourceLocation textureBackground = new ResourceLocation("reccomplex", RecurrentComplex.filePathTextures + "gui_edit_inventory_gen.png");
   public static Scale WEIGHT_SCALE = Scales.pow(5.0F);
   public String key;
   public GenericLootTable.Component component;
   public SaveDirectoryData saveDirectoryData;
   private GuiButton backBtn;
   private GuiButton nextPageBtn;
   private GuiButton prevPageBtn;
   private List<GuiSlider> weightSliders = new ArrayList();
   private List<GuiSliderRange> minMaxSliders = new ArrayList();
   private int currentPage;

   public GuiEditLootTableItems(EntityPlayer player, GenericLootTable.Component component, String key, SaveDirectoryData saveDirectoryData) {
      super(new ContainerEditLootTableItems(player, key, component));
      this.key = key;
      this.component = component;
      this.saveDirectoryData = saveDirectoryData;
      this.xSize = 308;
      this.ySize = 219;
      ((ContainerEditLootTableItems)this.inventorySlots).inventory.addWatcher(this);
   }

   public void initGui() {
      super.initGui();
      Keyboard.enableRepeatEvents(true);
      this.buttonList.clear();
      this.weightSliders.clear();
      this.minMaxSliders.clear();
      int leftEdge = this.width / 2 - this.xSize / 2;
      int topEdge = this.height / 2 - this.ySize / 2;
      int shiftRightPage = leftEdge + 288;
      this.buttonList.add(this.backBtn = new GuiButton(0, leftEdge, this.height / 2 + 90, this.xSize / 3 - 1, 20, IvTranslations.get("gui.back")));
      GuiButton saveButton = new GuiButton(1, leftEdge + this.xSize / 3 + 1, this.height / 2 + 90, this.xSize / 3 - 2, 20, TextFormatting.GREEN + IvTranslations.get("reccomplex.gui.save"));
      saveButton.enabled = false;
      this.buttonList.add(saveButton);
      GuiButton hideButton = new GuiButton(2, leftEdge + this.xSize / 3 * 2 + 1, this.height / 2 + 90, this.xSize / 3 - 1, 20, IvTranslations.get("reccomplex.gui.hidegui"));
      hideButton.enabled = false;
      this.buttonList.add(hideButton);
      this.buttonList.add(this.nextPageBtn = new GuiButton(10, shiftRightPage, this.height / 2 - 80, 20, 20, ">"));
      this.buttonList.add(this.prevPageBtn = new GuiButton(11, shiftRightPage, this.height / 2 - 50, 20, 20, "<"));

      for(int col = 0; col < 1; ++col) {
         for(int row = 0; row < 4; ++row) {
            int availableSize = 262;
            int baseX = leftEdge + 20 + col * 288;
            int onePart = availableSize / 5;
            GuiSliderRange minMaxSlider = new GuiSliderRange(100, baseX, topEdge + 18 + row * 18, onePart * 2 - 2, 18, "");
            minMaxSlider.addListener((slider) -> {
               List<GenericLootTable.RandomizedItemStack> chestContents = this.component.items;
               if (slider.id < 300) {
                  int stackIndex = slider.id - 200;
                  if (stackIndex < chestContents.size()) {
                     GenericLootTable.RandomizedItemStack chestContent = (GenericLootTable.RandomizedItemStack)chestContents.get(stackIndex);
                     IntegerRange intRange = Ranges.roundedIntRange(minMaxSlider.getRange());
                     chestContent.min = intRange.getMin();
                     chestContent.max = intRange.getMax();
                     minMaxSlider.setRange(new FloatRange((float)chestContent.min, (float)chestContent.max));
                     this.updateAllItemSliders();
                  }
               }

            });
            minMaxSlider.setMinValue(1.0F);
            this.buttonList.add(minMaxSlider);
            this.minMaxSliders.add(minMaxSlider);
            GuiSlider weightSlider = new GuiSlider(200, baseX + onePart * 2, topEdge + 18 + row * 18, onePart * 3, 18, IvTranslations.get("reccomplex.gui.random.weight"));
            weightSlider.addListener((slider) -> {
               List<GenericLootTable.RandomizedItemStack> chestContents = this.component.items;
               if (slider.id < 200 && slider.id >= 100) {
                  int stackIndex = slider.id - 100;
                  if (stackIndex < chestContents.size()) {
                     ((GenericLootTable.RandomizedItemStack)chestContents.get(stackIndex)).weight = (double)WEIGHT_SCALE.in(slider.getValue());
                     this.updateAllItemSliders();
                  }
               }

            });
            weightSlider.setMinValue(WEIGHT_SCALE.out(0.0F));
            weightSlider.setMaxValue(WEIGHT_SCALE.out(100.0F));
            this.buttonList.add(weightSlider);
            this.weightSliders.add(weightSlider);
         }
      }

      this.setPage(this.currentPage);
   }

   public void setPage(int colShift) {
      this.currentPage = colShift;
      ((ContainerEditLootTableItems)this.inventorySlots).scrollTo(colShift);
      this.updateAllItemSliders();
      this.updatePageButtons();
   }

   private void updateAllItemSliders() {
      List<GenericLootTable.RandomizedItemStack> chestContents = this.component.items;

      for(int i = 0; i < this.weightSliders.size(); ++i) {
         GuiSlider weightSlider = (GuiSlider)this.weightSliders.get(i);
         GuiSliderRange minMaxSlider = (GuiSliderRange)this.minMaxSliders.get(i);
         int index = i + this.currentPage * 4;
         weightSlider.id = index + 100;
         minMaxSlider.id = index + 200;
         if (index < chestContents.size()) {
            GenericLootTable.RandomizedItemStack chestContent = (GenericLootTable.RandomizedItemStack)chestContents.get(index);
            minMaxSlider.setRange(new FloatRange((float)chestContent.min, (float)chestContent.max));
            minMaxSlider.setMaxValue((float)chestContent.itemStack.getMaxStackSize());
            minMaxSlider.enabled = true;
            minMaxSlider.displayString = IvTranslations.format("reccomplex.gui.inventorygen.minMax", new Object[]{chestContent.min, chestContent.max});
            weightSlider.setValue(WEIGHT_SCALE.out((float)chestContent.weight));
            weightSlider.enabled = true;
            weightSlider.displayString = IvTranslations.format("reccomplex.gui.inventorygen.weightNumber", new Object[]{String.format("%.4f", WEIGHT_SCALE.in(weightSlider.getValue()))});
         } else {
            minMaxSlider.setRange(new FloatRange(1.0F, 1.0F));
            minMaxSlider.setMaxValue(64.0F);
            minMaxSlider.enabled = false;
            minMaxSlider.displayString = IvTranslations.get("reccomplex.gui.inventorygen.range");
            weightSlider.setValue(weightSlider.getMinValue());
            weightSlider.enabled = false;
            weightSlider.displayString = IvTranslations.format("reccomplex.gui.random.weight", new Object[0]);
         }
      }

   }

   private void updatePageButtons() {
      List<GenericLootTable.RandomizedItemStack> chestContents = this.component.items;
      int neededSlots = chestContents.size() + 1;
      this.nextPageBtn.enabled = (this.currentPage + 1) * 4 <= neededSlots;
      this.prevPageBtn.enabled = this.currentPage > 0;
   }

   protected void actionPerformed(GuiButton button) {
      if (button.enabled) {
         if (button.id == 0) {
            RCGuiHandler.editLootTableComponent(this.mc.player, this.key, this.component, this.saveDirectoryData);
         } else if (button.id == 2) {
            GuiHider.hideGUI();
         } else if (button.id == 10) {
            this.setPage(this.currentPage + 1);
            RecurrentComplex.network.sendToServer(PacketGuiAction.packetGuiAction("igSelectCol", new Number[]{this.currentPage}));
         } else if (button.id == 11) {
            this.setPage(this.currentPage - 1);
            RecurrentComplex.network.sendToServer(PacketGuiAction.packetGuiAction("igSelectCol", new Number[]{this.currentPage}));
         }
      }

   }

   protected void keyTyped(char keyChar, int keyCode) throws IOException {
      if (keyCode != this.mc.gameSettings.keyBindInventory.getKeyCode()) {
         if (keyCode == 1) {
            if (this.backBtn != null) {
               this.actionPerformed(this.backBtn);
            }

         } else {
            super.keyTyped(keyChar, keyCode);
            if (keyCode == 203 && this.prevPageBtn.enabled) {
               this.actionPerformed(this.prevPageBtn);
            } else if (keyCode == 205 && this.nextPageBtn.enabled) {
               this.actionPerformed(this.nextPageBtn);
            }

         }
      }
   }

   public void onGuiClosed() {
      super.onGuiClosed();
      Keyboard.enableRepeatEvents(false);
   }

   public boolean doesGuiPauseGame() {
      return true;
   }

   protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
      this.mc.getTextureManager().bindTexture(textureBackground);
      GlStateManager.color(1.0F, 1.0F, 1.0F);
      this.drawTexturedModalRect(this.width / 2 - 88 - 10 - 1, this.height / 2 - 13, 0, 0, 176, 90);

      for(int i = 0; i < 4; ++i) {
         this.drawTexturedModalRect(this.width / 2 - 144 - 11, this.height / 2 - 91 + i * 18, 7, 7, 18, 18);
      }

   }

   private void drawPlaceholderString(GuiTextField textField, String string) {
      if (StringUtils.isNullOrEmpty(textField.getText())) {
         this.drawString(this.fontRenderer, string, textField.x + 5, textField.y + 7, -7829368);
      }

   }

   public void inventoryChanged(IInventory inventory) {
      this.updateAllItemSliders();
      this.updatePageButtons();
   }

   protected void mouseReleased(int mouseX, int mouseY, int state) {
      super.mouseReleased(mouseX, mouseY, state);
      if (state == 0) {
         for(Object object : this.buttonList) {
            GuiButton button = (GuiButton)object;
            button.mouseReleased(mouseX, mouseY);
         }
      }

   }
}
