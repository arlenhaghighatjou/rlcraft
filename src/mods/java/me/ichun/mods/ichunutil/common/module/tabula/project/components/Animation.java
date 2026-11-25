package me.ichun.mods.ichunutil.common.module.tabula.project.components;

import com.google.common.collect.Ordering;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map.Entry;
import org.apache.commons.lang3.RandomStringUtils;

public class Animation {
   public String name;
   public String identifier;
   public boolean loops;
   public TreeMap<String, ArrayList<AnimationComponent>> sets = new TreeMap(Ordering.natural());
   public transient int playTime;
   public transient boolean playing;

   public Animation(String name) {
      this.name = name;
      this.identifier = RandomStringUtils.randomAscii(20);
   }

   public void createAnimComponent(String cubeIdent, String name, int length, int pos) {
      ArrayList<AnimationComponent> set = (ArrayList)this.sets.get(cubeIdent);
      if (set == null) {
         set = new ArrayList();
         this.sets.put(cubeIdent, set);
      }

      set.add(new AnimationComponent(name, length, pos));
   }

   public void update() {
      if (this.playing) {
         ++this.playTime;
         if (this.playTime > this.getLength()) {
            if (this.loops) {
               this.playTime = 0;
            } else {
               this.stop();
            }
         }
      }

   }

   public void play() {
      if (!this.playing) {
         this.playing = true;
         this.playTime = 0;
      }

   }

   public void stop() {
      this.playing = false;
   }

   public int getLength() {
      int lastTick = 0;

      for(Entry<String, ArrayList<AnimationComponent>> e : this.sets.entrySet()) {
         for(AnimationComponent comp : (ArrayList)e.getValue()) {
            if (comp.startKey + comp.length > lastTick) {
               lastTick = comp.startKey + comp.length;
            }
         }
      }

      return lastTick;
   }
}
