package org.orecruncher.dsurround.client.gui;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.ConfigGuiType;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.client.config.HoverChecker;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.config.GuiConfigEntries.IConfigEntry;
import net.minecraftforge.fml.client.config.GuiConfigEntries.NumberSliderEntry;
import net.minecraftforge.fml.client.config.GuiEditArrayEntries.IArrayEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;
import org.orecruncher.dsurround.registry.RegistryManager;
import org.orecruncher.dsurround.registry.sound.SoundMetadata;
import org.orecruncher.lib.ForgeUtils;

@SideOnly(Side.CLIENT)
public class SoundConfigEntry extends NumberSliderEntry {
   private static final int ID_CULLED = 10000;
   private static final int ID_BLOCKED = 10001;
   private static final int ID_PLAY = 10002;
   private final CheckBoxButton cull;
   private final CheckBoxButton block;
   private final PlaySoundButton play;
   private final HoverChecker cullHover;
   private final HoverChecker blockHover;
   private final HoverChecker playHover;
   private final HoverChecker sliderHover;
   private final IConfigElement realConfig;

   public SoundConfigEntry(@Nonnull GuiConfig owningScreen, @Nonnull GuiConfigEntries owningEntryList, @Nonnull IConfigElement configElement) {
      super(owningScreen, owningEntryList, new SoundConfigEntry.ConfigElementSliderAdapter(configElement));
      String soundName = configElement.getName();
      String parms = (String)configElement.get();
      boolean culled = parms.contains("cull");
      boolean blocked = parms.contains("block");
      this.cull = new CheckBoxButton(10000, GuiConstants.TEXT_CULL, culled, false);
      this.block = new CheckBoxButton(10001, GuiConstants.TEXT_BLOCK, blocked, false);
      this.play = new PlaySoundButton(10002, soundName);
      this.cullHover = new HoverChecker(this.cull, 800);
      this.blockHover = new HoverChecker(this.block, 800);
      this.playHover = new HoverChecker(this.play, 800);
      this.sliderHover = new HoverChecker((GuiButton)null, 800);
      List<String> text = Lists.newArrayList();
      ResourceLocation soundResource = new ResourceLocation(soundName);
      String modName = ForgeUtils.getModName(soundResource);
      if ("UNKNOWN".equals(modName)) {
         modName = soundResource.getResourceDomain();
      }

      text.add(TextFormatting.GREEN + modName);
      text.add(TextFormatting.GOLD + soundName);
      SoundMetadata data = RegistryManager.SOUND.getSoundMetadata(soundResource);
      if (data != null) {
         boolean spaceAdded = false;
         String title = data.getTitle();
         if (!StringUtils.isEmpty(title)) {
            spaceAdded = true;
            text.add("");
            text.add(TextFormatting.YELLOW + title);
         }

         List<String> credits = data.getCredits();
         if (credits != null && credits.size() > 0) {
            if (!spaceAdded) {
               text.add("");
            }

            text.addAll(credits);
         }
      }

      this.toolTip = ImmutableList.copyOf(text);
      this.realConfig = configElement;
   }

   public void drawToolTip(int mouseX, int mouseY) {
      super.drawToolTip(mouseX, mouseY);
      boolean canHover = mouseY < this.owningScreen.entryList.bottom && mouseY > this.owningScreen.entryList.top;
      if (this.cullHover.checkHover(mouseX, mouseY, canHover)) {
         this.owningScreen.drawToolTip(this.toolTip, mouseX, mouseY);
      }

      if (this.blockHover.checkHover(mouseX, mouseY, canHover)) {
         this.owningScreen.drawToolTip(this.toolTip, mouseX, mouseY);
      }

      if (this.playHover.checkHover(mouseX, mouseY, canHover)) {
         this.owningScreen.drawToolTip(this.toolTip, mouseX, mouseY);
      }

   }

   public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partial) {
      this.owningEntryList.controlWidth -= 205;
      super.drawEntry(slotIndex, x, y, listWidth, slotHeight, mouseX, mouseY, isSelected, partial);
      this.owningEntryList.controlWidth += 205;
      this.sliderHover.updateBounds(y, y + slotHeight, x, this.owningEntryList.scrollBarX - 196);
      int buttonWidth = 68;
      this.play.x = this.owningEntryList.scrollBarX - 115;
      this.play.y = y;
      this.play.enabled = this.enabled();
      this.play.drawButton(this.mc, mouseX, mouseY, partial);
      this.cull.x = this.owningEntryList.scrollBarX - 115 - 68;
      this.cull.y = y;
      this.cull.enabled = this.enabled();
      this.cull.drawButton(this.mc, mouseX, mouseY, partial);
      this.block.x = this.owningEntryList.scrollBarX - 115 - 136;
      this.block.y = y;
      this.block.enabled = this.enabled();
      this.block.drawButton(this.mc, mouseX, mouseY, partial);
   }

   public boolean isDefault() {
      return super.isDefault() && this.cull.isDefault() && this.block.isDefault();
   }

   public boolean isChanged() {
      return super.isChanged() || this.cull.isChanged() || this.block.isChanged();
   }

   public void undoChanges() {
      super.undoChanges();
      this.cull.undoChanges();
      this.block.undoChanges();
   }

   public void setToDefault() {
      super.setToDefault();
      this.cull.setToDefault();
      this.block.setToDefault();
   }

   public boolean mousePressed(int index, int x, int y, int mouseEvent, int relativeX, int relativeY) {
      if (this.cull.mousePressed(this.mc, x, y)) {
         this.cull.toggleState();
      } else if (this.block.mousePressed(this.mc, x, y)) {
         this.block.toggleState();
      } else {
         if (!this.play.mousePressed(this.mc, x, y)) {
            return super.mousePressed(index, x, y, mouseEvent, relativeX, relativeY);
         }

         float volume = (float)((Integer)this.getCurrentValue()).intValue() / 100.0F;
         this.play.playSound(this.mc, volume);
      }

      return true;
   }

   public void onGuiClosed() {
      this.play.stopSound();
   }

   public boolean saveConfigElement() {
      if (this.enabled() && this.isChanged()) {
         StringBuilder builder = new StringBuilder();
         if (this.cull.getValue()) {
            builder.append("cull").append(' ');
         }

         if (this.block.getValue()) {
            builder.append("block").append(' ');
         }

         int volume = ((GuiSlider)this.btnValue).getValueInt();
         if (volume != 100) {
            builder.append(volume);
         }

         this.realConfig.set(builder.toString().trim());
         return this.realConfig.requiresMcRestart();
      } else {
         return false;
      }
   }

   private static class ConfigElementSliderAdapter implements IConfigElement {
      private final IConfigElement element;
      private int initialValue;

      public ConfigElementSliderAdapter(@Nonnull IConfigElement element) {
         this.element = element;
         String n = ((String)element.get()).replace("cull", "").replace("block", "").trim();
         if (StringUtils.isEmpty(n)) {
            this.initialValue = 100;
         } else {
            this.initialValue = Integer.parseInt(n);
         }

      }

      public boolean isProperty() {
         return true;
      }

      public Class<? extends IConfigEntry> getConfigEntryClass() {
         return this.element.getConfigEntryClass();
      }

      public Class<? extends IArrayEntry> getArrayEntryClass() {
         return this.element.getArrayEntryClass();
      }

      public String getName() {
         return this.element.getName();
      }

      public String getQualifiedName() {
         return this.element.getQualifiedName();
      }

      public String getLanguageKey() {
         return this.element.getQualifiedName();
      }

      public String getComment() {
         return this.element.getQualifiedName();
      }

      public List<IConfigElement> getChildElements() {
         return this.element.getChildElements();
      }

      public ConfigGuiType getType() {
         return ConfigGuiType.INTEGER;
      }

      public boolean isList() {
         return false;
      }

      public boolean isListLengthFixed() {
         return true;
      }

      public int getMaxListLength() {
         return this.element.getMaxListLength();
      }

      public boolean isDefault() {
         return this.initialValue == 100;
      }

      public Object getDefault() {
         return Integer.valueOf(100);
      }

      public Object[] getDefaults() {
         return new Object[]{Integer.valueOf(100)};
      }

      public void setToDefault() {
         this.element.setToDefault();
      }

      public boolean requiresWorldRestart() {
         return false;
      }

      public boolean showInGui() {
         return this.element.showInGui();
      }

      public boolean requiresMcRestart() {
         return false;
      }

      public Object get() {
         return this.initialValue;
      }

      public Object[] getList() {
         return new Object[]{this.initialValue};
      }

      public void set(Object value) {
         this.initialValue = Integer.parseInt((String)value);
      }

      public void set(Object[] aVal) {
         this.set(aVal[0]);
      }

      public String[] getValidValues() {
         return this.element.getValidValues();
      }

      public Object getMinValue() {
         return Integer.valueOf(0);
      }

      public Object getMaxValue() {
         return Integer.valueOf(400);
      }

      public Pattern getValidationPattern() {
         return this.element.getValidationPattern();
      }
   }
}
