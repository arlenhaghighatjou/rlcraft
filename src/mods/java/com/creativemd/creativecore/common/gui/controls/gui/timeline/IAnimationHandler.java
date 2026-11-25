package com.creativemd.creativecore.common.gui.controls.gui.timeline;

public interface IAnimationHandler {
   void loop(boolean var1);

   void play();

   void pause();

   void stop();

   void set(int var1);

   int get();
}
