package xzeroair.trinkets.client.gui.entityPropertiesGui;

import com.google.common.collect.Lists;
import java.awt.Dimension;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.client.GuiScrollingList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.api.TrinketHelper;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.capabilities.race.EntityProperties;
import xzeroair.trinkets.traits.AbilityHandler;
import xzeroair.trinkets.traits.abilities.interfaces.IAbilityInterface;
import xzeroair.trinkets.util.config.ConfigHelper;
import xzeroair.trinkets.util.helpers.ColorHelper;

@SideOnly(Side.CLIENT)
public class GuiAttributesScreen extends GuiScreen {
   public EntityPlayer player;
   public EntityProperties properties;
   protected GuiPropertiesSlider r;
   protected GuiPropertiesSlider g;
   protected GuiPropertiesSlider b;
   protected GuiPropertiesSlider a;
   public ColorHelper color;
   public int buttonPressed;
   public int redSlider = 5;
   public int greenSlider = 6;
   public int blueSlider = 7;
   public int alphaSlider = 8;
   protected boolean flip = false;
   public static ResourceLocation background = null;
   private float oldMouseX;
   private float oldMouseY;
   private GuiAttributesScrollingList abilitySelectionList;
   private GuiScrollingList abilityDescriptions;
   private GuiScrollingList raceAttributes;
   private GuiScrollingList raceBonuses;
   private int selected = -1;
   private AbilityHandler.AbilityHolder selectedAbility;
   private int listWidth;
   private ArrayList<AbilityHandler.AbilityHolder> abilities;
   private int buttonMargin = 1;
   private int numButtons = GuiAttributesScreen.SortType.values().length;
   private String lastFilterText = "";
   private boolean sorted = false;
   private GuiAttributesScreen.SortType sortType = GuiAttributesScreen.SortType.NORMAL;
   public static final DecimalFormat DECIMALFORMAT = new DecimalFormat("#.##");

   public GuiAttributesScreen(EntityPlayer player) {
      this.player = player;
      this.color = new ColorHelper();
      this.properties = Capabilities.getEntityProperties(player);
   }

   public void selectAbilityIndex(int index) {
      if (index != this.selected) {
         this.selected = index;
         this.selectedAbility = index >= 0 && index <= this.abilities.size() ? (AbilityHandler.AbilityHolder)this.abilities.get(this.selected) : null;
         this.updateCache();
      }
   }

   public boolean abilityIndexSelected(int index) {
      return index == this.selected;
   }

   public void initGui() {
      this.buttonList.clear();
      super.initGui();
      this.addButton(new GuiPropertiesButton(1, 2, 2, 50, 20, "<--"));
      this.addButton(new GuiPropertiesButton(2, this.width - 16, 2, 14, 20, TextFormatting.RED + "X"));
      this.abilities = Lists.newArrayList(this.properties.getAbilityHandler().getActiveAbilities().values());
      int slotHeight = 20;
      if (this.abilities != null && !this.abilities.isEmpty()) {
         for(AbilityHandler.AbilityHolder ability : this.abilities) {
            String name = ability.getAbility().getDisplayName();
            this.listWidth = Math.max(this.listWidth, this.getFontRenderer().getStringWidth(name) + 10);
         }

         this.listWidth = Math.min(this.listWidth, 150);
         this.abilitySelectionList = new GuiAttributesScrollingList(0, this, this.abilities, this.listWidth, 20);
         this.updateCache();
      }
   }

   protected void mouseReleased(int mouseX, int mouseY, int state) {
      super.mouseReleased(mouseX, mouseY, state);
      this.buttonPressed = 0;
   }

   protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
      super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
   }

   protected void mouseClicked(int x, int y, int button) throws IOException {
      super.mouseClicked(x, y, button);
   }

   protected void keyTyped(char c, int keyCode) throws IOException {
      super.keyTyped(c, keyCode);
      if (keyCode == Minecraft.getMinecraft().gameSettings.keyBindInventory.getKeyCode()) {
         this.player.closeScreen();
         this.displayNormalInventory();
      }

   }

   public void displayNormalInventory() {
      GuiInventory gui = new GuiInventory(this.mc.player);
      this.mc.displayGuiScreen(gui);
   }

   public void updateScreen() {
      super.updateScreen();
   }

   protected void actionPerformed(GuiButton button) throws IOException {
      this.buttonPressed = button.id;
      super.actionPerformed(button);
      if (button.id == 1) {
         this.mc.player.openGui(Trinkets.instance, 2, this.mc.player.world, 0, 0, 0);
      }

      if (button.id == 2) {
         this.displayNormalInventory();
      }

   }

   public void onGuiClosed() {
      super.onGuiClosed();
   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.oldMouseX = (float)mouseX;
      this.oldMouseY = (float)mouseY;
      this.drawDefaultBackground();
      super.drawScreen(mouseX, mouseY, partialTicks);
      if (this.properties != null) {
         if (this.abilitySelectionList != null) {
            this.abilitySelectionList.drawScreen(mouseX, mouseY, partialTicks);
         }

         if (this.abilityDescriptions != null) {
            this.abilityDescriptions.drawScreen(mouseX, mouseY, partialTicks);
         }

         if (this.raceAttributes != null) {
            this.raceAttributes.drawScreen(mouseX, mouseY, partialTicks);
         }

         if (this.raceBonuses != null) {
            this.raceBonuses.drawScreen(mouseX, mouseY, partialTicks);
         }

      }
   }

   public void handleMouseInput() throws IOException {
      int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
      int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
      super.handleMouseInput();
      if (this.abilityDescriptions != null) {
         this.abilityDescriptions.handleMouseInput(mouseX, mouseY);
      }

      if (this.abilitySelectionList != null) {
         this.abilitySelectionList.handleMouseInput(mouseX, mouseY);
      }

   }

   public int drawLine(String line, int offset, int shifty) {
      this.fontRenderer.drawString(line, offset, shifty, 14151146);
      return shifty + 10;
   }

   public Minecraft getMinecraftInstance() {
      return this.mc;
   }

   public FontRenderer getFontRenderer() {
      return this.fontRenderer;
   }

   private void updateCache() {
      this.abilityDescriptions = null;
      this.raceAttributes = null;
      ResourceLocation logoPath2 = null;
      Dimension logoDims2 = new Dimension(0, 0);
      List<String> lines2 = new ArrayList();

      try {
         String[] attributeConfig = this.properties.getCurrentRace().getRaceAttributes().getAttributes();
         if (attributeConfig != null && attributeConfig.length > 0) {
            for(String entry : attributeConfig) {
               ConfigHelper.AttributeEntry attributeShell = ConfigHelper.getAttributeEntry(entry);
               if (attributeShell != null) {
                  String name = attributeShell.getAttribute();
                  double amount = attributeShell.getAmount();
                  int operation = attributeShell.getOperation();
                  if (this.player != null && this.player.getAttributeMap().getAttributeInstanceByName(name) != null) {
                     boolean flag = false;
                     double d1;
                     if (operation != 1 && operation != 2) {
                        d1 = amount;
                     } else {
                        d1 = amount * 100.0D;
                     }

                     TextComponentTranslation AttrName = new TextComponentTranslation("attribute.name." + name, new Object[0]);
                     if (flag) {
                        TextComponentTranslation never = new TextComponentTranslation("attribute.modifier.equals." + operation, new Object[]{DECIMALFORMAT.format(d1), AttrName.getFormattedText()});
                        lines2.add(" " + never.getFormattedText());
                     } else if (amount > 0.0D) {
                        TextComponentTranslation addition = new TextComponentTranslation("attribute.modifier.plus." + operation, new Object[]{DECIMALFORMAT.format(d1), AttrName.getFormattedText()});
                        addition.getStyle().setColor(TextFormatting.BLUE);
                        String s = addition.getFormattedText();
                        lines2.add(" " + s);
                     } else if (amount < 0.0D) {
                        d1 = d1 * -1.0D;
                        TextComponentTranslation subtraction = new TextComponentTranslation("attribute.modifier.take." + operation, new Object[]{DECIMALFORMAT.format(d1), AttrName.getFormattedText()});
                        subtraction.getStyle().setColor(TextFormatting.RED);
                        String s = subtraction.getFormattedText();
                        lines2.add(" " + s);
                     }
                  }
               }
            }
         }
      } catch (Exception var23) {
         ;
      }

      this.raceAttributes = new GuiAttributesScreen.Info(this.listWidth + 40, lines2, logoPath2, logoDims2, 32, this.height - 18 + 4, this.width - this.listWidth - 50);
      if (this.selectedAbility != null) {
         ResourceLocation logoPath = null;
         Dimension logoDims = new Dimension(0, 0);
         List<String> lines = new ArrayList();

         try {
            String source = this.selectedAbility.getSourceID();
            IAbilityInterface abilityInstance = this.selectedAbility.getAbility();
            TrinketHelper.SlotInformation slotInfo = this.selectedAbility.getInfo();
            lines.add(TextFormatting.GOLD + abilityInstance.getDisplayName());
            lines.add(TextFormatting.AQUA + source);
            lines.add((Object)null);
            abilityInstance.getDescription(lines);
         } catch (Exception var22) {
            var22.printStackTrace();
         }

         this.abilityDescriptions = new GuiAttributesScreen.Info(this.width - this.listWidth - 50 - this.listWidth - 30, lines, logoPath, logoDims);
      }
   }

   private class Info extends GuiScrollingList {
      @Nullable
      private ResourceLocation logoPath;
      private Dimension logoDims;
      private List<ITextComponent> lines = null;

      public Info(int width, List<String> lines, @Nullable ResourceLocation logoPath, Dimension logoDims, int top, int bottom, int left) {
         super(GuiAttributesScreen.this.getMinecraftInstance(), width, GuiAttributesScreen.this.height, top, bottom, left, 60, GuiAttributesScreen.this.width, GuiAttributesScreen.this.height);
         this.lines = this.resizeContent(lines);
         this.logoPath = logoPath;
         this.logoDims = logoDims;
         this.setHeaderInfo(true, this.getHeaderHeight());
      }

      public Info(int width, List<String> lines, @Nullable ResourceLocation logoPath, Dimension logoDims) {
         super(GuiAttributesScreen.this.getMinecraftInstance(), width, GuiAttributesScreen.this.height, 32, GuiAttributesScreen.this.height - 18 + 4, GuiAttributesScreen.this.listWidth + 20, 60, GuiAttributesScreen.this.width, GuiAttributesScreen.this.height);
         this.lines = this.resizeContent(lines);
         this.logoPath = logoPath;
         this.logoDims = logoDims;
         this.setHeaderInfo(true, this.getHeaderHeight());
      }

      protected int getSize() {
         return 0;
      }

      protected void elementClicked(int index, boolean doubleClick) {
      }

      protected boolean isSelected(int index) {
         return false;
      }

      protected void drawBackground() {
      }

      protected void drawSlot(int slotIdx, int entryRight, int slotTop, int slotBuffer, Tessellator tess) {
      }

      private List<ITextComponent> resizeContent(List<String> lines) {
         List<ITextComponent> ret = new ArrayList();

         for(String line : lines) {
            if (line == null) {
               ret.add((Object)null);
            } else {
               ITextComponent chat = ForgeHooks.newChatWithLinks(line, false);
               int maxTextLength = this.listWidth - 8;
               if (maxTextLength >= 0) {
                  ret.addAll(GuiUtilRenderComponents.splitText(chat, maxTextLength, GuiAttributesScreen.this.getFontRenderer(), false, true));
               }
            }
         }

         return ret;
      }

      private int getHeaderHeight() {
         int height = 0;
         if (this.logoPath != null) {
            double scaleX = (double)this.logoDims.width / 200.0D;
            double scaleY = (double)this.logoDims.height / 65.0D;
            double scale = 1.0D;
            if (scaleX > 1.0D || scaleY > 1.0D) {
               scale = 1.0D / Math.max(scaleX, scaleY);
            }

            this.logoDims.width = (int)((double)this.logoDims.width * scale);
            this.logoDims.height = (int)((double)this.logoDims.height * scale);
            height = height + this.logoDims.height;
            height = height + 10;
         }

         height = height + this.lines.size() * 10;
         if (height < this.bottom - this.top - 8) {
            height = this.bottom - this.top - 8;
         }

         return height;
      }

      protected void drawHeader(int entryRight, int relativeY, Tessellator tess) {
         int top = relativeY;
         if (this.logoPath != null) {
            GlStateManager.enableBlend();
            GuiAttributesScreen.this.getMinecraftInstance().renderEngine.bindTexture(this.logoPath);
            BufferBuilder wr = tess.getBuffer();
            int offset = this.left + this.listWidth / 2 - this.logoDims.width / 2;
            wr.begin(7, DefaultVertexFormats.POSITION_TEX);
            wr.pos((double)offset, (double)(relativeY + this.logoDims.height), (double)GuiAttributesScreen.this.zLevel).tex(0.0D, 1.0D).endVertex();
            wr.pos((double)(offset + this.logoDims.width), (double)(relativeY + this.logoDims.height), (double)GuiAttributesScreen.this.zLevel).tex(1.0D, 1.0D).endVertex();
            wr.pos((double)(offset + this.logoDims.width), (double)relativeY, (double)GuiAttributesScreen.this.zLevel).tex(1.0D, 0.0D).endVertex();
            wr.pos((double)offset, (double)relativeY, (double)GuiAttributesScreen.this.zLevel).tex(0.0D, 0.0D).endVertex();
            tess.draw();
            GlStateManager.disableBlend();
            top = relativeY + this.logoDims.height + 10;
         }

         for(ITextComponent line : this.lines) {
            if (line != null) {
               GlStateManager.enableBlend();
               GuiAttributesScreen.this.getFontRenderer().drawStringWithShadow(line.getFormattedText(), (float)(this.left + 4), (float)top, 16777215);
               GlStateManager.disableAlpha();
               GlStateManager.disableBlend();
            }

            top += 10;
         }

      }

      protected void clickHeader(int x, int y) {
         int offset = y;
         if (this.logoPath != null) {
            offset = y - (this.logoDims.height + 10);
         }

         if (offset > 0) {
            int lineIdx = offset / 10;
            if (lineIdx < this.lines.size()) {
               ITextComponent line = (ITextComponent)this.lines.get(lineIdx);
               if (line != null) {
                  int k = -4;

                  for(ITextComponent part : line) {
                     if (part instanceof TextComponentString) {
                        k += GuiAttributesScreen.this.fontRenderer.getStringWidth(((TextComponentString)part).getText());
                        if (k >= x) {
                           GuiAttributesScreen.this.handleComponentClick(part);
                           break;
                        }
                     }
                  }
               }

            }
         }
      }
   }

   private static enum SortType implements Comparator<IAbilityInterface> {
      NORMAL(24),
      A_TO_Z(25) {
         protected int compare(String name1, String name2) {
            return name1.compareTo(name2);
         }
      },
      Z_TO_A(26) {
         protected int compare(String name1, String name2) {
            return name2.compareTo(name1);
         }
      };

      private int buttonID;

      private SortType(int buttonID) {
         this.buttonID = buttonID;
      }

      @Nullable
      public static GuiAttributesScreen.SortType getTypeForButton(GuiButton button) {
         for(GuiAttributesScreen.SortType t : values()) {
            if (t.buttonID == button.id) {
               return t;
            }
         }

         return null;
      }

      protected int compare(String name1, String name2) {
         return 0;
      }

      public int compare(IAbilityInterface o1, IAbilityInterface o2) {
         String name1 = StringUtils.stripControlCodes(o1.getDisplayName()).toLowerCase();
         String name2 = StringUtils.stripControlCodes(o2.getDisplayName()).toLowerCase();
         return this.compare(name1, name2);
      }
   }
}
