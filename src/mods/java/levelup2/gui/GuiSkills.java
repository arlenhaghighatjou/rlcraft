package levelup2.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import levelup2.LevelUp2;
import levelup2.api.IPlayerSkill;
import levelup2.network.SkillPacketHandler;
import levelup2.player.PlayerExtension;
import levelup2.skills.SkillRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Mouse;

public class GuiSkills extends GuiScreen {
   private static final int X_MIN = SkillRegistry.smallestDisplayColumn * 32 - 12;
   private static final int Y_MIN = SkillRegistry.smallestDisplayRow * 32 - 170;
   private static final int X_MAX = SkillRegistry.largestDisplayColumn * 32 - 77;
   private static final int Y_MAX = SkillRegistry.largestDisplayRow * 32 - 247;
   private static final ResourceLocation BACKGROUND = new ResourceLocation("levelup2", "textures/gui/background.png");
   private int imageWidth = 256;
   private int imageHeight = 202;
   private int lastX;
   private int lastY;
   private double xScrollO;
   private double yScrollO;
   private double xScrollP;
   private double yScrollP;
   private double targetX;
   private double targetY;
   private boolean isScrolling;
   private byte skillTree = 0;
   protected PlayerExtension player = (PlayerExtension)SkillRegistry.getPlayer(LevelUp2.proxy.getPlayer());
   protected Map<String, Integer> skills = new HashMap();
   private IPlayerSkill highlightedSkill = null;
   protected int availableLevels = 0;
   protected int levelSpend = 0;
   private boolean closedWithButton = false;

   public GuiSkills() {
      this.availableLevels = LevelUp2.proxy.getPlayer().experienceLevel;
      this.targetX = -12.0D;
      this.targetY = (double)Y_MIN;
      this.xScrollO = this.targetX;
      this.xScrollP = this.targetX;
      this.yScrollO = this.targetY;
      this.yScrollP = this.targetY;
      this.skills.clear();

      for(String str : this.player.getSkills().keySet()) {
         this.skills.put(str, this.player.getSkills().get(str));
      }

   }

   public void initGui() {
      this.buttonList.clear();
      this.buttonList.add(new GuiButton(3, this.width / 2 + 32, this.height / 2 + 74, 80, 20, I18n.format("gui.done", new Object[0])));

      for(int i = 0; i < 3; ++i) {
         this.buttonList.add(new GuiButton(i, (this.width - this.imageWidth) / 2 + 16 + i * 48, this.height / 2 + 74, 45, 20, I18n.format("gui.levelup.skillmenu." + i, new Object[0])));
      }

   }

   protected void actionPerformed(GuiButton button) {
      if (button.id == 3) {
         this.closedWithButton = true;
         this.mc.displayGuiScreen((GuiScreen)null);
         this.mc.setIngameFocus();
      } else if (button.id >= 0 && button.id < 3 && button.id != this.skillTree) {
         this.skillTree = (byte)button.id;
      }

   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   public void updateScreen() {
      this.xScrollO = this.xScrollP;
      this.yScrollO = this.yScrollP;
      double d0 = this.targetX - this.xScrollP;
      double d1 = this.targetY - this.yScrollP;
      if (d0 * d0 + d1 * d1 < 4.0D) {
         this.xScrollP += d0;
         this.yScrollP += d1;
      } else {
         this.xScrollP += d0 * 0.85D;
         this.yScrollP += d1 * 0.85D;
      }

   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      if (Mouse.isButtonDown(0)) {
         int edgeX = (this.width - this.imageWidth) / 2;
         int edgeY = (this.height - this.imageHeight) / 2;
         int offX = edgeX + 8;
         int offY = edgeY + 17;
         if (mouseX >= offX && mouseX < offX + 224 && mouseY >= offY && mouseY < offY + 155 && this.highlightedSkill == null) {
            if (!this.isScrolling) {
               this.isScrolling = true;
            } else {
               this.xScrollP -= (double)((float)(mouseX - this.lastX));
               this.yScrollP -= (double)((float)(mouseY - this.lastY));
               this.xScrollO = this.xScrollP;
               this.yScrollO = this.yScrollP;
               this.targetX = this.xScrollP;
               this.targetY = this.yScrollP;
            }

            this.lastX = mouseX;
            this.lastY = mouseY;
         }
      } else {
         this.isScrolling = false;
      }

      if (this.targetX < (double)X_MIN) {
         this.targetX = (double)X_MIN;
      }

      if (this.targetY < (double)Y_MIN) {
         this.targetY = (double)Y_MIN;
      }

      if (this.targetX >= (double)X_MAX) {
         this.targetX = (double)(X_MAX - 1);
      }

      if (this.targetY >= (double)Y_MAX) {
         this.targetY = (double)(Y_MAX - 1);
      }

      this.drawDefaultBackground();
      this.drawSkills(mouseX, mouseY, partialTicks);
      GlStateManager.disableLighting();
      GlStateManager.disableDepth();
      this.drawTitle();
      GlStateManager.enableLighting();
      GlStateManager.enableDepth();
   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      if (this.highlightedSkill != null && mouseButton == 0) {
         this.mc.displayGuiScreen(new GuiSkillChoice(this.highlightedSkill, ((Integer)this.skills.get(this.highlightedSkill.getSkillName())).intValue(), this));
      }

   }

   private void drawTitle() {
      int edgeX = (this.width - this.imageWidth) / 2;
      int edgeY = (this.height - this.imageHeight) / 2;
      this.fontRenderer.drawString(I18n.format("gui.levelup.skillmenu." + this.skillTree, new Object[0]), edgeX + 15, edgeY + 5, 4210752);
      this.drawLevelOnEnd(edgeX, edgeY);
   }

   private void drawLevelOnEnd(int edgeX, int edgeY) {
      String levels = I18n.format("gui.levelup.budget", new Object[]{this.levelSpend});
      int textWidth = this.fontRenderer.getStringWidth(levels);
      int x = edgeX + this.imageWidth - 15 - textWidth;
      int y = edgeY + 5;
      this.fontRenderer.drawString(levels, x, y, 1607962);
   }

   private void drawSkills(int mouseX, int mouseY, float partialTicks) {
      int x = MathHelper.floor(this.xScrollO + (this.xScrollP - this.xScrollO) * (double)partialTicks);
      int y = MathHelper.floor(this.yScrollO + (this.yScrollP - this.yScrollO) * (double)partialTicks);
      if (x < X_MIN) {
         x = X_MIN;
      }

      if (y < Y_MIN) {
         y = Y_MIN;
      }

      if (x > X_MAX) {
         x = X_MAX;
      }

      if (y > Y_MAX) {
         y = Y_MAX;
      }

      int edgeX = (this.width - this.imageWidth) / 2;
      int edgeY = (this.height - this.imageHeight) / 2;
      int offsetX = edgeX + 16;
      int offsetY = edgeY + 17;
      this.zLevel = 0.0F;
      GlStateManager.depthFunc(518);
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)offsetX, (float)offsetY, -200.0F);
      GlStateManager.enableDepth();
      GlStateManager.enableTexture2D();
      GlStateManager.disableLighting();
      GlStateManager.enableRescaleNormal();
      GlStateManager.enableColorMaterial();
      int k1 = x + 288 >> 4;
      int l1 = y + 288 >> 4;
      int i2 = (x + 288) % 16;
      int j2 = (y + 288) % 16;
      Random random = new Random();
      float f = 16.0F;
      float f1 = 16.0F;

      for(int l3 = 0; (float)l3 * f - (float)j2 < 155.0F; ++l3) {
         float f2 = 0.6F - (float)(l1 + l3) / 25.0F * 0.3F;
         GlStateManager.color(f2, f2, f2, 1.0F);

         for(int i4 = 0; (float)i4 * f1 - (float)i2 < 224.0F; ++i4) {
            random.setSeed((long)(this.mc.getSession().getPlayerID().hashCode() + k1 + i4 + (l1 + l3) * 16));
            int j4 = random.nextInt(1 + l1 + l3) + (l1 + l3) / 2;
            TextureAtlasSprite textureatlassprite = this.getTexture(Blocks.SAND);
            if (j4 <= 37 && l1 + l3 != 35) {
               if (j4 == 22) {
                  if (random.nextInt(2) == 0) {
                     textureatlassprite = this.getTexture(Blocks.DIAMOND_ORE);
                  } else {
                     textureatlassprite = this.getTexture(Blocks.REDSTONE_ORE);
                  }
               } else if (j4 == 10) {
                  textureatlassprite = this.getTexture(Blocks.IRON_ORE);
               } else if (j4 == 8) {
                  textureatlassprite = this.getTexture(Blocks.COAL_ORE);
               } else if (j4 > 4) {
                  textureatlassprite = this.getTexture(Blocks.STONE);
               } else if (j4 > 0) {
                  textureatlassprite = this.getTexture(Blocks.DIRT);
               }
            } else {
               Block block = Blocks.BEDROCK;
               textureatlassprite = this.getTexture(block);
            }

            this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            this.drawTexturedModalRect(i4 * 16 - i2, l3 * 16 - j2, textureatlassprite, 16, 16);
         }
      }

      GlStateManager.depthFunc(515);
      this.mc.getTextureManager().bindTexture(BACKGROUND);

      for(IPlayerSkill skill : SkillRegistry.getSkillRegistry()) {
         if (skill.getSkillType() == this.skillTree && skill.isEnabled()) {
            List<IPlayerSkill> prerequisite = this.getPrerequisiteSkills(skill);
            if (!prerequisite.isEmpty()) {
               int skillX = skill.getSkillColumn() * 32 - x + 11;
               int skillY = skill.getSkillRow() * 32 - 160 - y + 11;

               for(IPlayerSkill pre : prerequisite) {
                  int preX = pre.getSkillColumn() * 32 - x + 11;
                  int preY = pre.getSkillRow() * 32 - 160 - y + 11;
                  int lineColor = ((Integer)this.skills.get(skill.getSkillName())).intValue() > 0 ? -6250336 : (((Integer)this.skills.get(pre.getSkillName())).intValue() > 0 ? -16711936 : -16777216);
                  this.drawHorizontalLine(skillX, preX, skillY, lineColor);
                  this.drawVerticalLine(preX, skillY, preY, lineColor);
               }
            }
         }
      }

      IPlayerSkill skill = null;
      int xPos = mouseX - offsetX;
      int yPos = mouseY - offsetY;
      RenderHelper.enableGUIStandardItemLighting();
      GlStateManager.disableLighting();
      GlStateManager.enableRescaleNormal();
      GlStateManager.enableColorMaterial();

      for(IPlayerSkill check : SkillRegistry.getSkillRegistry()) {
         if (check.getSkillType() == this.skillTree && check.isEnabled()) {
            int checkX = check.getSkillColumn() * 32 - x;
            int checkY = check.getSkillRow() * 32 - 160 - y;
            if (checkX >= -24 && checkY >= -24 && checkX <= 224 && checkY <= 155) {
               float col = 0.3F;
               if (this.isMaxLevel(check)) {
                  col = 0.9F;
               } else if (((Integer)this.skills.get(check.getSkillName())).intValue() > 0) {
                  col = 0.75F;
               } else if (this.canUnlock(check)) {
                  col = 0.6F;
               }

               GlStateManager.color(col, col, col, 1.0F);
               this.mc.getTextureManager().bindTexture(BACKGROUND);
               GlStateManager.enableBlend();
               if (check.isMaxLevel(((Integer)this.skills.get(check.getSkillName())).intValue())) {
                  this.drawTexturedModalRect(checkX, checkY, 26, 202, 26, 26);
               } else {
                  this.drawTexturedModalRect(checkX, checkY, 0, 202, 26, 26);
               }

               GlStateManager.disableBlend();
               if (((Integer)this.skills.get(check.getSkillName())).intValue() == 0) {
                  GlStateManager.color(0.1F, 0.1F, 0.1F, 1.0F);
               }

               GlStateManager.disableLighting();
               GlStateManager.enableCull();
               this.mc.getRenderItem().renderItemAndEffectIntoGUI(check.getRepresentativeStack(), checkX + 4, checkY + 4);
               GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
               GlStateManager.disableLighting();
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               if (xPos >= checkX && xPos <= checkX + 22 && yPos >= checkY && yPos <= checkY + 22) {
                  skill = check;
               }
            }
         }
      }

      GlStateManager.disableDepth();
      GlStateManager.enableBlend();
      GlStateManager.popMatrix();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(BACKGROUND);
      this.drawTexturedModalRect(edgeX, edgeY, 0, 0, this.imageWidth, this.imageHeight);
      this.zLevel = 0.0F;
      GlStateManager.depthFunc(515);
      GlStateManager.disableDepth();
      GlStateManager.enableTexture2D();
      super.drawScreen(mouseX, mouseY, partialTicks);
      if (skill != null) {
         int xOff = mouseX + 12;
         int yOff = mouseY - 4;
         String skillName = I18n.format("skill." + skill.getSkillName() + ".name", new Object[0]);
         String skillLevel = this.skills.get(skill.getSkillName()) + "/" + skill.getMaxLevel();
         if (this.canUnlock(skill)) {
            int width = Math.max(this.fontRenderer.getStringWidth(skillName), 80);
            int height = this.fontRenderer.getWordWrappedHeight(skillName, width);
            int levelCost = skill.getLevelCost(((Integer)this.skills.get(skill.getSkillName())).intValue());
            if (levelCost > 0) {
               height += 12;
               this.fontRenderer.drawSplitString(I18n.format("levelup.cost", new Object[]{levelCost}), xOff, yOff + 24, width, 16514415);
            }

            this.drawGradientRect(xOff - 3, yOff - 3, xOff + width + 3, yOff + height + 3 + 12, -1073741824, -1073741824);
            this.fontRenderer.drawSplitString(skillLevel, xOff, yOff + 12, width, 16514415);
         } else {
            int width = Math.max(this.fontRenderer.getStringWidth(skillName), 80);
            if (this.isLockedSkill(skill)) {
               int height = this.fontRenderer.getWordWrappedHeight(skillName, width);
               this.drawGradientRect(xOff - 3, yOff - 3, xOff + width + 3, yOff + height + 3, -1073741824, -1073741824);
            } else if (this.hasPrerequisites(skill)) {
               int height = this.fontRenderer.getWordWrappedHeight(skillName, width) + 12 * this.getMissingPrereqAmount(skill);
               if (this.getMissingPrereqAmount(skill) > 0) {
                  List<String> names = this.getMissingPrereqSkills(skill);

                  for(int i = 0; i < names.size(); ++i) {
                     String name = i == 0 ? I18n.format("levelup.prereq", new Object[]{names.get(i)}) : (String)names.get(i);
                     int place = i + 1;
                     if (this.fontRenderer.getStringWidth(name) > width) {
                        width = this.fontRenderer.getStringWidth(name);
                     }

                     this.fontRenderer.drawSplitString(name, xOff, yOff + 12 * place, width, 16514415);
                  }
               }

               this.drawGradientRect(xOff - 3, yOff - 3, xOff + width + 3, yOff + height + 3, -1073741824, -1073741824);
            }
         }

         this.fontRenderer.drawStringWithShadow(skillName, (float)xOff, (float)yOff, -8355712);
      }

      if (skill != this.highlightedSkill) {
         this.highlightedSkill = skill;
      }

      GlStateManager.enableDepth();
      GlStateManager.enableLighting();
      RenderHelper.disableStandardItemLighting();
   }

   private boolean hasPrerequisites(IPlayerSkill skill) {
      return skill.getPrerequisites() != null && skill.getPrerequisites().length != 0;
   }

   private IPlayerSkill getSkillFromName(String name) {
      for(IPlayerSkill skill : SkillRegistry.getSkillRegistry()) {
         if (skill.getSkillName().equals(name)) {
            return skill;
         }
      }

      return null;
   }

   private List<IPlayerSkill> getPrerequisiteSkills(IPlayerSkill skill) {
      List<IPlayerSkill> skills = new ArrayList();
      if (this.hasPrerequisites(skill)) {
         for(String name : skill.getPrerequisites()) {
            IPlayerSkill check = this.getSkillFromName(name);
            if (check != null && check.isEnabled()) {
               skills.add(check);
            }
         }
      }

      return skills;
   }

   private int getMissingPrereqAmount(IPlayerSkill skill) {
      return this.getMissingPrereqSkills(skill).size();
   }

   private List<String> getMissingPrereqSkills(IPlayerSkill skill) {
      List<String> names = new ArrayList();
      List<IPlayerSkill> skills = this.getPrerequisiteSkills(skill);
      if (!skills.isEmpty()) {
         for(IPlayerSkill check : skills) {
            if (((Integer)this.skills.get(check.getSkillName())).intValue() == 0) {
               names.add(I18n.format("skill." + check.getSkillName() + ".name", new Object[0]));
            }
         }
      }

      return names;
   }

   private boolean isMaxLevel(IPlayerSkill skill) {
      return ((Integer)this.skills.get(skill.getSkillName())).intValue() == skill.getMaxLevel();
   }

   private boolean isLockedSkill(IPlayerSkill skill) {
      return skill.getLevelCost(0) == -1;
   }

   protected boolean canUnlock(IPlayerSkill skill) {
      List<IPlayerSkill> skills = this.getPrerequisiteSkills(skill);
      if (!skills.isEmpty()) {
         for(IPlayerSkill check : skills) {
            if (((Integer)this.skills.get(check.getSkillName())).intValue() == 0) {
               return false;
            }
         }
      }

      return skill.getLevelCost(((Integer)this.skills.get(skill.getSkillName())).intValue()) > -1;
   }

   private TextureAtlasSprite getTexture(Block blockIn) {
      return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(blockIn.getDefaultState());
   }

   public void onGuiClosed() {
      if (this.closedWithButton && this.skillsChanged()) {
         FMLProxyPacket pkt = SkillPacketHandler.getPacket(Side.SERVER, 2, (byte)-1, this.skills, this.levelSpend);
         SkillPacketHandler.skillChannel.sendToServer(pkt);
      }

   }

   private boolean skillsChanged() {
      for(IPlayerSkill skill : SkillRegistry.getSkillRegistry()) {
         if (!((Integer)this.skills.get(skill.getSkillName())).equals(this.player.getSkills().get(skill.getSkillName()))) {
            return true;
         }
      }

      return false;
   }

   private boolean canUnlockSkill(IPlayerSkill skill) {
      int levels = this.availableLevels - this.levelSpend;
      return this.canUnlock(skill) && levels > 0 && skill.getLevelCost(((Integer)this.skills.get(skill.getSkillName())).intValue()) >= levels && skill.getLevelCost(((Integer)this.skills.get(skill.getSkillName())).intValue()) > -1;
   }
}
