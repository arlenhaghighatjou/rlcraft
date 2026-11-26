package net.minecraft.client.gui;

import static net.lax1dude.eaglercraft.v1_8.opengl.RealOpenGLEnums.*;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.IntObjectMap;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import net.lax1dude.eaglercraft.v1_8.Keyboard;
import net.lax1dude.eaglercraft.v1_8.Mouse;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.PauseMenuCustomizeState;
import net.lax1dude.eaglercraft.v1_8.PointerInputAbstraction;
import net.lax1dude.eaglercraft.v1_8.Touch;
import net.lax1dude.eaglercraft.v1_8.internal.EnumTouchEvent;
import net.lax1dude.eaglercraft.v1_8.internal.KeyboardConstants;
import net.lax1dude.eaglercraft.v1_8.minecraft.EnumInputEvent;
import net.lax1dude.eaglercraft.v1_8.touch_gui.TouchControls;
import net.lax1dude.eaglercraft.v1_8.webview.GuiScreenServerInfo;

@SideOnly(Side.CLIENT)
public abstract class GuiScreen extends Gui implements GuiYesNoCallback
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Splitter NEWLINE_SPLITTER = Splitter.on('\n');
    public Minecraft mc;
    protected RenderItem itemRender;
    public int width;
    public int height;
    public List<GuiButton> buttonList = Lists.<GuiButton>newArrayList();
    protected List<GuiLabel> labelList = Lists.<GuiLabel>newArrayList();
    public boolean allowUserInput;
    protected FontRenderer fontRenderer;
    protected GuiButton selectedButton;
    private int eventButton;
    private long lastMouseEvent;
    private int touchValue;
    private String clickedLinkURI;
    private boolean focused;
    protected boolean keyHandled, mouseHandled; // Forge: allow canceling key and mouse Post events from handleMouseInput and handleKeyboardInput
    protected long showingCloseKey = 0;
    protected int touchModeCursorPosX = -1;
    protected int touchModeCursorPosY = -1;
    private long lastTouchEvent;
    public final IntObjectMap<int[]> touchStarts = new IntObjectHashMap<int[]>();

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        for (int i = 0; i < this.buttonList.size(); ++i)
        {
            ((GuiButton)this.buttonList.get(i)).drawButton(this.mc, mouseX, mouseY, partialTicks);
        }

        for (int j = 0; j < this.labelList.size(); ++j)
        {
            ((GuiLabel)this.labelList.get(j)).drawLabel(this.mc, mouseX, mouseY);
        }

        long millis = EagRuntime.steadyTimeMillis();
        long closeKeyTimeout = millis - this.showingCloseKey;

        if (closeKeyTimeout < 3000L && this.showingCloseKey != 0L)
        {
            int alpha1 = 0xC0000000;
            int alpha2 = 0xFF000000;

            if (closeKeyTimeout > 2500L)
            {
                float f = (float)(3000L - closeKeyTimeout) * 0.002F;

                if (f < 0.03F)
                {
                    f = 0.03F;
                }

                alpha1 = (int)(f * 192.0F) << 24;
                alpha2 = (int)(f * 255.0F) << 24;
            }

            String str;
            int k = this.getCloseKey();

            if (k == KeyboardConstants.KEY_GRAVE)
            {
                str = I18n.format("gui.exitKeyRetarded");
            }
            else
            {
                str = I18n.format("gui.exitKey", Keyboard.getKeyName(k));
            }

            int w = this.fontRenderer.getStringWidth(str);
            int x = (this.width - w - 4) / 2;
            int y = 10;
            drawRect(x, y, x + w + 4, y + 12, alpha1);

            if (closeKeyTimeout > 2500L)
            {
                GlStateManager.enableBlend();
            }

            this.fontRenderer.drawStringWithShadow(str, (float)(x + 2), (float)(y + 2), 0xFFAAAA | alpha2);

            if (closeKeyTimeout > 2500L)
            {
                GlStateManager.disableBlend();
            }
        }
    }

    protected int getCloseKey()
    {
        if (this instanceof GuiContainer)
        {
            return this.mc.gameSettings.keyBindInventory.getKeyCode();
        }
        else
        {
            return this.mc.gameSettings.keyBindClose.getKeyCode();
        }
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (!this.canCloseGui())
        {
            return;
        }

        if (((this.mc.world == null || this.mc.player.getHealth() <= 0.0F) && keyCode == 1) || keyCode == this.mc.gameSettings.keyBindClose.getKeyCode() || (keyCode == 1 && (this.mc.gameSettings.keyBindClose.getKeyCode() == 0 || Keyboard.areKeysLocked())))
        {
            this.mc.displayGuiScreen((GuiScreen)null);

            if (this.mc.currentScreen == null)
            {
                this.mc.setIngameFocus();
            }
        }
        else if (keyCode == 1)
        {
            this.showingCloseKey = EagRuntime.steadyTimeMillis();
        }
    }

    protected <T extends GuiButton> T addButton(T buttonIn)
    {
        this.buttonList.add(buttonIn);
        return buttonIn;
    }

    public static String getClipboardString()
    {
        return EagRuntime.getClipboard();
    }

    public static void setClipboardString(String copyText)
    {
        if (!StringUtils.isEmpty(copyText))
        {
            EagRuntime.setClipboard(copyText);
        }
    }

    protected void renderToolTip(ItemStack stack, int x, int y)
    {
        FontRenderer font = stack.getItem().getFontRenderer(stack);
        net.minecraftforge.fml.client.config.GuiUtils.preItemToolTip(stack);
        this.drawHoveringText(this.getItemToolTip(stack), x, y, (font == null ? fontRenderer : font));
        net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();
    }

    public List<String> getItemToolTip(ItemStack p_191927_1_)
    {
        List<String> list = p_191927_1_.getTooltipProfanityFilter(this.mc.player, this.mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);

        for (int i = 0; i < list.size(); ++i)
        {
            if (i == 0)
            {
                list.set(i, p_191927_1_.getItem().getForgeRarity(p_191927_1_).getColor() + (String)list.get(i));
            }
            else
            {
                list.set(i, TextFormatting.GRAY + (String)list.get(i));
            }
        }

        return list;
    }

    public void drawHoveringText(String text, int x, int y)
    {
        this.drawHoveringText(Arrays.asList(text), x, y);
    }

    public void setFocused(boolean hasFocusedControlIn)
    {
        this.focused = hasFocusedControlIn;
    }

    public boolean isFocused()
    {
        return this.focused;
    }

    public void drawHoveringText(List<String> textLines, int x, int y)
    {
        drawHoveringText(textLines, x, y, fontRenderer);
    }

    protected void drawHoveringText(List<String> textLines, int x, int y, FontRenderer font)
    {
        net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText(textLines, x, y, width, height, -1, font);
        if (false && !textLines.isEmpty())
        {
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int i = 0;

            for (String s : textLines)
            {
                int j = this.fontRenderer.getStringWidth(s);

                if (j > i)
                {
                    i = j;
                }
            }

            int l1 = x + 12;
            int i2 = y - 12;
            int k = 8;

            if (textLines.size() > 1)
            {
                k += 2 + (textLines.size() - 1) * 10;
            }

            if (l1 + i > this.width)
            {
                l1 -= 28 + i;
            }

            if (i2 + k + 6 > this.height)
            {
                i2 = this.height - k - 6;
            }

            this.zLevel = 300.0F;
            this.itemRender.zLevel = 300.0F;
            int l = -267386864;
            this.drawGradientRect(l1 - 3, i2 - 4, l1 + i + 3, i2 - 3, -267386864, -267386864);
            this.drawGradientRect(l1 - 3, i2 + k + 3, l1 + i + 3, i2 + k + 4, -267386864, -267386864);
            this.drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 + k + 3, -267386864, -267386864);
            this.drawGradientRect(l1 - 4, i2 - 3, l1 - 3, i2 + k + 3, -267386864, -267386864);
            this.drawGradientRect(l1 + i + 3, i2 - 3, l1 + i + 4, i2 + k + 3, -267386864, -267386864);
            int i1 = 1347420415;
            int j1 = 1344798847;
            this.drawGradientRect(l1 - 3, i2 - 3 + 1, l1 - 3 + 1, i2 + k + 3 - 1, 1347420415, 1344798847);
            this.drawGradientRect(l1 + i + 2, i2 - 3 + 1, l1 + i + 3, i2 + k + 3 - 1, 1347420415, 1344798847);
            this.drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 - 3 + 1, 1347420415, 1347420415);
            this.drawGradientRect(l1 - 3, i2 + k + 2, l1 + i + 3, i2 + k + 3, 1344798847, 1344798847);

            for (int k1 = 0; k1 < textLines.size(); ++k1)
            {
                String s1 = textLines.get(k1);
                this.fontRenderer.drawStringWithShadow(s1, (float)l1, (float)i2, -1);

                if (k1 == 0)
                {
                    i2 += 2;
                }

                i2 += 10;
            }

            this.zLevel = 0.0F;
            this.itemRender.zLevel = 0.0F;
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
    }

    public void handleComponentHover(ITextComponent component, int x, int y)
    {
        if (component != null && component.getStyle().getHoverEvent() != null)
        {
            HoverEvent hoverevent = component.getStyle().getHoverEvent();

            if (hoverevent.getAction() == HoverEvent.Action.SHOW_ITEM)
            {
                ItemStack itemstack = ItemStack.EMPTY;

                try
                {
                    NBTBase nbtbase = JsonToNBT.getTagFromJson(hoverevent.getValue().getUnformattedText());

                    if (nbtbase instanceof NBTTagCompound)
                    {
                        itemstack = new ItemStack((NBTTagCompound)nbtbase);
                    }
                }
                catch (NBTException var9)
                {
                    ;
                }

                if (itemstack.isEmpty())
                {
                    this.drawHoveringText(TextFormatting.RED + "Invalid Item!", x, y);
                }
                else
                {
                    this.renderToolTip(itemstack, x, y);
                }
            }
            else if (hoverevent.getAction() == HoverEvent.Action.SHOW_ENTITY)
            {
                if (this.mc.gameSettings.advancedItemTooltips)
                {
                    try
                    {
                        NBTTagCompound nbttagcompound = JsonToNBT.getTagFromJson(hoverevent.getValue().getUnformattedText());
                        List<String> list = Lists.<String>newArrayList();
                        list.add(nbttagcompound.getString("name"));

                        if (nbttagcompound.hasKey("type", 8))
                        {
                            String s = nbttagcompound.getString("type");
                            list.add("Type: " + s);
                        }

                        list.add(nbttagcompound.getString("id"));
                        this.drawHoveringText(list, x, y);
                    }
                    catch (NBTException var8)
                    {
                        this.drawHoveringText(TextFormatting.RED + "Invalid Entity!", x, y);
                    }
                }
            }
            else if (hoverevent.getAction() == HoverEvent.Action.SHOW_TEXT)
            {
                this.drawHoveringText(this.mc.fontRenderer.listFormattedStringToWidth(hoverevent.getValue().getFormattedText(), Math.max(this.width / 2, 200)), x, y);
            }

            GlStateManager.disableLighting();
        }
    }

    protected void setText(String newChatText, boolean shouldOverwrite)
    {
    }

    public boolean handleComponentClick(ITextComponent component)
    {
        if (component == null)
        {
            return false;
        }
        else
        {
            ClickEvent clickevent = component.getStyle().getClickEvent();

            if (isShiftKeyDown())
            {
                if (component.getStyle().getInsertion() != null)
                {
                    this.setText(component.getStyle().getInsertion(), false);
                }
            }
            else if (clickevent != null)
            {
                if (clickevent.getAction() == ClickEvent.Action.OPEN_URL)
                {
                    if (!this.mc.gameSettings.chatLinks)
                    {
                        return false;
                    }

                    String uri = clickevent.getValue();

                    if (this.mc.gameSettings.chatLinksPrompt)
                    {
                        this.clickedLinkURI = uri;
                        this.mc.displayGuiScreen(new GuiConfirmOpenLink(this, uri, 31102009, false));
                    }
                    else
                    {
                        this.openWebLink(uri);
                    }
                }
                else if (clickevent.getAction() == ClickEvent.Action.OPEN_FILE)
                {
                    // rip
                }
                else if (clickevent.getAction() == ClickEvent.Action.SUGGEST_COMMAND)
                {
                    this.setText(clickevent.getValue(), true);
                }
                else if (clickevent.getAction() == ClickEvent.Action.RUN_COMMAND)
                {
                    this.sendChatMessage(clickevent.getValue(), false);
                }
                else
                {
                    LOGGER.error("Don't know how to handle {}", (Object)clickevent);
                }

                return true;
            }

            return false;
        }
    }

    public void sendChatMessage(String msg)
    {
        this.sendChatMessage(msg, true);
    }

    public void sendChatMessage(String msg, boolean addToChat)
    {
        msg = net.minecraftforge.event.ForgeEventFactory.onClientSendMessage(msg);
        if (msg.isEmpty()) return;
        if (addToChat)
        {
            this.mc.ingameGUI.getChatGUI().addToSentMessages(msg);
        }
        if (net.minecraftforge.client.ClientCommandHandler.instance.executeCommand(mc.player, msg) != 0) return;

        this.mc.player.sendChatMessage(msg);
    }

    protected void touchStarted(int touchX, int touchY, int uid) throws IOException
    {
        if (this.shouldTouchGenerateMouseEvents())
        {
            this.mouseClicked(touchX, touchY, 12345);
        }
    }

    protected void touchTapped(int touchX, int touchY, int uid) throws IOException
    {
        if (this.shouldTouchGenerateMouseEvents())
        {
            this.mouseClicked(touchX, touchY, 0);
            this.mouseReleased(touchX, touchY, 0);
        }
    }

    protected void touchMoved(int touchX, int touchY, int uid)
    {
    }

    protected void touchEndMove(int touchX, int touchY, int uid)
    {
        if (this.shouldTouchGenerateMouseEvents())
        {
            this.mouseReleased(touchX, touchY, 12345);
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        boolean touchMode = PointerInputAbstraction.isTouchMode();

        if (mouseButton == 0 || mouseButton == 12345)
        {
            for (int i = 0; i < this.buttonList.size(); ++i)
            {
                GuiButton guibutton = this.buttonList.get(i);

                if (touchMode && (mouseButton == 12345) != guibutton.isSliderTouchEvents())
                {
                    continue;
                }

                if (guibutton.mousePressed(this.mc, mouseX, mouseY))
                {
                    net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre event = new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre(this, guibutton, this.buttonList);
                    if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event))
                        break;
                    guibutton = event.getButton();
                    this.selectedButton = guibutton;
                    guibutton.playPressSound(this.mc.getSoundHandler());
                    this.actionPerformed(guibutton);
                    if (this.equals(this.mc.currentScreen))
                        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Post(this, event.getButton(), this.buttonList));
                }
            }
        }
    }

    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        if (this.selectedButton != null && (state == 0 || state == 12345) && (!PointerInputAbstraction.isTouchMode() || (state == 12345) == this.selectedButton.isSliderTouchEvents()))
        {
            this.selectedButton.mouseReleased(mouseX, mouseY);
            this.selectedButton = null;
        }
    }

    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick)
    {
    }

    protected void actionPerformed(GuiButton button) throws IOException
    {
    }

    public void setWorldAndResolution(Minecraft mc, int width, int height)
    {
        this.mc = mc;
        this.itemRender = mc.getRenderItem();
        this.fontRenderer = mc.fontRenderer;
        this.width = width;
        this.height = height;
        if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Pre(this, this.buttonList)))
        {
        this.buttonList.clear();
        this.initGui();
        }
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Post(this, this.buttonList));
    }

    public void setGuiSize(int w, int h)
    {
        this.width = w;
        this.height = h;
    }

    public void initGui()
    {
    }

    public void handleInput() throws IOException
    {
        boolean noTouch = true;

        while (Touch.next())
        {
            noTouch = false;
            this.handleTouchInput();
            TouchControls.handleInput();
        }

        if (Mouse.isCreated())
        {
            while (Mouse.next())
            {
                if (!noTouch)
                {
                    continue;
                }

                this.mouseHandled = false;
                if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.MouseInputEvent.Pre(this))) continue;
                this.handleMouseInput();
                if (this.equals(this.mc.currentScreen) && !this.mouseHandled) net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.MouseInputEvent.Post(this));
            }
        }

        if (Keyboard.isCreated())
        {
            while (Keyboard.next())
            {
                this.keyHandled = false;
                if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent.Pre(this))) continue;
                this.handleKeyboardInput();
                if (this.equals(this.mc.currentScreen) && !this.keyHandled) net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent.Post(this));
            }
        }
    }

    public void handleTouchInput() throws IOException
    {
        EnumTouchEvent et = Touch.getEventType();

        if (et == EnumTouchEvent.TOUCHSTART)
        {
            PointerInputAbstraction.enterTouchModeHook();
        }

        float scaleFac = this.getEaglerScale();

        for (int t = 0, c = Touch.getEventTouchPointCount(); t < c; ++t)
        {
            int u = Touch.getEventTouchPointUID(t);
            int i = Touch.getEventTouchX(t);
            int j = Touch.getEventTouchY(t);

            if (et == EnumTouchEvent.TOUCHSTART)
            {
                if (TouchControls.handleTouchBegin(u, i, j))
                {
                    continue;
                }
            }
            else if (et == EnumTouchEvent.TOUCHEND)
            {
                if (TouchControls.handleTouchEnd(u, i, j))
                {
                    continue;
                }
            }

            i = applyEaglerScale(scaleFac, i * this.width / this.mc.displayWidth, this.width);
            j = applyEaglerScale(scaleFac, this.height - j * this.height / this.mc.displayHeight - 1, this.height);
            float rad = Touch.getEventTouchRadiusMixed(t);
            float si = rad * this.width / this.mc.displayWidth / scaleFac;

            if (si < 1.0F)
            {
                si = 1.0F;
            }

            float sj = rad * this.height / this.mc.displayHeight / scaleFac;

            if (sj < 1.0F)
            {
                sj = 1.0F;
            }

            int[] ck = this.touchStarts.remove(u);

            switch (et)
            {
                case TOUCHSTART:
                    if (t == 0)
                    {
                        this.touchModeCursorPosX = i;
                        this.touchModeCursorPosY = j;
                    }

                    this.lastTouchEvent = EagRuntime.steadyTimeMillis();
                    this.touchStarts.put(u, new int[] {i, j, 0});
                    this.touchStarted(i, j, u);
                    break;
                case TOUCHMOVE:
                    if (t == 0)
                    {
                        this.touchModeCursorPosX = i;
                        this.touchModeCursorPosY = j;
                    }

                    if (ck != null && Math.abs(ck[0] - i) < si && Math.abs(ck[1] - j) < sj)
                    {
                        this.touchStarts.put(u, ck);
                        break;
                    }

                    this.touchStarts.put(u, new int[] {i, j, (ck != null && this.isTouchDraggingStateLocked(u)) ? ck[2] : 1});
                    this.touchMoved(i, j, u);

                    if (t == 0 && this.shouldTouchGenerateMouseEvents())
                    {
                        this.mouseClickMove(i, j, 0, EagRuntime.steadyTimeMillis() - this.lastTouchEvent);
                    }

                    break;
                case TOUCHEND:
                    if (ck == null)
                    {
                        break;
                    }

                    if (t == 0)
                    {
                        this.touchModeCursorPosX = -1;
                        this.touchModeCursorPosY = -1;
                    }

                    if (ck[2] == 1)
                    {
                        this.touchEndMove(i, j, u);
                    }
                    else
                    {
                        this.touchTapped(ck[0], ck[1], u);
                    }

                    break;
            }
        }
    }

    public boolean isTouchPointDragging(int uid)
    {
        int[] ret = this.touchStarts.get(uid);
        return ret != null && ret[2] == 1;
    }

    public void handleMouseInput() throws IOException
    {
        float f = this.getEaglerScale();
        int i = applyEaglerScale(f, Mouse.getEventX() * this.width / this.mc.displayWidth, this.width);
        int j = applyEaglerScale(f, this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1, this.height);
        int k = Mouse.getEventButton();

        if (Mouse.getEventButtonState())
        {
            PointerInputAbstraction.enterMouseModeHook();

            if (this.mc.gameSettings.touchscreen && this.touchValue++ > 0)
            {
                return;
            }

            this.eventButton = k;
            this.lastMouseEvent = Minecraft.getSystemTime();
            this.mouseClicked(i, j, this.eventButton);
        }
        else if (k != -1)
        {
            if (this.mc.gameSettings.touchscreen && --this.touchValue > 0)
            {
                return;
            }

            this.eventButton = -1;
            this.mouseReleased(i, j, k);
        }
        else if (this.eventButton != -1 && this.lastMouseEvent > 0L)
        {
            long l = Minecraft.getSystemTime() - this.lastMouseEvent;
            this.mouseClickMove(i, j, this.eventButton, l);
        }
    }

    public void handleKeyboardInput() throws IOException
    {
        char c0 = Keyboard.getEventCharacter();

        if (Keyboard.getEventKey() == 0 && c0 >= ' ' || Keyboard.getEventKeyState())
        {
            this.keyTyped(c0, Keyboard.getEventKey());
        }

        this.mc.dispatchKeypresses();
    }

    public void updateScreen()
    {
    }

    public void onGuiClosed()
    {
    }

    public void drawDefaultBackground()
    {
        this.drawWorldBackground(0);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.BackgroundDrawnEvent(this));
    }

    protected boolean isPartOfPauseMenu()
    {
        return false;
    }

    public void drawWorldBackground(int tint)
    {
        if (this.mc.world != null)
        {
            boolean ingame = this.isPartOfPauseMenu();
            ResourceLocation loc = (ingame && PauseMenuCustomizeState.icon_background_pause != null) ? PauseMenuCustomizeState.icon_background_pause : PauseMenuCustomizeState.icon_background_all;
            float aspect = (ingame && PauseMenuCustomizeState.icon_background_pause != null) ? 1.0F / PauseMenuCustomizeState.icon_background_pause_aspect : 1.0F / PauseMenuCustomizeState.icon_background_all_aspect;

            if (loc != null)
            {
                GlStateManager.disableLighting();
                GlStateManager.disableFog();
                GlStateManager.enableBlend();
                GlStateManager.disableAlpha();
                GlStateManager.enableTexture2D();
                GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
                Tessellator tessellator = Tessellator.getInstance();
                WorldRenderer bufferbuilder = tessellator.getBuffer();
                this.mc.getTextureManager().bindTexture(loc);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                float f = 64.0F;
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                bufferbuilder.pos(0.0D, (double)this.height, 0.0D).tex(0.0D, (double)((float)this.height / f)).color(64, 64, 64, 192).endVertex();
                bufferbuilder.pos((double)this.width, (double)this.height, 0.0D).tex((double)((float)this.width / f * aspect), (double)((float)this.height / f)).color(64, 64, 64, 192).endVertex();
                bufferbuilder.pos((double)this.width, 0.0D, 0.0D).tex((double)((float)this.width / f * aspect), 0.0D).color(64, 64, 64, 192).endVertex();
                bufferbuilder.pos(0.0D, 0.0D, 0.0D).tex(0.0D, 0.0D).color(64, 64, 64, 192).endVertex();
                tessellator.draw();
                GlStateManager.enableAlpha();
            }
            else
            {
                this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
            }

            if (!(this instanceof GuiScreenServerInfo))
            {
                loc = (ingame && PauseMenuCustomizeState.icon_watermark_pause != null) ? PauseMenuCustomizeState.icon_watermark_pause : PauseMenuCustomizeState.icon_watermark_all;
                aspect = (ingame && PauseMenuCustomizeState.icon_watermark_pause != null) ? PauseMenuCustomizeState.icon_watermark_pause_aspect : PauseMenuCustomizeState.icon_watermark_all_aspect;

                if (loc != null)
                {
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    this.mc.getTextureManager().bindTexture(loc);
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(8.0F, (float)(this.height - 72), 0.0F);
                    float f2 = 0.25F;
                    GlStateManager.scale(f2 * aspect, f2, f2);
                    this.drawTexturedModalRect(0, 0, 0, 0, 256, 256);
                    GlStateManager.popMatrix();
                }
            }
        }
        else
        {
            this.drawBackground(tint);
        }
    }

    public void drawBackground(int tint)
    {
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer bufferbuilder = tessellator.getBuffer();
        this.mc.getTextureManager().bindTexture(OPTIONS_BACKGROUND);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        float f = 32.0F;
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(0.0D, (double)this.height, 0.0D).tex(0.0D, (double)((float)this.height / 32.0F + (float)tint)).color(64, 64, 64, 255).endVertex();
        bufferbuilder.pos((double)this.width, (double)this.height, 0.0D).tex((double)((float)this.width / 32.0F), (double)((float)this.height / 32.0F + (float)tint)).color(64, 64, 64, 255).endVertex();
        bufferbuilder.pos((double)this.width, 0.0D, 0.0D).tex((double)((float)this.width / 32.0F), (double)tint).color(64, 64, 64, 255).endVertex();
        bufferbuilder.pos(0.0D, 0.0D, 0.0D).tex(0.0D, (double)tint).color(64, 64, 64, 255).endVertex();
        tessellator.draw();
    }

    public boolean doesGuiPauseGame()
    {
        return true;
    }

    public void confirmClicked(boolean result, int id)
    {
        if (id == 31102009)
        {
            if (result)
            {
                this.openWebLink(this.clickedLinkURI);
            }

            this.clickedLinkURI = null;
            this.mc.displayGuiScreen(this);
        }
    }

    private void openWebLink(String url)
    {
        EagRuntime.openLink(url);
    }

    public static boolean isCtrlKeyDown()
    {
        return Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157);
    }

    public static boolean isShiftKeyDown()
    {
        return Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
    }

    public static boolean isAltKeyDown()
    {
        return Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184);
    }

    public static boolean isKeyComboCtrlX(int keyID)
    {
        return keyID == 45 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    public static boolean isKeyComboCtrlV(int keyID)
    {
        return keyID == 47 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    public static boolean isKeyComboCtrlC(int keyID)
    {
        return keyID == 46 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    public static boolean isKeyComboCtrlA(int keyID)
    {
        return keyID == 30 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    public void onResize(Minecraft mcIn, int w, int h)
    {
        this.setWorldAndResolution(mcIn, w, h);
    }

    public boolean shouldHangupIntegratedServer()
    {
        return true;
    }

    public boolean blockPTTKey()
    {
        return false;
    }

    public void fireInputEvent(EnumInputEvent event, String param)
    {
    }

    public boolean showCopyPasteButtons()
    {
        return false;
    }

    public static int applyEaglerScale(float scaleFac, int coord, int screenDim)
    {
        return (int)((coord - (1.0F - scaleFac) * screenDim * 0.5F) / scaleFac);
    }

    public float getEaglerScale()
    {
        return PointerInputAbstraction.isTouchMode() ? this.getTouchModeScale() : 1.0F;
    }

    protected float getTouchModeScale()
    {
        return 1.0F;
    }

    public boolean canCloseGui()
    {
        return true;
    }

    protected boolean isTouchDraggingStateLocked(int uid)
    {
        return false;
    }

    protected boolean shouldTouchGenerateMouseEvents()
    {
        return true;
    }
}
