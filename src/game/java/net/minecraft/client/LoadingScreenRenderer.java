package net.minecraft.client;

import static net.lax1dude.eaglercraft.v1_8.opengl.RealOpenGLEnums.*;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IProgressUpdate;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LoadingScreenRenderer implements IProgressUpdate
{
    public String message = "";
    public final Minecraft mc;
    public String currentlyDisplayedText = "";
    public long systemTime = Minecraft.getSystemTime();
    public boolean loadingSuccess;

    public LoadingScreenRenderer(Minecraft mcIn)
    {
        this.mc = mcIn;
    }

    public void resetProgressAndMessage(String message)
    {
        this.loadingSuccess = false;
        this.displayString(message);
    }

    public void displaySavingString(String message)
    {
        this.loadingSuccess = true;
        this.displayString(message);
    }

    private void displayString(String message)
    {
        this.currentlyDisplayedText = message;

        if (this.mc.running)
        {
            GlStateManager.clear(GL_DEPTH_BUFFER_BIT);
            GlStateManager.matrixMode(GL_PROJECTION);
            GlStateManager.loadIdentity();
            GlStateManager.ortho(0.0D, this.mc.scaledResolution.getScaledWidth_double(), this.mc.scaledResolution.getScaledHeight_double(), 0.0D, 100.0D, 300.0D);
            GlStateManager.matrixMode(GL_MODELVIEW);
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0F, 0.0F, -200.0F);
        }
    }

    public void displayLoadingString(String message)
    {
        if (this.mc.running)
        {
            this.systemTime = 0L;
            this.message = message;
            this.setLoadingProgress(-1);
            this.systemTime = 0L;
        }
    }

    public void eaglerShow(String line1, String line2)
    {
        if (this.mc.running)
        {
            this.systemTime = 0L;
            this.currentlyDisplayedText = line1;
            this.message = line2;
            this.setLoadingProgress(-1);
            this.systemTime = 0L;
        }
    }

    public void eaglerShowRefreshResources()
    {
        this.eaglerShow(I18n.format("resourcePack.load.refreshing"), I18n.format("resourcePack.load.pleaseWait"));
    }

    public void setLoadingProgress(int progress)
    {
        if (this.mc.running)
        {
            long i = Minecraft.getSystemTime();

            if (i - this.systemTime >= 100L)
            {
                this.systemTime = i;
                ScaledResolution scaledresolution = this.mc.scaledResolution;
                int k = scaledresolution.getScaledWidth();
                int l = scaledresolution.getScaledHeight();
                GlStateManager.clear(GL_DEPTH_BUFFER_BIT);
                GlStateManager.matrixMode(GL_PROJECTION);
                GlStateManager.loadIdentity();
                GlStateManager.ortho(0.0D, scaledresolution.getScaledWidth_double(), scaledresolution.getScaledHeight_double(), 0.0D, 100.0D, 300.0D);
                GlStateManager.matrixMode(GL_MODELVIEW);
                GlStateManager.loadIdentity();
                GlStateManager.translate(0.0F, 0.0F, -200.0F);
                GlStateManager.clear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

                Tessellator tessellator = Tessellator.getInstance();
                WorldRenderer bufferbuilder = tessellator.getBuffer();
                this.mc.getTextureManager().bindTexture(Gui.OPTIONS_BACKGROUND);
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                bufferbuilder.pos(0.0D, (double)l, 0.0D).tex(0.0D, (double)((float)l / 32.0F)).color(64, 64, 64, 255).endVertex();
                bufferbuilder.pos((double)k, (double)l, 0.0D).tex((double)((float)k / 32.0F), (double)((float)l / 32.0F)).color(64, 64, 64, 255).endVertex();
                bufferbuilder.pos((double)k, 0.0D, 0.0D).tex((double)((float)k / 32.0F), 0.0D).color(64, 64, 64, 255).endVertex();
                bufferbuilder.pos(0.0D, 0.0D, 0.0D).tex(0.0D, 0.0D).color(64, 64, 64, 255).endVertex();
                tessellator.draw();

                if (progress >= 0)
                {
                    int k1 = k / 2 - 50;
                    int l1 = l / 2 + 16;
                    GlStateManager.disableTexture2D();
                    bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
                    bufferbuilder.pos((double)k1, (double)l1, 0.0D).color(128, 128, 128, 255).endVertex();
                    bufferbuilder.pos((double)k1, (double)(l1 + 2), 0.0D).color(128, 128, 128, 255).endVertex();
                    bufferbuilder.pos((double)(k1 + 100), (double)(l1 + 2), 0.0D).color(128, 128, 128, 255).endVertex();
                    bufferbuilder.pos((double)(k1 + 100), (double)l1, 0.0D).color(128, 128, 128, 255).endVertex();
                    bufferbuilder.pos((double)k1, (double)l1, 0.0D).color(128, 255, 128, 255).endVertex();
                    bufferbuilder.pos((double)k1, (double)(l1 + 2), 0.0D).color(128, 255, 128, 255).endVertex();
                    bufferbuilder.pos((double)(k1 + progress), (double)(l1 + 2), 0.0D).color(128, 255, 128, 255).endVertex();
                    bufferbuilder.pos((double)(k1 + progress), (double)l1, 0.0D).color(128, 255, 128, 255).endVertex();
                    tessellator.draw();
                    GlStateManager.enableTexture2D();
                }

                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
                this.mc.fontRenderer.drawStringWithShadow(this.currentlyDisplayedText, (float)((k - this.mc.fontRenderer.getStringWidth(this.currentlyDisplayedText)) / 2), (float)(l / 2 - 4 - 16), 16777215);

                if (this.message != null)
                {
                    this.mc.fontRenderer.drawStringWithShadow(this.message, (float)((k - this.mc.fontRenderer.getStringWidth(this.message)) / 2), (float)(l / 2 - 4 + 8), 16777215);
                }

                this.mc.updateDisplay();
            }
        }
    }

    public void setDoneWorking()
    {
    }
}
