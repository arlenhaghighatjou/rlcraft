package net.ilexiconn.llibrary.server.config;

import java.awt.Color;
import java.io.File;
import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.gui.element.color.ColorMode;
import net.ilexiconn.llibrary.server.nbt.NBTHandler;
import net.ilexiconn.llibrary.server.nbt.NBTMutatorProperty;
import net.ilexiconn.llibrary.server.nbt.NBTProperty;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public class LLibraryConfig implements INBTSerializable<NBTTagCompound> {
   @NBTProperty
   private int accentColor = -16547192;
   @NBTMutatorProperty(
      type = String.class
   )
   private ColorMode colorMode = ColorMode.LIGHT;
   @NBTProperty
   private boolean patreonEffects = true;
   @NBTProperty
   private boolean versionCheck = false;
   @NBTProperty
   private boolean tabsAlwaysVisible = false;
   @NBTProperty
   private boolean tabsLeftSide = true;

   public LLibraryConfig() {
   }

   public int getPrimaryColor() {
      return this.colorMode.getPrimaryColor();
   }

   public int getSecondaryColor() {
      return this.colorMode.getSecondaryColor();
   }

   public int getTertiaryColor() {
      return this.colorMode.getTertiaryColor();
   }

   public int getPrimarySubcolor() {
      return this.colorMode.getPrimarySubcolor();
   }

   public int getSecondarySubcolor() {
      return this.colorMode.getSecondarySubcolor();
   }

   public int getTextColor() {
      return this.colorMode.getTextColor();
   }

   public int getInvertedTextColor() {
      return this.colorMode.getInvertedTextColor();
   }

   public int getAccentColor() {
      return this.accentColor;
   }

   public void setAccentColor(int accentColor) {
      this.accentColor = accentColor;
   }

   public int getDarkAccentColor() {
      int r = this.accentColor >> 16 & 255;
      int g = this.accentColor >> 8 & 255;
      int b = this.accentColor & 255;
      float[] hsb = Color.RGBtoHSB(r, g, b, (float[])null);
      Color newColor = Color.getHSBColor(hsb[0], hsb[1], hsb[2] * 0.85F);
      return newColor.getRGB() | -16777216;
   }

   public String getColorMode() {
      return this.colorMode.getName();
   }

   public void setColorMode(String colorMode) {
      this.colorMode = ColorMode.getColorMode(colorMode);
   }

   public boolean hasPatreonEffects() {
      return this.patreonEffects;
   }

   public void setPatreonEffects(boolean patreonEffects) {
      this.patreonEffects = patreonEffects;
   }

   public boolean hasVersionCheck() {
      return this.versionCheck;
   }

   public void setVersionCheck(boolean versionCheck) {
      this.versionCheck = versionCheck;
   }

   public boolean areTabsAlwaysVisible() {
      return this.tabsAlwaysVisible;
   }

   public void setTabsAlwaysVisible(boolean tabsAlwaysVisible) {
      this.tabsAlwaysVisible = tabsAlwaysVisible;
   }

   public boolean areTabsLeftSide() {
      return this.tabsLeftSide;
   }

   public void setTabsLeftSide(boolean tabsLeftSide) {
      this.tabsLeftSide = tabsLeftSide;
   }

   public NBTTagCompound serializeNBT() {
      NBTTagCompound compound = new NBTTagCompound();
      NBTHandler.INSTANCE.saveNBTData(this, compound);
      return compound;
   }

   public void deserializeNBT(NBTTagCompound compound) {
      NBTHandler.INSTANCE.loadNBTData(this, compound);
   }

   public void load() {
      if (!LLibrary.LLIBRARY_ROOT.exists()) {
         LLibrary.LLIBRARY_ROOT.mkdirs();
      }

      File file = new File(LLibrary.LLIBRARY_ROOT, "config.dat");

      try {
         if (!file.exists() && file.createNewFile()) {
            this.save();
         }

         this.deserializeNBT(CompressedStreamTools.read(file));
      } catch (Exception var3) {
         LLibrary.LOGGER.error("Failed to load config", var3);
      }

   }

   public void save() {
      if (!LLibrary.LLIBRARY_ROOT.exists()) {
         LLibrary.LLIBRARY_ROOT.mkdirs();
      }

      File file = new File(LLibrary.LLIBRARY_ROOT, "config.dat");

      try {
         CompressedStreamTools.write(this.serializeNBT(), file);
      } catch (Exception var3) {
         LLibrary.LOGGER.error("Failed to save config", var3);
      }

   }
}
