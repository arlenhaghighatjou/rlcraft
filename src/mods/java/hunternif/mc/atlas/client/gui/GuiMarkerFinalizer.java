package hunternif.mc.atlas.client.gui;

import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.client.gui.core.GuiComponent;
import hunternif.mc.atlas.client.gui.core.GuiScrollingContainer;
import hunternif.mc.atlas.client.gui.core.ISelectListener;
import hunternif.mc.atlas.client.gui.core.ToggleGroup;
import hunternif.mc.atlas.registry.MarkerRegistry;
import hunternif.mc.atlas.registry.MarkerType;
import hunternif.mc.atlas.registry.MarkerTypes;
import hunternif.mc.atlas.util.Log;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.World;

public class GuiMarkerFinalizer extends GuiComponent {
   private static final MarkerType defaultMarker = MarkerTypes.RED_X_SMALL;
   private World world;
   private int atlasID;
   private int dimension;
   private int x;
   private int z;
   MarkerType selectedType;
   private static final int BUTTON_WIDTH = 100;
   private static final int BUTTON_SPACING = 4;
   private static final int TYPE_SPACING = 1;
   private static final int TYPE_BG_FRAME = 4;
   private GuiButton btnDone;
   private GuiButton btnCancel;
   private GuiTextField textField;
   private final GuiScrollingContainer scroller;
   private ToggleGroup<GuiMarkerInList> typeRadioGroup;
   private final List<GuiMarkerFinalizer.IMarkerTypeSelectListener> listeners;

   GuiMarkerFinalizer() {
      this.selectedType = defaultMarker;
      this.listeners = new ArrayList();
      this.scroller = new GuiScrollingContainer();
      this.scroller.setWheelScrollsHorizontally();
      this.addChild(this.scroller);
   }

   void setMarkerData(World world, int atlasID, int dimension, int markerX, int markerZ) {
      this.world = world;
      this.atlasID = atlasID;
      this.dimension = dimension;
      this.x = markerX;
      this.z = markerZ;
      this.setBlocksScreen(true);
   }

   void addListener(GuiMarkerFinalizer.IMarkerTypeSelectListener listener) {
      this.listeners.add(listener);
   }

   void removeListener(GuiMarkerFinalizer.IMarkerTypeSelectListener listener) {
      this.listeners.remove(listener);
   }

   void removeAllListeners() {
      this.listeners.clear();
   }

   public void initGui() {
      this.buttonList.add(this.btnDone = new GuiButton(0, this.width / 2 - 100 - 2, this.height / 2 + 40, 100, 20, I18n.format("gui.done", new Object[0])));
      this.buttonList.add(this.btnCancel = new GuiButton(0, this.width / 2 + 2, this.height / 2 + 40, 100, 20, I18n.format("gui.cancel", new Object[0])));
      int var10005 = (this.width - 200) / 2;
      this.textField = new GuiTextField(0, Minecraft.getMinecraft().fontRenderer, var10005, this.height / 2 - 81, 200, 20);
      this.textField.setFocused(true);
      this.textField.setText("");
      this.scroller.removeAllContent();
      int typeCount = 0;

      for(MarkerType type : MarkerRegistry.getValues()) {
         if (!type.isTechnical()) {
            ++typeCount;
         }
      }

      int allTypesWidth = typeCount * 35 - 1;
      int scrollerWidth = Math.min(allTypesWidth, 240);
      this.scroller.setViewportSize(scrollerWidth, 34);
      this.scroller.setGuiCoords((this.width - scrollerWidth) / 2, this.height / 2 - 25);
      this.typeRadioGroup = new ToggleGroup<GuiMarkerInList>();
      this.typeRadioGroup.addListener((button) -> {
         this.selectedType = button.getMarkerType();

         for(GuiMarkerFinalizer.IMarkerTypeSelectListener listener : this.listeners) {
            listener.onSelectMarkerType(button.getMarkerType());
         }

      });
      int contentX = 0;

      for(MarkerType markerType : MarkerRegistry.getValues()) {
         if (!markerType.isTechnical()) {
            GuiMarkerInList markerGui = new GuiMarkerInList(markerType);
            this.typeRadioGroup.addButton(markerGui);
            if (this.selectedType.equals(markerType)) {
               this.typeRadioGroup.setSelectedButton(markerGui);
            }

            this.scroller.addContent(markerGui).setRelativeX(contentX);
            contentX += 35;
         }
      }

   }

   protected void mouseClicked(int par1, int par2, int par3) throws IOException {
      super.mouseClicked(par1, par2, par3);
      this.textField.mouseClicked(par1, par2, par3);
   }

   protected void keyTyped(char par1, int par2) throws IOException {
      super.keyTyped(par1, par2);
      this.textField.textboxKeyTyped(par1, par2);
   }

   protected void actionPerformed(GuiButton button) {
      if (button == this.btnDone) {
         AtlasAPI.markers.putMarker(this.world, true, this.atlasID, this.selectedType.getRegistryName().toString(), this.textField.getText(), this.x, this.z);
         Log.info("Put marker in Atlas #%d \"%s\" at (%d, %d)", this.atlasID, this.textField.getText(), this.x, this.z);
         this.close();
      } else if (button == this.btnCancel) {
         this.close();
      }

   }

   public void drawScreen(int mouseX, int mouseY, float partialTick) {
      this.drawDefaultBackground();
      this.drawCenteredString(I18n.format("gui.antiqueatlas.marker.label", new Object[0]), this.height / 2 - 97, 16777215, true);
      this.textField.drawTextBox();
      this.drawCenteredString(I18n.format("gui.antiqueatlas.marker.type", new Object[0]), this.height / 2 - 44, 16777215, true);
      this.drawGradientRect(this.scroller.getGuiX() - 4, this.scroller.getGuiY() - 4, this.scroller.getGuiX() + this.scroller.getWidth() + 4, this.scroller.getGuiY() + this.scroller.getHeight() + 4, -2012213232, -1727000560);
      super.drawScreen(mouseX, mouseY, partialTick);
   }

   interface IMarkerTypeSelectListener {
      void onSelectMarkerType(MarkerType var1);
   }
}
