package org.orecruncher.dsurround.capabilities.dimension;

public interface IDimensionInfoEx extends IDimensionInfo {
   float getRainIntensity();

   float getCurrentRainIntensity();

   void setRainIntensity(float var1);

   void setCurrentRainIntensity(float var1);

   float getMinRainIntensity();

   void setMinRainIntensity(float var1);

   float getMaxRainIntensity();

   void setMaxRainIntensity(float var1);

   int getThunderTimer();

   void setThunderTimer(int var1);

   void randomizeRain();

   String configString();
}
