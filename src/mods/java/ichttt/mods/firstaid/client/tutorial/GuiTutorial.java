package ichttt.mods.firstaid.client.tutorial;

import ichttt.mods.firstaid.FirstAid;
import ichttt.mods.firstaid.FirstAidConfig;
import ichttt.mods.firstaid.api.CapabilityExtendedHealthSystem;
import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import ichttt.mods.firstaid.client.ClientProxy;
import ichttt.mods.firstaid.client.gui.GuiHealthScreen;
import ichttt.mods.firstaid.client.util.HealthRenderUtils;
import ichttt.mods.firstaid.common.damagesystem.PlayerDamageModel;
import ichttt.mods.firstaid.common.network.MessageClientRequest;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTutorial extends GuiScreen {
   private final GuiHealthScreen parent;
   private final AbstractPlayerDamageModel demoModel = PlayerDamageModel.create();
   private int guiTop;
   private final TutorialAction action;

   public GuiTutorial() {
      this.parent = new GuiHealthScreen(this.demoModel);
      this.action = new TutorialAction(this);
      this.action.addTextWrapper("firstaid.tutorial.welcome");
      this.action.addTextWrapper("firstaid.tutorial.line1");
      this.action.addTextWrapper("firstaid.tutorial.line2");
      this.action.addActionCallable((guiTutorial) -> {
         guiTutorial.demoModel.LEFT_FOOT.damage(4.0F, (EntityPlayer)null, false);
      });
      this.action.addTextWrapper("firstaid.tutorial.line3");
      this.action.addActionCallable((guiTutorial) -> {
         guiTutorial.demoModel.applyMorphine();
      });
      this.action.addTextWrapper("firstaid.tutorial.line4");
      this.action.addTextWrapper("firstaid.tutorial.line5");
      this.action.addActionCallable((guiTutorial) -> {
         guiTutorial.demoModel.LEFT_FOOT.heal(3.0F, (EntityPlayer)null, false);
      });
      if (FirstAidConfig.externalHealing.sleepHealPercentage != 0.0D) {
         this.action.addTextWrapper("firstaid.tutorial.sleephint");
      }

      this.action.addTextWrapper("firstaid.tutorial.line6");
      this.action.addActionCallable((guiTutorial) -> {
         guiTutorial.demoModel.HEAD.damage(16.0F, (EntityPlayer)null, false);
      });
      this.action.addTextWrapper("firstaid.tutorial.line7");
      this.action.addTextWrapper("firstaid.tutorial.line8", ClientProxy.showWounds.getDisplayName());
      this.action.addTextWrapper("firstaid.tutorial.end");
      this.action.next();
   }

   public void initGui() {
      this.parent.setWorldAndResolution(this.mc, this.width, this.height);
      this.guiTop = this.parent.guiTop - 30;
      this.buttonList.add(new GuiButton(0, this.parent.guiLeft + 256 - 34, this.guiTop + 4, 32, 20, ">"));
      this.buttonList.addAll(this.parent.getButtons());
      this.parent.getButtons().clear();
   }

   protected void actionPerformed(GuiButton button) {
      if (button.id == 9) {
         FirstAid.NETWORKING.sendToServer(new MessageClientRequest(MessageClientRequest.Type.TUTORIAL_COMPLETE));
         this.mc.displayGuiScreen((GuiScreen)null);
      } else if (button.id == 0) {
         if (this.action.hasNext()) {
            this.action.next();
         } else {
            FirstAid.NETWORKING.sendToServer(new MessageClientRequest(MessageClientRequest.Type.TUTORIAL_COMPLETE));
            this.mc.displayGuiScreen(new GuiHealthScreen((AbstractPlayerDamageModel)this.mc.player.getCapability(CapabilityExtendedHealthSystem.INSTANCE, (EnumFacing)null)));
         }
      }

   }

   public void drawOffsetString(String s, int yOffset) {
      this.drawString(this.mc.fontRenderer, s, this.parent.guiLeft + 30, this.guiTop + yOffset, 16777215);
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      GlStateManager.pushMatrix();
      this.parent.drawScreen(mouseX, mouseY, partialTicks);
      GlStateManager.popMatrix();
      this.mc.getTextureManager().bindTexture(HealthRenderUtils.GUI_LOCATION);
      this.drawTexturedModalRect(this.parent.guiLeft, this.guiTop, 0, 139, 256, 28);
      GlStateManager.pushMatrix();
      this.action.draw();
      GlStateManager.popMatrix();
      this.drawCenteredString(this.mc.fontRenderer, I18n.format("firstaid.tutorial.notice", new Object[0]), this.parent.guiLeft + 128, this.parent.guiTop + 128, 16777215);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   public void onGuiClosed() {
      GuiHealthScreen.isOpen = false;
   }
}
