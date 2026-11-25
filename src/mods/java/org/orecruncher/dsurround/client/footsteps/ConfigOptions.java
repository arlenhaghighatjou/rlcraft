package org.orecruncher.dsurround.client.footsteps;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.registry.acoustics.IOptions;

@SideOnly(Side.CLIENT)
public class ConfigOptions implements IOptions {
   protected long delayMin = 0L;
   protected long delayMax = 0L;
   protected float glidingVolume = 0.0F;
   protected float glidingPitch = 0.0F;
   protected float volumeScale = 1.0F;
   protected float pitchScale = 1.0F;

   public ConfigOptions() {
   }

   public long getDelayMin() {
      return this.delayMin;
   }

   public void setDelayMin(long v) {
      this.delayMin = v;
   }

   public long getDelayMax() {
      return this.delayMax;
   }

   public void setDelayMax(long v) {
      this.delayMax = v;
   }

   public float getGlidingVolume() {
      return this.glidingVolume;
   }

   public void setGlidingVolume(float v) {
      this.glidingVolume = v;
   }

   public float getGlidingPitch() {
      return this.glidingPitch;
   }

   public void setGlidingPitch(float v) {
      this.glidingPitch = v;
   }

   public float getVolumeScale() {
      return this.volumeScale;
   }

   public void setVolumeScale(float s) {
      this.volumeScale = s;
   }

   public float getPitchScale() {
      return this.pitchScale;
   }

   public void setPitchScale(float p) {
      this.pitchScale = p;
   }
}
