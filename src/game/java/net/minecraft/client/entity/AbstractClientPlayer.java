package net.minecraft.client.entity;

import net.lax1dude.eaglercraft.v1_8.mojang.authlib.GameProfile;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.lax1dude.eaglercraft.v1_8.profanity_filter.ProfanityFilter;
import net.lax1dude.eaglercraft.v1_8.profile.SkinModel;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class AbstractClientPlayer extends EntityPlayer
{
    private NetworkPlayerInfo playerInfo;
    public float rotateElytraX;
    public float rotateElytraY;
    public float rotateElytraZ;
    public long eaglerHighPolyAnimationTick = EagRuntime.steadyTimeMillis();
    public float eaglerHighPolyAnimationFloat1 = 0.0f;
    public float eaglerHighPolyAnimationFloat2 = 0.0f;
    public float eaglerHighPolyAnimationFloat3 = 0.0f;
    public float eaglerHighPolyAnimationFloat4 = 0.0f;
    public float eaglerHighPolyAnimationFloat5 = 0.0f;
    public float eaglerHighPolyAnimationFloat6 = 0.0f;
    public EaglercraftUUID clientBrandUUIDCache = null;
    private String nameProfanityFilter = null;

    public AbstractClientPlayer(World worldIn, GameProfile playerProfile)
    {
        super(worldIn, playerProfile);
    }

    public boolean isSpectator()
    {
        NetworkPlayerInfo networkplayerinfo = Minecraft.getMinecraft().getConnection().getPlayerInfo(this.getGameProfile().getId());
        return networkplayerinfo != null && networkplayerinfo.getGameType() == GameType.SPECTATOR;
    }

    public boolean isCreative()
    {
        NetworkPlayerInfo networkplayerinfo = Minecraft.getMinecraft().getConnection().getPlayerInfo(this.getGameProfile().getId());
        return networkplayerinfo != null && networkplayerinfo.getGameType() == GameType.CREATIVE;
    }

    public boolean hasPlayerInfo()
    {
        return this.getPlayerInfo() != null;
    }

    @Nullable
    protected NetworkPlayerInfo getPlayerInfo()
    {
        if (this.playerInfo == null)
        {
            this.playerInfo = Minecraft.getMinecraft().getConnection().getPlayerInfo(this.getUniqueID());
        }

        return this.playerInfo;
    }

    public boolean hasSkin()
    {
        NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
        return networkplayerinfo != null && networkplayerinfo.hasLocationSkin();
    }

    public ResourceLocation getLocationSkin()
    {
        NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
        return networkplayerinfo == null ? DefaultPlayerSkin.getDefaultSkin(this.getUniqueID()) : networkplayerinfo.getLocationSkin();
    }

    @Nullable
    public ResourceLocation getLocationCape()
    {
        NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
        return networkplayerinfo == null ? null : networkplayerinfo.getLocationCape();
    }

    public boolean isPlayerInfoSet()
    {
        return this.getPlayerInfo() != null;
    }

    @Nullable
    public ResourceLocation getLocationElytra()
    {
        NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
        return networkplayerinfo == null ? null : networkplayerinfo.getLocationElytra();
    }

    public String getSkinType()
    {
        NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
        return networkplayerinfo == null ? DefaultPlayerSkin.getSkinType(this.getUniqueID()) : networkplayerinfo.getSkinType();
    }

    public SkinModel getEaglerSkinModel()
    {
        NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
        return networkplayerinfo == null ? SkinModel.STEVE : networkplayerinfo.getEaglerSkinModel();
    }

    public String getNameProfanityFilter()
    {
        if (Minecraft.getMinecraft().isEnableProfanityFilter())
        {
            if (this.nameProfanityFilter == null)
            {
                this.nameProfanityFilter = ProfanityFilter.getInstance().profanityFilterString(this.getGameProfile().getName());
            }

            return this.nameProfanityFilter;
        }
        else
        {
            return this.getGameProfile().getName();
        }
    }

    public ITextComponent getDisplayNameProfanityFilter()
    {
        TextComponentString textcomponentstring = new TextComponentString(ScorePlayerTeam.formatPlayerName(this.getTeam(), this.getNameProfanityFilter()));
        textcomponentstring.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + this.getName() + " "));
        textcomponentstring.getStyle().setHoverEvent(this.getHoverEvent());
        textcomponentstring.getStyle().setInsertion(this.getName());
        return textcomponentstring;
    }

    public float getFovModifier()
    {
        float f = 1.0F;

        if (this.capabilities.isFlying)
        {
            f *= 1.1F;
        }

        IAttributeInstance iattributeinstance = this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        f = (float)((double)f * ((iattributeinstance.getAttributeValue() / (double)this.capabilities.getWalkSpeed() + 1.0D) / 2.0D));

        if (this.capabilities.getWalkSpeed() == 0.0F || Float.isNaN(f) || Float.isInfinite(f))
        {
            f = 1.0F;
        }

        if (this.isHandActive() && this.getActiveItemStack().getItem() instanceof net.minecraft.item.ItemBow)
        {
            int i = this.getItemInUseMaxCount();
            float f1 = (float)i / 20.0F;

            if (f1 > 1.0F)
            {
                f1 = 1.0F;
            }
            else
            {
                f1 = f1 * f1;
            }

            f *= 1.0F - f1 * 0.15F;
        }

        return net.minecraftforge.client.ForgeHooksClient.getOffsetFOV(this, f);
    }
}
