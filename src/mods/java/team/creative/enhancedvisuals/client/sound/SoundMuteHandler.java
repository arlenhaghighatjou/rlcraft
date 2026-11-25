package team.creative.enhancedvisuals.client.sound;

import com.creativemd.creativecore.common.config.premade.curve.DecimalCurve;
import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import paulscode.sound.Library;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.Source;

public class SoundMuteHandler {
   public static boolean isMuting = false;
   public static HashMap<Source, Float> sources = null;
   public static Library soundLibrary;
   public static SoundSystem sndSystem;
   public static ArrayList<String> ignoredSounds;
   public static DecimalCurve muteGraph;
   public static int timeTick = 0;

   public SoundMuteHandler() {
   }

   public static void tick() {
      if (isMuting) {
         double factor = muteGraph.valueAt((double)timeTick);
         if (factor <= 0.0D) {
            endMuting();
         } else {
            setMuteVolume((float)(1.0D - factor));
            ++timeTick;
         }
      }

   }

   public static void updateSounds() {
      try {
         HashMap<String, Source> sourcesAndIDs = null;
         synchronized(SoundSystemConfig.THREAD_SYNC) {
            sourcesAndIDs = new HashMap(soundLibrary.getSources());
         }

         for(Source source : sourcesAndIDs.values()) {
            if (!sources.containsKey(source) && !ignoredSounds.contains(source.sourcename)) {
               sources.put(source, Float.valueOf(source.sourceVolume));
            }
         }
      } catch (Exception var4) {
         var4.printStackTrace();
      }

   }

   public static boolean startMuting(DecimalCurve muteGraph) {
      if (soundLibrary == null) {
         SoundHandler handler = Minecraft.getMinecraft().getSoundHandler();
         SoundManager sndManager = (SoundManager)ReflectionHelper.getPrivateValue(SoundHandler.class, handler, new String[]{"sndManager", "sndManager"});
         sndSystem = (SoundSystem)ReflectionHelper.getPrivateValue(SoundManager.class, sndManager, new String[]{"sndSystem", "sndSystem"});
         soundLibrary = (Library)ReflectionHelper.getPrivateValue(SoundSystem.class, sndSystem, new String[]{"soundLibrary"});
      }

      if (isMuting && muteGraph.valueAt((double)timeTick) > muteGraph.valueAt(0.0D)) {
         muteGraph = muteGraph;
         timeTick = 0;
         tick();
         return true;
      } else if (!isMuting) {
         muteGraph = muteGraph;
         timeTick = 0;
         ignoredSounds = new ArrayList();
         isMuting = true;
         tick();
         return true;
      } else {
         return false;
      }
   }

   public static void endMuting() {
      setMuteVolume(1.0F);
      sources = null;
      isMuting = false;
      ignoredSounds = null;
   }

   public static synchronized void setMuteVolume(float muteVolume) {
      if (isMuting && sources != null) {
         synchronized(SoundSystemConfig.THREAD_SYNC) {
            for(Source source : sources.keySet()) {
               sndSystem.setVolume(source.sourcename, ((Float)sources.get(source)).floatValue() * muteVolume);
            }
         }
      }

   }
}
