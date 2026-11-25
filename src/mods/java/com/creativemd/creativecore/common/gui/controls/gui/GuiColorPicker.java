package com.creativemd.creativecore.common.gui.controls.gui;

import com.creativemd.creativecore.CreativeCore;
import com.creativemd.creativecore.CreativeCoreConfig;
import com.creativemd.creativecore.common.config.holder.CreativeConfigRegistry;
import com.creativemd.creativecore.common.config.sync.ConfigurationChangePacket;
import com.creativemd.creativecore.common.gui.client.style.ColoredDisplayStyle;
import com.creativemd.creativecore.common.gui.client.style.DisplayStyle;
import com.creativemd.creativecore.common.gui.client.style.Style;
import com.creativemd.creativecore.common.gui.container.GuiParent;
import com.creativemd.creativecore.common.gui.event.gui.GuiControlChangedEvent;
import com.creativemd.creativecore.common.packet.PacketHandler;
import com.creativemd.creativecore.common.utils.mc.ColorUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.util.Color;

public class GuiColorPicker extends GuiParent {
   public GuiColorPicker.GuiColorPickerPalette palette;
   public Color color;
   private static final DisplayStyle SELECTED = new ColoredDisplayStyle(-256);

   public GuiColorPicker(String name, int x, int y, Color color, boolean hasAlpha, int alphaMin) {
      super(name, x, y, 129, hasAlpha ? 40 : 30);
      this.marginWidth = 0;
      this.color = color;
      this.setStyle(Style.emptyStyle);
      this.addControl(new GuiButtonHold("r-", "<", 0, 0, 1, 5) {
         public void onClicked(int x, int y, int button) {
            GuiColorPicker.this.onColorChanged();
            GuiColoredSteppedSlider slider = (GuiColoredSteppedSlider)GuiColorPicker.this.get("r");
            slider.setValue(slider.value - 1.0D);
         }
      });
      this.addControl(new GuiButtonHold("r+", ">", 98, 0, 1, 5) {
         public void onClicked(int x, int y, int button) {
            GuiColorPicker.this.onColorChanged();
            GuiColoredSteppedSlider slider = (GuiColoredSteppedSlider)GuiColorPicker.this.get("r");
            slider.setValue(slider.value + 1.0D);
         }
      });
      this.addControl(new GuiButtonHold("g-", "<", 0, 10, 1, 5) {
         public void onClicked(int x, int y, int button) {
            GuiColorPicker.this.onColorChanged();
            GuiColoredSteppedSlider slider = (GuiColoredSteppedSlider)GuiColorPicker.this.get("g");
            slider.setValue(slider.value - 1.0D);
         }
      });
      this.addControl(new GuiButtonHold("g+", ">", 98, 10, 1, 5) {
         public void onClicked(int x, int y, int button) {
            GuiColorPicker.this.onColorChanged();
            GuiColoredSteppedSlider slider = (GuiColoredSteppedSlider)GuiColorPicker.this.get("g");
            slider.setValue(slider.value + 1.0D);
         }
      });
      this.addControl(new GuiButtonHold("b-", "<", 0, 20, 1, 5) {
         public void onClicked(int x, int y, int button) {
            GuiColorPicker.this.onColorChanged();
            GuiColoredSteppedSlider slider = (GuiColoredSteppedSlider)GuiColorPicker.this.get("b");
            slider.setValue(slider.value - 1.0D);
         }
      });
      this.addControl(new GuiButtonHold("b+", ">", 98, 20, 1, 5) {
         public void onClicked(int x, int y, int button) {
            GuiColorPicker.this.onColorChanged();
            GuiColoredSteppedSlider slider = (GuiColoredSteppedSlider)GuiColorPicker.this.get("b");
            slider.setValue(slider.value + 1.0D);
         }
      });
      if (hasAlpha) {
         this.addControl(new GuiButtonHold("a-", "<", 0, 30, 1, 5) {
            public void onClicked(int x, int y, int button) {
               GuiColorPicker.this.onColorChanged();
               GuiColoredSteppedSlider slider = (GuiColoredSteppedSlider)GuiColorPicker.this.get("a");
               slider.setValue(slider.value - 1.0D);
            }
         });
         this.addControl(new GuiButtonHold("a+", ">", 98, 30, 1, 5) {
            public void onClicked(int x, int y, int button) {
               GuiColorPicker.this.onColorChanged();
               GuiColoredSteppedSlider slider = (GuiColoredSteppedSlider)GuiColorPicker.this.get("a");
               slider.setValue(slider.value + 1.0D);
            }
         });
      } else {
         color.setAlpha(255);
      }

      this.addControl((new GuiColoredSteppedSlider("r", 8, 0, 84, 5, this, ColorUtils.ColorPart.RED)).setStyle(defaultStyle));
      this.addControl((new GuiColoredSteppedSlider("g", 8, 10, 84, 5, this, ColorUtils.ColorPart.GREEN)).setStyle(defaultStyle));
      this.addControl((new GuiColoredSteppedSlider("b", 8, 20, 84, 5, this, ColorUtils.ColorPart.BLUE)).setStyle(defaultStyle));
      if (hasAlpha) {
         GuiColoredSteppedSlider alpha = new GuiColoredSteppedSlider("a", 8, 30, 84, 5, this, ColorUtils.ColorPart.ALPHA);
         alpha.minValue = (double)alphaMin;
         this.addControl(alpha.setStyle(defaultStyle));
      }

      this.addControl((new GuiColorPlate("plate", 107, 2, 20, 20, color)).setStyle(defaultStyle));
      this.addControl(new GuiButton("more", 105, 28) {
         public void onClicked(int x, int y, int button) {
            if (GuiColorPicker.this.palette != null) {
               GuiColorPicker.this.closePalette();
            } else {
               GuiColorPicker.this.openPalette();
            }

         }
      });
   }

   public void setColor(Color color) {
      this.color.setColor(color);
      ((GuiColoredSteppedSlider)this.get("r")).value = (double)color.getRed();
      ((GuiColoredSteppedSlider)this.get("g")).value = (double)color.getGreen();
      ((GuiColoredSteppedSlider)this.get("b")).value = (double)color.getBlue();
      if (this.has("a")) {
         ((GuiColoredSteppedSlider)this.get("a")).value = (double)color.getAlpha();
      }

   }

   public void onColorChanged() {
      if (this.palette != null) {
         this.palette.onChanged();
      }

      this.raiseEvent(new GuiControlChangedEvent(this));
   }

   public void openPalette() {
      this.palette = new GuiColorPicker.GuiColorPickerPalette(this.name + "palette", this, this.posX, this.posY + this.height, this.width - this.getContentOffset() * 2, 100);
      this.getGui().controls.add(this.palette);
      this.palette.parent = this.getGui();
      this.palette.moveControlToTop();
      this.palette.onOpened();
      this.getGui().refreshControls();
      this.palette.rotation = this.rotation;
      this.palette.posX = this.getPixelOffsetX() - this.getGui().getPixelOffsetX() - this.getContentOffset();
      this.palette.posY = this.getPixelOffsetY() - this.getGui().getPixelOffsetY() - this.getContentOffset() + this.height;
      if (this.palette.posY + this.palette.height > this.getParent().height && this.posY >= this.palette.height) {
         this.palette.posY -= this.height + this.palette.height;
      }

   }

   public void closePalette() {
      if (this.palette != null) {
         this.palette.savePalette();
         this.getGui().controls.remove(this.palette);
         this.removeListener(this.palette);
         this.palette = null;
      }

   }

   public static class GuiColorPickerPalette extends GuiParent {
      public final GuiColorPicker picker;
      public GuiColorPicker.GuiColorPickerPaletteEntry selected;
      public GuiButton add;
      public List<GuiColorPicker.GuiColorPickerPaletteEntry> entries;

      public GuiColorPickerPalette(String name, final GuiColorPicker picker, int x, int y, int width, int height) {
         super(name, x, y, width, height);
         this.picker = picker;
         List<String> lines = new ArrayList();
         CreativeCore.configHandler.loadClientFields();

         for(CreativeCoreConfig.ColorPalette palette : CreativeCore.config.palette) {
            lines.add(palette.name);
         }

         this.addControl(new GuiComboBox("palette", 0, 0, width - this.getContentOffset() * 2, lines));
         this.add = new GuiButton("+", 0, 0, 10, 10) {
            public void onClicked(int x, int y, int button) {
               GuiColorPicker.GuiColorPickerPaletteEntry entry = new GuiColorPicker.GuiColorPickerPaletteEntry(GuiColorPickerPalette.this, "" + GuiColorPickerPalette.this.entries.size(), 0, 0, 10, 10, new Color(picker.color));
               GuiColorPickerPalette.this.entries.add(entry);
               GuiColorPickerPalette.this.addControl(entry);
               GuiColorPickerPalette.this.refreshEntries();
            }
         };
         this.addControl(this.add);
         this.addListener(this);
         this.loadPalette();
      }

      public void comboChanged(GuiControlChangedEvent event) {
         if (event.source.is("palette")) {
            this.loadPalette();
         }

      }

      public void onClosed() {
         super.onClosed();
         this.savePalette();
      }

      public void loadPalette() {
         GuiComboBox box = (GuiComboBox)this.get("palette");
         this.removeControls(new String[]{"palette", "+"});
         this.entries = new ArrayList();
         if (box.index != -1) {
            CreativeCoreConfig.ColorPalette palette = (CreativeCoreConfig.ColorPalette)CreativeCore.config.palette.get(box.index);

            for(int i = 0; i < palette.colors.size(); ++i) {
               GuiColorPicker.GuiColorPickerPaletteEntry entry = new GuiColorPicker.GuiColorPickerPaletteEntry(this, "" + i, 0, 0, 10, 10, ColorUtils.IntToRGBA(((Integer)palette.colors.get(i)).intValue()));
               this.entries.add(entry);
               this.addControl(entry);
            }
         }

         this.refreshEntries();
      }

      public void savePalette() {
         GuiComboBox box = (GuiComboBox)this.get("palette");
         if (box.index != -1) {
            CreativeCoreConfig.ColorPalette palette = (CreativeCoreConfig.ColorPalette)CreativeCore.config.palette.get(box.index);
            palette.colors = new ArrayList();

            for(GuiColorPicker.GuiColorPickerPaletteEntry entry : this.entries) {
               palette.colors.add(Integer.valueOf(ColorUtils.RGBAToInt(entry.getColor())));
            }

            String[] path = new String[]{"creativecore", "palette"};
            if (CreativeCore.configHandler.isSynchronizedWithServer("creativecore.palette")) {
               PacketHandler.sendPacketToServer(new ConfigurationChangePacket(path, CreativeConfigRegistry.ROOT.followPath(path).save(true, false, Side.SERVER)));
            } else {
               CreativeCore.configHandler.save(Side.CLIENT);
            }
         }

      }

      public void refreshEntries() {
         int offsetY = 21;
         int size = 18;
         int perRow = (this.width - 5) / size;

         for(int i = 0; i < this.entries.size(); ++i) {
            int row = i / perRow;
            int col = i % perRow;
            GuiColorPicker.GuiColorPickerPaletteEntry entry = (GuiColorPicker.GuiColorPickerPaletteEntry)this.entries.get(i);
            entry.posX = col * size;
            entry.posY = offsetY + row * size;
         }

         int row = this.entries.size() / perRow;
         int col = this.entries.size() % perRow;
         this.add.posX = col * size;
         this.add.posY = offsetY + row * size;
      }

      public void onLoseFocus() {
         if (!this.picker.isMouseOver() && !this.isMouseOver()) {
            this.picker.closePalette();
         }

      }

      public void onChanged() {
         if (this.selected != null) {
            this.selected.setColor(new Color(this.picker.color));
         }

      }

      public boolean canOverlap() {
         return true;
      }
   }

   public static class GuiColorPickerPaletteEntry extends GuiColorPlate {
      public final GuiColorPicker.GuiColorPickerPalette palette;

      public GuiColorPickerPaletteEntry(GuiColorPicker.GuiColorPickerPalette palette, String name, int x, int y, int width, int height, Color color) {
         super(name, x, y, width, height, color);
         this.palette = palette;
      }

      public DisplayStyle getBorderDisplay(DisplayStyle display) {
         return this.palette.selected == this ? GuiColorPicker.SELECTED : super.getBorderDisplay(display);
      }

      public boolean mousePressed(int x, int y, int button) {
         if (button == 1) {
            playSound(SoundEvents.UI_BUTTON_CLICK);
            this.palette.entries.remove(this);
            this.palette.removeControl(this);
            if (this.palette.selected == this) {
               this.palette.selected = null;
            }

            this.palette.refreshEntries();
            return true;
         } else {
            this.palette.selected = this;
            this.palette.picker.setColor(this.getColor());
            playSound(SoundEvents.UI_BUTTON_CLICK);
            return true;
         }
      }

      public void onLoseFocus() {
         if (this.palette.selected == this) {
            this.palette.selected = null;
         }

      }
   }
}
