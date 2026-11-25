package com.lycanitesmobs.client.gui.beastiary;

import com.lycanitesmobs.GuiHandler;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.client.gui.BaseScreen;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.info.CreatureInfo;
import java.io.IOException;
import java.net.URI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.settings.GameSettings.Options;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public abstract class BeastiaryScreen extends BaseScreen {
   static int OPENED_GUI_SCALE;
   static boolean GUI_ACTIVE;
   public EntityPlayer player;
   public ExtendedPlayer playerExt;
   public EntityLivingBase creaturePreviewEntity;
   public float creaturePreviewTicks = 0.0F;
   public ScaledResolution scaledResolution;
   public int centerX;
   public int centerY;
   public int windowWidth;
   public int windowHeight;
   public int halfX;
   public int halfY;
   public int windowX;
   public int windowY;
   public int colLeftX;
   public int colLeftY;
   public int colLeftWidth;
   public int colLeftHeight;
   public int colLeftCenterX;
   public int colLeftCenterY;
   public int colRightX;
   public int colRightY;
   public int colRightWidth;
   public int colRightHeight;
   public int colRightCenterX;
   public int colRightCenterY;

   public BeastiaryScreen(EntityPlayer player) {
      this.player = player;
      this.playerExt = ExtendedPlayer.getForPlayer(player);
      this.mc = Minecraft.getMinecraft();
      if (this.mc.gameSettings.guiScale == 2 && !GUI_ACTIVE) {
         GUI_ACTIVE = true;
      } else {
         OPENED_GUI_SCALE = this.mc.gameSettings.guiScale;
         this.mc.gameSettings.setOptionValue(Options.GUI_SCALE, 2 - OPENED_GUI_SCALE);
      }

   }

   public void onGuiClosed() {
      if (this.mc.gameSettings.guiScale == 2 && !GUI_ACTIVE) {
         this.mc.gameSettings.setOptionValue(Options.GUI_SCALE, OPENED_GUI_SCALE - 2);
      }

      GUI_ACTIVE = false;
      super.onGuiClosed();
   }

   public String getTitle() {
      return "Beastiary";
   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   public int getScaledX(float x) {
      if (this.scaledResolution == null) {
         this.scaledResolution = new ScaledResolution(this.mc);
      }

      float targetAspect = 0.5625F;
      float scaledHeight = (float)this.scaledResolution.getScaledHeight();
      float scaledWidth = (float)this.scaledResolution.getScaledWidth();
      float currentAspect = scaledHeight * x / (scaledWidth * x);
      if (currentAspect < targetAspect) {
         scaledWidth = scaledHeight + scaledHeight * targetAspect;
      } else if (currentAspect > targetAspect) {
         float var10000 = scaledWidth + scaledWidth * targetAspect;
      }

      float guiWidth = scaledWidth * x;
      return Math.round(Math.max(x, guiWidth));
   }

   public int getScaledY(float y) {
      float baseHeight = (float)Math.round((float)this.getScaledX(y) * 0.5625F);
      return Math.round(baseHeight * y);
   }

   public void initGui() {
      super.initGui();
      if (this.scaledResolution == null) {
         this.scaledResolution = new ScaledResolution(this.mc);
      }

      this.zLevel = -1000.0F;
      this.windowWidth = this.getScaledX(0.95F);
      this.windowHeight = this.getScaledY(0.95F);
      this.halfX = this.windowWidth / 2;
      this.halfY = this.windowHeight / 2;
      this.windowX = this.width / 2 - this.windowWidth / 2;
      this.windowY = this.height / 2 - this.windowHeight / 2;
      this.centerX = this.windowX + this.windowWidth / 2;
      this.centerY = this.windowY + this.windowHeight / 2;
      this.colLeftX = this.windowX + this.getScaledX(0.041666668F);
      this.colLeftY = this.windowY + this.getScaledY(0.42592594F);
      this.colLeftWidth = this.getScaledX(0.16666667F);
      this.colLeftHeight = this.getScaledX(0.35185185F);
      this.colLeftCenterX = this.colLeftX + Math.round((float)(this.colLeftWidth / 2));
      this.colLeftCenterY = this.colLeftY + Math.round((float)(this.colLeftHeight / 2));
      this.colRightX = this.windowX + this.getScaledX(0.25F);
      this.colRightY = this.windowY + this.getScaledY(0.3888889F);
      this.colRightWidth = this.getScaledX(0.65625F);
      this.colRightHeight = this.getScaledX(0.37037036F);
      this.colRightCenterX = this.colRightX + Math.round((float)(this.colRightWidth / 2));
      this.colRightCenterY = this.colRightY + Math.round((float)(this.colRightHeight / 2));
      this.buttonList.clear();
      this.initControls();
   }

   protected void initControls() {
      int menuPadding = 6;
      int menuX = this.centerX - Math.round((float)this.windowWidth / 2.0F) + menuPadding;
      int menuY = this.windowY + menuPadding;
      int menuWidth = this.windowWidth - menuPadding * 2;
      int buttonCount = 5;
      int buttonPadding = 2;
      int buttonX = menuX + buttonPadding;
      int buttonWidth = Math.round((float)(menuWidth / buttonCount)) - buttonPadding * 2;
      int buttonWidthPadded = buttonWidth + buttonPadding * 2;
      int buttonHeight = 20;
      GuiButton button = new GuiButton(GuiHandler.Beastiary.INDEX.id, buttonX + buttonWidthPadded * this.buttonList.size(), menuY, buttonWidth, buttonHeight, LanguageManager.translate("gui.beastiary.index.title"));
      this.buttonList.add(button);
      button = new GuiButton(GuiHandler.Beastiary.CREATURES.id, buttonX + buttonWidthPadded * this.buttonList.size(), menuY, buttonWidth, buttonHeight, LanguageManager.translate("gui.beastiary.creatures"));
      this.buttonList.add(button);
      button = new GuiButton(GuiHandler.Beastiary.PETS.id, buttonX + buttonWidthPadded * this.buttonList.size(), menuY, buttonWidth, buttonHeight, LanguageManager.translate("gui.beastiary.pets"));
      this.buttonList.add(button);
      button = new GuiButton(GuiHandler.Beastiary.SUMMONING.id, buttonX + buttonWidthPadded * this.buttonList.size(), menuY, buttonWidth, buttonHeight, LanguageManager.translate("gui.beastiary.summoning"));
      this.buttonList.add(button);
      button = new GuiButton(GuiHandler.Beastiary.ELEMENTS.id, buttonX + buttonWidthPadded * this.buttonList.size(), menuY, buttonWidth, buttonHeight, LanguageManager.translate("gui.beastiary.elements"));
      this.buttonList.add(button);
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawBackground(mouseX, mouseY, partialTicks);
      this.drawForeground(mouseX, mouseY, partialTicks);
      this.updateControls(mouseX, mouseY, partialTicks);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   public void drawBackground(int mouseX, int mouseY, float partialTicks) {
      this.drawTexture(AssetManager.getTexture("GUIBeastiaryBackground"), (float)this.windowX, (float)this.windowY, this.zLevel, 1.0F, 1.0F, (float)this.windowWidth, (float)this.windowHeight);
   }

   protected void updateControls(int mouseX, int mouseY, float partialTicks) {
      for(GuiButton button : this.buttonList) {
         button.drawButton(this.mc, mouseX, mouseY, partialTicks);
      }

   }

   public void drawForeground(int mouseX, int mouseY, float partialTicks) {
      String title = "§l§n" + this.getTitle();
      float width = (float)this.getFontRenderer().getStringWidth(title);
      this.getFontRenderer().drawString(title, (float)(this.colRightCenterX - Math.round(width / 2.0F)), (float)this.colRightY, 16777215, true);
   }

   protected void actionPerformed(GuiButton guiButton) throws IOException {
      if (guiButton != null) {
         if (guiButton.id == GuiHandler.Beastiary.INDEX.id) {
            IndexBeastiaryScreen.openToPlayer(this.player);
         }

         if (guiButton.id == GuiHandler.Beastiary.CREATURES.id) {
            CreaturesBeastiaryScreen.openToPlayer(this.player);
         }

         if (guiButton.id == GuiHandler.Beastiary.PETS.id) {
            PetsBeastiaryScreen.openToPlayer(this.player);
         }

         if (guiButton.id == GuiHandler.Beastiary.SUMMONING.id) {
            SummoningBeastiaryScreen.openToPlayer(this.player);
         }

         if (guiButton.id == GuiHandler.Beastiary.ELEMENTS.id) {
            ElementsBeastiaryScreen.openToPlayer(this.player);
         }
      }

      super.actionPerformed(guiButton);
   }

   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      if (keyCode == 1 || keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
         this.mc.player.closeScreen();
      }

      super.keyTyped(typedChar, keyCode);
   }

   protected void openURI(URI uri) {
      try {
         Class oclass = Class.forName("java.awt.Desktop");
         Object object = oclass.getMethod("getDesktop").invoke((Object)null);
         oclass.getMethod("browse", URI.class).invoke(object, uri);
      } catch (Throwable var4) {
         LycanitesMobs.logWarning("", "Unable to open link: " + uri.toString());
      }

   }

   public void drawLevel(CreatureInfo creatureInfo, ResourceLocation texture, int x, int y) {
      int level = creatureInfo.summonCost;
      if (level <= 10) {
         this.drawBar(texture, x, y, 0.0F, 9.0F, 9.0F, level, 10);
      }

   }

   public void renderCreature(CreatureInfo creatureInfo, int x, int y, int mouseX, int mouseY, float partialTicks) {
      if (creatureInfo == null) {
         this.creaturePreviewEntity = null;
      } else {
         try {
            boolean subspeciesMatch = true;
            if (this.creaturePreviewEntity instanceof BaseCreatureEntity) {
               subspeciesMatch = ((BaseCreatureEntity)this.creaturePreviewEntity).getSubspeciesIndex() == this.getDisplaySubspecies(creatureInfo);
               if (subspeciesMatch) {
                  subspeciesMatch = ((BaseCreatureEntity)this.creaturePreviewEntity).getVariantIndex() == this.getDisplayVariant(creatureInfo);
               }
            }

            if (this.creaturePreviewEntity == null || this.creaturePreviewEntity.getClass() != creatureInfo.entityClass || !subspeciesMatch) {
               this.creaturePreviewEntity = (EntityLivingBase)creatureInfo.entityClass.getConstructor(World.class).newInstance(this.player.getEntityWorld());
               this.creaturePreviewEntity.onGround = true;
               if (this.creaturePreviewEntity instanceof BaseCreatureEntity) {
                  ((BaseCreatureEntity)this.creaturePreviewEntity).setSubspecies(this.getDisplaySubspecies(creatureInfo));
                  ((BaseCreatureEntity)this.creaturePreviewEntity).setVariant(this.getDisplayVariant(creatureInfo));
                  ((BaseCreatureEntity)this.creaturePreviewEntity).updateSize();
               }

               if (this.creaturePreviewEntity instanceof AgeableCreatureEntity) {
                  ((AgeableCreatureEntity)this.creaturePreviewEntity).setGrowingAge(0);
               }

               this.onCreateDisplayEntity(creatureInfo, this.creaturePreviewEntity);
               this.playCreatureSelectSound(creatureInfo);
            }

            if (this.creaturePreviewEntity != null) {
               int creatureSize = 70;
               float creatureWidth = this.creaturePreviewEntity.width;
               float creatureHeight = this.creaturePreviewEntity.height;
               int scale = Math.round(1.8F / Math.max(creatureWidth, creatureHeight) * (float)creatureSize);
               int posY = y + 32 + creatureSize;
               float lookX = (float)x - (float)mouseX;
               float lookY = (float)posY - (float)mouseY;
               this.creaturePreviewTicks += partialTicks;
               if (this.creaturePreviewEntity instanceof BaseCreatureEntity) {
                  BaseCreatureEntity previewCreatureBase = (BaseCreatureEntity)this.creaturePreviewEntity;
                  previewCreatureBase.onlyRenderTicks = this.creaturePreviewTicks;
               }

               GlStateManager.enableColorMaterial();
               GlStateManager.pushMatrix();
               GlStateManager.translate((float)x, (float)posY, -500.0F);
               GlStateManager.scale((float)(-scale), (float)scale, (float)scale);
               GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
               float f = this.creaturePreviewEntity.renderYawOffset;
               float f1 = this.creaturePreviewEntity.rotationYaw;
               float f2 = this.creaturePreviewEntity.rotationPitch;
               float f3 = this.creaturePreviewEntity.prevRotationYawHead;
               float f4 = this.creaturePreviewEntity.rotationYawHead;
               GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
               RenderHelper.enableStandardItemLighting();
               GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
               GlStateManager.rotate(-((float)Math.atan((double)(lookY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
               this.creaturePreviewEntity.renderYawOffset = (float)Math.atan((double)(lookX / 40.0F)) * 20.0F;
               this.creaturePreviewEntity.rotationYaw = (float)Math.atan((double)(lookX / 40.0F)) * 40.0F;
               this.creaturePreviewEntity.rotationPitch = -((float)Math.atan((double)(lookY / 40.0F))) * 20.0F;
               this.creaturePreviewEntity.rotationYawHead = this.creaturePreviewEntity.rotationYaw;
               this.creaturePreviewEntity.prevRotationYawHead = this.creaturePreviewEntity.rotationYaw;
               GlStateManager.translate(0.0F, 0.0F, 0.0F);
               RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
               rendermanager.setPlayerViewY(180.0F);
               rendermanager.setRenderShadow(false);
               rendermanager.renderEntity(this.creaturePreviewEntity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, true);
               rendermanager.setRenderShadow(true);
               this.creaturePreviewEntity.renderYawOffset = f;
               this.creaturePreviewEntity.rotationYaw = f1;
               this.creaturePreviewEntity.rotationPitch = f2;
               this.creaturePreviewEntity.prevRotationYawHead = f3;
               this.creaturePreviewEntity.rotationYawHead = f4;
               GlStateManager.popMatrix();
               RenderHelper.disableStandardItemLighting();
               GlStateManager.disableRescaleNormal();
               GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
               GlStateManager.disableTexture2D();
               GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
            }
         } catch (Exception var22) {
            LycanitesMobs.logWarning("", "An exception occurred when trying to preview a creature in the Beastiary.");
            var22.printStackTrace();
         }

      }
   }

   public int getDisplaySubspecies(CreatureInfo creatureInfo) {
      return this.playerExt.selectedSubspecies;
   }

   public int getDisplayVariant(CreatureInfo creatureInfo) {
      return this.playerExt.selectedVariant;
   }

   public void playCreatureSelectSound(CreatureInfo creatureInfo) {
      String soundSuffix = "";
      if (creatureInfo.getSubspecies(this.playerExt.selectedSubspecies).name != null) {
         soundSuffix = soundSuffix + "." + creatureInfo.getSubspecies(this.playerExt.selectedSubspecies).name;
      }

      SoundEvent soundEvent = AssetManager.getSound(creatureInfo.getName() + soundSuffix + "_say");
      this.player.getEntityWorld().playSound(this.player, this.player.posX, this.player.posY, this.player.posZ, soundEvent, SoundCategory.NEUTRAL, 1.0F, 1.0F);
   }

   public void onCreateDisplayEntity(CreatureInfo creatureInfo, EntityLivingBase entity) {
   }
}
