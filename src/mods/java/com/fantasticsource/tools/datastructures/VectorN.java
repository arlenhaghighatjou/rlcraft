package com.fantasticsource.tools.datastructures;

import com.fantasticsource.lwjgl.Quaternion;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.Tools;

public class VectorN {
   public double[] values;

   public VectorN(double... values) {
      if (values == null) {
         throw new NullPointerException();
      } else {
         this.values = new double[values.length];
         System.arraycopy(values, 0, this.values, 0, values.length);
      }
   }

   public VectorN copy() {
      return new VectorN(this.values);
   }

   public double getMagnitude() {
      return Math.sqrt(this.getMagnitudeSquared());
   }

   public double getMagnitudeSquared() {
      double squareMagnitude = 0.0D;

      for(double d : this.values) {
         squareMagnitude += d * d;
      }

      return squareMagnitude;
   }

   public VectorN scale(double scalar) {
      for(int i = 0; i < this.values.length; ++i) {
         this.values[i] *= scalar;
      }

      return this;
   }

   public VectorN setMagnitude(double magnitude) {
      double ratio = magnitude / this.getMagnitude();

      for(int i = 0; i < this.values.length; ++i) {
         this.values[i] *= ratio;
      }

      return this;
   }

   public VectorN add(double... values) {
      for(int i = 0; i < this.values.length && i < values.length; ++i) {
         this.values[i] += values[i];
      }

      return this;
   }

   public VectorN add(VectorN... vectors) {
      for(VectorN vector : vectors) {
         for(int i = 0; i < this.values.length && i < vector.values.length; ++i) {
            this.values[i] += vector.values[i];
         }
      }

      return this;
   }

   public VectorN subtract(double... values) {
      for(int i = 0; i < this.values.length && i < values.length; ++i) {
         this.values[i] -= values[i];
      }

      return this;
   }

   public VectorN subtract(VectorN... vectors) {
      for(VectorN vector : vectors) {
         for(int i = 0; i < this.values.length && i < vector.values.length; ++i) {
            this.values[i] -= vector.values[i];
         }
      }

      return this;
   }

   public VectorN multiply(double... values) {
      for(int i = 0; i < this.values.length && i < values.length; ++i) {
         this.values[i] *= values[i];
      }

      return this;
   }

   public VectorN multiply(VectorN... vectors) {
      for(VectorN vector : vectors) {
         for(int i = 0; i < this.values.length && i < vector.values.length; ++i) {
            this.values[i] *= vector.values[i];
         }
      }

      return this;
   }

   public VectorN divide(double... values) {
      for(int i = 0; i < this.values.length && i < values.length; ++i) {
         this.values[i] /= values[i];
      }

      return this;
   }

   public VectorN divide(VectorN... vectors) {
      for(VectorN vector : vectors) {
         for(int i = 0; i < this.values.length && i < vector.values.length; ++i) {
            this.values[i] /= vector.values[i];
         }
      }

      return this;
   }

   public VectorN power(double... values) {
      for(int i = 0; i < this.values.length && i < values.length; ++i) {
         this.values[i] = Math.pow(this.values[i], values[i]);
      }

      return this;
   }

   public VectorN power(VectorN... vectors) {
      for(VectorN vector : vectors) {
         for(int i = 0; i < this.values.length && i < vector.values.length; ++i) {
            this.values[i] = Math.pow(this.values[i], vector.values[i]);
         }
      }

      return this;
   }

   public VectorN mod(double... values) {
      for(int i = 0; i < this.values.length && i < values.length; ++i) {
         this.values[i] %= values[i];
      }

      return this;
   }

   public VectorN mod(VectorN... vectors) {
      for(VectorN vector : vectors) {
         for(int i = 0; i < this.values.length && i < vector.values.length; ++i) {
            this.values[i] %= vector.values[i];
         }
      }

      return this;
   }

   public VectorN posMod(double... values) {
      for(int i = 0; i < this.values.length && i < values.length; ++i) {
         this.values[i] = Tools.posMod(this.values[i], values[i]);
      }

      return this;
   }

   public VectorN posMod(VectorN... vectors) {
      for(VectorN vector : vectors) {
         for(int i = 0; i < this.values.length && i < vector.values.length; ++i) {
            this.values[i] = Tools.posMod(this.values[i], vector.values[i]);
         }
      }

      return this;
   }

   public VectorN lowLimit(double... values) {
      for(int i = 0; i < this.values.length && i < values.length; ++i) {
         this.values[i] = Tools.max(this.values[i], values[i]);
      }

      return this;
   }

   public VectorN lowLimit(VectorN... vectors) {
      for(VectorN vector : vectors) {
         for(int i = 0; i < this.values.length && i < vector.values.length; ++i) {
            this.values[i] = Tools.max(this.values[i], vector.values[i]);
         }
      }

      return this;
   }

   public VectorN highLimit(double... values) {
      for(int i = 0; i < this.values.length && i < values.length; ++i) {
         this.values[i] = Tools.min(this.values[i], values[i]);
      }

      return this;
   }

   public VectorN highLimit(VectorN... vectors) {
      for(VectorN vector : vectors) {
         for(int i = 0; i < this.values.length && i < vector.values.length; ++i) {
            this.values[i] = Tools.min(this.values[i], vector.values[i]);
         }
      }

      return this;
   }

   public VectorN round() {
      for(int i = 0; i < this.values.length; ++i) {
         this.values[i] = (double)Math.round(this.values[i]);
      }

      return this;
   }

   public VectorN floor() {
      for(int i = 0; i < this.values.length; ++i) {
         this.values[i] = Math.floor(this.values[i]);
      }

      return this;
   }

   public VectorN ceil() {
      for(int i = 0; i < this.values.length; ++i) {
         this.values[i] = Math.ceil(this.values[i]);
      }

      return this;
   }

   public VectorN crossProduct(VectorN other) {
      double[] values = new double[this.values.length];
      System.arraycopy(this.values, 0, values, 0, values.length);
      this.values[0] = values[1] * other.values[2] - values[2] * other.values[1];
      this.values[1] = values[2] * other.values[0] - values[0] * other.values[2];
      this.values[2] = values[0] * other.values[1] - values[1] * other.values[0];
      return this;
   }

   public VectorN rotate(VectorN axis, double theta) {
      Quaternion quaternion = MCTools.rotatedQuaternion(new Quaternion((float)this.values[0], (float)this.values[1], (float)this.values[2], 0.0F), new Quaternion((float)axis.values[0], (float)axis.values[1], (float)axis.values[2], 0.0F), theta);
      this.values[0] = (double)quaternion.x;
      this.values[1] = (double)quaternion.y;
      this.values[2] = (double)quaternion.z;
      return this;
   }

   public String toString() {
      if (this.values.length == 0) {
         return "()";
      } else {
         StringBuilder result = new StringBuilder("(" + this.values[0]);

         for(int i = 1; i < this.values.length; ++i) {
            result.append(", ").append(this.values[i]);
         }

         return result + ")";
      }
   }
}
