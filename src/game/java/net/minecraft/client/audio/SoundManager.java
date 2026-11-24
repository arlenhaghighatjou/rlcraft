package net.minecraft.client.audio;

import net.lax1dude.eaglercraft.v1_8.EaglercraftSoundManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.SoundCategory;

public class SoundManager extends EaglercraftSoundManager
{
    public SoundManager(SoundHandler handler, GameSettings settings)
    {
        super(settings, handler);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.sound.SoundSetupEvent(this));
    }

    public void setVolume(SoundCategory category, float volume)
    {
        setSoundCategoryVolume(category, volume);
    }

    public void reloadSoundSystem()
    {
        super.reloadSoundSystem();
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.sound.SoundLoadEvent(this));
    }
}
