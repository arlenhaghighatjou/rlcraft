package vazkii.quark.misc.client.gui;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.util.ResourceLocation;
import net.lax1dude.eaglercraft.v1_8.Keyboard;
import vazkii.arl.network.NetworkHandler;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.network.message.MessageTuneNoteBlock;
import vazkii.quark.misc.feature.NoteBlockInterface;
import vazkii.quark.misc.feature.NoteBlocksMobSounds;

public class GuiNoteBlock extends GuiScreen {
   private static final ResourceLocation noteblockResource = new ResourceLocation("quark", "textures/misc/noteblock.png");
   private static final int TEXTURE_WIDTH = 512;
   private static final int TEXTURE_HEIGHT = 256;
   private static final String[] KEY_NAMES = new String[]{"F#", "G", "G#", "A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#"};
   private static final int[] KEY_BINDS_QWERTY = new int[]{16, 17, 18, 19, 20, 21, 22, 23, 24, 30, 31, 32, 33, 34, 35, 36, 37, 38, 44, 45, 46, 47, 48, 49, 50};
   private static final int[] KEY_BINDS_AZERTY = new int[]{30, 44, 18, 19, 20, 21, 22, 23, 24, 25, 16, 31, 32, 33, 34, 35, 36, 37, 38, 50, 17, 45, 46, 47, 48};
   private static final int[] KEY_BINDS_DVORAK = new int[]{25, 21, 33, 34, 46, 19, 38, 24, 18, 22, 23, 32, 35, 20, 49, 31, 16, 36, 37, 45, 48, 50, 17, 47, 44};
   private final List<GuiNoteBlock.Key> whiteKeys = new ArrayList();
   private final List<GuiNoteBlock.Key> blackKeys = new ArrayList();
   protected GuiNoteBlock.Key hoveredKey = null;
   protected boolean hoversNoteBlock = false;
   protected final TileEntityNote noteBlock;
   protected final GuiNoteBlock.CoordinateHolder coords = new GuiNoteBlock.CoordinateHolder();

   public GuiNoteBlock(TileEntityNote noteBlock) {
      this.noteBlock = noteBlock;
   }

   public void initGui() {
      this.whiteKeys.clear();
      this.blackKeys.clear();
      this.blackKeys.add(new GuiNoteBlock.BlackKey(0, 0));
      this.blackKeys.add(new GuiNoteBlock.BlackKey(1, 2));
      this.blackKeys.add(new GuiNoteBlock.BlackKey(2, 4));
      this.blackKeys.add(new GuiNoteBlock.BlackKey(4, 7));
      this.blackKeys.add(new GuiNoteBlock.BlackKey(5, 9));
      this.blackKeys.add(new GuiNoteBlock.BlackKey(7, 12));
      this.blackKeys.add(new GuiNoteBlock.BlackKey(8, 14));
      this.blackKeys.add(new GuiNoteBlock.BlackKey(9, 16));
      this.blackKeys.add(new GuiNoteBlock.BlackKey(11, 19));
      this.blackKeys.add(new GuiNoteBlock.BlackKey(12, 21));
      this.blackKeys.add(new GuiNoteBlock.BlackKey(14, 24));
      this.whiteKeys.add(new GuiNoteBlock.WhiteKey(0, 1, 1));
      this.whiteKeys.add(new GuiNoteBlock.WhiteKey(1, 3, 1));
      this.whiteKeys.add(new GuiNoteBlock.WhiteKey(2, 5, 2));
      this.whiteKeys.add(new GuiNoteBlock.WhiteKey(3, 6, 0));
      this.whiteKeys.add(new GuiNoteBlock.WhiteKey(4, 8, 1));
      this.whiteKeys.add(new GuiNoteBlock.WhiteKey(5, 10, 2));
      this.whiteKeys.add(new GuiNoteBlock.WhiteKey(6, 11, 0));
      this.whiteKeys.add(new GuiNoteBlock.WhiteKey(7, 13, 1));
      this.whiteKeys.add(new GuiNoteBlock.WhiteKey(8, 15, 1));
      this.whiteKeys.add(new GuiNoteBlock.WhiteKey(9, 17, 2));
      this.whiteKeys.add(new GuiNoteBlock.WhiteKey(10, 18, 0));
      this.whiteKeys.add(new GuiNoteBlock.WhiteKey(11, 20, 1));
      this.whiteKeys.add(new GuiNoteBlock.WhiteKey(12, 22, 2));
      this.whiteKeys.add(new GuiNoteBlock.WhiteKey(13, 23, 0));
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      if (this.noteBlock != null && this.noteBlock.getWorld().getTileEntity(this.noteBlock.getPos()) == this.noteBlock) {
         int panelWidth = 320;
         int panelHeight = 102;
         int left = this.width / 2 - panelWidth / 2;
         int top = this.height / 2 - panelHeight / 2;
         GlStateManager.pushMatrix();
         GlStateManager.translate((float)left, (float)top, 0.0F);
         this.mc.renderEngine.bindTexture(noteblockResource);
         drawModalRectWithCustomSizedTexture(0, 0, 0.0F, 0.0F, panelWidth, panelHeight, 512.0F, 256.0F);
         this.coords.baseX = 46;
         this.coords.baseY = 8;
         this.coords.mouseX = mouseX - left;
         this.coords.mouseY = mouseY - top;
         this.hoveredKey = null;
         this.hoversNoteBlock = this.coords.mouseX >= 9 && this.coords.mouseX < 40 && this.coords.mouseY >= 9 && this.coords.mouseY < 40;

         for(GuiNoteBlock.Key k : this.blackKeys) {
            this.mc.renderEngine.bindTexture(noteblockResource);
            if (k.renderKey(this.mc, true, this.coords)) {
               this.hoveredKey = k;
            }
         }

         this.coords.baseX += 7;

         for(GuiNoteBlock.Key k : this.whiteKeys) {
            this.mc.renderEngine.bindTexture(noteblockResource);
            if (k.renderKey(this.mc, this.hoveredKey == null, this.coords)) {
               this.hoveredKey = k;
            }
         }

         this.mc.renderEngine.bindTexture(noteblockResource);
         float scale = 2.0F;
         GlStateManager.scale(scale, scale, scale);
         ItemStack head = this.getHead();
         if (!head.isEmpty()) {
            RenderHelper.enableGUIStandardItemLighting();
            this.mc.getRenderItem().renderItemAndEffectIntoGUI(head, 4, 30);
         } else {
            drawModalRectWithCustomSizedTexture(4, 30, (float)(this.getNote() * 16), (float)panelHeight, 16, 16, 512.0F, 256.0F);
         }

         GlStateManager.scale(1.0F / scale, 1.0F / scale, 1.0F / scale);
         scale = 1.8F;
         RenderHelper.enableGUIStandardItemLighting();
         GlStateManager.translate(9.0F, 9.0F, 0.0F);
         GlStateManager.scale(scale, scale, scale);
         this.mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(Blocks.NOTEBLOCK), 0, 0);
         GlStateManager.popMatrix();
         if (this.hoversNoteBlock) {
            vazkii.arl.util.RenderHelper.renderTooltip(mouseX, mouseY, Collections.singletonList(I18n.format("quarkmisc.incrementNote", new Object[0])));
         }

      } else {
         this.mc.displayGuiScreen((GuiScreen)null);
      }
   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      if (mouseButton == 0) {
         if (this.hoveredKey != null) {
            NetworkHandler.INSTANCE.sendToServer(new MessageTuneNoteBlock(this.noteBlock, false, this.hoveredKey.clicks));
         } else if (this.hoversNoteBlock) {
            NetworkHandler.INSTANCE.sendToServer(new MessageTuneNoteBlock(this.noteBlock, true, (byte)0));
         }
      }

   }

   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      super.keyTyped(typedChar, keyCode);

      for(GuiNoteBlock.Key k : this.whiteKeys) {
         this.play(k, keyCode);
      }

      for(GuiNoteBlock.Key k : this.blackKeys) {
         this.play(k, keyCode);
      }

   }

   private void play(GuiNoteBlock.Key k, int code) {
      int expected = getKey(k.clicks);
      if (expected == code) {
         NetworkHandler.INSTANCE.sendToServer(new MessageTuneNoteBlock(this.noteBlock, false, k.clicks));
      }

   }

   private int getNote() {
      IBlockState state = this.noteBlock.getWorld().getBlockState(this.noteBlock.getPos().down());
      Material material = state.getMaterial();
      Block block = state.getBlock();
      int note = 0;
      if (material == Material.ROCK) {
         note = 1;
      } else if (material == Material.SAND) {
         note = 2;
      } else if (material == Material.GLASS) {
         note = 3;
      } else if (material == Material.WOOD) {
         note = 4;
      }

      if (block == Blocks.CLAY) {
         note = 5;
      } else if (block == Blocks.GOLD_BLOCK) {
         note = 6;
      } else if (block == Blocks.WOOL) {
         note = 7;
      } else if (block == Blocks.PACKED_ICE) {
         note = 8;
      } else if (block == Blocks.BONE_BLOCK) {
         note = 9;
      }

      return note;
   }

   private ItemStack getHead() {
      if (!ModuleLoader.isFeatureEnabled(NoteBlocksMobSounds.class)) {
         return ItemStack.EMPTY;
      } else {
         int type = NoteBlocksMobSounds.getSkullType(this.noteBlock.getWorld(), this.noteBlock.getPos());
         return type != -1 && type != 3 ? new ItemStack(Items.SKULL, 1, type) : ItemStack.EMPTY;
      }
   }

   private static int getKey(int index) {
      int[] keys = KEY_BINDS_QWERTY;
      if (NoteBlockInterface.keyboardLayout.equalsIgnoreCase("azerty")) {
         keys = KEY_BINDS_AZERTY;
      } else if (NoteBlockInterface.keyboardLayout.equalsIgnoreCase("dvorak")) {
         keys = KEY_BINDS_DVORAK;
      }

      return keys[index];
   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   private static class BlackKey extends GuiNoteBlock.Key {
      public BlackKey(int position, int clicks) {
         super(position * 18, 0, 14, 45, clicks);
      }

      boolean renderKey(Minecraft mc, boolean canHover, GuiNoteBlock.CoordinateHolder c) {
         boolean hovered = canHover && this.isHovered(c) || Keyboard.isKeyDown(GuiNoteBlock.getKey(this.clicks));
         int u = 374;
         int v = hovered ? this.h : 0;
         Gui.drawModalRectWithCustomSizedTexture(c.baseX + this.x, c.baseY + this.y, (float)u, (float)v, this.w, this.h, 512.0F, 256.0F);
         if (hovered) {
            this.renderNote(mc, c);
         }

         return hovered;
      }
   }

   private static class CoordinateHolder {
      public int baseX;
      public int baseY;
      public int mouseX;
      public int mouseY;

      private CoordinateHolder() {
      }
   }

   private abstract static class Key {
      public final int x;
      public final int y;
      public final int w;
      public final int h;
      public final byte clicks;
      public final String desc;

      public Key(int x, int y, int w, int h, int clicks) {
         this.x = x;
         this.y = y;
         this.w = w;
         this.h = h;
         this.clicks = (byte)clicks;
         this.desc = GuiNoteBlock.KEY_NAMES[clicks];
      }

      final boolean isHovered(GuiNoteBlock.CoordinateHolder c) {
         int x = c.baseX + this.x;
         int y = c.baseY + this.y;
         return c.mouseX >= x && c.mouseY >= y && c.mouseX < x + this.w && c.mouseY < y + this.h;
      }

      final void renderNote(Minecraft mc, GuiNoteBlock.CoordinateHolder c) {
         int x = c.baseX + this.x;
         int y = c.baseY + this.y;
         int sw = mc.fontRenderer.getStringWidth(this.desc);
         int color = Color.HSBtoRGB((float)(24 - this.clicks) / 24.0F - 0.6F, 1.0F, 1.0F);
         mc.fontRenderer.drawString(this.desc, x + this.w / 2 - sw / 2, y + this.h - 24, color);
         String s = Integer.toString(this.clicks);
         sw = mc.fontRenderer.getStringWidth(s);
         mc.fontRenderer.drawString(s, x + this.w / 2 - sw / 2, y + this.h - 13, this instanceof GuiNoteBlock.BlackKey ? 13421772 : 3355443);
         GlStateManager.color(1.0F, 1.0F, 1.0F);
      }

      abstract boolean renderKey(Minecraft var1, boolean var2, GuiNoteBlock.CoordinateHolder var3);
   }

   private static class WhiteKey extends GuiNoteBlock.Key {
      private final int type;

      public WhiteKey(int position, int clicks, int type) {
         super(position * 18, 0, 18, 86, clicks);
         this.type = type;
      }

      boolean renderKey(Minecraft mc, boolean canHover, GuiNoteBlock.CoordinateHolder c) {
         boolean hovered = canHover && this.isHovered(c) || Keyboard.isKeyDown(GuiNoteBlock.getKey(this.clicks));
         int u = 320 + this.w * this.type;
         int v = hovered ? this.h : 0;
         Gui.drawModalRectWithCustomSizedTexture(c.baseX + this.x, c.baseY + this.y, (float)u, (float)v, this.w, this.h, 512.0F, 256.0F);
         if (hovered) {
            this.renderNote(mc, c);
         }

         return hovered;
      }
   }
}
