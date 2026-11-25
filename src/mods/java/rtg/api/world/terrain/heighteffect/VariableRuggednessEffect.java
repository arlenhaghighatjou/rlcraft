package rtg.api.world.terrain.heighteffect;

import rtg.api.world.RTGWorld;

public class VariableRuggednessEffect extends HeightEffect {
   public static float STANDARD_RUGGEDNESS_WAVELENGTH = 200.0F;
   public static int STANDARD_RUGGEDNESS_OCTAVE = 1;
   public HeightEffect smoothTerrain;
   public HeightEffect ruggedTerrain;
   public float startTransition;
   public float transitionWidth;
   public int octave;
   public float wavelength;

   public VariableRuggednessEffect() {
      this.startTransition = 2.14748365E9F;
      this.transitionWidth = 0.0F;
      this.octave = 1;
   }

   public VariableRuggednessEffect(HeightEffect smoothTerrain, HeightEffect ruggedTerrain, float startTransition, float transitionWidth, float wavelength) {
      this.startTransition = 2.14748365E9F;
      this.transitionWidth = 0.0F;
      this.octave = 1;
      this.smoothTerrain = smoothTerrain;
      this.ruggedTerrain = ruggedTerrain;
      this.startTransition = startTransition;
      this.transitionWidth = transitionWidth;
      this.wavelength = wavelength;
   }

   public VariableRuggednessEffect(HeightEffect smoothTerrain, HeightEffect ruggedTerrain, float startTransition, float transitionWidth) {
      this(smoothTerrain, ruggedTerrain, startTransition, transitionWidth, STANDARD_RUGGEDNESS_WAVELENGTH);
   }

   public final float added(RTGWorld rtgWorld, float x, float y) {
      float choice = rtgWorld.simplexInstance(this.octave).noise2f(x / this.wavelength, y / this.wavelength);
      if (choice <= this.startTransition) {
         return this.smoothTerrain.added(rtgWorld, x, y);
      } else if (choice >= this.startTransition + this.transitionWidth) {
         return this.ruggedTerrain.added(rtgWorld, x, y);
      } else {
         float smooth = this.smoothTerrain.added(rtgWorld, x, y);
         float rugged = this.ruggedTerrain.added(rtgWorld, x, y);
         return ((choice - this.startTransition) * rugged + (this.startTransition + this.transitionWidth - choice) * smooth) / this.transitionWidth;
      }
   }
}
