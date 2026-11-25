package com.github.alexthe666.iceandfire.client.gui;

import com.github.alexthe666.iceandfire.client.StatCollector;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.inventory.ContainerDragon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiDragon extends GuiContainer {
   private static final ResourceLocation texture = new ResourceLocation("iceandfire:textures/gui/dragon.png");
   private IInventory playerInventory;
   private IInventory dragonInv;
   private EntityDragonBase dragon;
   private float mousePosx;
   private float mousePosY;

   public GuiDragon(IInventory playerInv, EntityDragonBase dragon) {
      super(new ContainerDragon(dragon, Minecraft.getMinecraft().player));
      this.playerInventory = playerInv;
      this.dragonInv = dragon.dragonInv;
      this.dragon = dragon;
      this.allowUserInput = false;
      this.ySize = 214;
   }

   public static void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, EntityDragonBase entity) {
      GlStateManager.enableColorMaterial();
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)posX, (float)posY, 50.0F);
      GlStateManager.scale((float)(-scale), (float)scale, (float)scale);
      float dragonscale = entity.getRenderSize() / 3.0F;
      GlStateManager.scale(1.0F / dragonscale, 1.0F / dragonscale, 1.0F / dragonscale);
      GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
      float f2 = entity.renderYawOffset;
      float f3 = entity.rotationYaw;
      float f4 = entity.rotationPitch;
      float f5 = entity.prevRotationYawHead;
      float f6 = entity.rotationYawHead;
      GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
      RenderHelper.enableStandardItemLighting();
      GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-((float)Math.atan((double)(mouseY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
      entity.renderYawOffset = (float)Math.atan((double)(mouseX / 40.0F)) * 20.0F;
      entity.rotationYaw = (float)Math.atan((double)(mouseX / 40.0F)) * 40.0F;
      entity.rotationPitch = -((float)Math.atan((double)(mouseY / 40.0F))) * 20.0F;
      entity.rotationYawHead = entity.rotationYaw;
      entity.prevRotationYawHead = entity.rotationYaw;
      GlStateManager.translate(0.0F, 0.0F, 0.0F);
      RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
      rendermanager.setPlayerViewY(180.0F);
      rendermanager.setRenderShadow(false);
      rendermanager.renderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
      rendermanager.setRenderShadow(true);
      entity.renderYawOffset = f2;
      entity.rotationYaw = f3;
      entity.rotationPitch = f4;
      entity.prevRotationYawHead = f5;
      entity.rotationYawHead = f6;
      GlStateManager.popMatrix();
      RenderHelper.disableStandardItemLighting();
      GlStateManager.disableRescaleNormal();
      GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
      GlStateManager.disableTexture2D();
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
   }

   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      String s1 = (new TextComponentTranslation(this.dragon.isFire ? "entity.firedragon.name" : "entity.icedragon.name", new Object[0])).getUnformattedText();
      this.fontRenderer.drawString(s1, this.xSize / 2 - this.fontRenderer.getStringWidth(s1) / 2, 6, 4210752);
      String s3 = this.dragon.getCustomNameTag().length() == 0 ? StatCollector.translateToLocal("dragon.unnamed") : StatCollector.translateToLocal("dragon.name") + this.dragon.getCustomNameTag();
      this.fontRenderer.drawString(s3, this.xSize / 2 - this.fontRenderer.getStringWidth(s3) / 2, 75, 16777215);
      String s2 = StatCollector.translateToLocal("dragon.health") + Math.min(this.dragon.getHealth(), this.dragon.getMaxHealth()) + "/" + this.dragon.getMaxHealth();
      this.fontRenderer.drawString(s2, this.xSize / 2 - this.fontRenderer.getStringWidth(s2) / 2, 84, 16777215);
      String s5 = StatCollector.translateToLocal("dragon.gender") + StatCollector.translateToLocal(this.dragon.isMale() ? "dragon.gender.male" : "dragon.gender.female");
      this.fontRenderer.drawString(s5, this.xSize / 2 - this.fontRenderer.getStringWidth(s5) / 2, 93, 16777215);
      String s6 = StatCollector.translateToLocal("dragon.hunger") + this.dragon.getHunger() + "/100";
      this.fontRenderer.drawString(s6, this.xSize / 2 - this.fontRenderer.getStringWidth(s6) / 2, 102, 16777215);
      String s4 = StatCollector.translateToLocal("dragon.stage") + this.dragon.getDragonStage() + " " + StatCollector.translateToLocal("dragon.days.front") + this.dragon.getAgeInDays() + " " + StatCollector.translateToLocal("dragon.days.back");
      this.fontRenderer.drawString(s4, this.xSize / 2 - this.fontRenderer.getStringWidth(s4) / 2, 111, 16777215);
      String s7 = this.dragon.getOwner() != null ? StatCollector.translateToLocal("dragon.owner") + this.dragon.getOwner().getName() : StatCollector.translateToLocal("dragon.untamed");
      this.fontRenderer.drawString(s7, this.xSize / 2 - this.fontRenderer.getStringWidth(s7) / 2, 120, 16777215);
   }

   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(texture);
      int k = (this.width - this.xSize) / 2;
      int l = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
      drawEntityOnScreen(k + 88, l + (int)(0.5F * this.dragon.flyProgress) + 55, 23, (float)(k + 51) - this.mousePosx, (float)(l + 75 - 50) - this.mousePosY, this.dragon);
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.mousePosx = (float)mouseX;
      this.mousePosY = (float)mouseY;
      this.drawDefaultBackground();
      super.drawScreen(mouseX, mouseY, partialTicks);
      this.renderHoveredToolTip(mouseX, mouseY);
   }
}
