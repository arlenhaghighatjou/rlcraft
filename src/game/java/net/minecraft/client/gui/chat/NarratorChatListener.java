package net.minecraft.client.gui.chat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class NarratorChatListener implements IChatListener
{
    public static final NarratorChatListener INSTANCE = new NarratorChatListener();

    public void say(ChatType chatTypeIn, ITextComponent message)
    {
    }

    public void announceMode(int p_193641_1_)
    {
        GuiToast guitoast = Minecraft.getMinecraft().getToastGui();
        SystemToast.addOrUpdate(guitoast, SystemToast.Type.NARRATOR_TOGGLE, new TextComponentTranslation("narrator.toast.disabled", new Object[0]), new TextComponentTranslation("options.narrator.notavailable", new Object[0]));
    }

    public boolean isActive()
    {
        return false;
    }

    public void clear()
    {
    }
}
