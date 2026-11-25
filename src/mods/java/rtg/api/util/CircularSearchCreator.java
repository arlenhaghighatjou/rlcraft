package rtg.api.util;

public class CircularSearchCreator {
   private boolean active = false;
   private int size;
   private float center;

   public CircularSearchCreator() {
   }

   public int[] pattern(float maxRadius, int requestedSize) {
      if (this.active) {
         throw new RuntimeException();
      } else {
         this.active = true;
         this.size = requestedSize;
         this.center = ((float)this.size - 1.0F) / 2.0F;
         int[] result = new int[this.size * this.size];
         boolean[] found = new boolean[this.size * this.size];
         int nextResult = 0;
         int smallerHalfSize = this.size / 2;
         int largerHalfSize = (this.size + 1) / 2;

         for(float radius = 0.0F; radius < maxRadius; radius += 0.01F) {
            for(int y = 0; y < largerHalfSize; ++y) {
               for(int x = smallerHalfSize; x < this.size; ++x) {
                  int index = x * this.size + y;
                  if (!found[index]) {
                     float distance = this.distanceFromCenter(x, y);
                     if (distance <= radius) {
                        result[nextResult++] = index;
                        found[index] = true;
                     }
                  }
               }
            }

            for(int x = this.size - 1; x >= smallerHalfSize; --x) {
               for(int y = largerHalfSize; y < this.size; ++y) {
                  int index = x * this.size + y;
                  if (!found[index]) {
                     float distance = this.distanceFromCenter(x, y);
                     if (distance <= radius) {
                        result[nextResult++] = index;
                        found[index] = true;
                     }
                  }
               }
            }

            for(int y = this.size - 1; y >= largerHalfSize - 1; --y) {
               for(int x = smallerHalfSize - 1; x > -1; --x) {
                  int index = x * this.size + y;
                  if (!found[index]) {
                     float distance = this.distanceFromCenter(x, y);
                     if (distance <= radius) {
                        result[nextResult++] = index;
                        found[index] = true;
                     }
                  }
               }
            }

            for(int x = 0; x < smallerHalfSize; ++x) {
               for(int y = largerHalfSize - 1; y > -1; --y) {
                  int index = x * this.size + y;
                  if (!found[index]) {
                     float distance = this.distanceFromCenter(x, y);
                     if (distance <= radius) {
                        result[nextResult++] = index;
                        found[index] = true;
                     }
                  }
               }
            }
         }

         this.active = false;
         if (nextResult < result.length) {
            int[] newResult = new int[nextResult];
            System.arraycopy(result, 0, newResult, 0, nextResult);
            result = newResult;
         }

         return result;
      }
   }

   private float distanceFromCenter(int x, int y) {
      return (float)Math.sqrt(Math.pow((double)((float)x - this.center), 2.0D) + Math.pow((double)((float)y - this.center), 2.0D));
   }
}
