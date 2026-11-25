package com.ferreusveritas.dynamictrees.worldgen.json;

import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.Map.Entry;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;

public class JsonMath {
   public JsonMath.MathOperator rootOp;
   private final Biome biome;

   public JsonMath(JsonElement mathElement, Biome biome) {
      this.biome = biome;
      if (mathElement.isJsonObject()) {
         JsonObject mathObject = mathElement.getAsJsonObject();

         for(Entry<String, JsonElement> entry : mathObject.entrySet()) {
            this.rootOp = this.processElement((String)entry.getKey(), (JsonElement)entry.getValue());
            if (this.rootOp != null) {
               return;
            }
         }
      }

   }

   private JsonMath.MathOperator getVariable(String name) {
      if (JsonMath.EnumMathFunction.NOISE.name.equals(name)) {
         return new JsonMath.Noise();
      } else if (JsonMath.EnumMathFunction.RAND.name.equals(name)) {
         return new JsonMath.Rand();
      } else if (JsonMath.EnumMathFunction.TREES.name.equals(name)) {
         return new JsonMath.Trees(this.biome);
      } else {
         return JsonMath.EnumMathFunction.RADIUS.name.equals(name) ? new JsonMath.Radius() : null;
      }
   }

   private JsonMath.MathOperator processElement(String key, JsonElement value) {
      JsonMath.EnumMathFunction op = JsonMath.EnumMathFunction.getFunction(key);
      ArrayList<JsonMath.MathOperator> paramList = new ArrayList();
      Species speciesArg = Species.NULLSPECIES;
      if (value.isJsonArray()) {
         for(JsonElement parameter : value.getAsJsonArray()) {
            JsonMath.MathOperator m = null;
            if (parameter.isJsonObject()) {
               Entry<String, JsonElement> entry = (Entry)parameter.getAsJsonObject().entrySet().iterator().next();
               m = this.processElement((String)entry.getKey(), (JsonElement)entry.getValue());
            } else if (parameter.isJsonPrimitive()) {
               if (parameter.getAsJsonPrimitive().isNumber()) {
                  m = new JsonMath.Const(parameter.getAsFloat());
               } else if (parameter.getAsJsonPrimitive().isString()) {
                  String name = parameter.getAsString();
                  JsonMath.MathOperator var = this.getVariable(name);
                  if (var != null) {
                     m = var;
                  } else if (TreeRegistry.findSpeciesSloppy(name) != Species.NULLSPECIES) {
                     speciesArg = TreeRegistry.findSpeciesSloppy(name);
                  }
               }
            }

            if (m != null) {
               paramList.add(m);
            }
         }
      }

      JsonMath.MathOperator[] paramArray = (JsonMath.MathOperator[])paramList.toArray(new JsonMath.MathOperator[0]);
      switch(op) {
      case NOISE:
         return new JsonMath.Noise();
      case RAND:
         return new JsonMath.Rand();
      case TREES:
         return new JsonMath.Trees(this.biome);
      case RADIUS:
         return new JsonMath.Radius();
      case ADD:
         return new JsonMath.Adder(paramArray);
      case SUB:
         return new JsonMath.Subtractor(paramArray);
      case MUL:
         return new JsonMath.Multiplier(paramArray);
      case DIV:
         return new JsonMath.Divider(paramArray);
      case MAX:
         return new JsonMath.Maximum(paramArray);
      case MIN:
         return new JsonMath.Minimum(paramArray);
      case IFGT:
         return new JsonMath.IfGreaterThan(paramArray);
      case SPECIES:
         return speciesArg != Species.NULLSPECIES ? new JsonMath.IfSpecies(speciesArg, paramArray) : null;
      case DEBUG:
         return new JsonMath.Debug(paramArray);
      default:
         return null;
      }
   }

   public float apply(EaglercraftRandom random, float noise) {
      JsonMath.MathContext mc = new JsonMath.MathContext(noise, random);
      return this.rootOp.apply(mc);
   }

   public float apply(EaglercraftRandom random, Species species, float radius) {
      JsonMath.MathContext mc = new JsonMath.MathSpeciesContext(random, species, radius);
      return this.rootOp.apply(mc);
   }

   public static class Adder implements JsonMath.MathOperator {
      private final JsonMath.MathOperator[] functions;
      private final boolean dual;

      public Adder(JsonMath.MathOperator[] functionArray) {
         this.functions = functionArray;
         this.dual = this.functions.length == 2;
      }

      public float apply(JsonMath.MathContext mc) {
         if (this.dual) {
            return this.functions[0].apply(mc) + this.functions[1].apply(mc);
         } else {
            float r = 0.0F;

            for(JsonMath.MathOperator f : this.functions) {
               r += f.apply(mc);
            }

            return r;
         }
      }
   }

   public static class Const implements JsonMath.MathOperator {
      private final float value;

      Const(float value) {
         this.value = value;
      }

      public float apply(JsonMath.MathContext mc) {
         return this.value;
      }
   }

   public static class Debug implements JsonMath.MathOperator {
      private final JsonMath.MathOperator[] functions;

      public Debug(JsonMath.MathOperator[] functionArray) {
         this.functions = functionArray;
      }

      public float apply(JsonMath.MathContext mc) {
         if (this.functions.length >= 1) {
            float val = this.functions[0].apply(mc);
            System.out.println("Json Debug Value: " + val);
            return val;
         } else {
            return 0.0F;
         }
      }
   }

   public static class Divider implements JsonMath.MathOperator {
      private final JsonMath.MathOperator[] functions;
      private final boolean dual;

      public Divider(JsonMath.MathOperator[] functionArray) {
         this.functions = functionArray;
         this.dual = this.functions.length == 2;
      }

      public float apply(JsonMath.MathContext mc) {
         if (this.dual) {
            return this.functions[0].apply(mc) / this.functions[1].apply(mc);
         } else {
            Float r = null;

            for(JsonMath.MathOperator f : this.functions) {
               float v = f.apply(mc);
               r = r == null ? v : r.floatValue() / v;
            }

            return r == null ? 0.0F : r.floatValue();
         }
      }
   }

   public static enum EnumMathFunction {
      CONST,
      NOISE,
      RAND,
      TREES,
      RADIUS,
      ADD,
      SUB,
      MUL,
      DIV,
      MOD,
      MAX,
      MIN,
      IFGT,
      SPECIES,
      DEBUG;

      public final String name = this.toString().toLowerCase();

      private EnumMathFunction() {
      }

      static JsonMath.EnumMathFunction getFunction(String findName) {
         for(JsonMath.EnumMathFunction fun : values()) {
            if (fun.name.equals(findName)) {
               return fun;
            }
         }

         return null;
      }
   }

   public static class IfGreaterThan implements JsonMath.MathOperator {
      private final JsonMath.MathOperator[] functions;

      public IfGreaterThan(JsonMath.MathOperator[] functionArray) {
         this.functions = functionArray;
      }

      public float apply(JsonMath.MathContext mc) {
         if (this.functions.length == 4) {
            return this.functions[0].apply(mc) > this.functions[1].apply(mc) ? this.functions[2].apply(mc) : this.functions[3].apply(mc);
         } else {
            return 0.0F;
         }
      }
   }

   public static class IfSpecies implements JsonMath.MathOperator {
      private final JsonMath.MathOperator[] functions;
      private final Species species;

      public IfSpecies(Species species, JsonMath.MathOperator[] functionArray) {
         this.species = species;
         this.functions = functionArray;
      }

      public float apply(JsonMath.MathContext mc) {
         if (mc instanceof JsonMath.MathSpeciesContext && this.functions.length == 2) {
            return ((JsonMath.MathSpeciesContext)mc).species == this.species ? this.functions[0].apply(mc) : this.functions[1].apply(mc);
         } else {
            return 0.0F;
         }
      }
   }

   public static class MathContext {
      public float noise;
      public EaglercraftRandom rand;

      public MathContext(float noise, EaglercraftRandom random) {
         this.noise = noise;
         this.rand = random;
      }
   }

   public interface MathOperator {
      float apply(JsonMath.MathContext var1);
   }

   public static class MathSpeciesContext extends JsonMath.MathContext {
      public float radius;
      public Species species;

      public MathSpeciesContext(EaglercraftRandom random, Species species, float radius) {
         super(0.0F, random);
         this.radius = radius;
         this.species = species;
      }
   }

   public static class Maximum implements JsonMath.MathOperator {
      private final JsonMath.MathOperator[] functions;
      private final boolean dual;

      public Maximum(JsonMath.MathOperator[] functionArray) {
         this.functions = functionArray;
         this.dual = this.functions.length == 2;
      }

      public float apply(JsonMath.MathContext mc) {
         if (this.dual) {
            return Math.max(this.functions[0].apply(mc), this.functions[1].apply(mc));
         } else {
            Float r = null;

            for(JsonMath.MathOperator f : this.functions) {
               float v = f.apply(mc);
               r = r == null ? v : Math.max(r.floatValue(), v);
            }

            return r == null ? 0.0F : r.floatValue();
         }
      }
   }

   public static class Minimum implements JsonMath.MathOperator {
      private final JsonMath.MathOperator[] functions;
      private final boolean dual;

      public Minimum(JsonMath.MathOperator[] functionArray) {
         this.functions = functionArray;
         this.dual = this.functions.length == 2;
      }

      public float apply(JsonMath.MathContext mc) {
         if (this.dual) {
            return Math.min(this.functions[0].apply(mc), this.functions[1].apply(mc));
         } else {
            Float r = null;

            for(JsonMath.MathOperator f : this.functions) {
               float v = f.apply(mc);
               r = r == null ? v : Math.min(r.floatValue(), v);
            }

            return r == null ? 0.0F : r.floatValue();
         }
      }
   }

   public static class Modulus implements JsonMath.MathOperator {
      private final JsonMath.MathOperator[] functions;
      private final boolean dual;

      public Modulus(JsonMath.MathOperator[] functionArray) {
         this.functions = functionArray;
         this.dual = this.functions.length == 2;
      }

      public float apply(JsonMath.MathContext mc) {
         return this.dual ? this.functions[0].apply(mc) % this.functions[1].apply(mc) : 0.0F;
      }
   }

   public static class Multiplier implements JsonMath.MathOperator {
      private final JsonMath.MathOperator[] functions;
      private final boolean dual;

      public Multiplier(JsonMath.MathOperator[] functionArray) {
         this.functions = functionArray;
         this.dual = this.functions.length == 2;
      }

      public float apply(JsonMath.MathContext mc) {
         if (this.dual) {
            return this.functions[0].apply(mc) * this.functions[1].apply(mc);
         } else {
            float r = 1.0F;

            for(JsonMath.MathOperator f : this.functions) {
               r *= f.apply(mc);
            }

            return r;
         }
      }
   }

   public static class Noise implements JsonMath.MathOperator {
      public Noise() {
      }

      public float apply(JsonMath.MathContext mc) {
         return mc.noise;
      }
   }

   public static class Radius implements JsonMath.MathOperator {
      public Radius() {
      }

      public float apply(JsonMath.MathContext mc) {
         return mc instanceof JsonMath.MathSpeciesContext ? ((JsonMath.MathSpeciesContext)mc).radius : 0.0F;
      }
   }

   public static class Rand implements JsonMath.MathOperator {
      public Rand() {
      }

      public float apply(JsonMath.MathContext mc) {
         return mc.rand.nextFloat();
      }
   }

   public static class Subtractor implements JsonMath.MathOperator {
      private final JsonMath.MathOperator[] functions;
      private final boolean dual;

      public Subtractor(JsonMath.MathOperator[] functionArray) {
         this.functions = functionArray;
         this.dual = this.functions.length == 2;
      }

      public float apply(JsonMath.MathContext mc) {
         if (this.dual) {
            return this.functions[0].apply(mc) - this.functions[1].apply(mc);
         } else {
            Float r = null;

            for(JsonMath.MathOperator f : this.functions) {
               float v = f.apply(mc);
               r = r == null ? v : r.floatValue() - v;
            }

            return r == null ? 0.0F : r.floatValue();
         }
      }
   }

   public static class Trees implements JsonMath.MathOperator {
      private final Biome biome;

      public Trees(Biome biome) {
         this.biome = biome;
      }

      public float apply(JsonMath.MathContext mc) {
         return MathHelper.clamp((float)this.biome.decorator.treesPerChunk / 10.0F, -1.0F, 1.0F);
      }
   }
}
