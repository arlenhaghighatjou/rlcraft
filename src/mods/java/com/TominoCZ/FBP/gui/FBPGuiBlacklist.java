package com.TominoCZ.FBP.gui;

import com.TominoCZ.FBP.FBP;
import com.TominoCZ.FBP.handler.FBPConfigHandler;
import com.TominoCZ.FBP.handler.FBPKeyInputHandler;
import com.TominoCZ.FBP.keys.FBPKeyBindings;
import com.TominoCZ.FBP.model.FBPModelHelper;
import com.TominoCZ.FBP.node.BlockNode;
import java.util.Arrays;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;
import net.lax1dude.eaglercraft.v1_8.Keyboard;
import net.lax1dude.eaglercraft.v1_8.Mouse;

public class FBPGuiBlacklist extends GuiScreen {
   FBPGuiButtonBlacklist animation;
   FBPGuiButtonBlacklist particle;
   final BlockPos selectedPos;
   final IBlockState selectedBlock;
   ItemStack displayItemStack;
   boolean closing = false;

   public FBPGuiBlacklist(BlockPos selected) {
      this.mc = Minecraft.getMinecraft();
      this.selectedPos = selected;
      IBlockState state = this.mc.world.getBlockState(this.selectedPos);
      this.selectedBlock = state.getBlock() == FBP.FBPBlock ? ((BlockNode)FBP.FBPBlock.blockNodes.get(this.selectedPos)).state : state;
      ItemStack is = this.selectedBlock.getActualState(this.mc.world, this.selectedPos).getBlock().getPickBlock(this.selectedBlock, this.mc.objectMouseOver, this.mc.world, this.selectedPos, this.mc.player);
      TileEntity te = this.mc.world.getTileEntity(this.selectedPos);

      try {
         if (te != null) {
            this.mc.storeTEInStack(is, te);
         }
      } catch (Throwable var6) {
         ;
      }

      this.displayItemStack = is.copy();
   }

   public FBPGuiBlacklist(ItemStack is) {
      this.mc = Minecraft.getMinecraft();
      this.selectedPos = null;
      this.selectedBlock = Block.getBlockFromName(is.getItem().getRegistryName().toString()).getStateFromMeta(is.getMetadata());
      this.displayItemStack = is.copy();
   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   public void initGui() {
      this.buttonList.clear();
      this.animation = new FBPGuiButtonBlacklist(0, this.width / 2 - 100 - 30, this.height / 2 - 30 + 35, "", false, FBP.INSTANCE.isBlacklisted(this.selectedBlock.getBlock(), false));
      this.particle = new FBPGuiButtonBlacklist(1, this.width / 2 + 100 - 30, this.height / 2 - 30 + 35, "", true, FBP.INSTANCE.isBlacklisted(this.selectedBlock.getBlock(), true));
      Item ib = Item.getItemFromBlock(this.selectedBlock.getBlock());
      Block b = ib instanceof ItemBlock ? ((ItemBlock)ib).getBlock() : null;
      this.animation.enabled = b != null && !(b instanceof BlockDoublePlant) && FBPModelHelper.isModelValid(b.getDefaultState());
      this.particle.enabled = this.selectedBlock.getBlock() != Blocks.REDSTONE_BLOCK;
      FBPGuiButton guide = new FBPGuiButton(-1, this.animation.x + 30, this.animation.y + 30 - 10, (this.animation.enabled ? "\u00a7a<" : "\u00a7c<") + "             " + (this.particle.enabled ? "\u00a7a>" : "\u00a7c>"), false, false);
      guide.enabled = false;
      this.buttonList.addAll(Arrays.asList(guide, this.animation, this.particle));
   }

   public void updateScreen() {
      Mouse.setGrabbed(true);
      boolean keyUp = false;
      if (this.selectedPos != null && (this.mc.objectMouseOver == null || !this.mc.objectMouseOver.typeOfHit.equals(Type.BLOCK) || this.mc.world.getBlockState(this.mc.objectMouseOver.getBlockPos()).getBlock() != this.selectedBlock.getBlock() && this.mc.world.getBlockState(this.mc.objectMouseOver.getBlockPos()).getBlock() != FBP.FBPBlock)) {
         keyUp = true;
         FBPKeyInputHandler.INSTANCE.onInput();
      }

      try {
         if (!Keyboard.isKeyDown(FBPKeyBindings.FBPFastAdd.getKeyCode()) || this.selectedPos == null && !Keyboard.isKeyDown(42)) {
            keyUp = true;
         }
      } catch (Exception var6) {
         try {
            if (!Mouse.isButtonDown(FBPKeyBindings.FBPFastAdd.getKeyCode() + 100) || this.selectedPos == null && !Keyboard.isKeyDown(42)) {
               keyUp = true;
            }
         } catch (Exception var5) {
            this.closing = true;
            var6.printStackTrace();
         }
      }

      if (this.closing || keyUp) {
         Block b = this.selectedBlock.getBlock();
         GuiButton selected = this.animation.isMouseOver() ? this.animation : (this.particle.isMouseOver() ? this.particle : null);
         if (selected != null) {
            boolean isParticle = this.particle.isMouseOver();
            if (selected.enabled) {
               if (!FBP.INSTANCE.isBlacklisted(b, isParticle)) {
                  FBP.INSTANCE.addToBlacklist(b, isParticle);
               } else {
                  FBP.INSTANCE.removeFromBlacklist(b, isParticle);
               }

               if (isParticle) {
                  FBPConfigHandler.writeParticleExceptions();
               } else {
                  FBPConfigHandler.writeAnimExceptions();
               }

               this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
         }

         if (keyUp) {
            FBPKeyInputHandler.INSTANCE.onInput();
         }

         this.mc.displayGuiScreen((GuiScreen)null);
      }

   }

   public void mouseClicked(int mouseX, int mouseY, int button) {
      GuiButton clicked = this.animation.isMouseOver() ? this.animation : (this.particle.isMouseOver() ? this.particle : null);
      if (clicked != null && clicked.enabled) {
         this.closing = true;
      }

   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      int optionRadius = 30;
      mouseX = MathHelper.clamp(mouseX, this.animation.x + optionRadius, this.particle.x + optionRadius);
      mouseY = this.height / 2 + 35;
      int x = this.width / 2 - 32;
      int y = this.height / 2 - 30 - 60;
      GlStateManager.enableDepth();
      GlStateManager.enableLight(0);
      GlStateManager.translate((float)x, (float)y, 0.0F);
      GlStateManager.scale(4.0F, 4.0F, 4.0F);
      GlStateManager.enableColorMaterial();
      this.itemRender.renderItemAndEffectIntoGUI(this.mc.player, this.displayItemStack, 0, 0);
      this.itemRender.zLevel = 0.0F;
      this.zLevel = 0.0F;
      GlStateManager.scale(0.25D, 0.25D, 0.25D);
      GlStateManager.translate((float)(-x), (float)(-y), 0.0F);
      String itemName = ((Impl)(this.selectedPos == null ? this.displayItemStack.getItem() : this.selectedBlock.getBlock())).getRegistryName().toString();
      itemName = ((itemName.contains(":") ? "\u00a76\u00a7l" : "\u00a7a\u00a7l") + itemName).replaceAll(":", "\u00a7c\u00a7l:\u00a7a\u00a7l");
      FBPGuiHelper._drawCenteredString(this.fontRenderer, itemName, this.width / 2, this.height / 2 - 19, 0);
      String animationText1 = this.animation.enabled ? (this.animation.isMouseOver() ? (this.animation.isInExceptions ? "\u00a7c\u00a7lREMOVE" : "\u00a7a\u00a7lADD") : "") : "\u00a7c\u00a7lCAN'T BE ANIMATED";
      String particleText1 = this.particle.enabled ? (this.particle.isMouseOver() ? (this.particle.isInExceptions ? "\u00a7c\u00a7lREMOVE" : "\u00a7a\u00a7lADD") : "") : "\u00a7c\u00a7lCAN'T BE ADDED";
      FBPGuiHelper._drawCenteredString(this.fontRenderer, animationText1, this.animation.x + 30, this.animation.y + 65, 0);
      FBPGuiHelper._drawCenteredString(this.fontRenderer, particleText1, this.particle.x + 30, this.particle.y + 65, 0);
      if (this.animation.isMouseOver()) {
         FBPGuiHelper._drawCenteredString(this.fontRenderer, "\u00a7a\u00a7lPLACE ANIMATION", this.animation.x + 30, this.animation.y - 12, 0);
      }

      if (this.particle.isMouseOver()) {
         FBPGuiHelper._drawCenteredString(this.fontRenderer, "\u00a7a\u00a7lPARTICLES", this.particle.x + 30, this.particle.y - 12, 0);
      }

      this.drawCenteredString(this.fontRenderer, "\u00a7LBlacklist a Block", this.width / 2, 20, this.fontRenderer.getColorCode('a'));
      this.mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/widgets.png"));
      super.drawScreen(mouseX, mouseY, partialTicks);
      this.mc.getTextureManager().bindTexture(FBP.FBP_WIDGETS);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
      GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
      GuiButton mouseOver = this.animation.isMouseOver() ? this.animation : (this.particle.isMouseOver() ? this.particle : null);
      int imageDiameter = 20;
      this.drawTexturedModalRect(mouseX - imageDiameter / 2, mouseY - imageDiameter / 2, mouseOver != null && !mouseOver.enabled ? 256 - imageDiameter * 2 : 256 - imageDiameter, 256 - imageDiameter, imageDiameter, imageDiameter);
   }
}
