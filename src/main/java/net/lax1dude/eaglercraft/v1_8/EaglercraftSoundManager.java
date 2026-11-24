/*
 * Copyright (c) 2022-2023 lax1dude, ayunami2000. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.lax1dude.eaglercraft.v1_8;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.lax1dude.eaglercraft.v1_8.internal.EnumPlatformType;
import net.lax1dude.eaglercraft.v1_8.internal.IAudioCacheLoader;
import net.lax1dude.eaglercraft.v1_8.internal.IAudioHandle;
import net.lax1dude.eaglercraft.v1_8.internal.IAudioResource;
import net.lax1dude.eaglercraft.v1_8.internal.PlatformAudio;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ISound.AttenuationType;
import net.minecraft.client.audio.ISoundEventListener;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.Sound;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.ResourceLocation;

public class EaglercraftSoundManager {
	
	protected class ActiveSoundEvent {

		protected final EaglercraftSoundManager manager;
		
		protected final ISound soundInstance;
		protected final SoundCategory soundCategory;
		protected final Sound soundConfig;
		protected IAudioHandle soundHandle;
		
		protected float activeX;
		protected float activeY;
		protected float activeZ;
		
		protected float activePitch;
		protected float activeGain;
		
		protected int repeatCounter = 0;
		protected boolean paused = false;
		
		protected ActiveSoundEvent(EaglercraftSoundManager manager, ISound soundInstance,
				SoundCategory soundCategory, Sound soundConfig, IAudioHandle soundHandle) {
			this.manager = manager;
			this.soundInstance = soundInstance;
			this.soundCategory = soundCategory;
			this.soundConfig = soundConfig;
			this.soundHandle = soundHandle;
			this.activeX = soundInstance.getXPosF();
			this.activeY = soundInstance.getYPosF();
			this.activeZ = soundInstance.getZPosF();
			this.activePitch = soundInstance.getPitch();
			this.activeGain = soundInstance.getVolume();
		}
		
		protected void updateLocation() {
			float x = soundInstance.getXPosF();
			float y = soundInstance.getYPosF();
			float z = soundInstance.getZPosF();
			float pitch = soundInstance.getPitch();
			float gain = soundInstance.getVolume();
			if(x != activeX || y != activeY || z != activeZ) {
				soundHandle.move(x, y, z);
				activeX = x;
				activeY = y;
				activeZ = z;
			}
			if(pitch != activePitch) {
				soundHandle.pitch(EaglercraftSoundManager.this.getNormalizedPitch(soundInstance, soundConfig));
				activePitch = pitch;
			}
			if(gain != activeGain) {
				soundHandle.gain(EaglercraftSoundManager.this.getNormalizedVolume(soundInstance, soundConfig, soundCategory));
				activeGain = gain;
			}
		}
		
	}
	
	protected static class WaitingSoundEvent {
		
		protected final ISound playSound;
		protected int playTicks;
		protected boolean paused = false;
		
		private WaitingSoundEvent(ISound playSound, int playTicks) {
			this.playSound = playSound;
			this.playTicks = playTicks;
		}
		
	}
	
	private static final Logger logger = LogManager.getLogger("SoundManager");
	
	private final GameSettings settings;
	private final SoundHandler handler;
	private final float[] categoryVolumes;
	private final List<ActiveSoundEvent> activeSounds;
	private final List<WaitingSoundEvent> queuedSounds;
	private final List<ISoundEventListener> listeners;
	private int soundId = 0;

	public EaglercraftSoundManager(GameSettings settings, SoundHandler handler) {
		this.settings = settings;
		this.handler = handler;
		SoundCategory[] categories = SoundCategory.values();
		categoryVolumes = new float[categories.length];
		for(int i = 0; i < categories.length; ++i) {
			categoryVolumes[i] = settings.getSoundLevel(categories[i]);
		}
		activeSounds = new LinkedList<>();
		queuedSounds = new LinkedList<>();
		listeners = new LinkedList<>();
	}

	public void unloadSoundSystem() {
		// handled by PlatformApplication
	}
	
	public void reloadSoundSystem() {
		PlatformAudio.flushAudioCache();
	}
	
	public void setSoundCategoryVolume(SoundCategory category, float volume) {
		categoryVolumes[category.ordinal()] = volume;
		Iterator<ActiveSoundEvent> soundItr = activeSounds.iterator();
		while(soundItr.hasNext()) {
			ActiveSoundEvent evt = soundItr.next();
			if((category == SoundCategory.MASTER || evt.soundCategory == category)
					&& !evt.soundHandle.shouldFree()) {
				float newVolume = getNormalizedVolume(evt.soundInstance, evt.soundConfig, evt.soundCategory);
				if(newVolume > 0.0f) {
					evt.soundHandle.gain(newVolume);
				}else {
					evt.soundHandle.end();
					soundItr.remove();
				}
			}
		}
	}
	
	public void stopAllSounds() {
		Iterator<ActiveSoundEvent> soundItr = activeSounds.iterator();
		while(soundItr.hasNext()) {
			ActiveSoundEvent evt = soundItr.next();
			if(!evt.soundHandle.shouldFree()) {
				evt.soundHandle.end();
			}
		}
		activeSounds.clear();
	}
	
	public void pauseAllSounds() {
		Iterator<ActiveSoundEvent> soundItr = activeSounds.iterator();
		while(soundItr.hasNext()) {
			ActiveSoundEvent evt = soundItr.next();
			if(!evt.soundHandle.shouldFree()) {
				evt.soundHandle.pause(true);
				evt.paused = true;
			}
		}
		Iterator<WaitingSoundEvent> soundItr2 = queuedSounds.iterator();
		while(soundItr2.hasNext()) {
			soundItr2.next().paused = true;
		}
	}
	
	public void resumeAllSounds() {
		Iterator<ActiveSoundEvent> soundItr = activeSounds.iterator();
		while(soundItr.hasNext()) {
			ActiveSoundEvent evt = soundItr.next();
			if(!evt.soundHandle.shouldFree()) {
				evt.soundHandle.pause(false);
				evt.paused = false;
			}
		}
		Iterator<WaitingSoundEvent> soundItr2 = queuedSounds.iterator();
		while(soundItr2.hasNext()) {
			soundItr2.next().paused = false;
		}
	}
	
	public void updateAllSounds() {
		Iterator<ActiveSoundEvent> soundItr = activeSounds.iterator();
		while(soundItr.hasNext()) {
			ActiveSoundEvent evt = soundItr.next();
			boolean persist = false;
			if(!evt.paused && (evt.soundInstance instanceof ITickableSound)) {
				boolean destroy = false;
				ITickableSound snd = (ITickableSound) evt.soundInstance;
				lbl : {
					try {
						snd.update();
						if(snd.isDonePlaying()) {
							destroy = true;
							break lbl;
						}
						persist = true;
					}catch(Throwable t) {
						logger.error("Error ticking sound: {}", t.toString());
						logger.error(t);
						destroy = true;
					}
				}
				if(destroy) {
					if(!evt.soundHandle.shouldFree()) {
						evt.soundHandle.end();
					}
					soundItr.remove();
					continue;
				}
			}
			if(evt.soundHandle.shouldFree()) {
				if(!persist) {
					soundItr.remove();
				}
			}else {
				evt.updateLocation();
			}
		}
		Iterator<WaitingSoundEvent> soundItr2 = queuedSounds.iterator();
		while(soundItr2.hasNext()) {
			WaitingSoundEvent evt = soundItr2.next();
			if(!evt.paused && --evt.playTicks <= 0) {
				soundItr2.remove();
				playSound(evt.playSound);
			}
		}
		PlatformAudio.clearAudioCache();
	}
	
	public boolean isSoundPlaying(ISound sound) {
		Iterator<ActiveSoundEvent> soundItr = activeSounds.iterator();
		while(soundItr.hasNext()) {
			ActiveSoundEvent evt = soundItr.next();
			if(evt.soundInstance == sound) {
				return !evt.soundHandle.shouldFree();
			}
		}
		return false;
	}
	
	public void stopSound(ISound sound) {
		Iterator<ActiveSoundEvent> soundItr = activeSounds.iterator();
		while(soundItr.hasNext()) {
			ActiveSoundEvent evt = soundItr.next();
			if(evt.soundInstance == sound) {
				if(!evt.soundHandle.shouldFree()) {
					evt.soundHandle.end();
					soundItr.remove();
					return;
				}
			}
		}
		Iterator<WaitingSoundEvent> soundItr2 = queuedSounds.iterator();
		while(soundItr2.hasNext()) {
			if(soundItr2.next().playSound == sound) {
				soundItr2.remove();
			}
		}
	}

	private final IAudioCacheLoader browserResourcePackLoader = filename -> {
		try {
			return EaglerInputStream.inputStreamToBytesQuiet(Minecraft.getMinecraft().getResourceManager()
					.getResource(new ResourceLocation(filename)).getInputStream());
		}catch(Throwable t) {
			return null;
		}
	};

	public void playSound(ISound sound) {
		if(!PlatformAudio.available()) {
			return;
		}
		if(sound != null) {
			sound = net.minecraftforge.client.ForgeHooksClient.playSound((net.minecraft.client.audio.SoundManager)this, sound);
		}
		if(sound != null && categoryVolumes[SoundCategory.MASTER.ordinal()] > 0.0f) {
			SoundEventAccessor accessor = sound.createAccessor(handler);
			if(accessor == null) {
				logger.warn("Unable to play unknown soundEvent(1): {}", sound.getSoundLocation().toString());
			}else {
				for(int i = 0, l = listeners.size(); i < l; ++i) {
					listeners.get(i).soundPlay(sound, accessor);
				}
				Sound etr = sound.getSound();
				if (etr == SoundHandler.MISSING_SOUND) {
					logger.warn("Unable to play empty soundEvent(2): {}", etr.getSoundLocation().toString());
				}else {
					ResourceLocation lc = etr.getSoundAsOggLocation();
					IAudioResource trk;
					if(EagRuntime.getPlatformType() != EnumPlatformType.DESKTOP) {
						trk = PlatformAudio.loadAudioDataNew(lc.toString(), !etr.isStreaming(), browserResourcePackLoader);
					}else {
						trk = PlatformAudio.loadAudioData(
								"/assets/" + lc.getResourceDomain() + "/" + lc.getResourcePath(), !etr.isStreaming());
					}
					if(trk == null) {
						logger.warn("Unable to play unknown soundEvent(3): {}", sound.getSoundLocation().toString());
					}else {
						
						SoundCategory category = sound.getCategory();
						ActiveSoundEvent newSound = new ActiveSoundEvent(this, sound, category, etr, null);

						float pitch = getNormalizedPitch(sound, etr);
						float attenuatedGain = getNormalizedVolume(sound, etr, category);
						boolean repeat = sound.canRepeat();
						
						AttenuationType tp = sound.getAttenuationType();
						if(tp == AttenuationType.LINEAR) {
							newSound.soundHandle = PlatformAudio.beginPlayback(trk, newSound.activeX, newSound.activeY,
									newSound.activeZ, attenuatedGain, pitch, repeat);
						}else {
							newSound.soundHandle = PlatformAudio.beginPlaybackStatic(trk, attenuatedGain, pitch, repeat);
						}
						
						if(newSound.soundHandle == null) {
							logger.error("Unable to play soundEvent(4): {}", sound.getSoundLocation().toString());
						}else {
							activeSounds.add(newSound);
							String id = Integer.toString(++soundId);
							if(etr.isStreaming()) {
								net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.sound.PlayStreamingSourceEvent((net.minecraft.client.audio.SoundManager)this, sound, id));
							}else {
								net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.sound.PlaySoundSourceEvent((net.minecraft.client.audio.SoundManager)this, sound, id));
							}
						}
					}
				}
			}
		}
	}
	
	public void playDelayedSound(ISound sound, int delay) {
		queuedSounds.add(new WaitingSoundEvent(sound, delay));
	}
	
	private float getNormalizedVolume(ISound sound, Sound entry, SoundCategory category) {
		return (float) MathHelper.clamp((double) sound.getVolume() * entry.getVolume(), 0.0D, 1.0D)
				* (category == SoundCategory.MASTER ? 1.0f : categoryVolumes[category.ordinal()])
				* categoryVolumes[SoundCategory.MASTER.ordinal()];
	}
	
	private float getNormalizedPitch(ISound sound, Sound entry) {
		return MathHelper.clamp(sound.getPitch() * (float)entry.getPitch(), 0.5f, 2.0f);
	}
	
	public void setListener(Entity player, float partialTicks) {
		if(!PlatformAudio.available()) {
			return;
		}
		if(player != null) {
			try {
				float f = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;
				float f1 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * partialTicks;
				double d0 = player.prevPosX + (player.posX - player.prevPosX) * (double) partialTicks;
				double d1 = player.prevPosY + (player.posY - player.prevPosY) * (double) partialTicks + (double) player.getEyeHeight();
				double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * (double) partialTicks;
				PlatformAudio.setListener((float)d0, (float)d1, (float)d2, f, f1);
			}catch(Throwable t) {
				// eaglercraft 1.5.2 had Infinity/NaN crashes for this function which
				// couldn't be resolved via if statement checks in the above variables
			}
		}
	}

	public void addListener(ISoundEventListener listener) {
		listeners.add(listener);
	}

	public void removeListener(ISoundEventListener listener) {
		listeners.remove(listener);
	}

	public void stop(String soundName, SoundCategory category) {
		ResourceLocation location = soundName.isEmpty() ? null : new ResourceLocation(soundName);
		Iterator<ActiveSoundEvent> soundItr = activeSounds.iterator();
		while(soundItr.hasNext()) {
			ActiveSoundEvent evt = soundItr.next();
			if((category == null || evt.soundCategory == category)
					&& (location == null || evt.soundInstance.getSoundLocation().equals(location))) {
				if(!evt.soundHandle.shouldFree()) {
					evt.soundHandle.end();
				}
				soundItr.remove();
			}
		}
		Iterator<WaitingSoundEvent> waitingItr = queuedSounds.iterator();
		while(waitingItr.hasNext()) {
			ISound queued = waitingItr.next().playSound;
			if((category == null || queued.getCategory() == category)
					&& (location == null || queued.getSoundLocation().equals(location))) {
				waitingItr.remove();
			}
		}
	}
	
}
