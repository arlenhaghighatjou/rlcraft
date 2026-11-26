package com.yyon.grapplinghook;

import com.yyon.grapplinghook.blocks.TileEntityGrappleModifier;
import com.arlenh7.eaglercraft.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.client.config.GuiSlider;

public class GuiModifier extends GuiScreen {
   private static final ResourceLocation texture = new ResourceLocation("grapplemod", "textures/gui/guimodifier_bg.png");
   int xSize = 221;
   int ySize = 221;
   protected int guiLeft;
   protected int guiTop;
   int posy;
   int id;
   HashMap<GuiButton, String> options;
   HashMap<GuiButton, String> tooltips;
   TileEntityGrappleModifier tileent;
   GrappleCustomization customization;
   grapplemod.upgradeCategories category = null;
   boolean allowed = false;
   boolean showinghelpscreen = false;
   GuiButton buttonpressed = null;

   public GuiModifier(TileEntityGrappleModifier tileent) {
      this.tileent = tileent;
      this.customization = tileent.customization;
   }

   public void initGui() {
      super.initGui();
      this.guiLeft = (this.width - this.xSize) / 2;
      this.guiTop = (this.height - this.ySize) / 2;
      this.clearscreen();
      this.mainscreen();
   }

   public void mainscreen() {
      this.buttonList.add(new GuiButton(1, this.guiLeft + 10, this.guiTop + this.ySize - 20 - 10, 50, 20, grapplemod.proxy.localize("grapplemodifier.close.desc")));
      this.buttonList.add(new GuiButton(2, this.guiLeft + this.xSize - 50 - 10, this.guiTop + this.ySize - 20 - 10, 50, 20, grapplemod.proxy.localize("grapplemodifier.reset.desc")));
      this.buttonList.add(new GuiButton(4, this.guiLeft + 10 + 75, this.guiTop + this.ySize - 20 - 10, 50, 20, grapplemod.proxy.localize("grapplemodifier.helpbutton.desc")));
      int y = 0;
      int x = 0;

      for(int i = 0; i < grapplemod.upgradeCategories.size(); ++i) {
         grapplemod.upgradeCategories category = grapplemod.upgradeCategories.fromInt(i);
         if (category != grapplemod.upgradeCategories.LIMITS) {
            if (i == grapplemod.upgradeCategories.size() / 2) {
               y = 0;
               ++x;
            }

            this.buttonList.add(new GuiButton(99 + i, this.guiLeft + 10 + 105 * x, this.guiTop + 15 + 30 * y, 95, 20, category.description));
            ++y;
         }
      }

   }

   public void clearscreen() {
      this.buttonList.clear();
      this.category = null;
      this.allowed = false;
      this.posy = 10;
      this.id = 10;
      this.options = new HashMap();
      this.tooltips = new HashMap();
   }

   public void notAllowedScreen(grapplemod.upgradeCategories category) {
      this.buttonList.add(new GuiButton(3, this.guiLeft + 10, this.guiTop + this.ySize - 20 - 10, 50, 20, grapplemod.proxy.localize("grapplemodifier.back.desc")));
      this.category = category;
      this.allowed = false;
   }

   public void helpscreen() {
      this.buttonList.add(new GuiButton(3, this.guiLeft + 10, this.guiTop + this.ySize - 20 - 10, 50, 20, grapplemod.proxy.localize("grapplemodifier.back.desc")));
   }

   public void addCheckbox(String option) {
      String text = grapplemod.proxy.localize(this.customization.getName(option));
      String desc = grapplemod.proxy.localize(this.customization.getDescription(option));
      GuiCheckBox checkbox = new GuiCheckBox(this.id++, 10 + this.guiLeft, this.posy + this.guiTop, text, this.customization.getBoolean(option));
      this.posy += 20;
      this.buttonList.add(checkbox);
      this.options.put(checkbox, option);
      this.tooltips.put(checkbox, desc);
   }

   public void addSlider(String option) {
      double d = this.customization.getDouble(option);
      d = Math.floor(d * 10.0D + 0.5D) / 10.0D;
      double max = this.customization.getMax(option, this.getLimits());
      double min = this.customization.getMin(option, this.getLimits());
      String text = grapplemod.proxy.localize(this.customization.getName(option));
      GuiSlider slider = new GuiSlider(this.id++, 10 + this.guiLeft, this.posy + this.guiTop, this.xSize - 20, 20, text + ": ", "", min, max, d, true, true);
      slider.displayString = text + ": " + Double.toString(d);
      slider.precision = 1;
      this.posy += 25;
      this.buttonList.add(slider);
      this.options.put(slider, option);
      String desc = grapplemod.proxy.localize(this.customization.getDescription(option));
      this.tooltips.put(slider, desc);
   }

   public void showCategoryScreen(grapplemod.upgradeCategories category) {
      this.buttonList.add(new GuiButton(3, this.guiLeft + 10, this.guiTop + this.ySize - 20 - 10, 50, 20, grapplemod.proxy.localize("grapplemodifier.back.desc")));
      this.category = category;
      this.allowed = true;
      if (category == grapplemod.upgradeCategories.ROPE) {
         this.addSlider("maxlen");
         this.addCheckbox("phaserope");
         this.addCheckbox("sticky");
      } else if (category == grapplemod.upgradeCategories.THROW) {
         this.addSlider("hookgravity");
         this.addSlider("throwspeed");
         this.addCheckbox("reelin");
         this.addSlider("verticalthrowangle");
         this.addSlider("sneakingverticalthrowangle");
         this.addCheckbox("detachonkeyrelease");
      } else if (category == grapplemod.upgradeCategories.MOTOR) {
         this.addCheckbox("motor");
         this.addSlider("motormaxspeed");
         this.addSlider("motoracceleration");
         this.addCheckbox("motorwhencrouching");
         this.addCheckbox("motorwhennotcrouching");
         this.addCheckbox("smartmotor");
         this.addCheckbox("motordampener");
         this.addCheckbox("pullbackwards");
      } else if (category == grapplemod.upgradeCategories.SWING) {
         this.addSlider("playermovementmult");
      } else if (category == grapplemod.upgradeCategories.STAFF) {
         this.addCheckbox("enderstaff");
      } else if (category == grapplemod.upgradeCategories.FORCEFIELD) {
         this.addCheckbox("repel");
         this.addSlider("repelforce");
      } else if (category == grapplemod.upgradeCategories.MAGNET) {
         this.addCheckbox("attract");
         this.addSlider("attractradius");
      } else if (category == grapplemod.upgradeCategories.DOUBLE) {
         this.addCheckbox("doublehook");
         this.addCheckbox("smartdoublemotor");
         this.addSlider("angle");
         this.addSlider("sneakingangle");
         this.addCheckbox("oneropepull");
      } else if (category == grapplemod.upgradeCategories.ROCKET) {
         this.addCheckbox("rocket");
         this.addSlider("rocket_force");
         this.addSlider("rocket_active_time");
         this.addSlider("rocket_refuel_ratio");
         this.addSlider("rocket_vertical_angle");
      }

      this.updateEnabled();
   }

   public void onGuiClosed() {
      this.updateOptions();
      this.tileent.setCustomizationClient(this.customization);
      super.onGuiClosed();
   }

   public void updateOptions() {
      for(GuiButton b : this.options.keySet()) {
         this.updateOption(b);
      }

   }

   public void updateOption(GuiButton b) {
      if (b instanceof GuiCheckBox) {
         boolean checked = ((GuiCheckBox)b).isChecked();
         String option = (String)this.options.get(b);
         this.customization.setBoolean(option, checked);
      } else if (b instanceof GuiSlider) {
         double d = ((GuiSlider)b).getValue();
         d = Math.floor(d * 10.0D + 0.5D) / 10.0D;
         String option = (String)this.options.get(b);
         this.customization.setDouble(option, d);
      }

      this.updateEnabled();
   }

   public void updateEnabled() {
      for(GuiButton b : this.options.keySet()) {
         String option = (String)this.options.get(b);
         boolean enabled = true;
         String desc = grapplemod.proxy.localize(this.customization.getDescription(option));
         if (!this.customization.isoptionvalid(option)) {
            desc = grapplemod.proxy.localize("grapplemodifier.incompatability.desc") + "\n" + desc;
            enabled = false;
         }

         int level = this.customization.optionEnabled(option);
         if (this.getLimits() < level) {
            if (level == 1) {
               desc = grapplemod.proxy.localize("grapplemodifier.limits.desc") + "\n" + desc;
            } else {
               desc = grapplemod.proxy.localize("grapplemodifier.locked.desc") + "\n" + desc;
            }

            enabled = false;
         }

         b.enabled = enabled;
         this.tooltips.put(b, desc);
      }

   }

   public int getLimits() {
      return !this.tileent.isUnlocked(grapplemod.upgradeCategories.LIMITS) && !Minecraft.getMinecraft().player.capabilities.isCreativeMode ? 0 : 1;
   }

   public void updateScreen() {
      if (this.buttonpressed != null) {
         GuiButton b = this.buttonpressed;
         this.buttonpressed = null;
         if (b.id == 1) {
            Minecraft.getMinecraft().player.closeScreen();
            return;
         }

         if (b.id == 2) {
            this.customization = new GrappleCustomization();
         } else if (b.id == 3) {
            this.showinghelpscreen = false;
            this.updateOptions();
            this.clearscreen();
            this.mainscreen();
         } else if (b.id == 4) {
            this.showinghelpscreen = true;
            this.clearscreen();
            this.helpscreen();
         } else if (this.options.containsKey(b)) {
            this.updateOption(b);
         } else {
            int categoryid = b.id - 99;
            grapplemod.upgradeCategories category = grapplemod.upgradeCategories.fromInt(categoryid);
            this.clearscreen();
            boolean unlocked = this.tileent.isUnlocked(category) || Minecraft.getMinecraft().player.capabilities.isCreativeMode;
            if (unlocked) {
               this.showCategoryScreen(category);
            } else {
               this.notAllowedScreen(category);
            }
         }
      }

      super.updateScreen();
   }

   protected void actionPerformed(GuiButton b) {
      this.buttonpressed = b;

      try {
         super.actionPerformed(b);
      } catch (IOException var3) {
         var3.printStackTrace();
      }

   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      if (this.fontRenderer != null) {
         Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
         GlStateManager.translate((float)this.guiLeft, (float)this.guiTop, 0.0F);
         if (this.category != null) {
            if (!this.allowed) {
               this.fontRenderer.drawString(grapplemod.proxy.localize("grapplemodifier.unlock1.desc"), 10, 10, Color.darkGray.getRGB());
               this.fontRenderer.drawString(this.category.description, 10, 25, Color.darkGray.getRGB());
               this.fontRenderer.drawString(grapplemod.proxy.localize("grapplemodifier.unlock2.desc"), 10, 40, Color.darkGray.getRGB());
               this.fontRenderer.drawString(grapplemod.proxy.localize("grapplemodifier.unlock3.desc"), 10, 55, Color.darkGray.getRGB());
               this.fontRenderer.drawString((new ItemStack(this.category.getItem())).getDisplayName(), 10, 70, Color.darkGray.getRGB());
               this.fontRenderer.drawString(grapplemod.proxy.localize("grapplemodifier.unlock4.desc"), 10, 85, Color.darkGray.getRGB());
            }
         } else if (this.showinghelpscreen) {
            String helptext = grapplemod.proxy.localize("grapplemodifier.help.desc");
            int linenum = 0;

            for(String line : helptext.split(Pattern.quote("\\n"))) {
               this.fontRenderer.drawString(line, 10, 10 + 15 * linenum, Color.darkGray.getRGB());
               ++linenum;
            }
         } else {
            this.fontRenderer.drawString(grapplemod.proxy.localize("grapplemodifier.apply.desc"), 10, this.ySize - 20 - 10 - 10, Color.darkGray.getRGB());
         }

         GlStateManager.translate((float)(-this.guiLeft), (float)(-this.guiTop), 0.0F);
         GlStateManager.disableRescaleNormal();
         RenderHelper.disableStandardItemLighting();
         GlStateManager.disableLighting();
         GlStateManager.disableDepth();
         super.drawScreen(mouseX, mouseY, partialTicks);
         GlStateManager.enableLighting();
         GlStateManager.enableDepth();
         GlStateManager.enableRescaleNormal();
         RenderHelper.enableStandardItemLighting();

         for(GuiButton b : this.tooltips.keySet()) {
            if (mouseX >= b.x && mouseY >= b.y && mouseX <= b.x + b.width && mouseY <= b.y + b.height) {
               this.drawHoveringText((String)this.tooltips.get(b), mouseX, mouseY);
            }
         }

      }
   }
}
