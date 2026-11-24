package net.minecraft.client.gui;

import java.io.IOException;
import net.lax1dude.eaglercraft.v1_8.opengl.EaglercraftGPU;
import net.lax1dude.eaglercraft.v1_8.opengl.ext.dynamiclights.DynamicLightsStateManager;
import net.lax1dude.eaglercraft.v1_8.Display;
import net.lax1dude.eaglercraft.v1_8.minecraft.GuiScreenVideoSettingsWarning;
import net.lax1dude.eaglercraft.v1_8.recording.ScreenRecordingController;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiVideoSettings extends GuiScreen
{
    private final GuiScreen parentGuiScreen;
    protected String screenTitle = "Video Settings";
    private final GameSettings guiGameSettings;
    private GuiListExtended optionsRowList;
    private boolean vsyncLock = false;
    private static final GameSettings.Options[] VIDEO_OPTIONS = new GameSettings.Options[] {GameSettings.Options.GRAPHICS, GameSettings.Options.RENDER_DISTANCE, GameSettings.Options.AMBIENT_OCCLUSION, GameSettings.Options.FRAMERATE_LIMIT, GameSettings.Options.EAGLER_VSYNC, GameSettings.Options.ANAGLYPH, GameSettings.Options.VIEW_BOBBING, GameSettings.Options.GUI_SCALE, GameSettings.Options.GAMMA, GameSettings.Options.RENDER_CLOUDS, GameSettings.Options.PARTICLES, GameSettings.Options.FXAA, GameSettings.Options.MIPMAP_LEVELS, GameSettings.Options.BLOCK_ALTERNATIVES, GameSettings.Options.ENTITY_SHADOWS, GameSettings.Options.FOG, GameSettings.Options.EAGLER_DYNAMIC_LIGHTS, GameSettings.Options.FULLSCREEN, GameSettings.Options.OF_CONNECTED_TEXTURES, GameSettings.Options.OF_BETTER_GRASS, GameSettings.Options.OF_CUSTOM_SKIES, GameSettings.Options.OF_SMART_LEAVES, GameSettings.Options.OF_CUSTOM_ITEMS, GameSettings.Options.FNAW_SKINS, GameSettings.Options.HUD_FPS, GameSettings.Options.HUD_COORDS, GameSettings.Options.HUD_PLAYER, GameSettings.Options.HUD_STATS, GameSettings.Options.HUD_WORLD, GameSettings.Options.HUD_24H, GameSettings.Options.CHUNK_FIX, GameSettings.Options.ATTACK_INDICATOR};

    public GuiVideoSettings(GuiScreen parentScreenIn, GameSettings gameSettingsIn)
    {
        this.parentGuiScreen = parentScreenIn;
        this.guiGameSettings = gameSettingsIn;
    }

    public void initGui()
    {
        this.screenTitle = I18n.format("options.videoTitle");
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height - 27, I18n.format("gui.done")));

        this.optionsRowList = new GuiOptionsRowList(this.mc, this.width, this.height, 32, this.height - 32, 25, VIDEO_OPTIONS);

        if (!DynamicLightsStateManager.isSupported())
        {
            GuiOptionButton btn = ((GuiOptionsRowList)this.optionsRowList).getButtonFor(GameSettings.Options.EAGLER_DYNAMIC_LIGHTS);

            if (btn != null)
            {
                btn.enabled = false;
            }
        }

        if (EaglercraftGPU.checkOpenGLESVersion() < 300)
        {
            GuiOptionSlider btn = ((GuiOptionsRowList)this.optionsRowList).getSliderFor(GameSettings.Options.MIPMAP_LEVELS);

            if (btn != null)
            {
                btn.displayString = I18n.format(GameSettings.Options.MIPMAP_LEVELS.getTranslation()) + ": N/A";
                btn.sliderValue = 0.0F;
                btn.enabled = false;
            }
        }

        if (!Display.supportsFullscreen())
        {
            GuiOptionButton btn = ((GuiOptionsRowList)this.optionsRowList).getButtonFor(GameSettings.Options.FULLSCREEN);

            if (btn != null)
            {
                btn.displayString = I18n.format(GameSettings.Options.FULLSCREEN.getTranslation()) + ": " + I18n.format("options.off");
                btn.enabled = false;
            }
        }
    }

    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        this.optionsRowList.handleMouseInput();
    }

    public void handleTouchInput() throws IOException
    {
        super.handleTouchInput();
        this.optionsRowList.handleTouchInput();
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (keyCode == 1)
        {
            this.mc.gameSettings.saveOptions();
        }

        super.keyTyped(typedChar, keyCode);
    }

    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.enabled)
        {
            if (button.id == 200)
            {
                this.mc.gameSettings.saveOptions();
                GuiScreen contScreen = this.parentGuiScreen;
                int vidIssues = this.mc.gameSettings.checkBadVideoSettings();

                if (vidIssues != 0)
                {
                    contScreen = new GuiScreenVideoSettingsWarning(contScreen, vidIssues);
                }

                this.mc.displayGuiScreen(contScreen);
            }
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        int i = this.guiGameSettings.guiScale;
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.optionsRowList.mouseClicked(mouseX, mouseY, mouseButton);

        if (this.guiGameSettings.guiScale != i)
        {
            ScaledResolution scaledresolution = this.mc.scaledResolution = new ScaledResolution(this.mc);
            int j = scaledresolution.getScaledWidth();
            int k = scaledresolution.getScaledHeight();
            this.setWorldAndResolution(this.mc, j, k);
            this.mc.voiceOverlay.setResolution(j, k);
            this.mc.notifRenderer.setResolution(this.mc, j, k, scaledresolution.getScaleFactor());
        }
    }

    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        int i = this.guiGameSettings.guiScale;
        super.mouseReleased(mouseX, mouseY, state);
        this.optionsRowList.mouseReleased(mouseX, mouseY, state);

        if (this.guiGameSettings.guiScale != i)
        {
            ScaledResolution scaledresolution = this.mc.scaledResolution = new ScaledResolution(this.mc);
            int j = scaledresolution.getScaledWidth();
            int k = scaledresolution.getScaledHeight();
            this.setWorldAndResolution(this.mc, j, k);
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.optionsRowList.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRenderer, this.screenTitle, this.width / 2, 5, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void updateScreen()
    {
        boolean vsyncLockEn = ScreenRecordingController.isVSyncLocked();

        if (vsyncLockEn != this.vsyncLock)
        {
            this.vsyncLock = vsyncLockEn;
            GuiOptionButton btn = ((GuiOptionsRowList)this.optionsRowList).getButtonFor(GameSettings.Options.EAGLER_VSYNC);

            if (btn != null)
            {
                btn.enabled = !vsyncLockEn;
            }
        }
    }

    // FORGE: fix for MC-64581 very laggy mipmap slider
    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();
        this.mc.gameSettings.onGuiClosed();
    }
}
