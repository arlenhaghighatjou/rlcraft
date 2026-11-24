package net.minecraft.client.gui;

import java.io.IOException;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.Mouse;
import net.lax1dude.eaglercraft.v1_8.boot_menu.GuiScreenEnterBootMenu;
import net.lax1dude.eaglercraft.v1_8.cookie.GuiScreenRevokeSessionToken;
import net.lax1dude.eaglercraft.v1_8.cookie.ServerCookieDataStore;
import net.lax1dude.eaglercraft.v1_8.internal.EnumCursorType;
import net.lax1dude.eaglercraft.v1_8.internal.EnumPlatformType;
import net.lax1dude.eaglercraft.v1_8.internal.KeyboardConstants;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerFolderResourcePack;
import net.lax1dude.eaglercraft.v1_8.minecraft.GuiScreenGenericErrorMessage;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.ext.deferred.EaglerDeferredPipeline;
import net.lax1dude.eaglercraft.v1_8.opengl.ext.deferred.gui.GuiShaderConfig;
import net.lax1dude.eaglercraft.v1_8.opengl.ext.deferred.gui.GuiShadersNotSupported;
import net.lax1dude.eaglercraft.v1_8.profile.GuiScreenImportExportProfile;
import net.lax1dude.eaglercraft.v1_8.recording.GuiScreenRecordingNote;
import net.lax1dude.eaglercraft.v1_8.recording.GuiScreenRecordingSettings;
import net.lax1dude.eaglercraft.v1_8.recording.ScreenRecordingController;
import net.lax1dude.eaglercraft.v1_8.sp.SingleplayerServerController;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiOptions extends GuiScreen
{
    private static final GameSettings.Options[] SCREEN_OPTIONS = new GameSettings.Options[] {GameSettings.Options.FOV};
    private final GuiScreen lastScreen;
    private final GameSettings settings;
    private GuiButton difficultyButton;
    private GuiLockIconButton lockButton;
    protected String title = "Options";
    private GuiButton broadcastSettings;

    public GuiOptions(GuiScreen p_i1046_1_, GameSettings p_i1046_2_)
    {
        this.lastScreen = p_i1046_1_;
        this.settings = p_i1046_2_;
    }

    public void initGui()
    {
        this.title = I18n.format("options.title");
        int i = 0;

        for (GameSettings.Options gamesettings$options : SCREEN_OPTIONS)
        {
            if (gamesettings$options.isFloat())
            {
                this.buttonList.add(new GuiOptionSlider(gamesettings$options.getOrdinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), gamesettings$options));
            }
            else
            {
                GuiOptionButton guioptionbutton = new GuiOptionButton(gamesettings$options.getOrdinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), gamesettings$options, this.settings.getKeyBinding(gamesettings$options));
                this.buttonList.add(guioptionbutton);
            }

            ++i;
        }

        if (this.mc.world != null)
        {
            EnumDifficulty enumdifficulty = this.mc.world.getDifficulty();
            this.difficultyButton = new GuiButton(108, this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), 150, 20, this.getDifficultyText(enumdifficulty));
            this.buttonList.add(this.difficultyButton);

            if (this.mc.isSingleplayer() && !this.mc.world.getWorldInfo().isHardcoreModeEnabled())
            {
                this.difficultyButton.setWidth(this.difficultyButton.getButtonWidth() - 20);
                this.lockButton = new GuiLockIconButton(109, this.difficultyButton.x + this.difficultyButton.getButtonWidth(), this.difficultyButton.y);
                this.buttonList.add(this.lockButton);
                this.lockButton.setLocked(this.mc.world.getWorldInfo().isDifficultyLocked());
                this.lockButton.enabled = !this.lockButton.isLocked();
                this.difficultyButton.enabled = !this.lockButton.isLocked();
            }
            else
            {
                this.difficultyButton.enabled = false;
            }
        }

        this.buttonList.add(new GuiButton(110, this.width / 2 - 155, this.height / 6 + 48 - 6, 150, 20, I18n.format("options.skinCustomisation")));
        this.buttonList.add(new GuiButton(8675309, this.width / 2 + 5, this.height / 6 + 48 - 6, 150, 20, I18n.format("shaders.gui.optionsButton")));
        this.buttonList.add(new GuiButton(106, this.width / 2 - 155, this.height / 6 + 72 - 6, 150, 20, I18n.format("options.sounds")));
        boolean support = ScreenRecordingController.isSupported();
        this.broadcastSettings = this.addButton(new GuiButton(107, this.width / 2 + 5, this.height / 6 + 72 - 6, 150, 20, I18n.format(support ? "options.screenRecording.button" : "options.screenRecording.unsupported")));
        this.broadcastSettings.enabled = support;
        this.buttonList.add(new GuiButton(101, this.width / 2 - 155, this.height / 6 + 96 - 6, 150, 20, I18n.format("options.video")));
        this.buttonList.add(new GuiButton(100, this.width / 2 + 5, this.height / 6 + 96 - 6, 150, 20, I18n.format("options.controls")));
        this.buttonList.add(new GuiButton(102, this.width / 2 - 155, this.height / 6 + 120 - 6, 150, 20, I18n.format("options.language")));
        this.buttonList.add(new GuiButton(103, this.width / 2 + 5, this.height / 6 + 120 - 6, 150, 20, I18n.format("options.chat.title")));
        GuiButton btn = this.addButton(new GuiButton(105, this.width / 2 - 155, this.height / 6 + 144 - 6, 150, 20, I18n.format("options.resourcepack")));
        btn.enabled = EaglerFolderResourcePack.isSupported();
        btn = this.addButton(new GuiButton(104, this.width / 2 + 5, this.height / 6 + 144 - 6, 150, 20, I18n.format("options.debugConsoleButton")));
        btn.enabled = EagRuntime.getPlatformType() != EnumPlatformType.DESKTOP;
        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, I18n.format("gui.done")));
    }

    public String getDifficultyText(EnumDifficulty p_175355_1_)
    {
        ITextComponent itextcomponent = new TextComponentString("");
        itextcomponent.appendSibling(new TextComponentTranslation("options.difficulty", new Object[0]));
        itextcomponent.appendText(": ");
        itextcomponent.appendSibling(new TextComponentTranslation(p_175355_1_.getDifficultyResourceKey(), new Object[0]));
        return itextcomponent.getFormattedText();
    }

    public void confirmClicked(boolean result, int id)
    {
        this.mc.displayGuiScreen(this);

        if (id == 109 && result && this.mc.world != null)
        {
            this.mc.world.getWorldInfo().setDifficultyLocked(true);
            SingleplayerServerController.setDifficulty(-1);
            this.lockButton.setLocked(true);
            this.lockButton.enabled = false;
            this.difficultyButton.enabled = false;
        }
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (keyCode == 1)
        {
            this.mc.gameSettings.saveOptions();
        }

        super.keyTyped(typedChar, keyCode);

        if (keyCode == KeyboardConstants.KEY_DELETE || keyCode == KeyboardConstants.KEY_BACK)
        {
            if (this.mc.world == null && EagRuntime.getConfiguration().isAllowBootMenu())
            {
                this.mc.displayGuiScreen(new GuiScreenEnterBootMenu(this));
            }
        }
    }

    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.enabled)
        {
            if (button.id < 100 && button instanceof GuiOptionButton)
            {
                GameSettings.Options gamesettings$options = ((GuiOptionButton)button).getOption();
                this.settings.setOptionValue(gamesettings$options, 1);
                button.displayString = this.settings.getKeyBinding(GameSettings.Options.byOrdinal(button.id));
            }

            if (button.id == 108)
            {
                this.mc.world.getWorldInfo().setDifficulty(EnumDifficulty.getDifficultyEnum(this.mc.world.getDifficulty().getDifficultyId() + 1));
                SingleplayerServerController.setDifficulty(this.mc.world.getWorldInfo().getDifficulty().getDifficultyId());
                this.difficultyButton.displayString = this.getDifficultyText(this.mc.world.getDifficulty());
            }

            if (button.id == 109)
            {
                this.mc.displayGuiScreen(new GuiYesNo(this, (new TextComponentTranslation("difficulty.lock.title", new Object[0])).getFormattedText(), (new TextComponentTranslation("difficulty.lock.question", new Object[] {new TextComponentTranslation(this.mc.world.getWorldInfo().getDifficulty().getDifficultyResourceKey(), new Object[0])})).getFormattedText(), 109));
            }

            if (button.id == 110)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiCustomizeSkin(this));
            }

            if (button.id == 8675309)
            {
                if (EaglerDeferredPipeline.isSupported())
                {
                    this.mc.displayGuiScreen(new GuiShaderConfig(this));
                }
                else
                {
                    this.mc.displayGuiScreen(new GuiShadersNotSupported(this, I18n.format(EaglerDeferredPipeline.getReasonUnsupported())));
                }
            }

            if (button.id == 101)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiVideoSettings(this, this.settings));
            }

            if (button.id == 100)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiControls(this, this.settings));
            }

            if (button.id == 102)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiLanguage(this, this.settings, this.mc.getLanguageManager()));
            }

            if (button.id == 103)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new ScreenChatOptions(this, this.settings));
            }

            if (button.id == 104)
            {
                EagRuntime.showDebugConsole();
            }

            if (button.id == 200)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(this.lastScreen);
            }

            if (button.id == 105)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiScreenResourcePacks(this));
            }

            if (button.id == 106)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiScreenOptionsSounds(this, this.settings));
            }

            if (button.id == 107)
            {
                if (ScreenRecordingController.isSupported())
                {
                    GuiScreen screen = new GuiScreenRecordingSettings(this);

                    if (!GuiScreenRecordingNote.hasShown)
                    {
                        screen = new GuiScreenRecordingNote(screen);
                    }

                    this.mc.displayGuiScreen(screen);
                }
            }
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 15, 16777215);

        if (this.mc.world == null && !EagRuntime.getConfiguration().isDemo())
        {
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.75F, 0.75F, 0.75F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            String text = I18n.format("editProfile.importExport");
            int w = this.fontRenderer.getStringWidth(text);
            boolean hover = mouseX > 1 && mouseY > 1 && mouseX < (w * 3 / 4) + 7 && mouseY < 12;

            if (hover)
            {
                Mouse.showCursor(EnumCursorType.HAND);
            }

            this.drawString(this.fontRenderer, TextFormatting.UNDERLINE + text, 5, 5, hover ? 0xFFEEEE22 : 0xFFCCCCCC);
            GlStateManager.popMatrix();
        }

        if (this.mc.world == null && EagRuntime.getConfiguration().isAllowBootMenu())
        {
            this.drawCenteredString(this.fontRenderer, I18n.format("options.pressDeleteText"), this.width / 2, this.height / 6 + 22, 11184810);
        }

        if (EagRuntime.getConfiguration().isEnableServerCookies() && this.mc.player == null)
        {
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.75F, 0.75F, 0.75F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            String text = I18n.format("revokeSessionToken.button");
            int w = this.fontRenderer.getStringWidth(text);
            boolean hover = mouseX > this.width - 5 - (w + 5) * 3 / 4 && mouseY > 1 && mouseX < this.width - 2 && mouseY < 12;

            if (hover)
            {
                Mouse.showCursor(EnumCursorType.HAND);
            }

            this.drawString(this.fontRenderer, TextFormatting.UNDERLINE + text, (this.width - 1) * 4 / 3 - w - 5, 5, hover ? 0xFFEEEE22 : 0xFFCCCCCC);
            GlStateManager.popMatrix();
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (this.mc.world == null && !EagRuntime.getConfiguration().isDemo())
        {
            int w = this.fontRenderer.getStringWidth(I18n.format("editProfile.importExport"));

            if (mouseX > 1 && mouseY > 1 && mouseX < (w * 3 / 4) + 7 && mouseY < 12)
            {
                this.mc.displayGuiScreen(new GuiScreenImportExportProfile(this));
                this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
        }

        if (EagRuntime.getConfiguration().isEnableServerCookies() && this.mc.player == null)
        {
            int w = this.fontRenderer.getStringWidth(I18n.format("revokeSessionToken.button"));

            if (mouseX > this.width - 5 - (w + 5) * 3 / 4 && mouseY > 1 && mouseX < this.width - 2 && mouseY < 12)
            {
                ServerCookieDataStore.flush();
                this.mc.displayGuiScreen(ServerCookieDataStore.numRevokable() == 0 ? new GuiScreenGenericErrorMessage("errorNoSessions.title", "errorNoSessions.desc", this) : new GuiScreenRevokeSessionToken(this));
                this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
        }
    }
}
