package com.github.alexthe666.iceandfire.client.gui.bestiary;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.StatCollector;
import com.github.alexthe666.iceandfire.core.ModBlocks;
import com.github.alexthe666.iceandfire.core.ModItems;
import com.github.alexthe666.iceandfire.enums.EnumBestiaryPages;
import com.github.alexthe666.iceandfire.enums.EnumDragonArmor;
import com.github.alexthe666.iceandfire.enums.EnumSeaSerpent;
import com.github.alexthe666.iceandfire.enums.EnumTroll;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiBestiary extends GuiScreen {
   protected static final int X = 390;
   protected static final int Y = 245;
   private static final ResourceLocation TEXTURE = new ResourceLocation("iceandfire:textures/gui/bestiary/bestiary.png");
   private static final ResourceLocation DRAWINGS_0 = new ResourceLocation("iceandfire:textures/gui/bestiary/drawings_0.png");
   private static final ResourceLocation DRAWINGS_1 = new ResourceLocation("iceandfire:textures/gui/bestiary/drawings_1.png");
   public List<EnumBestiaryPages> allPageTypes = new ArrayList();
   public EnumBestiaryPages pageType;
   public List<IndexPageButton> indexButtons = new ArrayList();
   public ChangePageButton previousPage;
   public ChangePageButton nextPage;
   public int bookPages;
   public int bookPagesTotal = 1;
   public int indexPages;
   public int indexPagesTotal = 1;
   protected ItemStack book;
   protected boolean index;
   protected FontRenderer font = this.getFont();

   private FontRenderer getFont() {
      FontRenderer font;
      if (!IceAndFire.CONFIG.useVanillaFont && Minecraft.getMinecraft().gameSettings.language.equalsIgnoreCase("en_us")) {
         font = (FontRenderer)IceAndFire.PROXY.getFontRenderer();
      } else {
         font = Minecraft.getMinecraft().fontRenderer;
      }

      font.setUnicodeFlag(Minecraft.getMinecraft().getLanguageManager().isCurrentLocaleUnicode());
      font.setBidiFlag(Minecraft.getMinecraft().getLanguageManager().isCurrentLanguageBidirectional());
      return font;
   }

   public GuiBestiary(ItemStack book) {
      this.book = book;
      int indexPageTotal = 0;
      if (!book.isEmpty() && book.getItem() != null && book.getItem() == ModItems.bestiary && book.getTagCompound() != null) {
         List<EnumBestiaryPages> pages = EnumBestiaryPages.containedPages(EnumBestiaryPages.toList(book.getTagCompound().getIntArray("Pages")));
         this.allPageTypes.addAll(pages);
         this.indexPagesTotal = (int)Math.ceil((double)pages.size() / 10.0D);
      }

      this.index = true;
   }

   public void initGui() {
      super.initGui();
      int centerX = (this.width - 390) / 2;
      int centerY = (this.height - 245) / 2;
      this.buttonList.add(this.previousPage = new ChangePageButton(0, centerX + 15, centerY + 215, false, this.bookPages, 0));
      this.buttonList.add(this.nextPage = new ChangePageButton(1, centerX + 357, centerY + 215, true, this.bookPages, 0));
      if (!this.allPageTypes.isEmpty()) {
         for(int i = 0; i < this.allPageTypes.size(); ++i) {
            int xIndex = i % -2;
            int yIndex = i % 10;
            IndexPageButton button = new IndexPageButton(2 + i, centerX + 15 + xIndex * 200, centerY + 10 + yIndex * 20 - (xIndex == 1 ? 20 : 0), StatCollector.translateToLocal("bestiary." + EnumBestiaryPages.values()[((EnumBestiaryPages)this.allPageTypes.get(i)).ordinal()].toString().toLowerCase()));
            this.indexButtons.add(button);
            this.buttonList.add(button);
         }
      }

   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      for(GuiButton button : this.buttonList) {
         if (button.id >= 2) {
            button.enabled = this.index;
            button.visible = this.index;
         }
      }

      for(int i = 0; i < this.indexButtons.size(); ++i) {
         if (i < 10 * (this.indexPages + 1) && i >= 10 * this.indexPages && this.index) {
            ((IndexPageButton)this.indexButtons.get(i)).visible = true;
         } else {
            ((IndexPageButton)this.indexButtons.get(i)).visible = false;
         }
      }

      this.drawDefaultBackground();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.renderEngine.bindTexture(TEXTURE);
      int cornerX = (this.width - 390) / 2;
      int cornerY = (this.height - 245) / 2;
      drawModalRectWithCustomSizedTexture(cornerX, cornerY, 0.0F, 0.0F, 390, 245, 390.0F, 390.0F);
      GL11.glDisable(32826);
      RenderHelper.disableStandardItemLighting();
      GL11.glDisable(2896);
      GL11.glDisable(2929);
      super.drawScreen(mouseX, mouseY, partialTicks);
      RenderHelper.enableGUIStandardItemLighting();
      GL11.glPushMatrix();
      GL11.glTranslatef((float)cornerX, (float)cornerY, 0.0F);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glEnable(32826);
      short short1 = 240;
      short short2 = 240;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)short1 / 1.0F, (float)short2 / 1.0F);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glDisable(2896);
      if (!this.index) {
         this.drawPerPage(this.bookPages);
         int pageLeft = this.bookPages * 2 + 1;
         int pageRight = pageLeft + 1;
         this.font.drawString("" + pageLeft, (float)(390 / 4), (float)(245 - (int)(245.0D * 0.13D)), 3158064, false);
         this.font.drawString("" + pageRight, (float)(390 - (int)(390.0D * 0.24D)), (float)(245 - (int)(245.0D * 0.13D)), 3158064, false);
      }

      GL11.glEnable(2896);
      GL11.glPopMatrix();
      GL11.glEnable(2896);
      GL11.glEnable(2929);
      RenderHelper.enableStandardItemLighting();
   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   public void drawPerPage(int bookPages) {
      switch(this.pageType) {
      case INTRODUCTION:
         if (bookPages == 0) {
            this.drawItemStack(new ItemStack(ModItems.manuscript), 30, 14, 2.5F);
            this.drawItemStack(new ItemStack(ModBlocks.silverOre), 110, 15, 2.5F);
            this.drawItemStack(new ItemStack(ModItems.silverIngot), 140, 48, 2.0F);
            int i = 133;
            ItemStack var77 = new ItemStack(ModItems.silver_shovel);
            i = i + 16;
            this.drawItemStack(var77, i, 85, 1.51F);
            var77 = new ItemStack(ModItems.silver_pickaxe);
            i = i + 16;
            this.drawItemStack(var77, i, 85, 1.5F);
            var77 = new ItemStack(ModItems.silver_axe);
            i = i + 16;
            this.drawItemStack(var77, i, 85, 1.5F);
            var77 = new ItemStack(ModItems.silver_sword);
            i = i + 16;
            this.drawItemStack(var77, i, 85, 1.5F);
            var77 = new ItemStack(ModItems.silver_hoe);
            i = i + 16;
            this.drawItemStack(var77, i, 85, 1.5F);
            var77 = new ItemStack(ModItems.silverNugget);
            i = i + 16;
            this.drawItemStack(var77, i, 85, 1.5F);
            int j = 148;
            var77 = new ItemStack(ModItems.silver_helmet);
            j = j + 16;
            this.drawItemStack(var77, j, 100, 1.5F);
            var77 = new ItemStack(ModItems.silver_chestplate);
            j = j + 16;
            this.drawItemStack(var77, j, 100, 1.5F);
            var77 = new ItemStack(ModItems.silver_leggings);
            j = j + 16;
            this.drawItemStack(var77, j, 100, 1.5F);
            var77 = new ItemStack(ModItems.silver_boots);
            j = j + 16;
            this.drawItemStack(var77, j, 100, 1.5F);
         }

         if (bookPages == 1) {
            this.drawItemStack(new ItemStack(ModBlocks.sapphireOre), 30, 20, 2.5F);
            this.drawItemStack(new ItemStack(ModItems.sapphireGem), 40, 60, 2.0F);
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawImage(DRAWINGS_0, 144, 0, 389, 1, 50, 50, 512.0F);
            GL11.glPopMatrix();
            boolean drawGold = Minecraft.getMinecraft().player.ticksExisted % 20 < 10;
            this.drawItemStack(new ItemStack(drawGold ? Items.GOLD_NUGGET : ModItems.silverNugget), 144, 34, 1.5F);
            this.drawItemStack(new ItemStack(drawGold ? Items.GOLD_NUGGET : ModItems.silverNugget), 161, 34, 1.5F);
            this.drawItemStack(new ItemStack(drawGold ? ModBlocks.goldPile : ModBlocks.silverPile), 151, 7, 2.0F);
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawImage(DRAWINGS_0, 144, 90, 389, 1, 50, 50, 512.0F);
            GL11.glPopMatrix();
            this.drawItemStack(new ItemStack(Blocks.PLANKS), 161, 124, 1.5F);
            this.drawItemStack(new ItemStack(Blocks.PLANKS), 161, 107, 1.5F);
            this.drawItemStack(new ItemStack(Items.BOOK), 161, 91, 1.5F);
            this.drawItemStack(new ItemStack(ModBlocks.lectern), 151, 78, 2.0F);
         }

         this.writeFromTxt();
         break;
      case FIREDRAGON:
         if (bookPages == 0) {
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawImage(DRAWINGS_0, 20, 50, 127, 0, 88, 62, 512.0F);
            GL11.glPopMatrix();
         }

         if (bookPages == 1) {
            GL11.glPushMatrix();
            this.drawImage(DRAWINGS_0, 38, 60, 0, 0, 127, 62, 512.0F);
            this.drawImage(DRAWINGS_0, 240, 5, 0, 185, 114, 62, 512.0F);
            this.drawImage(DRAWINGS_0, 240, 150, 0, 62, 99, 37, 512.0F);
            GL11.glPopMatrix();
         }

         if (bookPages == 2) {
            GL11.glPushMatrix();
            this.drawImage(DRAWINGS_0, 42, 80, 0, 99, 100, 41, 512.0F);
            this.drawImage(DRAWINGS_0, 44, 160, 0, 140, 95, 45, 512.0F);
            GL11.glPopMatrix();
         }

         if (bookPages == 3) {
            GL11.glPushMatrix();
            this.drawImage(DRAWINGS_0, 45, 50, 214, 0, 87, 63, 512.0F);
            this.drawImage(DRAWINGS_0, 45, 110, 214, 62, 89, 62, 512.0F);
            this.drawImage(DRAWINGS_0, 245, 10, 214, 124, 89, 63, 512.0F);
            this.drawImage(DRAWINGS_0, 245, 70, 214, 184, 89, 64, 512.0F);
            this.drawImage(DRAWINGS_0, 245, 130, 300, 0, 88, 62, 512.0F);
            GL11.glPopMatrix();
         }

         this.writeFromTxt();
         break;
      case FIREDRAGONEGG:
         if (bookPages == 0) {
            GL11.glPushMatrix();
            this.drawImage(DRAWINGS_0, 20, 95, 327, 118, 24, 33, 512.0F);
            this.drawImage(DRAWINGS_0, 60, 95, 303, 118, 24, 33, 512.0F);
            this.drawImage(DRAWINGS_0, 95, 95, 351, 118, 24, 33, 512.0F);
            this.drawImage(DRAWINGS_0, 135, 95, 375, 118, 24, 33, 512.0F);
            GL11.glPopMatrix();
         }

         if (bookPages == 1) {
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawImage(DRAWINGS_0, 25, 20, 303, 62, 71, 56, 512.0F);
            GL11.glPopMatrix();
         }

         this.writeFromTxt();
         break;
      case ICEDRAGON:
         if (bookPages == 0) {
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawImage(DRAWINGS_0, 20, 50, 127, 248, 87, 62, 512.0F);
            GL11.glPopMatrix();
         }

         if (bookPages == 1) {
            GL11.glPushMatrix();
            this.drawImage(DRAWINGS_0, 38, 60, 0, 247, 126, 61, 512.0F);
            this.drawImage(DRAWINGS_0, 240, 5, 0, 432, 114, 62, 512.0F);
            this.drawImage(DRAWINGS_0, 240, 150, 0, 309, 99, 37, 512.0F);
            GL11.glPopMatrix();
         }

         if (bookPages == 2) {
            GL11.glPushMatrix();
            this.drawImage(DRAWINGS_0, 42, 80, 0, 346, 100, 41, 512.0F);
            this.drawImage(DRAWINGS_0, 44, 160, 0, 387, 95, 45, 512.0F);
            GL11.glPopMatrix();
         }

         if (bookPages == 3) {
            GL11.glPushMatrix();
            this.drawImage(DRAWINGS_0, 50, 52, 214, 248, 86, 62, 512.0F);
            this.drawImage(DRAWINGS_0, 50, 112, 214, 310, 87, 62, 512.0F);
            this.drawImage(DRAWINGS_0, 250, 12, 214, 372, 87, 63, 512.0F);
            this.drawImage(DRAWINGS_0, 250, 72, 214, 432, 87, 64, 512.0F);
            this.drawImage(DRAWINGS_0, 250, 132, 300, 248, 71, 62, 512.0F);
            GL11.glPopMatrix();
         }

         this.writeFromTxt();
         break;
      case ICEDRAGONEGG:
         if (bookPages == 0) {
            GL11.glPushMatrix();
            this.drawImage(DRAWINGS_0, 20, 95, 327, 366, 24, 33, 512.0F);
            this.drawImage(DRAWINGS_0, 60, 95, 303, 366, 24, 33, 512.0F);
            this.drawImage(DRAWINGS_0, 95, 95, 351, 366, 24, 33, 512.0F);
            this.drawImage(DRAWINGS_0, 135, 95, 375, 366, 24, 33, 512.0F);
            GL11.glPopMatrix();
         }

         if (bookPages == 1) {
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawImage(DRAWINGS_0, 25, 20, 303, 309, 71, 56, 512.0F);
            GL11.glPopMatrix();
         }

         this.writeFromTxt();
         break;
      case TAMEDDRAGONS:
         if (bookPages == 0) {
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawImage(DRAWINGS_0, 144, 90, 389, 1, 50, 50, 512.0F);
            GL11.glPopMatrix();
            this.drawItemStack(new ItemStack(Items.BONE), 145, 124, 1.5F);
            this.drawItemStack(new ItemStack(Items.PORKCHOP), 145, 107, 1.5F);
            this.drawItemStack(new ItemStack(Items.BONE), 145, 91, 1.5F);
            this.drawItemStack(new ItemStack(Items.PORKCHOP), 161, 124, 1.5F);
            this.drawItemStack(new ItemStack(Items.BONE), 161, 107, 1.5F);
            this.drawItemStack(new ItemStack(Items.PORKCHOP), 161, 91, 1.5F);
            this.drawItemStack(new ItemStack(Items.BONE), 177, 124, 1.5F);
            this.drawItemStack(new ItemStack(Items.PORKCHOP), 177, 107, 1.5F);
            this.drawItemStack(new ItemStack(Items.BONE), 177, 91, 1.5F);
            this.drawItemStack(new ItemStack(ModItems.dragon_meal), 151, 78, 2.0F);
         }

         if (bookPages == 1) {
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawImage(DRAWINGS_0, 144, 0, 389, 1, 50, 50, 512.0F);
            GL11.glPopMatrix();
            this.drawItemStack(new ItemStack(ModItems.dragon_skull), 161, 17, 1.5F);
            this.drawItemStack(new ItemStack(Items.STICK), 161, 32, 1.5F);
            this.drawItemStack(new ItemStack(ModItems.dragon_stick), 151, 10, 2.0F);
         }

         if (bookPages == 2) {
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawItemStack(new ItemStack(ModBlocks.fire_lily), 5, 14, 2.5F);
            this.drawItemStack(new ItemStack(ModBlocks.frost_lily), 30, 14, 2.5F);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawImage(DRAWINGS_0, 144, 0, 389, 1, 50, 50, 512.0F);
            GL11.glPopMatrix();
            boolean drawFire = Minecraft.getMinecraft().player.ticksExisted % 40 < 20;
            this.drawItemStack(new ItemStack(drawFire ? ModBlocks.fire_lily : ModBlocks.frost_lily), 161, 17, 1.5F);
            this.drawItemStack(new ItemStack(Items.BOWL), 161, 32, 1.5F);
            this.drawItemStack(new ItemStack(drawFire ? Items.BLAZE_ROD : Items.PRISMARINE_CRYSTALS), 177, 17, 1.5F);
            this.drawItemStack(new ItemStack(drawFire ? ModItems.fire_stew : ModItems.frost_stew), 151, 10, 2.0F);
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawImage(DRAWINGS_0, 144, 65, 389, 1, 50, 50, 512.0F);
            GL11.glPopMatrix();
            this.drawItemStack(new ItemStack(Items.STICK), 144, 97, 1.5F);
            GL11.glPushMatrix();
            GL11.glScalef(0.9F, 0.9F, 1.0F);
            this.drawItemStack(new ItemStack(ModItems.dragonbone), 180, 110, 1.5F);
            this.drawItemStack(new ItemStack(ModItems.dragonbone), 180, 92, 1.5F);
            this.drawItemStack(new ItemStack(ModItems.dragonbone), 198, 92, 1.5F);
            this.drawItemStack(new ItemStack(ModItems.dragonbone), 198, 74, 1.5F);
            GL11.glPopMatrix();
            this.drawItemStack(new ItemStack(ModItems.dragon_horn), 151, 60, 2.0F);
         }

         if (bookPages == 3) {
            int j = 18;
            ItemStack var73 = new ItemStack(ModItems.dragon_armor_iron, 1, 0);
            j = j + 16;
            this.drawItemStack(var73, j, 60, 1.5F);
            var73 = new ItemStack(ModItems.dragon_armor_iron, 1, 1);
            j = j + 16;
            this.drawItemStack(var73, j, 60, 1.5F);
            var73 = new ItemStack(ModItems.dragon_armor_iron, 1, 2);
            j = j + 16;
            this.drawItemStack(var73, j, 60, 1.5F);
            var73 = new ItemStack(ModItems.dragon_armor_iron, 1, 3);
            j = j + 16;
            this.drawItemStack(var73, j, 60, 1.5F);
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawImage(DRAWINGS_0, 144, 10, 389, 1, 50, 50, 512.0F);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(0.9F, 0.9F, 1.0F);
            this.drawItemStack(new ItemStack(ModItems.dragonbone), 160, 12, 1.5F);
            this.drawItemStack(new ItemStack(ModItems.dragonbone), 180, 31, 1.5F);
            this.drawItemStack(new ItemStack(Items.IRON_INGOT), 199, 50, 1.5F);
            GL11.glPopMatrix();
            this.drawItemStack(new ItemStack(ModItems.dragon_flute), 151, 18, 2.0F);
         }

         this.writeFromTxt();
         break;
      case MATERIALS:
         if (bookPages == 0) {
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawItemStack(new ItemStack(ModItems.dragonscales_red), 18, 16, 2.5F);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawItemStack(new ItemStack(ModItems.dragonbone), 70, 10, 2.5F);
            GL11.glPopMatrix();
            this.drawItemStack(new ItemStack(ModItems.witherbone), 112, 70, 2.5F);
            int j = 18;
            ItemStack var63 = new ItemStack(EnumDragonArmor.armor_red.helmet);
            j = j + 16;
            this.drawItemStack(var63, j, 115, 1.5F);
            var63 = new ItemStack(EnumDragonArmor.armor_red.chestplate);
            j = j + 16;
            this.drawItemStack(var63, j, 115, 1.5F);
            var63 = new ItemStack(EnumDragonArmor.armor_red.leggings);
            j = j + 16;
            this.drawItemStack(var63, j, 115, 1.5F);
            var63 = new ItemStack(EnumDragonArmor.armor_red.boots);
            j = j + 16;
            this.drawItemStack(var63, j, 115, 1.5F);
         }

         if (bookPages == 1) {
            int j = 1;
            ItemStack var67 = new ItemStack(ModItems.dragonbone_sword);
            j = j + 16;
            this.drawItemStack(var67, j, 14, 1.5F);
            var67 = new ItemStack(ModItems.dragonbone_pickaxe);
            j = j + 16;
            this.drawItemStack(var67, j, 14, 1.5F);
            var67 = new ItemStack(ModItems.dragonbone_axe);
            j = j + 16;
            this.drawItemStack(var67, j, 14, 1.5F);
            var67 = new ItemStack(ModItems.dragonbone_shovel);
            j = j + 16;
            this.drawItemStack(var67, j, 14, 1.5F);
            var67 = new ItemStack(ModItems.dragonbone_hoe);
            j = j + 16;
            this.drawItemStack(var67, j, 14, 1.5F);
            var67 = new ItemStack(ModItems.dragonbone_bow);
            j = j + 16;
            this.drawItemStack(var67, j, 14, 1.5F);
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawItemStack(new ItemStack(ModItems.fire_dragon_flesh), 18, 24, 2.5F);
            this.drawItemStack(new ItemStack(ModItems.fire_dragon_heart), 70, 14, 2.5F);
            this.drawItemStack(new ItemStack(ModItems.dragon_skull), 70, 39, 2.5F);
            GL11.glPopMatrix();
         }

         if (bookPages == 2) {
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawItemStack(new ItemStack(ModItems.fire_dragon_blood), 18, 24, 2.5F);
            GL11.glPopMatrix();
         }

         this.writeFromTxt();
         break;
      case ALCHEMY:
         this.writeFromTxt();
         if (bookPages == 0) {
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawItemStack(new ItemStack(ModItems.fire_dragon_blood), 10, 24, 2.5F);
            this.drawItemStack(new ItemStack(ModItems.ice_dragon_blood), 26, 24, 2.5F);
            GL11.glPopMatrix();
            boolean drawFire = Minecraft.getMinecraft().player.ticksExisted % 40 < 20;
            this.drawItemStack(new ItemStack(ModItems.dragonbone_sword), 161, 17, 1.5F);
            this.drawItemStack(new ItemStack(drawFire ? ModItems.fire_dragon_blood : ModItems.ice_dragon_blood), 161, 32, 1.5F);
            this.drawItemStack(new ItemStack(drawFire ? ModItems.dragonbone_sword_fire : ModItems.dragonbone_sword_ice), 151, 10, 2.0F);
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawImage(DRAWINGS_0, 144, 0, 389, 1, 50, 50, 512.0F);
            GL11.glPopMatrix();
         }
         break;
      case VILLAGERS:
         if (bookPages == 0) {
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawImage(DRAWINGS_0, 35, 50, 388, 52, 58, 36, 512.0F);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawItemStack(new ItemStack(ModItems.fishing_spear), 70, 2, 2.5F);
            GL11.glPopMatrix();
         }

         this.writeFromTxt();
         break;
      case HIPPOGRYPH:
         if (bookPages == 0) {
            GL11.glPushMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(0.8F, 0.8F, 1.0F);
            this.drawImage(DRAWINGS_0, 29, 150, 303, 151, 61, 36, 512.0F);
            this.drawImage(DRAWINGS_0, 91, 150, 364, 151, 61, 36, 512.0F);
            this.drawImage(DRAWINGS_0, 151, 150, 425, 151, 61, 36, 512.0F);
            this.drawImage(DRAWINGS_0, 29, 190, 303, 187, 61, 36, 512.0F);
            this.drawImage(DRAWINGS_0, 91, 190, 364, 187, 61, 36, 512.0F);
            this.drawImage(DRAWINGS_0, 151, 190, 425, 187, 61, 36, 512.0F);
            this.drawImage(DRAWINGS_0, 90, 230, 425, 223, 61, 35, 512.0F);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawItemStack(new ItemStack(Items.RABBIT_FOOT), 70, 20, 2.5F);
            GL11.glPopMatrix();
            GL11.glPopMatrix();
         }

         if (bookPages == 1) {
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawItemStack(new ItemStack(Items.STICK), 16, 24, 2.5F);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawImage(DRAWINGS_0, 144, 10, 389, 1, 50, 50, 512.0F);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(0.9F, 0.9F, 1.0F);
            this.drawItemStack(new ItemStack(Items.FEATHER), 160, 31, 1.5F);
            int drawType = Minecraft.getMinecraft().player.ticksExisted % 60 > 40 ? 2 : (Minecraft.getMinecraft().player.ticksExisted % 60 > 20 ? 1 : 0);
            this.drawItemStack(new ItemStack(drawType == 0 ? Items.IRON_HORSE_ARMOR : (drawType == 1 ? Items.GOLDEN_HORSE_ARMOR : Items.DIAMOND_HORSE_ARMOR)), 180, 31, 1.5F);
            this.drawItemStack(new ItemStack(Items.FEATHER), 199, 31, 1.5F);
            GL11.glPopMatrix();
            this.drawItemStack(new ItemStack(drawType == 0 ? ModItems.iron_hippogryph_armor : (drawType == 1 ? ModItems.gold_hippogryph_armor : ModItems.diamond_hippogryph_armor)), 151, 18, 2.0F);
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawItemStack(new ItemStack(Items.RABBIT_STEW), 70, 23, 2.5F);
            GL11.glPopMatrix();
         }

         this.writeFromTxt();
         break;
      case GORGON:
         if (bookPages == 0) {
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawImage(DRAWINGS_0, 10, 89, 473, 117, 19, 34, 512.0F);
            this.drawImage(DRAWINGS_0, 50, 78, 399, 106, 28, 45, 512.0F);
            this.drawImage(DRAWINGS_0, 100, 89, 455, 117, 18, 34, 512.0F);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawImage(DRAWINGS_0, 144, 70, 389, 1, 50, 50, 512.0F);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(0.9F, 0.9F, 1.0F);
            this.drawItemStack(new ItemStack(Items.STRING), 160, 97, 1.5F);
            this.drawItemStack(new ItemStack(Items.LEATHER), 180, 97, 1.5F);
            this.drawItemStack(new ItemStack(Items.STRING), 199, 97, 1.5F);
            GL11.glPopMatrix();
            this.drawItemStack(new ItemStack(ModItems.blindfold), 171, 65, 2.0F);
         }

         if (bookPages == 1) {
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawItemStack(new ItemStack(ModItems.gorgon_head), 16, 12, 2.5F);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.7F, 1.7F, 1.0F);
            this.drawImage(DRAWINGS_0, 37, 95, 473, 117, 19, 34, 512.0F);
            this.drawImage(DRAWINGS_0, 60, 95, 455, 117, 18, 34, 512.0F);
            GL11.glPopMatrix();
         }

         this.writeFromTxt();
         break;
      case PIXIE:
         if (bookPages == 0) {
            GL11.glPushMatrix();
            this.drawImage(DRAWINGS_0, 20, 60, 371, 258, 47, 35, 512.0F);
            this.drawImage(DRAWINGS_0, 42, 95, 416, 258, 45, 35, 512.0F);
            this.drawImage(DRAWINGS_0, 67, 60, 462, 258, 47, 35, 512.0F);
            this.drawImage(DRAWINGS_0, 88, 95, 370, 293, 47, 35, 512.0F);
            this.drawImage(DRAWINGS_0, 110, 60, 416, 293, 47, 35, 512.0F);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawItemStack(new ItemStack(ModItems.pixie_dust), 70, 10, 2.5F);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(0.9F, 0.9F, 1.0F);
            GL11.glTranslatef(20.0F, 24.0F, 0.0F);
            GL11.glPushMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawImage(DRAWINGS_0, 144, 100, 389, 1, 50, 50, 512.0F);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(0.9F, 0.9F, 1.0F);
            this.drawItemStack(new ItemStack(Blocks.GLASS), 160, 113, 1.5F);
            this.drawItemStack(new ItemStack(Blocks.GLASS), 199, 113, 1.5F);
            this.drawItemStack(new ItemStack(Blocks.PLANKS), 180, 113, 1.5F);
            this.drawItemStack(new ItemStack(Blocks.GLASS), 160, 131, 1.5F);
            this.drawItemStack(new ItemStack(Blocks.GLASS), 199, 131, 1.5F);
            this.drawItemStack(new ItemStack(Blocks.GLASS), 180, 150, 1.5F);
            this.drawItemStack(new ItemStack(Blocks.GLASS), 160, 150, 1.5F);
            this.drawItemStack(new ItemStack(Blocks.GLASS), 199, 150, 1.5F);
            GL11.glPopMatrix();
            this.drawItemStack(new ItemStack(ModBlocks.jar_empty), 171, 85, 2.0F);
            GL11.glPopMatrix();
            GL11.glPopMatrix();
         }

         if (bookPages == 1) {
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawItemStack(new ItemStack(ModItems.ambrosia), 19, 22, 2.5F);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawImage(DRAWINGS_0, 144, 100, 389, 1, 50, 50, 512.0F);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(0.9F, 0.9F, 1.0F);
            this.drawItemStack(new ItemStack(ModItems.pixie_dust), 180, 131, 1.5F);
            this.drawItemStack(new ItemStack(Items.GOLDEN_CARROT), 160, 131, 1.5F);
            this.drawItemStack(new ItemStack(Items.SPECKLED_MELON), 199, 131, 1.5F);
            this.drawItemStack(new ItemStack(Items.BOWL), 180, 150, 1.5F);
            GL11.glPopMatrix();
            this.drawItemStack(new ItemStack(ModItems.ambrosia), 171, 85, 2.0F);
            GL11.glPopMatrix();
         }

         this.writeFromTxt();
         break;
      case CYCLOPS:
         if (bookPages == 0) {
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.5F);
            this.drawImage(DRAWINGS_0, 185, 8, 399, 328, 24, 63, 512.0F);
            GL11.glPopMatrix();
         }

         if (bookPages == 1) {
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.5F);
            this.drawImage(DRAWINGS_0, 50, 35, 423, 328, 24, 63, 512.0F);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawImage(DRAWINGS_0, 144, 50, 389, 1, 50, 50, 512.0F);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(0.9F, 0.9F, 1.0F);
            this.drawItemStack(new ItemStack(Items.LEATHER_HELMET), 180, 76, 1.5F);
            this.drawItemStack(new ItemStack(Blocks.WOOL), 160, 76, 1.5F);
            this.drawItemStack(new ItemStack(Blocks.WOOL), 199, 76, 1.5F);
            this.drawItemStack(new ItemStack(Blocks.WOOL), 160, 57, 1.5F);
            this.drawItemStack(new ItemStack(Blocks.WOOL), 180, 57, 1.5F);
            this.drawItemStack(new ItemStack(Blocks.WOOL), 199, 57, 1.5F);
            GL11.glPopMatrix();
            this.drawItemStack(new ItemStack(ModItems.sheep_helmet), 165, 45, 2.0F);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawImage(DRAWINGS_0, 144, 95, 389, 1, 50, 50, 512.0F);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(0.9F, 0.9F, 1.0F);
            this.drawItemStack(new ItemStack(Items.LEATHER_CHESTPLATE), 180, 126, 1.5F);
            this.drawItemStack(new ItemStack(Blocks.WOOL), 160, 126, 1.5F);
            this.drawItemStack(new ItemStack(Blocks.WOOL), 199, 126, 1.5F);
            this.drawItemStack(new ItemStack(Blocks.WOOL), 160, 107, 1.5F);
            this.drawItemStack(new ItemStack(Blocks.WOOL), 199, 107, 1.5F);
            this.drawItemStack(new ItemStack(Blocks.WOOL), 160, 145, 1.5F);
            this.drawItemStack(new ItemStack(Blocks.WOOL), 180, 145, 1.5F);
            this.drawItemStack(new ItemStack(Blocks.WOOL), 199, 145, 1.5F);
            GL11.glPopMatrix();
            this.drawItemStack(new ItemStack(ModItems.sheep_chestplate), 165, 95, 2.0F);
            GL11.glPopMatrix();
         }

         if (bookPages == 2) {
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.5F);
            this.drawImage(DRAWINGS_0, 185, 30, 447, 328, 24, 63, 512.0F);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawImage(DRAWINGS_0, 13, 24, 389, 1, 50, 50, 512.0F);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(0.9F, 0.9F, 1.0F);
            this.drawItemStack(new ItemStack(Items.LEATHER_LEGGINGS), 34, 46, 1.5F);
            this.drawItemStack(new ItemStack(Blocks.WOOL), 14, 46, 1.5F);
            this.drawItemStack(new ItemStack(Blocks.WOOL), 53, 46, 1.5F);
            this.drawItemStack(new ItemStack(Blocks.WOOL), 14, 27, 1.5F);
            this.drawItemStack(new ItemStack(Blocks.WOOL), 34, 27, 1.5F);
            this.drawItemStack(new ItemStack(Blocks.WOOL), 53, 27, 1.5F);
            this.drawItemStack(new ItemStack(Blocks.WOOL), 14, 65, 1.5F);
            this.drawItemStack(new ItemStack(Blocks.WOOL), 53, 65, 1.5F);
            GL11.glPopMatrix();
            this.drawItemStack(new ItemStack(ModItems.sheep_leggings), 64, 27, 2.0F);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawImage(DRAWINGS_0, 13, 84, 389, 1, 50, 50, 512.0F);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(0.9F, 0.9F, 1.0F);
            this.drawItemStack(new ItemStack(Items.LEATHER_BOOTS), 34, 94, 1.5F);
            this.drawItemStack(new ItemStack(Blocks.WOOL), 14, 113, 1.5F);
            this.drawItemStack(new ItemStack(Blocks.WOOL), 53, 113, 1.5F);
            this.drawItemStack(new ItemStack(Blocks.WOOL), 14, 94, 1.5F);
            this.drawItemStack(new ItemStack(Blocks.WOOL), 53, 94, 1.5F);
            GL11.glPopMatrix();
            this.drawItemStack(new ItemStack(ModItems.sheep_boots), 64, 73, 2.0F);
            GL11.glPopMatrix();
         }

         this.writeFromTxt();
         break;
      case SIREN:
         if (bookPages == 0) {
            GL11.glPushMatrix();
            GL11.glScalef(1.25F, 1.25F, 1.25F);
            this.drawImage(DRAWINGS_1, 190, 25, 0, 0, 25, 42, 512.0F);
            this.drawImage(DRAWINGS_1, 220, 15, 25, 0, 25, 42, 512.0F);
            this.drawImage(DRAWINGS_1, 255, 25, 50, 0, 25, 42, 512.0F);
            this.drawImage(DRAWINGS_1, 190, 135, 0, 42, 26, 28, 512.0F);
            this.drawImage(DRAWINGS_1, 220, 125, 26, 42, 26, 28, 512.0F);
            this.drawImage(DRAWINGS_1, 255, 135, 52, 42, 26, 28, 512.0F);
            GL11.glPopMatrix();
         }

         if (bookPages == 1) {
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawItemStack(new ItemStack(ModItems.earplugs), 18, 40, 2.5F);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(0.9F, 0.9F, 1.0F);
            GL11.glPushMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawImage(DRAWINGS_0, 160, 0, 389, 1, 50, 50, 512.0F);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(0.9F, 0.9F, 1.0F);
            this.drawItemStack(new ItemStack(Item.getItemFromBlock(Blocks.WOODEN_BUTTON)), 180, 20, 1.5F);
            this.drawItemStack(new ItemStack(Item.getItemFromBlock(Blocks.WOODEN_BUTTON)), 215, 20, 1.5F);
            GL11.glPopMatrix();
            this.drawItemStack(new ItemStack(ModItems.earplugs), 170, 10, 2.0F);
            GL11.glPopMatrix();
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawItemStack(new ItemStack(ModItems.shiny_scales), 123, 75, 1.5F);
            GL11.glPopMatrix();
         }

         this.writeFromTxt();
         break;
      case HIPPOCAMPUS:
         if (bookPages == 0) {
            GL11.glPushMatrix();
            this.drawImage(DRAWINGS_1, 210, 25, 0, 70, 57, 49, 512.0F);
            this.drawImage(DRAWINGS_1, 265, 25, 57, 70, 57, 49, 512.0F);
            this.drawImage(DRAWINGS_1, 320, 25, 0, 119, 57, 49, 512.0F);
            this.drawImage(DRAWINGS_1, 210, 80, 57, 119, 57, 49, 512.0F);
            this.drawImage(DRAWINGS_1, 265, 80, 0, 168, 57, 49, 512.0F);
            this.drawImage(DRAWINGS_1, 320, 80, 57, 168, 57, 49, 512.0F);
            GL11.glPopMatrix();
         }

         if (bookPages == 1) {
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawItemStack(new ItemStack(Blocks.SPONGE), 37, 33, 1.5F);
            this.drawItemStack(new ItemStack(Items.PRISMARINE_CRYSTALS), 37, 73, 1.5F);
            GL11.glPopMatrix();
         }

         if (bookPages == 2) {
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawItemStack(new ItemStack(Items.STICK), 35, 25, 1.5F);
            this.drawItemStack(new ItemStack(ModItems.shiny_scales), 35, 75, 1.5F);
            GL11.glPopMatrix();
         }

         this.writeFromTxt();
         break;
      case DEATHWORM:
         if (bookPages == 0) {
            GL11.glPushMatrix();
            this.drawImage(DRAWINGS_1, 230, 25, 0, 217, 133, 16, 512.0F);
            this.drawImage(DRAWINGS_1, 230, 50, 0, 233, 133, 16, 512.0F);
            this.drawImage(DRAWINGS_1, 230, 75, 0, 249, 133, 16, 512.0F);
            GL11.glPopMatrix();
         }

         if (bookPages == 1) {
            GL11.glPushMatrix();
            this.drawImage(DRAWINGS_1, 25, 95, 0, 265, 148, 44, 512.0F);
            this.drawImage(DRAWINGS_1, 250, 5, 0, 309, 81, 162, 512.0F);
            GL11.glPopMatrix();
         }

         if (bookPages == 2) {
            int drawType = Minecraft.getMinecraft().player.ticksExisted % 60 > 40 ? 2 : (Minecraft.getMinecraft().player.ticksExisted % 60 > 20 ? 1 : 0);
            GL11.glPushMatrix();
            GL11.glScalef(2.5F, 2.5F, 1.0F);
            this.drawItemStack(new ItemStack(ModItems.deathworm_chitin, 1, drawType), 17, 30, 1.5F);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawItemStack(new ItemStack(drawType == 2 ? ModItems.deathworm_red_helmet : (drawType == 1 ? ModItems.deathworm_white_helmet : ModItems.deathworm_yellow_helmet)), 92, 8, 1.5F);
            this.drawItemStack(new ItemStack(drawType == 2 ? ModItems.deathworm_red_chestplate : (drawType == 1 ? ModItems.deathworm_white_chestplate : ModItems.deathworm_yellow_chestplate)), 112, 8, 1.5F);
            this.drawItemStack(new ItemStack(drawType == 2 ? ModItems.deathworm_red_leggings : (drawType == 1 ? ModItems.deathworm_white_leggings : ModItems.deathworm_yellow_leggings)), 132, 8, 1.5F);
            this.drawItemStack(new ItemStack(drawType == 2 ? ModItems.deathworm_red_boots : (drawType == 1 ? ModItems.deathworm_white_boots : ModItems.deathworm_yellow_boots)), 152, 8, 1.5F);
            this.drawItemStack(new ItemStack(ModItems.deathworm_egg), 125, 42, 1.5F);
            GL11.glPopMatrix();
         }

         if (bookPages == 3) {
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawItemStack(new ItemStack(ModItems.deathworm_egg, 1, 1), 125, 4, 1.5F);
            this.drawItemStack(new ItemStack(Items.FISHING_ROD), 115, 55, 1.5F);
            this.drawItemStack(new ItemStack(Items.FISHING_ROD), 135, 55, 1.5F);
            GL11.glPopMatrix();
         }

         this.writeFromTxt();
         break;
      case COCKATRICE:
         if (bookPages == 0) {
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawImage(DRAWINGS_1, 155, 10, 114, 0, 88, 36, 512.0F);
            this.drawImage(DRAWINGS_1, 155, 45, 114, 36, 88, 36, 512.0F);
            GL11.glPopMatrix();
         }

         if (bookPages == 1) {
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawImage(DRAWINGS_0, 18, 10, 389, 1, 50, 50, 512.0F);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(0.9F, 0.9F, 1.0F);
            this.drawItemStack(new ItemStack(Items.STRING), 20, 30, 1.5F);
            this.drawItemStack(new ItemStack(Items.LEATHER), 40, 30, 1.5F);
            this.drawItemStack(new ItemStack(Items.STRING), 59, 30, 1.5F);
            GL11.glPopMatrix();
            this.drawItemStack(new ItemStack(ModItems.blindfold), 60, 18, 2.0F);
            this.drawItemStack(new ItemStack(ModItems.witherbone), 30, 58, 2.5F);
            this.drawItemStack(new ItemStack(ModItems.rotten_egg), 109, 18, 2.5F);
         }

         this.writeFromTxt();
         break;
      case STYMPHALIANBIRD:
         if (bookPages == 0) {
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawImage(DRAWINGS_1, 34, 46, 114, 72, 59, 37, 512.0F);
            this.drawImage(DRAWINGS_1, 155, 35, 114, 109, 67, 35, 512.0F);
            GL11.glPopMatrix();
            this.drawItemStack(new ItemStack(ModItems.stymphalian_bird_feather), 109, 60, 2.5F);
         }

         if (bookPages == 1) {
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawImage(DRAWINGS_0, 18, 10, 389, 1, 50, 50, 512.0F);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(0.9F, 0.9F, 1.0F);
            this.drawItemStack(new ItemStack(Items.FLINT), 40, 13, 1.5F);
            this.drawItemStack(new ItemStack(Items.STICK), 40, 30, 1.5F);
            this.drawItemStack(new ItemStack(ModItems.stymphalian_bird_feather), 40, 49, 1.5F);
            GL11.glPopMatrix();
            this.drawItemStack(new ItemStack(ModItems.stymphalian_arrow), 60, 18, 2.0F);
         }

         this.writeFromTxt();
         break;
      case TROLL:
         if (bookPages == 0) {
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawImage(DRAWINGS_1, 15, 60, 156, 211, 25, 58, 512.0F);
            this.drawImage(DRAWINGS_1, 50, 55, 181, 211, 25, 58, 512.0F);
            this.drawImage(DRAWINGS_1, 85, 60, 206, 211, 25, 58, 512.0F);
            this.drawImage(DRAWINGS_1, 155, 22, 114, 145, 24, 66, 512.0F);
            this.drawImage(DRAWINGS_1, 190, 19, 188, 142, 47, 69, 512.0F);
            GL11.glPopMatrix();
         }

         if (bookPages == 1) {
            int i = Minecraft.getMinecraft().player.ticksExisted % (EnumTroll.Weapon.values().length * 20) / 20;
            this.drawItemStack(new ItemStack(EnumTroll.Weapon.values()[i].item), 30, 7, 2.5F);
            int j = Minecraft.getMinecraft().player.ticksExisted % (EnumTroll.values().length * 20) / 20;
            this.drawItemStack(new ItemStack(EnumTroll.values()[j].leather), 100, 30, 2.5F);
            this.drawItemStack(new ItemStack(ModItems.troll_tusk), 120, 30, 2.5F);
         }

         if (bookPages == 2) {
            int j = Minecraft.getMinecraft().player.ticksExisted % (EnumTroll.values().length * 20) / 20;
            this.drawItemStack(new ItemStack(EnumTroll.values()[j].helmet), 27, 15, 1.5F);
            this.drawItemStack(new ItemStack(EnumTroll.values()[j].chestplate), 47, 15, 1.5F);
            this.drawItemStack(new ItemStack(EnumTroll.values()[j].leggings), 67, 15, 1.5F);
            this.drawItemStack(new ItemStack(EnumTroll.values()[j].boots), 87, 15, 1.5F);
         }

         this.writeFromTxt();
         break;
      case MYRMEX:
         if (bookPages == 0) {
            GL11.glPushMatrix();
            GL11.glScalef(1.51F, 1.51F, 1.0F);
            this.drawImage(DRAWINGS_1, 137, 10, 202, 16, 57, 21, 512.0F);
            this.drawImage(DRAWINGS_1, 195, 10, 278, 16, 57, 21, 512.0F);
            GL11.glPopMatrix();
         }

         if (bookPages == 1) {
            GL11.glPushMatrix();
            GL11.glScalef(1.51F, 1.51F, 1.0F);
            this.drawImage(DRAWINGS_1, 7, 17, 202, 37, 59, 21, 512.0F);
            this.drawImage(DRAWINGS_1, 65, 17, 278, 37, 59, 21, 512.0F);
            this.drawImage(DRAWINGS_1, 7, 77, 202, 58, 59, 21, 512.0F);
            this.drawImage(DRAWINGS_1, 65, 77, 278, 58, 59, 21, 512.0F);
            this.drawImage(DRAWINGS_1, 145, 20, 278, 103, 43, 45, 512.0F);
            this.drawImage(DRAWINGS_1, 195, 20, 321, 103, 43, 45, 512.0F);
            GL11.glPopMatrix();
         }

         if (bookPages == 2) {
            GL11.glPushMatrix();
            GL11.glScalef(1.51F, 1.51F, 1.0F);
            this.drawImage(DRAWINGS_1, 25, 13, 202, 79, 76, 24, 512.0F);
            this.drawImage(DRAWINGS_1, 25, 40, 278, 79, 76, 24, 512.0F);
            GL11.glPopMatrix();
            this.drawItemStack(new ItemStack(ModItems.myrmex_desert_chitin), 125, 43, 2.0F);
            this.drawItemStack(new ItemStack(ModItems.myrmex_jungle_chitin), 155, 43, 2.0F);
            int i = 133;
            boolean jungle = Minecraft.getMinecraft().player.ticksExisted % 60 > 30;
            ItemStack var10001 = new ItemStack(jungle ? ModItems.myrmex_jungle_shovel : ModItems.myrmex_desert_shovel);
            i = i + 16;
            this.drawItemStack(var10001, i, 100, 1.51F);
            var10001 = new ItemStack(jungle ? ModItems.myrmex_jungle_pickaxe : ModItems.myrmex_desert_pickaxe);
            i = i + 16;
            this.drawItemStack(var10001, i, 100, 1.5F);
            var10001 = new ItemStack(jungle ? ModItems.myrmex_jungle_axe : ModItems.myrmex_desert_axe);
            i = i + 16;
            this.drawItemStack(var10001, i, 100, 1.5F);
            var10001 = new ItemStack(jungle ? ModItems.myrmex_jungle_sword : ModItems.myrmex_desert_sword);
            i = i + 16;
            this.drawItemStack(var10001, i, 100, 1.5F);
            var10001 = new ItemStack(jungle ? ModItems.myrmex_jungle_sword_venom : ModItems.myrmex_desert_sword_venom);
            i = i + 16;
            this.drawItemStack(var10001, i, 100, 1.5F);
            var10001 = new ItemStack(jungle ? ModItems.myrmex_jungle_hoe : ModItems.myrmex_desert_hoe);
            i = i + 16;
            this.drawItemStack(var10001, i, 100, 1.5F);
            int j = 148;
            var10001 = new ItemStack(jungle ? ModItems.myrmex_jungle_helmet : ModItems.myrmex_desert_helmet);
            j = j + 16;
            this.drawItemStack(var10001, j, 115, 1.5F);
            var10001 = new ItemStack(jungle ? ModItems.myrmex_jungle_chestplate : ModItems.myrmex_desert_chestplate);
            j = j + 16;
            this.drawItemStack(var10001, j, 115, 1.5F);
            var10001 = new ItemStack(jungle ? ModItems.myrmex_jungle_leggings : ModItems.myrmex_desert_leggings);
            j = j + 16;
            this.drawItemStack(var10001, j, 115, 1.5F);
            var10001 = new ItemStack(jungle ? ModItems.myrmex_jungle_boots : ModItems.myrmex_desert_boots);
            j = j + 16;
            this.drawItemStack(var10001, j, 115, 1.5F);
         }

         if (bookPages == 3) {
            this.drawItemStack(new ItemStack(ModItems.myrmex_stinger), 35, 22, 2.5F);
            this.drawItemStack(new ItemStack(ModItems.myrmex_desert_resin), 25, 64, 2.0F);
            this.drawItemStack(new ItemStack(ModItems.myrmex_jungle_resin), 55, 64, 2.0F);
         }

         if (bookPages == 4) {
            this.drawItemStack(new ItemStack(ModItems.myrmex_desert_staff), 25, 73, 2.0F);
            this.drawItemStack(new ItemStack(ModItems.myrmex_jungle_staff), 55, 73, 2.0F);
            this.drawItemStack(new ItemStack(ModItems.myrmex_desert_egg), 125, 90, 2.0F);
            this.drawItemStack(new ItemStack(ModItems.myrmex_jungle_egg), 155, 90, 2.0F);
         }

         this.writeFromTxt();
         break;
      case AMPHITHERE:
         if (bookPages == 0) {
            GL11.glPushMatrix();
            GL11.glScalef(0.75F, 0.75F, 0.75F);
            this.drawImage(DRAWINGS_1, 70, 97, 257, 163, 136, 93, 512.0F);
            this.drawImage(DRAWINGS_1, 270, 50, 148, 267, 120, 51, 512.0F);
            this.drawImage(DRAWINGS_1, 380, 50, 148, 318, 120, 51, 512.0F);
            this.drawImage(DRAWINGS_1, 270, 100, 148, 369, 120, 51, 512.0F);
            this.drawImage(DRAWINGS_1, 380, 100, 148, 420, 120, 51, 512.0F);
            this.drawImage(DRAWINGS_1, 330, 150, 268, 267, 120, 51, 512.0F);
            GL11.glPopMatrix();
         }

         if (bookPages == 2) {
            this.drawItemStack(new ItemStack(ModItems.amphithere_feather), 30, 20, 2.5F);
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawImage(DRAWINGS_0, 18, 70, 389, 1, 50, 50, 512.0F);
            GL11.glScalef(0.65F, 0.65F, 0.65F);
            this.drawItemStack(new ItemStack(Items.FLINT), 36, 73, 1.5F);
            this.drawItemStack(new ItemStack(Items.STICK), 36, 89, 1.5F);
            this.drawItemStack(new ItemStack(ModItems.amphithere_feather), 36, 106, 1.5F);
            GL11.glPopMatrix();
            this.drawItemStack(new ItemStack(ModItems.amphithere_arrow), 60, 65, 2.0F);
         }

         this.writeFromTxt();
         break;
      case SEASERPENT:
         if (bookPages == 0) {
            GL11.glPushMatrix();
            GL11.glScalef(0.75F, 0.75F, 0.75F);
            this.drawImage(DRAWINGS_1, 290, 5, 422, 0, 90, 64, 512.0F);
            this.drawImage(DRAWINGS_1, 380, 5, 422, 64, 90, 64, 512.0F);
            this.drawImage(DRAWINGS_1, 290, 70, 422, 128, 90, 64, 512.0F);
            this.drawImage(DRAWINGS_1, 380, 70, 422, 192, 90, 64, 512.0F);
            this.drawImage(DRAWINGS_1, 290, 140, 422, 256, 90, 64, 512.0F);
            this.drawImage(DRAWINGS_1, 380, 140, 422, 320, 90, 64, 512.0F);
            this.drawImage(DRAWINGS_1, 345, 210, 422, 384, 90, 64, 512.0F);
            GL11.glPopMatrix();
         }

         if (bookPages == 1) {
            this.drawImage(DRAWINGS_1, 60, 90, 337, 0, 70, 83, 512.0F);
            int j = Minecraft.getMinecraft().player.ticksExisted % (EnumSeaSerpent.values().length * 20) / 20;
            this.drawItemStack(new ItemStack(EnumSeaSerpent.values()[j].scale), 130, 40, 2.5F);
            this.drawItemStack(new ItemStack(ModItems.sea_serpent_fang), 90, 40, 2.5F);
         }

         if (bookPages == 2) {
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            this.drawImage(DRAWINGS_0, 18, 30, 389, 1, 50, 50, 512.0F);
            GL11.glScalef(0.65F, 0.65F, 0.65F);
            int j = Minecraft.getMinecraft().player.ticksExisted % (EnumSeaSerpent.values().length * 20) / 20;
            this.drawItemStack(new ItemStack(ModItems.sea_serpent_fang), 36, 32, 1.5F);
            this.drawItemStack(new ItemStack(Items.STICK), 36, 48, 1.5F);
            this.drawItemStack(new ItemStack(EnumSeaSerpent.values()[j].scale), 36, 66, 1.5F);
            this.drawItemStack(new ItemStack(EnumSeaSerpent.values()[j].helmet), 34, 125, 1.5F);
            this.drawItemStack(new ItemStack(EnumSeaSerpent.values()[j].chestplate), 50, 125, 1.5F);
            this.drawItemStack(new ItemStack(EnumSeaSerpent.values()[j].leggings), 66, 125, 1.5F);
            this.drawItemStack(new ItemStack(EnumSeaSerpent.values()[j].boots), 82, 125, 1.5F);
            GL11.glPopMatrix();
            this.drawItemStack(new ItemStack(ModItems.sea_serpent_arrow), 60, 33, 2.0F);
         }

         this.writeFromTxt();
      }

   }

   public void writeFromTxt() {
      String filePath = "assets/iceandfire/lang/bestiary/" + Minecraft.getMinecraft().gameSettings.language + "/";
      if (this.getClass().getClassLoader().getResourceAsStream(filePath) == null) {
         filePath = "assets/iceandfire/lang/bestiary/en_US/";
      }

      String fileName = this.pageType.toString().toLowerCase() + "_" + this.bookPages + ".txt";
      InputStream fileReader = this.getClass().getClassLoader().getResourceAsStream(filePath + fileName);

      try {
         BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileReader));
         String line = null;
         int linenumber = 0;

         while((line = bufferedReader.readLine()) != null) {
            GL11.glPushMatrix();
            if (this.usingVanillaFont()) {
               GL11.glScalef(0.945F, 0.945F, 0.945F);
               GL11.glTranslatef(0.0F, 5.5F, 0.0F);
            }

            if (linenumber <= 19) {
               this.font.drawString(line, 15.0F, (float)(20 + linenumber * 10), 3158064, false);
            } else {
               this.font.drawString(line, 220.0F, (float)((linenumber - 19) * 10), 3158064, false);
            }

            ++linenumber;
            GL11.glPopMatrix();
         }

         fileReader.close();
      } catch (Exception var7) {
         var7.printStackTrace();
      }

      GL11.glPushMatrix();
      GL11.glScalef(2.0F, 2.0F, 2.0F);
      this.font.drawString(StatCollector.translateToLocal("bestiary." + this.pageType.toString().toLowerCase()), 10.0F, 2.0F, 8025450, false);
      GL11.glPopMatrix();
   }

   private boolean usingVanillaFont() {
      return this.font == Minecraft.getMinecraft().fontRenderer;
   }

   public void drawImage(ResourceLocation texture, int x, int y, int u, int v, int width, int height, float scale) {
      this.mc.renderEngine.bindTexture(texture);
      drawModalRectWithCustomSizedTexture(x, y, (float)u, (float)v, width, height, scale, scale);
   }

   private void drawItemStack(ItemStack stack, int x, int y, float scale) {
      GL11.glPushMatrix();
      GL11.glScalef(scale, scale, scale);
      GlStateManager.translate(0.0F, 0.0F, 32.0F);
      this.zLevel = 200.0F;
      this.itemRender.zLevel = 200.0F;
      FontRenderer font = null;
      if (!stack.isEmpty()) {
         font = stack.getItem().getFontRenderer(stack);
      }

      if (font == null) {
         font = this.fontRenderer;
      }

      this.itemRender.renderItemAndEffectIntoGUI(stack, x, y);
      this.itemRender.renderItemOverlayIntoGUI(font, stack, x, y, (String)null);
      this.zLevel = 0.0F;
      this.itemRender.zLevel = 0.0F;
      GL11.glPopMatrix();
   }

   protected void actionPerformed(GuiButton button) {
      if (button.id == 0) {
         label50: {
            if (this.index) {
               if (this.indexPages <= 0) {
                  break label50;
               }
            } else if (this.pageType == null || this.bookPages <= 0) {
               break label50;
            }

            if (this.index) {
               --this.indexPages;
            } else {
               --this.bookPages;
            }
         }
      }

      if (button.id == 1) {
         label41: {
            if (this.index) {
               if (this.indexPages >= this.indexPagesTotal - 1) {
                  break label41;
               }
            } else if (this.pageType == null || this.bookPages >= this.pageType.pages) {
               break label41;
            }

            if (this.index) {
               ++this.indexPages;
            } else {
               ++this.bookPages;
            }
         }
      }

      if (button.id >= 2 && this.indexButtons.get(button.id - 2) != null && this.allPageTypes.get(button.id - 2) != null && button instanceof IndexPageButton) {
         this.index = false;
         this.indexPages = 0;
         this.bookPages = 0;
         this.pageType = (EnumBestiaryPages)this.allPageTypes.get(button.id - 2);
      }

   }
}
