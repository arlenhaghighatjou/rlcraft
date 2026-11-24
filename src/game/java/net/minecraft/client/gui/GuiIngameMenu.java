package net.minecraft.client.gui;

import java.io.IOException;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.Mouse;
import net.lax1dude.eaglercraft.v1_8.PauseMenuCustomizeState;
import net.lax1dude.eaglercraft.v1_8.minecraft.GuiButtonWithStupidIcons;
import net.lax1dude.eaglercraft.v1_8.notifications.GuiButtonNotifBell;
import net.lax1dude.eaglercraft.v1_8.notifications.GuiScreenNotifications;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.sp.SingleplayerServerController;
import net.lax1dude.eaglercraft.v1_8.sp.gui.GuiScreenLANInfo;
import net.lax1dude.eaglercraft.v1_8.sp.gui.GuiScreenLANNotSupported;
import net.lax1dude.eaglercraft.v1_8.sp.gui.GuiShareToLan;
import net.lax1dude.eaglercraft.v1_8.sp.lan.LANServerController;
import net.lax1dude.eaglercraft.v1_8.update.GuiUpdateCheckerOverlay;
import net.lax1dude.eaglercraft.v1_8.voice.GuiVoiceMenu;
import net.lax1dude.eaglercraft.v1_8.webview.GuiScreenPhishingWarning;
import net.lax1dude.eaglercraft.v1_8.webview.GuiScreenRecieveServerInfo;
import net.lax1dude.eaglercraft.v1_8.webview.GuiScreenServerInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.gui.advancements.GuiScreenAdvancements;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiIngameMenu extends GuiScreen
{
    private int visibleTime;
    private GuiButton lanButton;
    boolean hasSentAutoSave = !SingleplayerServerController.isWorldRunning();
    private final GuiUpdateCheckerOverlay updateCheckerOverlay;
    private GuiVoiceMenu voiceMenu;
    private GuiButtonNotifBell notifBellButton;

    public GuiIngameMenu()
    {
        this.updateCheckerOverlay = new GuiUpdateCheckerOverlay(true, this);

        if (EagRuntime.getConfiguration().isAllowVoiceClient())
        {
            this.voiceMenu = new GuiVoiceMenu(this);
        }
    }

    public void initGui()
    {
        this.buttonList.clear();
        this.updateCheckerOverlay.setResolution(this.mc, this.width, this.height);
        int i = -16;
        this.buttonList.add(new GuiButtonWithStupidIcons(1, this.width / 2 - 100, this.height / 4 + 120 + i, I18n.format("menu.returnToMenu"), PauseMenuCustomizeState.icon_disconnect_L, PauseMenuCustomizeState.icon_disconnect_L_aspect, PauseMenuCustomizeState.icon_disconnect_R, PauseMenuCustomizeState.icon_disconnect_R_aspect));

        if (!this.mc.isIntegratedServerRunning())
        {
            (this.buttonList.get(0)).displayString = I18n.format("menu.disconnect");

            if (this.mc.player != null && this.mc.player.connection.getEaglerMessageProtocol().ver >= 4)
            {
                this.notifBellButton = this.addButton(new GuiButtonNotifBell(11, this.width - 22, this.height - 22));
                this.notifBellButton.setUnread(this.mc.player.connection.getNotifManager().getUnread());
            }
        }

        this.buttonList.add(new GuiButtonWithStupidIcons(4, this.width / 2 - 100, this.height / 4 + 24 + i, I18n.format("menu.returnToGame"), PauseMenuCustomizeState.icon_backToGame_L, PauseMenuCustomizeState.icon_backToGame_L_aspect, PauseMenuCustomizeState.icon_backToGame_R, PauseMenuCustomizeState.icon_backToGame_R_aspect));
        this.buttonList.add(new GuiButtonWithStupidIcons(0, this.width / 2 - 100, this.height / 4 + 96 + i, 98, 20, I18n.format("menu.options"), PauseMenuCustomizeState.icon_options_L, PauseMenuCustomizeState.icon_options_L_aspect, PauseMenuCustomizeState.icon_options_R, PauseMenuCustomizeState.icon_options_R_aspect));
        this.buttonList.add(new GuiButton(12, this.width / 2 + 2, this.height / 4 + 96 + i, 98, 20, I18n.format("fml.menu.modoptions")));
        boolean serverInfo = PauseMenuCustomizeState.serverInfoMode != PauseMenuCustomizeState.DISCORD_MODE_NONE;
        this.lanButton = this.addButton(new GuiButtonWithStupidIcons(7, this.width / 2 - 100, this.height / 4 + 72 + i, serverInfo ? 98 : 200, 20, I18n.format(LANServerController.isLANOpen() ? "menu.closeLan" : "menu.openToLan"), PauseMenuCustomizeState.icon_discord_L, PauseMenuCustomizeState.icon_discord_L_aspect, PauseMenuCustomizeState.icon_discord_R, PauseMenuCustomizeState.icon_discord_R_aspect));
        this.buttonList.add(new GuiButtonWithStupidIcons(5, this.width / 2 - 100, this.height / 4 + 48 + i, 98, 20, I18n.format("gui.advancements"), PauseMenuCustomizeState.icon_achievements_L, PauseMenuCustomizeState.icon_achievements_L_aspect, PauseMenuCustomizeState.icon_achievements_R, PauseMenuCustomizeState.icon_achievements_R_aspect));
        this.buttonList.add(new GuiButtonWithStupidIcons(6, this.width / 2 + 2, this.height / 4 + 48 + i, 98, 20, I18n.format("gui.stats"), PauseMenuCustomizeState.icon_statistics_L, PauseMenuCustomizeState.icon_statistics_L_aspect, PauseMenuCustomizeState.icon_statistics_R, PauseMenuCustomizeState.icon_statistics_R_aspect));
        this.lanButton.enabled = SingleplayerServerController.isWorldRunning();

        if (PauseMenuCustomizeState.discordButtonMode != PauseMenuCustomizeState.DISCORD_MODE_NONE)
        {
            this.lanButton.enabled = true;
            this.lanButton.id = 8;
            this.lanButton.displayString = "" + PauseMenuCustomizeState.discordButtonText;
        }

        if (serverInfo)
        {
            this.buttonList.add(new GuiButtonWithStupidIcons(9, this.width / 2 + 2, this.height / 4 + 72 + i, 98, 20, PauseMenuCustomizeState.serverInfoButtonText, PauseMenuCustomizeState.icon_serverInfo_L, PauseMenuCustomizeState.icon_serverInfo_L_aspect, PauseMenuCustomizeState.icon_serverInfo_R, PauseMenuCustomizeState.icon_serverInfo_R_aspect));
        }

        if (!this.hasSentAutoSave)
        {
            this.hasSentAutoSave = true;
            SingleplayerServerController.autoSave();
        }
    }

    protected void actionPerformed(GuiButton button) throws IOException
    {
        switch (button.id)
        {
            case 0:
                this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
                break;
            case 1:
                boolean flag = this.mc.isIntegratedServerRunning() || this.mc.isDemo();
                button.enabled = false;
                this.mc.world.sendQuittingDisconnectingPacket();
                this.mc.loadWorld((WorldClient)null);

                if (flag)
                {
                    this.mc.shutdownIntegratedServer(new GuiMainMenu());
                }
                else
                {
                    this.mc.shutdownIntegratedServer(new GuiMultiplayer(new GuiMainMenu()));
                }

            case 2:
            case 3:
            default:
                break;
            case 4:
                this.mc.displayGuiScreen((GuiScreen)null);
                this.mc.setIngameFocus();
                break;
            case 5:
                if (this.mc.player != null)
                this.mc.displayGuiScreen(new GuiScreenAdvancements(this.mc.player.connection.getAdvancementManager()));
                break;
            case 6:
                if (this.mc.player != null)
                this.mc.displayGuiScreen(new GuiStats(this, this.mc.player.getStatFileWriter()));
                break;
            case 7:
                if (!LANServerController.supported())
                {
                    this.mc.displayGuiScreen(new GuiScreenLANNotSupported(this));
                }
                else if (LANServerController.isLANOpen())
                {
                    if (LANServerController.hasPeers())
                    {
                        this.mc.displayGuiScreen(new GuiYesNo(this, I18n.format("networkSettings.delete"), I18n.format("lanServer.wouldYouLikeToKick"), 0));
                    }
                    else
                    {
                        this.confirmClicked(false, 0);
                    }
                }
                else
                {
                    this.mc.displayGuiScreen(GuiScreenLANInfo.showLANInfoScreen(new GuiShareToLan(this, this.mc.playerController.getCurrentGameType().getName())));
                }

                break;
            case 8:
                if (PauseMenuCustomizeState.discordButtonMode == PauseMenuCustomizeState.DISCORD_MODE_INVITE_URL && PauseMenuCustomizeState.discordInviteURL != null)
                {
                    EagRuntime.openLink(PauseMenuCustomizeState.discordInviteURL);
                }

                break;
            case 9:
                switch (PauseMenuCustomizeState.serverInfoMode)
                {
                    case PauseMenuCustomizeState.SERVER_INFO_MODE_EXTERNAL_URL:
                        if (PauseMenuCustomizeState.serverInfoURL != null)
                        {
                            EagRuntime.openLink(PauseMenuCustomizeState.serverInfoURL);
                        }

                        break;
                    case PauseMenuCustomizeState.SERVER_INFO_MODE_SHOW_EMBED_OVER_HTTP:
                        if (PauseMenuCustomizeState.serverInfoURL != null)
                        {
                            GuiScreen screen = GuiScreenServerInfo.createForCurrentState(this, PauseMenuCustomizeState.serverInfoURL);

                            if (!this.mc.gameSettings.hasHiddenPhishWarning && !GuiScreenPhishingWarning.hasShownMessage)
                            {
                                screen = new GuiScreenPhishingWarning(screen);
                            }

                            this.mc.displayGuiScreen(screen);
                        }

                        break;
                    case PauseMenuCustomizeState.SERVER_INFO_MODE_SHOW_EMBED_OVER_WS:
                        if (PauseMenuCustomizeState.serverInfoHash != null)
                        {
                            GuiScreen screen = new GuiScreenRecieveServerInfo(this, PauseMenuCustomizeState.serverInfoHash);

                            if (!this.mc.gameSettings.hasHiddenPhishWarning && !GuiScreenPhishingWarning.hasShownMessage)
                            {
                                screen = new GuiScreenPhishingWarning(screen);
                            }

                            this.mc.displayGuiScreen(screen);
                        }

                        break;
                    default:
                        break;
                }

                break;
            case 11:
                this.mc.displayGuiScreen(new GuiScreenNotifications(this));
                break;
            case 12:
                net.minecraftforge.fml.client.FMLClientHandler.instance().showInGameModOptions(this);
                break;
        }
    }

    private boolean isVoiceMenuShown()
    {
        return this.voiceMenu != null && (!this.mc.isSingleplayer() || LANServerController.isHostingLAN());
    }

    public void updateScreen()
    {
        super.updateScreen();
        ++this.visibleTime;

        if (this.isVoiceMenuShown())
        {
            this.voiceMenu.updateScreen();
        }

        if (Mouse.isActuallyGrabbed())
        {
            Mouse.setGrabbed(false);
        }

        if (this.notifBellButton != null && this.mc.player != null)
        {
            this.notifBellButton.setUnread(this.mc.player.connection.getNotifManager().getUnread());
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        String titleStr = I18n.format("menu.game");
        int titleStrWidth = this.fontRenderer.getStringWidth(titleStr);
        this.drawString(this.fontRenderer, titleStr, (this.width - titleStrWidth) / 2, 20, 16777215);

        if (PauseMenuCustomizeState.icon_title_L != null)
        {
            this.mc.getTextureManager().bindTexture(PauseMenuCustomizeState.icon_title_L);
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)((this.width - titleStrWidth) / 2) - 6.0F - 16.0F * PauseMenuCustomizeState.icon_title_L_aspect, 16.0F, 0.0F);
            float f2 = 0.0625F;
            GlStateManager.scale(f2 * PauseMenuCustomizeState.icon_title_L_aspect, f2, f2);
            this.drawTexturedModalRect(0, 0, 0, 0, 256, 256);
            GlStateManager.popMatrix();
        }

        if (PauseMenuCustomizeState.icon_title_R != null)
        {
            this.mc.getTextureManager().bindTexture(PauseMenuCustomizeState.icon_title_L);
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)((this.width - titleStrWidth) / 2 + titleStrWidth + 6), 16.0F, 0.0F);
            float f2 = 0.0625F;
            GlStateManager.scale(f2 * PauseMenuCustomizeState.icon_title_R_aspect, f2, f2);
            this.drawTexturedModalRect(0, 0, 0, 0, 256, 256);
            GlStateManager.popMatrix();
        }

        this.updateCheckerOverlay.drawScreen(mouseX, mouseY, partialTicks);

        if (LANServerController.isLANOpen())
        {
            int offset = this.updateCheckerOverlay.getSharedWorldInfoYOffset();
            String str = I18n.format("lanServer.pauseMenu0");
            this.drawString(this.fontRenderer, str, 6, 10 + offset, 0xFFFF55);

            if (this.mc.gameSettings.hideJoinCode)
            {
                GlStateManager.pushMatrix();
                GlStateManager.translate(7.0F, 25.0F + (float)offset, 0.0F);
                GlStateManager.scale(0.75F, 0.75F, 0.75F);
                str = I18n.format("lanServer.showCode");
                int w = this.fontRenderer.getStringWidth(str);
                boolean hover = mouseX > 4 && mouseX < 8 + w * 3 / 4 && mouseY > 24 + offset && mouseY < 25 + offset + 8;
                this.drawString(this.fontRenderer, TextFormatting.UNDERLINE + str, 0, 0, hover ? 0xEEEEAA : 0xCCCC55);
                GlStateManager.popMatrix();
            }
            else
            {
                int w = this.fontRenderer.getStringWidth(str);
                GlStateManager.pushMatrix();
                GlStateManager.translate((float)(6 + w + 3), (float)(11 + offset), 0.0F);
                GlStateManager.scale(0.75F, 0.75F, 0.75F);
                str = I18n.format("lanServer.hideCode");
                int w2 = this.fontRenderer.getStringWidth(str);
                boolean hover = mouseX > 6 + w + 2 && mouseX < 6 + w + 3 + w2 * 3 / 4 && mouseY > 11 + offset - 1 && mouseY < 11 + offset + 6;
                this.drawString(this.fontRenderer, TextFormatting.UNDERLINE + str, 0, 0, hover ? 0xEEEEAA : 0xCCCC55);
                GlStateManager.popMatrix();
                this.drawString(this.fontRenderer, TextFormatting.GRAY + I18n.format("lanServer.pauseMenu1") + " " + TextFormatting.RESET + LANServerController.getCurrentURI(), 6, 25 + offset, 0xFFFFFF);
                this.drawString(this.fontRenderer, TextFormatting.GRAY + I18n.format("lanServer.pauseMenu2") + " " + TextFormatting.RESET + LANServerController.getCurrentCode(), 6, 35 + offset, 0xFFFFFF);
            }
        }

        try
        {
            if (this.isVoiceMenuShown())
            {
                if (this.voiceMenu.isBlockingInput())
                {
                    super.drawScreen(0, 0, partialTicks);
                }
                else
                {
                    super.drawScreen(mouseX, mouseY, partialTicks);
                }

                this.voiceMenu.drawScreen(mouseX, mouseY, partialTicks);
            }
            else
            {
                super.drawScreen(mouseX, mouseY, partialTicks);
            }
        }
        catch (GuiVoiceMenu.AbortedException ex)
        {
        }
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        try
        {
            if (this.isVoiceMenuShown())
            {
				if (keyCode == 1 && this.voiceMenu.isBlockingInput()) {
					this.voiceMenu.dismissModal();
					return;
				}
                this.voiceMenu.keyTyped(typedChar, keyCode);
            }

			if (keyCode == 1) {
				this.mc.displayGuiScreen(null);
				return;
			}
            super.keyTyped(typedChar, keyCode);
        }
        catch (GuiVoiceMenu.AbortedException ex)
        {
        }
    }

    public void confirmClicked(boolean result, int id)
    {
        this.mc.displayGuiScreen(this);
        LANServerController.closeLANNoKick();

        if (result)
        {
            LANServerController.cleanupLAN();
            SingleplayerServerController.configureLAN(this.mc.world.getWorldInfo().getGameType(), false);
        }

        this.mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString(I18n.format("lanServer.closed")));
        this.lanButton.displayString = I18n.format("menu.openToLan");
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        try
        {
            if (this.isVoiceMenuShown())
            {
                this.voiceMenu.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
        catch (GuiVoiceMenu.AbortedException ex)
        {
            return;
        }

        if (mouseButton == 0 && LANServerController.isLANOpen())
        {
            int offset = this.updateCheckerOverlay.getSharedWorldInfoYOffset();

            if (this.mc.gameSettings.hideJoinCode)
            {
                int w = this.fontRenderer.getStringWidth(I18n.format("lanServer.showCode"));

                if (mouseX > 4 && mouseX < 8 + w * 3 / 4 && mouseY > 24 + offset && mouseY < 25 + offset + 8)
                {
                    this.mc.gameSettings.hideJoinCode = false;
                    this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    this.mc.gameSettings.saveOptions();
                }
            }
            else
            {
                int w = this.fontRenderer.getStringWidth(I18n.format("lanServer.pauseMenu0"));
                int w2 = this.fontRenderer.getStringWidth(I18n.format("lanServer.hideCode"));

                if (mouseX > 6 + w + 2 && mouseX < 6 + w + 3 + w2 * 3 / 4 && mouseY > 11 + offset - 1 && mouseY < 11 + offset + 6)
                {
                    this.mc.gameSettings.hideJoinCode = true;
                    this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    this.mc.gameSettings.saveOptions();
                }
            }
        }

        this.updateCheckerOverlay.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void setWorldAndResolution(Minecraft mc, int width, int height)
    {
        super.setWorldAndResolution(mc, width, height);

        if (this.voiceMenu != null)
        {
            this.voiceMenu.setResolution(mc, width, height);
        }
    }

    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        try
        {
            if (this.isVoiceMenuShown())
            {
                this.voiceMenu.mouseReleased(mouseX, mouseY, state);
            }

            super.mouseReleased(mouseX, mouseY, state);
        }
        catch (GuiVoiceMenu.AbortedException ex)
        {
        }
    }

    protected boolean isPartOfPauseMenu()
    {
        return true;
    }
}
