package com.tmtravlr.soundfilters;

import com.tmtravlr.soundfilters.filters.BaseFilter;
import com.tmtravlr.soundfilters.filters.FilterException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import javax.sound.sampled.AudioFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.MathHelper;
import paulscode.sound.FilenameURL;
import paulscode.sound.SoundBuffer;
import paulscode.sound.Source;
import paulscode.sound.libraries.ChannelLWJGLOpenAL;
import paulscode.sound.libraries.SourceLWJGLOpenAL;

public class ModifiedLWJGLOpenALSource extends SourceLWJGLOpenAL {
   public ModifiedLWJGLOpenALSource(FloatBuffer listenerPosition, IntBuffer myBuffer, Source old, SoundBuffer soundBuffer) {
      super(listenerPosition, myBuffer, old, soundBuffer);
   }

   public ModifiedLWJGLOpenALSource(FloatBuffer listenerPosition, IntBuffer myBuffer, boolean priority, boolean toStream, boolean toLoop, String sourcename, FilenameURL filenameURL, SoundBuffer soundBuffer, float x, float y, float z, int attModel, float distOrRoll, boolean temporary) {
      super(listenerPosition, myBuffer, priority, toStream, toLoop, sourcename, filenameURL, soundBuffer, x, y, z, attModel, distOrRoll, temporary);
   }

   public ModifiedLWJGLOpenALSource(FloatBuffer listenerPosition, AudioFormat audioFormat, boolean priority, String sourcename, float x, float y, float z, int attModel, float distOrRoll) {
      super(listenerPosition, audioFormat, priority, sourcename, x, y, z, attModel, distOrRoll);
   }

   public boolean stopped() {
      boolean stopped = super.stopped();
      if (this.channel != null && this.channel.attachedSource == this && !stopped && !this.paused()) {
         this.updateFilters();
      }

      return stopped;
   }

   private void updateFilters() {
      ChannelLWJGLOpenAL alChannel = (ChannelLWJGLOpenAL)this.channel;
      if (alChannel != null && alChannel.ALSource != null && this.position != null) {
         Minecraft mc = Minecraft.getMinecraft();
         boolean isMusic = this.toStream && this.position.x == 0.0F && this.position.y == 0.0F && this.position.z == 0.0F && this.attModel == 0;
         if (!isMusic && mc != null && mc.world != null) {
            boolean isOccluded = false;
            SoundTickHandler.DoubleWithTimeout sourceInfo = new SoundTickHandler.DoubleWithTimeout((Source)null, 0.0D, 10);
            if (SoundFiltersMod.doOcclusion && this.position != null) {
               SoundTickHandler.ComparablePosition sourcePosition = new SoundTickHandler.ComparablePosition(this.position.x, this.position.y, this.position.z);
               if (SoundTickHandler.sourceOcclusionMap.containsKey(sourcePosition)) {
                  SoundTickHandler.DoubleWithTimeout tempSourceInfo = (SoundTickHandler.DoubleWithTimeout)SoundTickHandler.sourceOcclusionMap.get(sourcePosition);
                  if (tempSourceInfo != null) {
                     sourceInfo = tempSourceInfo;
                     tempSourceInfo.source = this;
                  } else {
                     SoundTickHandler.sourceOcclusionMap.put(sourcePosition, sourceInfo);
                  }
               }

               isOccluded = sourceInfo.amount > 0.05D;
            }

            SoundFiltersMod.lowPassFilter.gain = SoundTickHandler.baseLowPassGain;
            SoundFiltersMod.lowPassFilter.gainHF = SoundTickHandler.baseLowPassGainHF;
            if (isOccluded && this.attModel != 0) {
               SoundFiltersMod.lowPassFilter.gain = (float)((double)SoundFiltersMod.lowPassFilter.gain * (1.0D - 1.0D * sourceInfo.amount));
               SoundFiltersMod.lowPassFilter.gainHF = (float)((double)SoundFiltersMod.lowPassFilter.gainHF * (1.0D - 1.0D * (double)MathHelper.sqrt(sourceInfo.amount)));
            }

            if (SoundFiltersMod.lowPassFilter.gain >= 1.0F && SoundFiltersMod.lowPassFilter.gainHF >= 1.0F) {
               SoundFiltersMod.lowPassFilter.disable();
            } else {
               SoundFiltersMod.lowPassFilter.enable();
               SoundFiltersMod.lowPassFilter.loadParameters();
            }

            if (SoundFiltersMod.reverbFilter.reflectionsDelay <= 0.0F && SoundFiltersMod.reverbFilter.lateReverbDelay <= 0.0F) {
               SoundFiltersMod.reverbFilter.disable();
            } else {
               SoundFiltersMod.reverbFilter.enable();
               SoundFiltersMod.reverbFilter.loadParameters();
            }

            try {
               BaseFilter.loadSourceFilter(alChannel.ALSource.get(0), 131077, SoundFiltersMod.lowPassFilter);
               BaseFilter.load3SourceFilters(alChannel.ALSource.get(0), 131078, SoundFiltersMod.reverbFilter, (BaseFilter)null, SoundFiltersMod.lowPassFilter);
            } catch (FilterException var9) {
               CrashReport crashreport = CrashReport.makeCrashReport(var9, "Updating Sound Filters");
               throw new ReportedException(crashreport);
            }
         } else {
            SoundFiltersMod.lowPassFilter.disable();
            SoundFiltersMod.reverbFilter.disable();

            try {
               BaseFilter.loadSourceFilter(alChannel.ALSource.get(0), 131077, (BaseFilter)null);
               BaseFilter.load3SourceFilters(alChannel.ALSource.get(0), 131078, (BaseFilter)null, (BaseFilter)null, (BaseFilter)null);
            } catch (FilterException var8) {
               CrashReport crashreport = CrashReport.makeCrashReport(var8, "Updating Sound Filters");
               throw new ReportedException(crashreport);
            }
         }
      }

   }
}
