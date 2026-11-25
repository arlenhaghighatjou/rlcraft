package noppes.vc.client.gui;

import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.vc.CommonUtils;
import noppes.vc.client.Client;
import noppes.vc.constants.PacketServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class GuiBook extends GuiScreen {
   private static final Logger logger = LogManager.getLogger();
   private static final ResourceLocation bookGuiTextures = new ResourceLocation("textures/gui/book.png");
   private final EntityPlayer editingPlayer;
   private ItemStack bookObj;
   private final boolean bookIsUnsigned;
   private boolean bookIsModified;
   private boolean bookGettingSigned;
   private int updateCount;
   private int bookImageWidth = 192;
   private int bookImageHeight = 192;
   private int bookTotalPages = 1;
   private int currPage;
   private NBTTagList bookPages;
   private String bookTitle = "";
   private GuiBook.NextPageButton buttonNextPage;
   private GuiBook.NextPageButton buttonPreviousPage;
   private GuiButton buttonDone;
   private GuiButton buttonSign;
   private GuiButton buttonFinalize;
   private GuiButton buttonCancel;
   private static final String __OBFID = "CL_00000744";
   private int x;
   private int y;
   private int z;

   public GuiBook(EntityPlayer par1EntityPlayer, ItemStack item, int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.editingPlayer = par1EntityPlayer;
      this.bookObj = item;
      this.bookIsUnsigned = item.getItem() == Items.WRITABLE_BOOK;
      if (item.hasTagCompound()) {
         NBTTagCompound nbttagcompound = item.getTagCompound();
         this.bookPages = nbttagcompound.getTagList("pages", 8);
         if (this.bookPages != null) {
            this.bookPages = this.bookPages.copy();
            this.bookTotalPages = this.bookPages.tagCount();
            if (this.bookTotalPages < 1) {
               this.bookTotalPages = 1;
            }
         }
      }

      if (this.bookPages == null && this.bookIsUnsigned) {
         this.bookPages = new NBTTagList();
         this.bookPages.appendTag(new NBTTagString(""));
         this.bookTotalPages = 1;
      }

   }

   public void updateScreen() {
      super.updateScreen();
      ++this.updateCount;
   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   public void initGui() {
      this.buttonList.clear();
      Keyboard.enableRepeatEvents(true);
      if (this.bookIsUnsigned) {
         this.buttonList.add(this.buttonSign = new GuiButton(3, this.width / 2 - 100, 4 + this.bookImageHeight, 98, 20, I18n.format("book.signButton", new Object[0])));
         this.buttonList.add(this.buttonDone = new GuiButton(0, this.width / 2 + 2, 4 + this.bookImageHeight, 98, 20, I18n.format("gui.done", new Object[0])));
         this.buttonList.add(this.buttonFinalize = new GuiButton(5, this.width / 2 - 100, 4 + this.bookImageHeight, 98, 20, I18n.format("book.finalizeButton", new Object[0])));
         this.buttonList.add(this.buttonCancel = new GuiButton(4, this.width / 2 + 2, 4 + this.bookImageHeight, 98, 20, I18n.format("gui.cancel", new Object[0])));
      } else {
         this.buttonList.add(this.buttonDone = new GuiButton(0, this.width / 2 - 100, 4 + this.bookImageHeight, 200, 20, I18n.format("gui.done", new Object[0])));
      }

      int i = (this.width - this.bookImageWidth) / 2;
      byte b0 = 2;
      this.buttonList.add(this.buttonNextPage = new GuiBook.NextPageButton(1, i + 120, b0 + 154, true));
      this.buttonList.add(this.buttonPreviousPage = new GuiBook.NextPageButton(2, i + 38, b0 + 154, false));
      this.updateButtons();
   }

   public void onGuiClosed() {
      Keyboard.enableRepeatEvents(false);
   }

   private void updateButtons() {
      this.buttonNextPage.visible = !this.bookGettingSigned && (this.currPage < this.bookTotalPages - 1 || this.bookIsUnsigned);
      this.buttonPreviousPage.visible = !this.bookGettingSigned && this.currPage > 0;
      this.buttonDone.visible = !this.bookIsUnsigned || !this.bookGettingSigned;
      if (this.bookIsUnsigned) {
         this.buttonSign.visible = !this.bookGettingSigned;
         this.buttonCancel.visible = this.bookGettingSigned;
         this.buttonFinalize.visible = this.bookGettingSigned;
         this.buttonFinalize.enabled = this.bookTitle.trim().length() > 0;
      }

   }

   private void sendBookToServer(boolean sign) {
      if (this.bookIsUnsigned && this.bookIsModified && this.bookPages != null) {
         while(this.bookPages.tagCount() > 1) {
            String s = this.bookPages.getStringTagAt(this.bookPages.tagCount() - 1);
            if (s.length() != 0) {
               break;
            }

            this.bookPages.removeTag(this.bookPages.tagCount() - 1);
         }

         if (this.bookObj.hasTagCompound()) {
            NBTTagCompound nbttagcompound = this.bookObj.getTagCompound();
            nbttagcompound.setTag("pages", this.bookPages);
         } else {
            this.bookObj.setTagInfo("pages", this.bookPages);
         }

         if (sign) {
            this.bookObj.setTagInfo("author", new NBTTagString(this.editingPlayer.getName()));
            this.bookObj.setTagInfo("title", new NBTTagString(this.bookTitle.trim()));
            this.bookObj = CommonUtils.ChangeItemStack(this.bookObj, Items.WRITTEN_BOOK);
         }

         Client.sendData(PacketServer.SAVE_BOOK, this.x, this.y, this.z, sign, this.bookObj.writeToNBT(new NBTTagCompound()));
      }

   }

   protected void actionPerformed(GuiButton p_146284_1_) {
      if (p_146284_1_.enabled) {
         if (p_146284_1_.id == 0) {
            this.mc.displayGuiScreen((GuiScreen)null);
            this.sendBookToServer(false);
         } else if (p_146284_1_.id == 3 && this.bookIsUnsigned) {
            this.bookGettingSigned = true;
         } else if (p_146284_1_.id == 1) {
            if (this.currPage < this.bookTotalPages - 1) {
               ++this.currPage;
            } else if (this.bookIsUnsigned) {
               this.addNewPage();
               if (this.currPage < this.bookTotalPages - 1) {
                  ++this.currPage;
               }
            }
         } else if (p_146284_1_.id == 2) {
            if (this.currPage > 0) {
               --this.currPage;
            }
         } else if (p_146284_1_.id == 5 && this.bookGettingSigned) {
            this.sendBookToServer(true);
            this.mc.displayGuiScreen((GuiScreen)null);
         } else if (p_146284_1_.id == 4 && this.bookGettingSigned) {
            this.bookGettingSigned = false;
         }

         this.updateButtons();
      }

   }

   private void addNewPage() {
      if (this.bookPages != null && this.bookPages.tagCount() < 50) {
         this.bookPages.appendTag(new NBTTagString(""));
         ++this.bookTotalPages;
         this.bookIsModified = true;
      }

   }

   protected void keyTyped(char par1, int par2) throws IOException {
      super.keyTyped(par1, par2);
      if (this.bookIsUnsigned) {
         if (this.bookGettingSigned) {
            this.keyTypedInTitle(par1, par2);
         } else {
            this.keyTypedInBook(par1, par2);
         }
      }

   }

   private void keyTypedInBook(char p_146463_1_, int p_146463_2_) {
      switch(p_146463_1_) {
      case '\u0016':
         this.pageInsertIntoCurrent(GuiScreen.getClipboardString());
         return;
      default:
         switch(p_146463_2_) {
         case 14:
            String s = this.pageGetCurrent();
            if (s.length() > 0) {
               this.pageSetCurrent(s.substring(0, s.length() - 1));
            }

            return;
         case 28:
         case 156:
            this.pageInsertIntoCurrent("\n");
            return;
         default:
            if (ChatAllowedCharacters.isAllowedCharacter(p_146463_1_)) {
               this.pageInsertIntoCurrent(Character.toString(p_146463_1_));
            }

         }
      }
   }

   private void keyTypedInTitle(char p_146460_1_, int p_146460_2_) {
      switch(p_146460_2_) {
      case 14:
         if (!this.bookTitle.isEmpty()) {
            this.bookTitle = this.bookTitle.substring(0, this.bookTitle.length() - 1);
            this.updateButtons();
         }

         return;
      case 28:
      case 156:
         if (!this.bookTitle.isEmpty()) {
            this.sendBookToServer(true);
            this.mc.displayGuiScreen((GuiScreen)null);
         }

         return;
      default:
         if (this.bookTitle.length() < 16 && ChatAllowedCharacters.isAllowedCharacter(p_146460_1_)) {
            this.bookTitle = this.bookTitle + Character.toString(p_146460_1_);
            this.updateButtons();
            this.bookIsModified = true;
         }

      }
   }

   private String pageGetCurrent() {
      return this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount() ? this.bookPages.getStringTagAt(this.currPage) : "";
   }

   private void pageSetCurrent(String p_146457_1_) {
      if (this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount()) {
         this.bookPages.set(this.currPage, new NBTTagString(p_146457_1_));
         this.bookIsModified = true;
      }

   }

   private void pageInsertIntoCurrent(String p_146459_1_) {
      String s1 = this.pageGetCurrent();
      String s2 = s1 + p_146459_1_;
      int i = this.fontRenderer.getWordWrappedHeight(s2 + "" + TextFormatting.BLACK + "_", 118);
      if (i <= 118 && s2.length() < 256) {
         this.pageSetCurrent(s2);
      }

   }

   public void drawScreen(int par1, int par2, float par3) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(bookGuiTextures);
      int k = (this.width - this.bookImageWidth) / 2;
      byte b0 = 2;
      this.drawTexturedModalRect(k, b0, 0, 0, this.bookImageWidth, this.bookImageHeight);
      if (this.bookGettingSigned) {
         String s = this.bookTitle;
         if (this.bookIsUnsigned) {
            if (this.updateCount / 6 % 2 == 0) {
               s = s + "" + TextFormatting.BLACK + "_";
            } else {
               s = s + "" + TextFormatting.GRAY + "_";
            }
         }

         String s1 = I18n.format("book.editTitle", new Object[0]);
         int l = this.fontRenderer.getStringWidth(s1);
         this.fontRenderer.drawString(s1, k + 36 + (116 - l) / 2, b0 + 16 + 16, 0);
         int i1 = this.fontRenderer.getStringWidth(s);
         this.fontRenderer.drawString(s, k + 36 + (116 - i1) / 2, b0 + 48, 0);
         String s2 = I18n.format("book.byAuthor", new Object[]{this.editingPlayer.getName()});
         int j1 = this.fontRenderer.getStringWidth(s2);
         this.fontRenderer.drawString(TextFormatting.DARK_GRAY + s2, k + 36 + (116 - j1) / 2, b0 + 48 + 10, 0);
         String s3 = I18n.format("book.finalizeWarning", new Object[0]);
         this.fontRenderer.drawSplitString(s3, k + 36, b0 + 80, 116, 0);
      } else {
         String s = I18n.format("book.pageIndicator", new Object[]{this.currPage + 1, this.bookTotalPages});
         String s1 = "";
         if (this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount()) {
            s1 = this.bookPages.getStringTagAt(this.currPage);
         }

         if (this.bookIsUnsigned) {
            if (this.fontRenderer.getBidiFlag()) {
               s1 = s1 + "_";
            } else if (this.updateCount / 6 % 2 == 0) {
               s1 = s1 + "" + TextFormatting.BLACK + "_";
            } else {
               s1 = s1 + "" + TextFormatting.GRAY + "_";
            }
         }

         int l = this.fontRenderer.getStringWidth(s);
         this.fontRenderer.drawString(s, k - l + this.bookImageWidth - 44, b0 + 16, 0);
         this.fontRenderer.drawSplitString(s1, k + 36, b0 + 16 + 16, 116, 0);
      }

      super.drawScreen(par1, par2, par3);
   }

   @SideOnly(Side.CLIENT)
   static class NextPageButton extends GuiButton {
      private final boolean isForward;
      private static final String __OBFID = "CL_00000745";

      public NextPageButton(int par1, int par2, int par3, boolean par4) {
         super(par1, par2, par3, 23, 13, "");
         this.isForward = par4;
      }

      public void drawButton(Minecraft p_146112_1_, int p_146112_2_, int p_146112_3_) {
         if (this.visible) {
            boolean flag = p_146112_2_ >= this.x && p_146112_3_ >= this.y && p_146112_2_ < this.x + this.width && p_146112_3_ < this.y + this.height;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            p_146112_1_.getTextureManager().bindTexture(GuiBook.bookGuiTextures);
            int k = 0;
            int l = 192;
            if (flag) {
               k += 23;
            }

            if (!this.isForward) {
               l += 13;
            }

            this.drawTexturedModalRect(this.x, this.y, k, l, 23, 13);
         }

      }
   }
}
