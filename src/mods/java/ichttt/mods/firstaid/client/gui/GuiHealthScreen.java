package ichttt.mods.firstaid.client.gui;

import ichttt.mods.firstaid.FirstAid;
import ichttt.mods.firstaid.FirstAidConfig;
import ichttt.mods.firstaid.api.damagesystem.AbstractDamageablePart;
import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import ichttt.mods.firstaid.api.enums.EnumPlayerPart;
import ichttt.mods.firstaid.client.ClientProxy;
import ichttt.mods.firstaid.client.HUDHandler;
import ichttt.mods.firstaid.client.util.EventCalendar;
import ichttt.mods.firstaid.client.util.HealthRenderUtils;
import ichttt.mods.firstaid.common.apiimpl.FirstAidRegistryImpl;
import ichttt.mods.firstaid.common.network.MessageApplyHealingItem;
import ichttt.mods.firstaid.common.network.MessageClientRequest;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiHealthScreen extends GuiScreen {
   public static final int X_SIZE = 256;
   public static final int Y_SIZE = 137;
   public static final ItemStack BED_ITEMSTACK = new ItemStack(Items.BED);
   private static final DecimalFormat FORMAT = new DecimalFormat("##.#");
   public static GuiHealthScreen INSTANCE;
   public static boolean isOpen = false;
   private final AbstractPlayerDamageModel damageModel;
   private final List<GuiHoldButton> holdButtons = new ArrayList();
   private final boolean disableButtons;
   private final float bedScaleFactor = EventCalendar.isGuiFun() ? 1.5F : 1.25F;
   public int guiLeft;
   public int guiTop;
   private GuiButton head;
   private GuiButton leftArm;
   private GuiButton leftLeg;
   private GuiButton leftFoot;
   private GuiButton body;
   private GuiButton rightArm;
   private GuiButton rightLeg;
   private GuiButton rightFoot;
   private EnumHand activeHand;

   public GuiHealthScreen(AbstractPlayerDamageModel damageModel) {
      this.damageModel = damageModel;
      this.disableButtons = true;
   }

   public GuiHealthScreen(AbstractPlayerDamageModel damageModel, EnumHand activeHand) {
      this.damageModel = damageModel;
      this.activeHand = activeHand;
      this.disableButtons = false;
   }

   public void initGui() {
      isOpen = true;
      this.guiLeft = (this.width - 256) / 2;
      this.guiTop = (this.height - 137) / 2;
      this.head = new GuiHoldButton(1, this.guiLeft + 4, this.guiTop + 8, 52, 20, I18n.format("gui.head", new Object[0]), false);
      this.buttonList.add(this.head);
      this.leftArm = new GuiHoldButton(2, this.guiLeft + 4, this.guiTop + 33, 52, 20, I18n.format("gui.left_arm", new Object[0]), false);
      this.buttonList.add(this.leftArm);
      this.leftLeg = new GuiHoldButton(3, this.guiLeft + 4, this.guiTop + 58, 52, 20, I18n.format("gui.left_leg", new Object[0]), false);
      this.buttonList.add(this.leftLeg);
      this.leftFoot = new GuiHoldButton(4, this.guiLeft + 4, this.guiTop + 83, 52, 20, I18n.format("gui.left_foot", new Object[0]), false);
      this.buttonList.add(this.leftFoot);
      this.body = new GuiHoldButton(5, this.guiLeft + 199, this.guiTop + 8, 52, 20, I18n.format("gui.body", new Object[0]), true);
      this.buttonList.add(this.body);
      this.rightArm = new GuiHoldButton(6, this.guiLeft + 199, this.guiTop + 33, 52, 20, I18n.format("gui.right_arm", new Object[0]), true);
      this.buttonList.add(this.rightArm);
      this.rightLeg = new GuiHoldButton(7, this.guiLeft + 199, this.guiTop + 58, 52, 20, I18n.format("gui.right_leg", new Object[0]), true);
      this.buttonList.add(this.rightLeg);
      this.rightFoot = new GuiHoldButton(8, this.guiLeft + 199, this.guiTop + 83, 52, 20, I18n.format("gui.right_foot", new Object[0]), true);
      this.buttonList.add(this.rightFoot);
      if (this.disableButtons) {
         this.head.enabled = false;
         this.leftArm.enabled = false;
         this.leftLeg.enabled = false;
         this.leftFoot.enabled = false;
         this.body.enabled = false;
         this.rightArm.enabled = false;
         this.rightLeg.enabled = false;
         this.rightFoot.enabled = false;
      }

      GuiButton buttonCancel = new GuiButton(9, this.width / 2 - 100, this.height - 50, I18n.format(this.disableButtons ? "gui.done" : "gui.cancel", new Object[0]));
      this.buttonList.add(buttonCancel);
      if (this.mc.gameSettings.showDebugInfo) {
         GuiButton refresh = new GuiButton(10, this.guiLeft + 218, this.guiTop + 115, 36, 20, "resync");
         this.buttonList.add(refresh);
      }

      this.holdButtons.clear();

      for(GuiButton button : this.buttonList) {
         if (button instanceof GuiHoldButton) {
            Integer holdTime = this.activeHand == null ? null : FirstAidRegistryImpl.INSTANCE.getPartHealingTime(this.mc.player.getHeldItem(this.activeHand));
            if (holdTime == null) {
               holdTime = Integer.MAX_VALUE;
            }

            ((GuiHoldButton)button).setup(holdTime.intValue(), (float)button.width / (float)HUDHandler.INSTANCE.getMaxLength());
            this.holdButtons.add((GuiHoldButton)button);
         }
      }

      super.initGui();
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawGradientRect(this.guiLeft, this.guiTop, this.guiLeft + 256, this.guiTop + 137, -16777216, -16777216);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(HealthRenderUtils.GUI_LOCATION);
      this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, 256, 137);
      int entityLookX = this.guiLeft + 128 - mouseX;
      int entityLookY = this.guiTop + 20 - mouseY;
      if (EventCalendar.isGuiFun()) {
         entityLookX = -entityLookX;
         entityLookY = -entityLookY;
      }

      GuiInventory.drawEntityOnScreen(this.width / 2, this.height / 2 + 30, 45, (float)entityLookX, (float)entityLookY, this.mc.player);
      super.drawScreen(mouseX, mouseY, partialTicks);
      int morphineTicks = this.damageModel.getMorphineTicks();
      if (morphineTicks > 0) {
         this.drawCenteredString(this.mc.fontRenderer, I18n.format("gui.morphine_left", new Object[]{StringUtils.ticksToElapsedTime(morphineTicks)}), this.guiLeft + 128, this.guiTop + 137 - (this.activeHand == null ? 21 : 29), 16777215);
      }

      if (this.activeHand != null) {
         this.drawCenteredString(this.mc.fontRenderer, I18n.format("gui.apply_hint", new Object[0]), this.guiLeft + 128, this.guiTop + 137 - (morphineTicks == 0 ? 21 : 11), 16777215);
      }

      this.mc.getTextureManager().bindTexture(Gui.ICONS);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      if (FirstAid.isSynced) {
         GlStateManager.pushMatrix();
         this.drawHealth(this.damageModel.HEAD, false, 14);
         this.drawHealth(this.damageModel.LEFT_ARM, false, 39);
         this.drawHealth(this.damageModel.LEFT_LEG, false, 64);
         this.drawHealth(this.damageModel.LEFT_FOOT, false, 89);
         this.drawHealth(this.damageModel.BODY, true, 14);
         this.drawHealth(this.damageModel.RIGHT_ARM, true, 39);
         this.drawHealth(this.damageModel.RIGHT_LEG, true, 64);
         this.drawHealth(this.damageModel.RIGHT_FOOT, true, 89);
         GlStateManager.popMatrix();
      }

      GlStateManager.pushMatrix();
      this.tooltipButton(this.head, this.damageModel.HEAD, mouseX, mouseY);
      this.tooltipButton(this.leftArm, this.damageModel.LEFT_ARM, mouseX, mouseY);
      this.tooltipButton(this.leftLeg, this.damageModel.LEFT_LEG, mouseX, mouseY);
      this.tooltipButton(this.leftFoot, this.damageModel.LEFT_FOOT, mouseX, mouseY);
      this.tooltipButton(this.body, this.damageModel.BODY, mouseX, mouseY);
      this.tooltipButton(this.rightArm, this.damageModel.RIGHT_ARM, mouseX, mouseY);
      this.tooltipButton(this.rightLeg, this.damageModel.RIGHT_LEG, mouseX, mouseY);
      this.tooltipButton(this.rightFoot, this.damageModel.RIGHT_FOOT, mouseX, mouseY);
      GlStateManager.popMatrix();
      double sleepHealing = FirstAidConfig.externalHealing.sleepHealPercentage;
      int renderBedX = Math.round((float)this.guiLeft / this.bedScaleFactor) + 2;
      int renderBedY = Math.round((float)(this.guiTop + 137) / this.bedScaleFactor) - 18;
      int bedX = (int)((float)renderBedX * this.bedScaleFactor);
      int bedY = (int)((float)renderBedY * this.bedScaleFactor);
      GlStateManager.pushMatrix();
      if (sleepHealing > 0.0D) {
         RenderHelper.enableGUIStandardItemLighting();
      }

      GlStateManager.scale(this.bedScaleFactor, this.bedScaleFactor, this.bedScaleFactor);
      this.mc.getRenderItem().renderItemAndEffectIntoGUI((EntityLivingBase)null, BED_ITEMSTACK, renderBedX, renderBedY);
      GlStateManager.popMatrix();
      if (mouseX >= bedX && mouseY >= bedY && (float)mouseX < (float)bedX + 16.0F * this.bedScaleFactor && (float)mouseY < (float)bedY + 16.0F * this.bedScaleFactor) {
         String s = sleepHealing == 0.0D ? I18n.format("gui.no_sleep_heal", new Object[0]) : I18n.format("gui.sleep_heal_amount", new Object[]{FORMAT.format(sleepHealing * 100.0D)});
         this.drawHoveringText(s, mouseX, mouseY);
      }

      this.holdButtonMouseCallback(mouseX, mouseY, true);
   }

   private void tooltipButton(GuiButton button, AbstractDamageablePart part, int mouseX, int mouseY) {
      boolean enabled = part.activeHealer == null;
      if (!enabled && button.isMouseOver()) {
         this.drawHoveringText(Arrays.asList(I18n.format("gui.active_item", new Object[0]) + ": " + I18n.format(part.activeHealer.stack.getUnlocalizedName() + ".name", new Object[0]), I18n.format("gui.next_heal", new Object[]{Math.round((float)(part.activeHealer.ticksPerHeal - part.activeHealer.getTicksPassed()) / 20.0F)})), mouseX, mouseY);
      }

      if (!this.disableButtons) {
         button.enabled = enabled;
      }

   }

   public void drawHealth(AbstractDamageablePart damageablePart, boolean right, int yOffset) {
      GlStateManager.pushMatrix();
      int xTranslation = this.guiLeft + (right ? getRightOffset(damageablePart) : 57);
      HealthRenderUtils.drawHealth(damageablePart, (float)xTranslation, (float)(this.guiTop + yOffset), this, true);
      GlStateManager.popMatrix();
   }

   private static int getRightOffset(AbstractDamageablePart damageablePart) {
      return HealthRenderUtils.drawAsString(damageablePart, true) ? 160 : 200 - Math.min(40, HealthRenderUtils.getMaxHearts((float)damageablePart.getMaxHealth()) * 9 + HealthRenderUtils.getMaxHearts(damageablePart.getAbsorption()) * 9 + 2);
   }

   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      if (keyCode == ClientProxy.showWounds.getKeyCode()) {
         this.mc.displayGuiScreen((GuiScreen)null);
      }

      super.keyTyped(typedChar, keyCode);
   }

   protected void actionPerformed(GuiButton button) {
      if (button.id < 9) {
         EnumPlayerPart playerPart = EnumPlayerPart.fromID(button.id);
         FirstAid.NETWORKING.sendToServer(new MessageApplyHealingItem(playerPart, this.activeHand));
         AbstractDamageablePart part = this.damageModel.getFromEnum(playerPart);
         part.activeHealer = FirstAidRegistryImpl.INSTANCE.getPartHealer(this.mc.player.getHeldItem(this.activeHand));
      } else if (button.id == 10) {
         FirstAid.NETWORKING.sendToServer(new MessageClientRequest(MessageClientRequest.Type.REQUEST_REFRESH));
         FirstAid.LOGGER.info("Requesting refresh");
         this.mc.player.sendStatusMessage(new TextComponentString("Re-downloading health data from server..."), true);
      }

      this.mc.displayGuiScreen((GuiScreen)null);
   }

   protected void holdButtonMouseCallback(int mouseX, int mouseY, boolean renderPass) {
      for(GuiHoldButton button : this.holdButtons) {
         if (button.mousePressed(this.mc, mouseX, mouseY)) {
            this.selectedButton = button;
            button.playPressSound(this.mc.getSoundHandler());
            this.actionPerformed(button);
         } else if (renderPass) {
            int timeLeft = button.getTimeLeft();
            if (timeLeft != -1) {
               float timeInSecs = (float)timeLeft / 1000.0F;
               if (timeInSecs < 0.0F) {
                  timeInSecs = 0.0F;
               }

               this.mc.getTextureManager().bindTexture(HealthRenderUtils.GUI_LOCATION);
               this.drawTexturedModalRect(button.x + (button.isRightSide ? 56 : -25), button.y - 2, button.isRightSide ? 2 : 0, 169, 22, 24);
               this.mc.fontRenderer.drawString(HealthRenderUtils.TEXT_FORMAT.format((double)timeInSecs), button.x + (button.isRightSide ? 60 : -20), button.y + 6, 16777215);
            }
         }
      }

   }

   protected void mouseReleased(int mouseX, int mouseY, int state) {
      super.mouseReleased(mouseX, mouseY, state);
      this.holdButtonMouseCallback(mouseX, mouseY, false);
   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   public void onGuiClosed() {
      INSTANCE = null;
      isOpen = false;
   }

   public List<GuiButton> getButtons() {
      return this.buttonList;
   }
}
