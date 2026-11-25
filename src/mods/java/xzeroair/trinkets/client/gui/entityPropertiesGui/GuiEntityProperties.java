package xzeroair.trinkets.client.gui.entityPropertiesGui;

import java.io.IOException;
import java.util.function.BiConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.capabilities.race.EntityProperties;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.helpers.ColorHelper;
import xzeroair.trinkets.util.helpers.DrawingHelper;

@SideOnly(Side.CLIENT)
public class GuiEntityProperties extends GuiScreen {
   public EntityPlayer player;
   public EntityProperties properties;
   public GuiTextField colorField;
   public GuiTextField colorField2;
   protected GuiPropertiesSlider r;
   protected GuiPropertiesSlider g;
   protected GuiPropertiesSlider b;
   protected GuiPropertiesSlider a;
   protected GuiPropertiesSlider r2;
   protected GuiPropertiesSlider g2;
   protected GuiPropertiesSlider b2;
   protected GuiPropertiesSlider a2;
   protected GuiPropertiesSlider variant;
   public int[] color;
   public int buttonPressed;
   public int manaBar = 1;
   public int abilityMenu = 2;
   public int manaOrientation = 3;
   public int manaBarAlways = 19;
   public int toggleTrait = 4;
   public int colorFieldID = 5;
   public int flipPlayer = 6;
   public int resetColor = 7;
   public int redSlider = 8;
   public int greenSlider = 9;
   public int blueSlider = 10;
   public int closeGui = 11;
   public int traitX = 12;
   public int traitY = 13;
   public int traitZ = 14;
   public int traitWidth = 15;
   public int redSlider2 = 16;
   public int greenSlider2 = 17;
   public int blueSlider2 = 18;
   public int colorFieldID2 = 19;
   public int resetColor2 = 20;
   public int variantSlider = 21;
   protected boolean flip = false;
   public static ResourceLocation background = null;
   private float oldMouseX;
   private float oldMouseY;

   public GuiEntityProperties(EntityPlayer player) {
      this.player = player;
      this.properties = Capabilities.getEntityProperties(player);
   }

   public void updateScreen() {
      super.updateScreen();
      this.colorField.updateCursorCounter();
   }

   public void initGui() {
      this.buttonList.clear();
      super.initGui();
      this.addButton(new GuiPropertiesButton(this.manaBar, 2, this.height - 22, 60, 20, "Mana Bar", (button, pressed) -> {
         this.mc.player.openGui(Trinkets.instance, 1, this.mc.player.world, 0, 0, 0);
      }));
      this.addButton(new GuiPropertiesButton(this.abilityMenu, 2, 2, 50, 20, "Stats", (button, pressed) -> {
         this.mc.player.openGui(Trinkets.instance, 3, this.mc.player.world, 0, 0, 0);
      }));
      this.addButton(new GuiPropertiesButton(this.manaOrientation, 64, this.height - 22, 14, 20, (button, pressed) -> {
         TrinketsConfig.CLIENT.MPBar.mana_horizontal = !TrinketsConfig.CLIENT.MPBar.mana_horizontal;
         ConfigManager.sync("xat", Type.INSTANCE);
      }, (button, renderPre) -> {
         button.displayString = TrinketsConfig.CLIENT.MPBar.mana_horizontal ? "H" : "V";
      }));
      this.addButton(new GuiPropertiesButton(this.manaBarAlways, 80, this.height - 22, 14, 20, (button, pressed) -> {
         TrinketsConfig.CLIENT.MPBar.always_shown = !TrinketsConfig.CLIENT.MPBar.always_shown;
         ConfigManager.sync("xat", Type.INSTANCE);
      }, (button, renderPre) -> {
         button.displayString = TrinketsConfig.CLIENT.MPBar.always_shown ? "A" : "^";
      }));
      this.addButton(new GuiPropertiesButton(this.toggleTrait, 2, 40, 60, 20, (button, pressed) -> {
         this.properties.getRaceHandler().setShowTraits(!this.properties.getRaceHandler().showTraits());
      }, (button, renderPre) -> {
         button.displayString = "" + this.properties.getRaceHandler().showTraits();
      }));
      this.addButton(new GuiPropertiesButton(this.flipPlayer, this.width / 2 - 30, 0, 60, 20, "Flip", (button, pressed) -> {
         this.flip = !this.flip;
      }));
      int bX = this.width - this.width / 4;
      int bY = this.height - this.height / 2;
      bX = bX - 30;
      bY = bY - (this.height / 4 + 34);
      this.variant = new GuiPropertiesSlider(this.variantSlider, bX, bY, 100, 20, "Variant", (float)this.properties.getRaceHandler().getTraitVariant() / 255.0F, 1.0F, 0.0F, (slider, wrapper) -> {
         this.properties.getRaceHandler().setTraitVariant((int)(slider.getSliderValue() * 510.0F / 255.0F));
         slider.displayString = "Variant: " + (int)(slider.getSliderValue() * 510.0F / 255.0F);
      });
      this.addButton(this.variant);
      bY = bY + 26;
      this.colorField = new GuiTextField(this.colorFieldID, this.fontRenderer, bX, bY, 100, 20);
      this.colorField.setMaxStringLength(8);
      String traitColor = this.properties == null ? "#ffffff" : this.properties.getRaceHandler().getTraitColor();
      float[] defaultRGB = ColorHelper.getRGBColor(traitColor);
      this.colorField.setText(traitColor);
      this.addButton(new GuiPropertiesButton(this.resetColor, bX + 102, bY - 1, 20, 20, "R", (button, pressed) -> {
         String defaultHex = ColorHelper.convertDecimalColorToHexadecimal(this.properties.getCurrentRace().getPrimaryColor());
         this.properties.getRaceHandler().setTraitColor(defaultHex);
         this.colorField.setText(defaultHex);
         this.colorField.setTextColor(this.properties.getCurrentRace().getPrimaryColor());
         float[] rgb = ColorHelper.getRGBColor(defaultHex);
         this.r.sliderValue = rgb[0];
         this.r.displayString = "Red: " + (int)(rgb[0] * 255.0F);
         this.g.sliderValue = rgb[1];
         this.g.displayString = "Green: " + (int)(rgb[1] * 255.0F);
         this.b.sliderValue = rgb[2];
         this.b.displayString = "Blue: " + (int)(rgb[2] * 255.0F);
      }));
      bY = bY + 21;
      this.r = new GuiPropertiesSlider(this.redSlider, bX, bY, 100, 20, "Red", defaultRGB[0], 1.0F, 0.0F, (slider, wrapper) -> {
         float[] rgb = ColorHelper.getRGBColor(this.properties.getRaceHandler().getTraitColor());
         String hex = ColorHelper.getHexColorFromRGB(slider.getSliderValue(), rgb[1], rgb[2]);
         int decimal = ColorHelper.convertHexadecimalToDecimal(hex);
         this.properties.getRaceHandler().setTraitColor(hex);
         slider.displayString = "Red: " + (int)(slider.sliderValue * slider.sliderMaxValue * 255.0F);
         this.colorField.setTextColor(decimal);
         this.colorField.setText(hex);
      });
      bY = bY + 20;
      this.g = new GuiPropertiesSlider(this.greenSlider, bX, bY, 100, 20, "Green", defaultRGB[1], 1.0F, 0.0F, (slider, wrapper) -> {
         float[] rgb = ColorHelper.getRGBColor(this.properties.getRaceHandler().getTraitColor());
         String hex = ColorHelper.getHexColorFromRGB(rgb[0], slider.getSliderValue(), rgb[2]);
         int decimal = ColorHelper.convertHexadecimalToDecimal(hex);
         this.properties.getRaceHandler().setTraitColor(hex);
         slider.displayString = "Green: " + (int)(slider.sliderValue * slider.sliderMaxValue * 255.0F);
         this.colorField.setTextColor(decimal);
         this.colorField.setText(hex);
      });
      bY = bY + 20;
      this.b = new GuiPropertiesSlider(this.blueSlider, bX, bY, 100, 20, "Blue", defaultRGB[2], 1.0F, 0.0F, (slider, wrapper) -> {
         float[] rgb = ColorHelper.getRGBColor(this.properties.getRaceHandler().getTraitColor());
         String hex = ColorHelper.getHexColorFromRGB(rgb[0], rgb[1], slider.getSliderValue());
         int decimal = ColorHelper.convertHexadecimalToDecimal(hex);
         this.properties.getRaceHandler().setTraitColor(hex);
         slider.displayString = "Blue: " + (int)(slider.sliderValue * slider.sliderMaxValue * 255.0F);
         this.colorField.setTextColor(decimal);
         this.colorField.setText(hex);
      });
      bY = bY + 20;
      this.addButton(this.r);
      this.addButton(this.g);
      this.addButton(this.b);
      bY = bY + 3;
      this.colorField2 = new GuiTextField(this.colorFieldID2, this.fontRenderer, bX, bY, 100, 20);
      this.colorField2.setMaxStringLength(8);
      String traitColor2 = this.properties == null ? "#ffffff" : this.properties.getRaceHandler().getAltTraitColor();
      float[] defaultRGB2 = ColorHelper.getRGBColor(traitColor2);
      this.colorField2.setText(traitColor2);
      this.addButton(new GuiPropertiesButton(this.resetColor2, bX + 102, bY - 1, 20, 20, "R", (button, pressed) -> {
         String defaultHex = ColorHelper.convertDecimalColorToHexadecimal(this.properties.getCurrentRace().getSecondaryColor());
         this.properties.getRaceHandler().setAltTraitColor(defaultHex);
         this.colorField2.setText(defaultHex);
         this.colorField2.setTextColor(this.properties.getCurrentRace().getSecondaryColor());
         float[] rgb = ColorHelper.getRGBColor(defaultHex);
         this.r2.sliderValue = rgb[0];
         this.r2.displayString = "Red: " + (int)(rgb[0] * 255.0F);
         this.g2.sliderValue = rgb[1];
         this.g2.displayString = "Green: " + (int)(rgb[1] * 255.0F);
         this.b2.sliderValue = rgb[2];
         this.b2.displayString = "Blue: " + (int)(rgb[2] * 255.0F);
      }));
      bY = bY + 21;
      this.r2 = new GuiPropertiesSlider(this.redSlider2, bX, bY, 100, 20, "Red", defaultRGB2[0], 1.0F, 0.0F, (slider, wrapper) -> {
         float[] rgb = ColorHelper.getRGBColor(this.properties.getRaceHandler().getAltTraitColor());
         String hex = ColorHelper.getHexColorFromRGB(slider.getSliderValue(), rgb[1], rgb[2]);
         int decimal = ColorHelper.convertHexadecimalToDecimal(hex);
         this.properties.getRaceHandler().setAltTraitColor(hex);
         slider.displayString = "Red: " + (int)(slider.sliderValue * slider.sliderMaxValue * 255.0F);
         this.colorField2.setTextColor(decimal);
         this.colorField2.setText(hex);
      });
      bY = bY + 20;
      this.g2 = new GuiPropertiesSlider(this.greenSlider2, bX, bY, 100, 20, "Green", defaultRGB2[1], 1.0F, 0.0F, (slider, wrapper) -> {
         float[] rgb = ColorHelper.getRGBColor(this.properties.getRaceHandler().getAltTraitColor());
         String hex = ColorHelper.getHexColorFromRGB(rgb[0], slider.getSliderValue(), rgb[2]);
         int decimal = ColorHelper.convertHexadecimalToDecimal(hex);
         this.properties.getRaceHandler().setAltTraitColor(hex);
         slider.displayString = "Green: " + (int)(slider.sliderValue * slider.sliderMaxValue * 255.0F);
         this.colorField2.setTextColor(decimal);
         this.colorField2.setText(hex);
      });
      bY = bY + 20;
      this.b2 = new GuiPropertiesSlider(this.blueSlider2, bX, bY, 100, 20, "Blue", defaultRGB2[2], 1.0F, 0.0F, (slider, wrapper) -> {
         float[] rgb = ColorHelper.getRGBColor(this.properties.getRaceHandler().getAltTraitColor());
         String hex = ColorHelper.getHexColorFromRGB(rgb[0], rgb[1], slider.getSliderValue());
         int decimal = ColorHelper.convertHexadecimalToDecimal(hex);
         this.properties.getRaceHandler().setAltTraitColor(hex);
         slider.displayString = "Blue: " + (int)(slider.sliderValue * slider.sliderMaxValue * 255.0F);
         this.colorField2.setTextColor(decimal);
         this.colorField2.setText(hex);
      });
      bY = bY + 20;
      this.addButton(this.r2);
      this.addButton(this.g2);
      this.addButton(this.b2);
      this.addButton(new GuiPropertiesButton(this.closeGui, this.width - 16, 2, 14, 20, TextFormatting.RED + "X", (button, pressed) -> {
         this.displayNormalInventory();
      }));
   }

   protected void mouseReleased(int mouseX, int mouseY, int state) {
      super.mouseReleased(mouseX, mouseY, state);
      this.buttonPressed = 0;
   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      this.colorField.mouseClicked(mouseX, mouseY, mouseButton);
      this.colorField2.mouseClicked(mouseX, mouseY, mouseButton);
   }

   public void onGuiClosed() {
      this.properties.sendInformationToServer();
   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.oldMouseX = (float)mouseX;
      this.oldMouseY = (float)mouseY;
      super.drawScreen(mouseX, mouseY, partialTicks);
      FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
      int backHeight = 30;
      if (this.properties.getCurrentRace().canFly()) {
         backHeight = 42;
         int flying = this.properties.getRaceHandler().canFly() ? '저' : 13107200;
         fontRenderer.drawStringWithShadow("Flying", 17.0F, 62.0F, flying);
      }

      DrawingHelper.Draw(2.0D, 30.0D, -100.0D, 0.0F, 0.0F, 0, 0, 60.0D, (double)backHeight, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.5F);
      fontRenderer.drawStringWithShadow("Show Trait", 6.0F, 32.0F, 16777215);
      this.colorField.drawTextBox();
      int exampleX = this.width - this.width / 4;
      int exampleY = this.height - this.height / 2;
      exampleX = exampleX - 30;
      exampleY = exampleY - this.height / 4;
      exampleY = exampleY - 8;
      float[] rgb = ColorHelper.getRGBColor(this.properties.getRaceHandler().getTraitColor());
      float rV = rgb[0];
      float gV = rgb[1];
      float bV = rgb[2];
      int rR = (int)(rV * 255.0F);
      int rG = (int)(gV * 255.0F);
      int rB = (int)(bV * 255.0F);
      this.colorField2.drawTextBox();
      DrawingHelper.Draw((double)(exampleX + 103), (double)exampleY, 0.0D, 0.0F, 0.0F, 0, 0, 18.0D, 18.0D, 0.0F, 0.0F, rV, gV, bV, 1.0F);
      float[] rgb2 = ColorHelper.getRGBColor(this.properties.getRaceHandler().getAltTraitColor());
      float rV2 = rgb2[0];
      float gV2 = rgb2[1];
      float bV2 = rgb2[2];
      int rR2 = (int)(rV2 * 255.0F);
      int rG2 = (int)(gV2 * 255.0F);
      int rB2 = (int)(bV2 * 255.0F);
      DrawingHelper.Draw((double)(exampleX + 103), (double)(exampleY + 84), 0.0D, 0.0F, 0.0F, 0, 0, 18.0D, 18.0D, 0.0F, 0.0F, rV2, gV2, bV2, 1.0F);
      int h = this.properties.getHeightValue();
      double scale = 300.0D / ((double)h * 1.0D) * 30.0D;
      DrawingHelper.Draw((double)(this.width / 2 - 50), (double)(this.height / 2 - 75), -100.0D, 0.0F, 0.0F, 0, 0, 100.0D, 180.0D, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.5F);
      DrawingHelper.drawEntityOnScreen(this.width / 2, this.height / 2 + 100, (double)((int)scale), this.flip, 180.0F, (double)((float)(this.width / 2) - this.oldMouseX), (double)((float)(this.height / 2 - 50) - this.oldMouseY), this.mc.player);
   }

   protected void keyTyped(char par1, int par2) throws IOException {
      super.keyTyped(par1, par2);
      if (this.colorField.isFocused()) {
         this.colorField.textboxKeyTyped(par1, par2);
         String text = this.colorField.getText().toLowerCase().replaceAll("[^#0-9a-f]", "");
         this.colorField.setText(text);
         float[] rgb = ColorHelper.getRGBColor(text);
         this.properties.getRaceHandler().setTraitColor(text);
         this.r.sliderValue = rgb[0];
         this.r.displayString = "Red: " + (int)(rgb[0] * 255.0F);
         this.g.sliderValue = rgb[1];
         this.g.displayString = "Green: " + (int)(rgb[1] * 255.0F);
         this.b.sliderValue = rgb[2];
         this.b.displayString = "Blue: " + (int)(rgb[2] * 255.0F);
      } else if (this.colorField2.isFocused()) {
         this.colorField2.textboxKeyTyped(par1, par2);
         String text = this.colorField2.getText().toLowerCase().replaceAll("[^#0-9a-f]", "");
         this.colorField2.setText(text);
         float[] rgb = ColorHelper.getRGBColor(text);
         this.properties.getRaceHandler().setAltTraitColor(text);
         this.r2.sliderValue = rgb[0];
         this.r2.displayString = "Red: " + (int)(rgb[0] * 255.0F);
         this.g2.sliderValue = rgb[1];
         this.g2.displayString = "Green: " + (int)(rgb[1] * 255.0F);
         this.b2.sliderValue = rgb[2];
         this.b2.displayString = "Blue: " + (int)(rgb[2] * 255.0F);
      }

   }

   public void displayNormalInventory() {
      GuiInventory gui = new GuiInventory(this.mc.player);
      this.mc.displayGuiScreen(gui);
   }
}
