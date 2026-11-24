package net.minecraft.client.gui;

import static net.lax1dude.eaglercraft.v1_8.opengl.RealOpenGLEnums.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.ImageData;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.IOUtils;
import net.lax1dude.eaglercraft.v1_8.EaglerBidiReorder;
import net.lax1dude.eaglercraft.v1_8.HString;
import net.lax1dude.eaglercraft.v1_8.minecraft.FontMappingHelper;

@SideOnly(Side.CLIENT)
public class FontRenderer implements IResourceManagerReloadListener
{
    private static final ResourceLocation[] UNICODE_PAGE_LOCATIONS = new ResourceLocation[256];
    protected final int[] charWidth = new int[256];
    public int FONT_HEIGHT = 9;
    public EaglercraftRandom fontRandom = new EaglercraftRandom();
    protected final byte[] glyphWidth = new byte[65536];
    protected final int[] colorCode = new int[32];
    protected final ResourceLocation locationFontTexture;
    protected final TextureManager renderEngine;
    protected float posX;
    protected float posY;
    protected boolean unicodeFlag;
    protected boolean bidiFlag;
    protected float red;
    protected float blue;
    protected float green;
    protected float alpha;
    protected int textColor;
    protected boolean randomStyle;
    protected boolean boldStyle;
    protected boolean italicStyle;
    protected boolean underlineStyle;
    protected boolean strikethroughStyle;
    protected static char[] codepointLookup = new char[] {192, 193, 194, 200, 202, 203, 205, 211, 212, 213, 218, 223, 227, 245, 287, 304, 305, 338, 339, 350, 351, 372, 373, 382, 519, 0, 0, 0, 0, 0, 0, 0, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 0, 199, 252, 233, 226, 228, 224, 229, 231, 234, 235, 232, 239, 238, 236, 196, 197, 201, 230, 198, 244, 246, 242, 251, 249, 255, 214, 220, 248, 163, 216, 215, 402, 225, 237, 243, 250, 241, 209, 170, 186, 191, 174, 172, 189, 188, 161, 171, 187, 9617, 9618, 9619, 9474, 9508, 9569, 9570, 9558, 9557, 9571, 9553, 9559, 9565, 9564, 9563, 9488, 9492, 9524, 9516, 9500, 9472, 9532, 9566, 9567, 9562, 9556, 9577, 9574, 9568, 9552, 9580, 9575, 9576, 9572, 9573, 9561, 9560, 9554, 9555, 9579, 9578, 9496, 9484, 9608, 9604, 9612, 9616, 9600, 945, 946, 915, 960, 931, 963, 956, 964, 934, 920, 937, 948, 8734, 8709, 8712, 8745, 8801, 177, 8805, 8804, 8992, 8993, 247, 8776, 176, 8729, 183, 8730, 8319, 178, 9632, 0};

    public FontRenderer(GameSettings gameSettingsIn, ResourceLocation location, TextureManager textureManagerIn, boolean unicode)
    {
        this.locationFontTexture = location;
        this.renderEngine = textureManagerIn;
        this.unicodeFlag = unicode;
        bindTexture(this.locationFontTexture);

        for (int i = 0; i < 32; ++i)
        {
            int j = (i >> 3 & 1) * 85;
            int k = (i >> 2 & 1) * 170 + j;
            int l = (i >> 1 & 1) * 170 + j;
            int i1 = (i >> 0 & 1) * 170 + j;

            if (i == 6)
            {
                k += 85;
            }

            if (gameSettingsIn.anaglyph)
            {
                int j1 = (k * 30 + l * 59 + i1 * 11) / 100;
                int k1 = (k * 30 + l * 70) / 100;
                int l1 = (k * 30 + i1 * 70) / 100;
                k = j1;
                l = k1;
                i1 = l1;
            }

            if (i >= 16)
            {
                k /= 4;
                l /= 4;
                i1 /= 4;
            }

            this.colorCode[i] = (k & 255) << 16 | (l & 255) << 8 | i1 & 255;
        }

        this.readGlyphSizes();
    }

    public void onResourceManagerReload(IResourceManager resourceManager)
    {
        this.readFontTexture();
        this.readGlyphSizes();
    }

    private void readFontTexture()
    {
        ImageData bufferedimage;

        try
        {
            bufferedimage = TextureUtil.readBufferedImage(getResource(this.locationFontTexture).getInputStream());
        }
        catch (IOException ioexception)
        {
            throw new RuntimeException(ioexception);
        }

        int lvt_3_2_ = bufferedimage.width;
        int lvt_4_1_ = bufferedimage.height;
        int[] lvt_5_1_ = bufferedimage.pixels;
        int lvt_6_1_ = lvt_4_1_ / 16;
        int lvt_7_1_ = lvt_3_2_ / 16;
        boolean lvt_8_1_ = true;
        float lvt_9_1_ = 8.0F / (float)lvt_7_1_;

        for (int lvt_10_1_ = 0; lvt_10_1_ < 256; ++lvt_10_1_)
        {
            int j1 = lvt_10_1_ % 16;
            int k1 = lvt_10_1_ / 16;

            if (lvt_10_1_ == 32)
            {
                this.charWidth[lvt_10_1_] = 4;
            }

            int l1;

            for (l1 = lvt_7_1_ - 1; l1 >= 0; --l1)
            {
                int i2 = j1 * lvt_7_1_ + l1;
                boolean flag1 = true;

                for (int j2 = 0; j2 < lvt_6_1_ && flag1; ++j2)
                {
                    int k2 = (k1 * lvt_7_1_ + j2) * lvt_3_2_;

                    if ((lvt_5_1_[i2 + k2] >> 24 & 255) != 0)
                    {
                        flag1 = false;
                    }
                }

                if (!flag1)
                {
                    break;
                }
            }

            ++l1;
            this.charWidth[lvt_10_1_] = (int)(0.5D + (double)((float)l1 * lvt_9_1_)) + 1;
        }
    }

    private void readGlyphSizes()
    {
        InputStream inputstream = null;

        try
        {
            inputstream = getResource(new ResourceLocation("font/glyph_sizes.bin")).getInputStream();
            inputstream.read(this.glyphWidth);
        }
        catch (IOException ioexception)
        {
            throw new RuntimeException(ioexception);
        }
        finally
        {
            IOUtils.closeQuietly(inputstream);
        }
    }

    private float renderChar(char ch, boolean italic)
    {
        if (ch == 160) return 4.0F; // forge: display nbsp as space. MC-2595
        if (ch == ' ')
        {
            return 4.0F;
        }
        else
        {
            int i = FontMappingHelper.lookupChar(ch, false);
            return i != -1 && !this.unicodeFlag ? this.renderDefaultChar(i, italic) : this.renderUnicodeChar(ch, italic);
        }
    }

    protected float renderDefaultChar(int ch, boolean italic)
    {
        int i = ch % 16 * 8;
        int j = ch / 16 * 8;
        int k = italic ? 1 : 0;
        bindTexture(this.locationFontTexture);
        int l = this.charWidth[ch];
        float f = (float)l - 0.01F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getBuffer();
        worldrenderer.begin(GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos((double)(this.posX + (float)k), (double)this.posY, 0.0D).tex((double)((float)i / 128.0F), (double)((float)j / 128.0F)).endVertex();
        worldrenderer.pos((double)(this.posX - (float)k), (double)(this.posY + 7.99F), 0.0D).tex((double)((float)i / 128.0F), (double)(((float)j + 7.99F) / 128.0F)).endVertex();
        worldrenderer.pos((double)(this.posX + f - 1.0F + (float)k), (double)this.posY, 0.0D).tex((double)(((float)i + f - 1.0F) / 128.0F), (double)((float)j / 128.0F)).endVertex();
        worldrenderer.pos((double)(this.posX + f - 1.0F - (float)k), (double)(this.posY + 7.99F), 0.0D).tex((double)(((float)i + f - 1.0F) / 128.0F), (double)(((float)j + 7.99F) / 128.0F)).endVertex();
        tessellator.draw();
        return (float)l;
    }

    private ResourceLocation getUnicodePageLocation(int page)
    {
        if (UNICODE_PAGE_LOCATIONS[page] == null)
        {
            UNICODE_PAGE_LOCATIONS[page] = new ResourceLocation(HString.format("textures/font/unicode_page_%02x.png", page));
        }

        return UNICODE_PAGE_LOCATIONS[page];
    }

    private void loadGlyphTexture(int page)
    {
        bindTexture(this.getUnicodePageLocation(page));
    }

    protected float renderUnicodeChar(char ch, boolean italic)
    {
        int i = this.glyphWidth[ch] & 255;

        if (i == 0)
        {
            return 0.0F;
        }
        else
        {
            int j = ch / 256;
            this.loadGlyphTexture(j);
            int k = i >>> 4;
            int l = i & 15;
            float f = (float)k;
            float f1 = (float)(l + 1);
            float f2 = (float)(ch % 16 * 16) + f;
            float f3 = (float)((ch & 255) / 16 * 16);
            float f4 = f1 - f - 0.02F;
            float f5 = italic ? 1.0F : 0.0F;
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getBuffer();
            worldrenderer.begin(GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.pos((double)(this.posX + f5), (double)this.posY, 0.0D).tex((double)(f2 / 256.0F), (double)(f3 / 256.0F)).endVertex();
            worldrenderer.pos((double)(this.posX - f5), (double)(this.posY + 7.99F), 0.0D).tex((double)(f2 / 256.0F), (double)((f3 + 15.98F) / 256.0F)).endVertex();
            worldrenderer.pos((double)(this.posX + f4 / 2.0F + f5), (double)this.posY, 0.0D).tex((double)((f2 + f4) / 256.0F), (double)(f3 / 256.0F)).endVertex();
            worldrenderer.pos((double)(this.posX + f4 / 2.0F - f5), (double)(this.posY + 7.99F), 0.0D).tex((double)((f2 + f4) / 256.0F), (double)((f3 + 15.98F) / 256.0F)).endVertex();
            tessellator.draw();
            return (f1 - f) / 2.0F + 1.0F;
        }
    }

    public int drawStringWithShadow(String text, float x, float y, int color)
    {
        return this.drawString(text, x, y, color, true);
    }

    public int drawString(String text, int x, int y, int color)
    {
        return this.drawString(text, (float)x, (float)y, color, false);
    }

    public int drawString(String text, float x, float y, int color, boolean dropShadow)
    {
        enableAlpha();
        this.resetStyles();
        int i;

        if (dropShadow)
        {
            i = this.renderString(text, x + 1.0F, y + 1.0F, color, true);
            i = Math.max(i, this.renderString(text, x, y, color, false));
        }
        else
        {
            i = this.renderString(text, x, y, color, false);
        }

        return i;
    }

    private String bidiReorder(String text)
    {
        return EaglerBidiReorder.bidiReorder(text);
    }

    protected void resetStyles()
    {
        this.randomStyle = false;
        this.boldStyle = false;
        this.italicStyle = false;
        this.underlineStyle = false;
        this.strikethroughStyle = false;
    }

    protected void renderStringAtPos(String text, boolean shadow)
    {
        for (int i = 0; i < text.length(); ++i)
        {
            char c0 = text.charAt(i);

            if (c0 == 167 && i + 1 < text.length())
            {
                int i1 = "0123456789abcdefklmnor".indexOf(String.valueOf(text.charAt(i + 1)).toLowerCase(Locale.ROOT).charAt(0));

                if (i1 < 16)
                {
                    this.randomStyle = false;
                    this.boldStyle = false;
                    this.strikethroughStyle = false;
                    this.underlineStyle = false;
                    this.italicStyle = false;

                    if (i1 < 0 || i1 > 15)
                    {
                        i1 = 15;
                    }

                    if (shadow)
                    {
                        i1 += 16;
                    }

                    int j1 = this.colorCode[i1];
                    this.textColor = j1;
                    setColor((float)(j1 >> 16) / 255.0F, (float)(j1 >> 8 & 255) / 255.0F, (float)(j1 & 255) / 255.0F, this.alpha);
                }
                else if (i1 == 16)
                {
                    this.randomStyle = true;
                }
                else if (i1 == 17)
                {
                    this.boldStyle = true;
                }
                else if (i1 == 18)
                {
                    this.strikethroughStyle = true;
                }
                else if (i1 == 19)
                {
                    this.underlineStyle = true;
                }
                else if (i1 == 20)
                {
                    this.italicStyle = true;
                }
                else if (i1 == 21)
                {
                    this.randomStyle = false;
                    this.boldStyle = false;
                    this.strikethroughStyle = false;
                    this.underlineStyle = false;
                    this.italicStyle = false;
                    setColor(this.red, this.blue, this.green, this.alpha);
                }

                ++i;
            }
            else
            {
                int j = FontMappingHelper.lookupChar(c0, false);

                if (this.randomStyle && j != -1)
                {
                    int k = this.getCharWidth(c0);
                    char c1;

                    while (true)
                    {
                        j = this.fontRandom.nextInt(codepointLookup.length);
                        c1 = codepointLookup[j];

                        if (k == this.getCharWidth(c1))
                        {
                            break;
                        }
                    }

                    c0 = c1;
                }

                float f1 = j == -1 || this.unicodeFlag ? 0.5f : 1f;
                boolean flag = (c0 == 0 || j == -1 || this.unicodeFlag) && shadow;

                if (flag)
                {
                    this.posX -= f1;
                    this.posY -= f1;
                }

                float f = this.renderChar(c0, this.italicStyle);

                if (flag)
                {
                    this.posX += f1;
                    this.posY += f1;
                }

                if (this.boldStyle)
                {
                    this.posX += f1;

                    if (flag)
                    {
                        this.posX -= f1;
                        this.posY -= f1;
                    }

                    this.renderChar(c0, this.italicStyle);
                    this.posX -= f1;

                    if (flag)
                    {
                        this.posX += f1;
                        this.posY += f1;
                    }

                    ++f;
                }
                doDraw(f);
            }
        }
    }

    protected void doDraw(float f)
    {
        {
            {

                if (this.strikethroughStyle)
                {
                    Tessellator tessellator = Tessellator.getInstance();
                    WorldRenderer bufferbuilder = tessellator.getBuffer();
                    GlStateManager.disableTexture2D();
                    bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
                    bufferbuilder.pos((double)this.posX, (double)(this.posY + (float)(this.FONT_HEIGHT / 2)), 0.0D).endVertex();
                    bufferbuilder.pos((double)(this.posX + f), (double)(this.posY + (float)(this.FONT_HEIGHT / 2)), 0.0D).endVertex();
                    bufferbuilder.pos((double)(this.posX + f), (double)(this.posY + (float)(this.FONT_HEIGHT / 2) - 1.0F), 0.0D).endVertex();
                    bufferbuilder.pos((double)this.posX, (double)(this.posY + (float)(this.FONT_HEIGHT / 2) - 1.0F), 0.0D).endVertex();
                    tessellator.draw();
                    GlStateManager.enableTexture2D();
                }

                if (this.underlineStyle)
                {
                    Tessellator tessellator1 = Tessellator.getInstance();
                    WorldRenderer bufferbuilder1 = tessellator1.getBuffer();
                    GlStateManager.disableTexture2D();
                    bufferbuilder1.begin(7, DefaultVertexFormats.POSITION);
                    int l = this.underlineStyle ? -1 : 0;
                    bufferbuilder1.pos((double)(this.posX + (float)l), (double)(this.posY + (float)this.FONT_HEIGHT), 0.0D).endVertex();
                    bufferbuilder1.pos((double)(this.posX + f), (double)(this.posY + (float)this.FONT_HEIGHT), 0.0D).endVertex();
                    bufferbuilder1.pos((double)(this.posX + f), (double)(this.posY + (float)this.FONT_HEIGHT - 1.0F), 0.0D).endVertex();
                    bufferbuilder1.pos((double)(this.posX + (float)l), (double)(this.posY + (float)this.FONT_HEIGHT - 1.0F), 0.0D).endVertex();
                    tessellator1.draw();
                    GlStateManager.enableTexture2D();
                }

                this.posX += (float)((int)f);
            }
        }
    }

    private int renderStringAligned(String text, int x, int y, int width, int color, boolean dropShadow)
    {
        if (this.bidiFlag)
        {
            int i = this.getStringWidth(this.bidiReorder(text));
            x = x + width - i;
        }

        return this.renderString(text, (float)x, (float)y, color, dropShadow);
    }

    private int renderString(String text, float x, float y, int color, boolean dropShadow)
    {
        if (text == null)
        {
            return 0;
        }
        else
        {
            if (this.bidiFlag)
            {
                text = this.bidiReorder(text);
            }

            if ((color & -67108864) == 0)
            {
                color |= -16777216;
            }

            if (dropShadow)
            {
                color = (color & 16579836) >> 2 | color & -16777216;
            }

            this.red = (float)(color >> 16 & 255) / 255.0F;
            this.blue = (float)(color >> 8 & 255) / 255.0F;
            this.green = (float)(color & 255) / 255.0F;
            this.alpha = (float)(color >> 24 & 255) / 255.0F;
            setColor(this.red, this.blue, this.green, this.alpha);
            this.posX = x;
            this.posY = y;
            this.renderStringAtPos(text, dropShadow);
            return (int)this.posX;
        }
    }

    public int getStringWidth(String text)
    {
        if (text == null)
        {
            return 0;
        }
        else
        {
            int i = 0;
            boolean flag = false;

            for (int j = 0; j < text.length(); ++j)
            {
                char c0 = text.charAt(j);
                int k = this.getCharWidth(c0);

                if (k < 0 && j < text.length() - 1)
                {
                    ++j;
                    c0 = text.charAt(j);

                    if (c0 != 'l' && c0 != 'L')
                    {
                        if (c0 == 'r' || c0 == 'R')
                        {
                            flag = false;
                        }
                    }
                    else
                    {
                        flag = true;
                    }

                    k = 0;
                }

                i += k;

                if (flag && k > 0)
                {
                    ++i;
                }
            }

            return i;
        }
    }

    public int getCharWidth(char character)
    {
        if (character == 160) return 4; // forge: display nbsp as space. MC-2595
        if (character == 167)
        {
            return -1;
        }
        else if (character == ' ')
        {
            return 4;
        }
        else
        {
            int i = FontMappingHelper.lookupChar(character, false);

            if (character > 0 && i != -1 && !this.unicodeFlag)
            {
                return this.charWidth[i];
            }
            else if (this.glyphWidth[character] != 0)
            {
                int j = this.glyphWidth[character] & 255;
                int k = j >>> 4;
                int l = j & 15;
                ++l;
                return (l - k) / 2 + 1;
            }
            else
            {
                return 0;
            }
        }
    }

    public String trimStringToWidth(String text, int width)
    {
        return this.trimStringToWidth(text, width, false);
    }

    public String trimStringToWidth(String text, int width, boolean reverse)
    {
        StringBuilder stringbuilder = new StringBuilder();
        int i = 0;
        int j = reverse ? text.length() - 1 : 0;
        int k = reverse ? -1 : 1;
        boolean flag = false;
        boolean flag1 = false;

        for (int l = j; l >= 0 && l < text.length() && i < width; l += k)
        {
            char c0 = text.charAt(l);
            int i1 = this.getCharWidth(c0);

            if (flag)
            {
                flag = false;

                if (c0 != 'l' && c0 != 'L')
                {
                    if (c0 == 'r' || c0 == 'R')
                    {
                        flag1 = false;
                    }
                }
                else
                {
                    flag1 = true;
                }
            }
            else if (i1 < 0)
            {
                flag = true;
            }
            else
            {
                i += i1;

                if (flag1)
                {
                    ++i;
                }
            }

            if (i > width)
            {
                break;
            }

            if (reverse)
            {
                stringbuilder.insert(0, c0);
            }
            else
            {
                stringbuilder.append(c0);
            }
        }

        return stringbuilder.toString();
    }

    private String trimStringNewline(String text)
    {
        while (text != null && text.endsWith("\n"))
        {
            text = text.substring(0, text.length() - 1);
        }

        return text;
    }

    public void drawSplitString(String str, int x, int y, int wrapWidth, int textColor)
    {
        this.resetStyles();
        this.textColor = textColor;
        str = this.trimStringNewline(str);
        this.renderSplitString(str, x, y, wrapWidth, false);
    }

    private void renderSplitString(String str, int x, int y, int wrapWidth, boolean addShadow)
    {
        for (String s : this.listFormattedStringToWidth(str, wrapWidth))
        {
            this.renderStringAligned(s, x, y, wrapWidth, this.textColor, addShadow);
            y += this.FONT_HEIGHT;
        }
    }

    public int getWordWrappedHeight(String str, int maxLength)
    {
        return this.FONT_HEIGHT * this.listFormattedStringToWidth(str, maxLength).size();
    }

    public void setUnicodeFlag(boolean unicodeFlagIn)
    {
        this.unicodeFlag = unicodeFlagIn;
    }

    public boolean getUnicodeFlag()
    {
        return this.unicodeFlag;
    }

    public void setBidiFlag(boolean bidiFlagIn)
    {
        this.bidiFlag = bidiFlagIn;
    }

    public List<String> listFormattedStringToWidth(String str, int wrapWidth)
    {
        return Arrays.<String>asList(this.wrapFormattedStringToWidth(str, wrapWidth).split("\n"));
    }

    String wrapFormattedStringToWidth(String str, int wrapWidth)
    {
        int i = this.sizeStringToWidth(str, wrapWidth);

        if (str.length() <= i)
        {
            return str;
        }
        else
        {
            String s = str.substring(0, i);
            char c0 = str.charAt(i);
            boolean flag = c0 == ' ' || c0 == '\n';
            String s1 = getFormatFromString(s) + str.substring(i + (flag ? 1 : 0));
            return s + "\n" + this.wrapFormattedStringToWidth(s1, wrapWidth);
        }
    }

    private int sizeStringToWidth(String str, int wrapWidth)
    {
        int i = str.length();
        int j = 0;
        int k = 0;
        int l = -1;

        for (boolean flag = false; k < i; ++k)
        {
            char c0 = str.charAt(k);

            switch (c0)
            {
                case '\n':
                    --k;
                    break;
                case ' ':
                    l = k;
                default:
                    j += this.getCharWidth(c0);

                    if (flag)
                    {
                        ++j;
                    }

                    break;
                case '\u00a7':

                    if (k < i - 1)
                    {
                        ++k;
                        char c1 = str.charAt(k);

                        if (c1 != 'l' && c1 != 'L')
                        {
                            if (c1 == 'r' || c1 == 'R' || isFormatColor(c1))
                            {
                                flag = false;
                            }
                        }
                        else
                        {
                            flag = true;
                        }
                    }
            }

            if (c0 == '\n')
            {
                ++k;
                l = k;
                break;
            }

            if (j > wrapWidth)
            {
                break;
            }
        }

        return k != i && l != -1 && l < k ? l : k;
    }

    private static boolean isFormatColor(char colorChar)
    {
        return colorChar >= '0' && colorChar <= '9' || colorChar >= 'a' && colorChar <= 'f' || colorChar >= 'A' && colorChar <= 'F';
    }

    private static boolean isFormatSpecial(char formatChar)
    {
        return formatChar >= 'k' && formatChar <= 'o' || formatChar >= 'K' && formatChar <= 'O' || formatChar == 'r' || formatChar == 'R';
    }

    public static String getFormatFromString(String text)
    {
        String s = "";
        int i = -1;
        int j = text.length();

        while ((i = text.indexOf(167, i + 1)) != -1)
        {
            if (i < j - 1)
            {
                char c0 = text.charAt(i + 1);

                if (isFormatColor(c0))
                {
                    s = "\u00a7" + c0;
                }
                else if (isFormatSpecial(c0))
                {
                    s = s + "\u00a7" + c0;
                }
            }
        }

        return s;
    }

    public boolean getBidiFlag()
    {
        return this.bidiFlag;
    }

    protected void setColor(float r, float g, float b, float a)
    {
        GlStateManager.color(r,g,b,a);
    }

    protected void enableAlpha()
    {
        GlStateManager.enableAlpha();
    }

    protected void bindTexture(ResourceLocation location)
    {
        renderEngine.bindTexture(location);
    }

    protected IResource getResource(ResourceLocation location) throws IOException
    {
        return Minecraft.getMinecraft().getResourceManager().getResource(location);
    }

    public int getColorCode(char character)
    {
        int i = "0123456789abcdef".indexOf(character);
        return i >= 0 && i < this.colorCode.length ? this.colorCode[i] : -1;
    }
}
