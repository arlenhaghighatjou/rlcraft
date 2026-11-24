package net.minecraft.client.gui;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.profanity_filter.ProfanityFilter;

@SideOnly(Side.CLIENT)
public class ChatLine
{
    private final int updateCounterCreated;
    private final ITextComponent lineString;
    private ITextComponent lineStringProfanityFilter;
    private final int chatLineID;

    public ChatLine(int updateCounterCreatedIn, ITextComponent lineStringIn, int chatLineIDIn)
    {
        this.lineString = lineStringIn;
        this.updateCounterCreated = updateCounterCreatedIn;
        this.chatLineID = chatLineIDIn;
    }

    public ITextComponent getChatComponent()
    {
        if (Minecraft.getMinecraft().isEnableProfanityFilter())
        {
            if (this.lineStringProfanityFilter == null)
            {
                this.lineStringProfanityFilter = ProfanityFilter.getInstance().profanityFilterChatComponent(this.lineString);
            }

            return this.lineStringProfanityFilter;
        }
        else
        {
            return this.lineString;
        }
    }

    public int getUpdatedCounter()
    {
        return this.updateCounterCreated;
    }

    public int getChatLineID()
    {
        return this.chatLineID;
    }
}
