package net.minecraft.client.network;

import net.lax1dude.eaglercraft.v1_8.mojang.authlib.GameProfile;
import net.lax1dude.eaglercraft.v1_8.profanity_filter.ProfanityFilter;
import net.lax1dude.eaglercraft.v1_8.profile.SkinModel;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class NetworkPlayerInfo
{
    private final GameProfile gameProfile;
    private String gameProfileProfanityFilter;
    private GameType gameType;
    private int responseTime;
    private ITextComponent displayName;
    private ITextComponent displayNameProfanityFilter;
    private int lastHealth;
    private int displayHealth;
    private long lastHealthTime;
    private long healthBlinkTime;
    private long renderVisibilityId;

    public NetworkPlayerInfo(GameProfile profile)
    {
        this.gameProfile = profile;
    }

    public NetworkPlayerInfo(SPacketPlayerListItem.AddPlayerData entry)
    {
        this.gameProfile = entry.getProfile();
        this.gameType = entry.getGameMode();
        this.responseTime = entry.getPing();
        this.displayName = entry.getDisplayName();
        this.displayNameProfanityFilter = null;
    }

    public GameProfile getGameProfile()
    {
        return this.gameProfile;
    }

    public GameType getGameType()
    {
        return this.gameType;
    }

    protected void setGameType(GameType gameMode)
    {
        this.gameType = gameMode;
    }

    public int getResponseTime()
    {
        return this.responseTime;
    }

    protected void setResponseTime(int latency)
    {
        this.responseTime = latency;
    }

    public boolean hasLocationSkin()
    {
        return true;
    }

    public String getSkinType()
    {
        return this.getEaglerSkinModel().profileSkinType;
    }

    public SkinModel getEaglerSkinModel()
    {
        return Minecraft.getMinecraft().getConnection().getTextureCache().getPlayerSkin(this.gameProfile).getModel();
    }

    public ResourceLocation getLocationSkin()
    {
        return Minecraft.getMinecraft().getConnection().getTextureCache().getPlayerSkin(this.gameProfile).getLocation();
    }

    @Nullable
    public ResourceLocation getLocationCape()
    {
        return Minecraft.getMinecraft().getConnection().getTextureCache().getPlayerCape(this.gameProfile);
    }

    @Nullable
    public ResourceLocation getLocationElytra()
    {
        return this.getLocationCape();
    }

    @Nullable
    public ScorePlayerTeam getPlayerTeam()
    {
        return Minecraft.getMinecraft().world.getScoreboard().getPlayersTeam(this.getGameProfile().getName());
    }

    public void setDisplayName(@Nullable ITextComponent displayNameIn)
    {
        this.displayName = displayNameIn;
        this.displayNameProfanityFilter = null;
    }

    @Nullable
    public ITextComponent getDisplayName()
    {
        return this.displayName;
    }

    @Nullable
    public ITextComponent getDisplayNameProfanityFilter()
    {
        if (Minecraft.getMinecraft().isEnableProfanityFilter())
        {
            if (this.displayName != null)
            {
                if (this.displayNameProfanityFilter == null)
                {
                    this.displayNameProfanityFilter = ProfanityFilter.getInstance().profanityFilterChatComponent(this.displayName);
                }

                return this.displayNameProfanityFilter;
            }
            else
            {
                return null;
            }
        }
        else
        {
            return this.displayName;
        }
    }

    public String getGameProfileNameProfanityFilter()
    {
        if (Minecraft.getMinecraft().isEnableProfanityFilter())
        {
            if (this.gameProfileProfanityFilter == null)
            {
                this.gameProfileProfanityFilter = ProfanityFilter.getInstance().profanityFilterString(this.gameProfile.getName());
            }

            return this.gameProfileProfanityFilter;
        }
        else
        {
            return this.gameProfile.getName();
        }
    }

    public int getLastHealth()
    {
        return this.lastHealth;
    }

    public void setLastHealth(int p_178836_1_)
    {
        this.lastHealth = p_178836_1_;
    }

    public int getDisplayHealth()
    {
        return this.displayHealth;
    }

    public void setDisplayHealth(int p_178857_1_)
    {
        this.displayHealth = p_178857_1_;
    }

    public long getLastHealthTime()
    {
        return this.lastHealthTime;
    }

    public void setLastHealthTime(long p_178846_1_)
    {
        this.lastHealthTime = p_178846_1_;
    }

    public long getHealthBlinkTime()
    {
        return this.healthBlinkTime;
    }

    public void setHealthBlinkTime(long p_178844_1_)
    {
        this.healthBlinkTime = p_178844_1_;
    }

    public long getRenderVisibilityId()
    {
        return this.renderVisibilityId;
    }

    public void setRenderVisibilityId(long p_178843_1_)
    {
        this.renderVisibilityId = p_178843_1_;
    }
}
