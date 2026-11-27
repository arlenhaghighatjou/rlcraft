package net.minecraft.client.gui;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ITabCompleter;
import net.minecraft.util.TabCompleter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import net.lax1dude.eaglercraft.v1_8.Keyboard;
import net.lax1dude.eaglercraft.v1_8.Mouse;
import net.lax1dude.eaglercraft.v1_8.PointerInputAbstraction;
import net.lax1dude.eaglercraft.v1_8.minecraft.EnumInputEvent;
import net.lax1dude.eaglercraft.v1_8.minecraft.GuiScreenVisualViewport;
import net.lax1dude.eaglercraft.v1_8.notifications.GuiButtonNotifBell;
import net.lax1dude.eaglercraft.v1_8.notifications.GuiScreenNotifications;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;

@SideOnly(Side.CLIENT)
public class GuiChat extends GuiScreenVisualViewport implements ITabCompleter
{
    private static final Logger LOGGER = LogManager.getLogger();
    private String historyBuffer = "";
    private int sentHistoryCursor = -1;
    private TabCompleter tabCompleter;
    protected GuiTextField inputField;
    private String defaultInputFieldText = "";
    private GuiButton exitButton;
    private GuiButtonNotifBell notifBellButton;

    public GuiChat()
    {
    }

    public GuiChat(String defaultText)
    {
        this.defaultInputFieldText = defaultText;
    }

    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);

        if (!(this instanceof GuiSleepMP))
        {
            this.exitButton = this.addButton(new GuiButton(69, this.width - 100, 3, 97, 20, I18n.format("chat.exit")));

            if (!this.mc.isIntegratedServerRunning() && this.mc.player != null && this.mc.player.connection.getEaglerMessageProtocol().ver >= 4)
            {
                this.notifBellButton = this.addButton(new GuiButtonNotifBell(70, this.width - 122, 3));
                this.notifBellButton.setUnread(this.mc.player.connection.getNotifManager().getUnread());
            }
        }

        this.sentHistoryCursor = this.mc.ingameGUI.getChatGUI().getSentMessages().size();
        this.inputField = new GuiTextField(0, this.fontRenderer, 4, this.height - 12, this.width - 4, 12);
        this.inputField.setMaxStringLength(256);
        this.inputField.setEnableBackgroundDrawing(false);
        this.inputField.setFocused(true);
        this.inputField.setText(this.defaultInputFieldText);
        this.inputField.setCanLoseFocus(false);
        this.tabCompleter = new GuiChat.ChatTabCompleter(this.inputField);
    }

    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
        this.mc.ingameGUI.getChatGUI().resetScroll();
    }

    public void updateScreen0()
    {
        this.inputField.updateCursorCounter();

        if (this.notifBellButton != null && this.mc.player != null)
        {
            this.notifBellButton.setUnread(this.mc.player.connection.getNotifManager().getUnread());
        }
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        this.tabCompleter.resetRequested();

        if (keyCode == 15)
        {
            this.tabCompleter.complete();
        }
        else
        {
            this.tabCompleter.resetDidComplete();
        }

        if (keyCode == 1 && (this.mc.gameSettings.keyBindClose.getKeyCode() == 0 || Keyboard.areKeysLocked()))
        {
            this.mc.displayGuiScreen((GuiScreen)null);
        }
        else if (keyCode != 28 && keyCode != 156)
        {
            if (keyCode == 200)
            {
                this.getSentHistory(-1);
            }
            else if (keyCode == 208)
            {
                this.getSentHistory(1);
            }
            else if (keyCode == 201)
            {
                this.mc.ingameGUI.getChatGUI().scroll(this.mc.ingameGUI.getChatGUI().getLineCount() - 1);
            }
            else if (keyCode == 209)
            {
                this.mc.ingameGUI.getChatGUI().scroll(-this.mc.ingameGUI.getChatGUI().getLineCount() + 1);
            }
            else
            {
                this.inputField.textboxKeyTyped(typedChar, keyCode);
            }
        }
        else
        {
            String s = this.inputField.getText().trim();

            if (!s.isEmpty())
            {
                this.sendChatMessage(s);
            }

            this.mc.displayGuiScreen((GuiScreen)null);
        }
    }

    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        int i = Mouse.getEventDWheel();

        if (i != 0)
        {
            if (i > 1)
            {
                i = 1;
            }

            if (i < -1)
            {
                i = -1;
            }

            if (!isShiftKeyDown())
            {
                i *= 7;
            }

            this.mc.ingameGUI.getChatGUI().scroll(i);
        }
    }

    protected void mouseClicked0(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        if (mouseButton == 0)
        {
            ITextComponent itextcomponent = this.mc.ingameGUI.getChatGUI().getChatComponent(PointerInputAbstraction.getVCursorX(), PointerInputAbstraction.getVCursorY());

            if (itextcomponent != null && this.handleComponentClick(itextcomponent))
            {
                return;
            }

            if (this.mc.notifRenderer.handleClicked(this, mouseX, mouseY))
            {
                return;
            }
        }

        this.inputField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked0(mouseX, mouseY, mouseButton);
    }

    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 69)
        {
            this.mc.displayGuiScreen((GuiScreen)null);
        }
        else if (button.id == 70)
        {
            this.mc.displayGuiScreen(new GuiScreenNotifications(this));
        }
    }

    protected void setText(String newChatText, boolean shouldOverwrite)
    {
        if (shouldOverwrite)
        {
            this.inputField.setText(newChatText);
        }
        else
        {
            this.inputField.writeText(newChatText);
        }
    }

    public void getSentHistory(int msgPos)
    {
        int i = this.sentHistoryCursor + msgPos;
        int j = this.mc.ingameGUI.getChatGUI().getSentMessages().size();
        i = MathHelper.clamp(i, 0, j);

        if (i != this.sentHistoryCursor)
        {
            if (i == j)
            {
                this.sentHistoryCursor = j;
                this.inputField.setText(this.historyBuffer);
            }
            else
            {
                if (this.sentHistoryCursor == j)
                {
                    this.historyBuffer = this.inputField.getText();
                }

                this.inputField.setText((String)this.mc.ingameGUI.getChatGUI().getSentMessages().get(i));
                this.sentHistoryCursor = i;
            }
        }
    }

    public void drawScreen0(int mouseX, int mouseY, float partialTicks)
    {
        drawRect(2, this.height - 14, this.width - 2, this.height - 2, Integer.MIN_VALUE);
        this.inputField.drawTextBox();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        ITextComponent itextcomponent = this.mc.ingameGUI.getChatGUI().getChatComponent(PointerInputAbstraction.getVCursorX(), PointerInputAbstraction.getVCursorY());

        if (itextcomponent != null && itextcomponent.getStyle().getHoverEvent() != null)
        {
            this.handleComponentHover(itextcomponent, mouseX, mouseY);
        }

        if (this.exitButton != null)
        {
            this.exitButton.y = 3 + this.mc.getToastGui().getHeight();
        }

        super.drawScreen0(mouseX, mouseY, partialTicks);
    }

    public boolean doesGuiPauseGame()
    {
        return false;
    }

    public void setCompletions(String... newCompletions)
    {
        this.tabCompleter.setCompletions(newCompletions);
    }

    public boolean blockPTTKey()
    {
        return true;
    }

    public boolean showCopyPasteButtons()
    {
        return true;
    }

    public void fireInputEvent(EnumInputEvent event, String param)
    {
        this.inputField.fireInputEvent(event, param);
    }

    @SideOnly(Side.CLIENT)
    public static class ChatTabCompleter extends TabCompleter
        {
            private final Minecraft client = Minecraft.getMinecraft();

            public ChatTabCompleter(GuiTextField p_i46749_1_)
            {
                super(p_i46749_1_, false);
            }

            public void complete()
            {
                super.complete();

                if (this.completions.size() > 1)
                {
                    StringBuilder stringbuilder = new StringBuilder();

                    for (String s : this.completions)
                    {
                        if (stringbuilder.length() > 0)
                        {
                            stringbuilder.append(", ");
                        }

                        stringbuilder.append(s);
                    }

                    this.client.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(stringbuilder.toString()), 1);
                }
            }

            @Nullable
            public BlockPos getTargetBlockPos()
            {
                BlockPos blockpos = null;

                if (this.client.objectMouseOver != null && this.client.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK)
                {
                    blockpos = this.client.objectMouseOver.getBlockPos();
                }

                return blockpos;
            }
        }

    public GuiTextField getDeclaredTextField()
    {
        return this.getClass() == GuiChat.class ? this.inputField : super.getDeclaredTextField();
    }
}
