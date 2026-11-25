package ivorius.reccomplex.world.gen.feature.structure.generic;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ivorius.reccomplex.json.JsonUtils;
import ivorius.reccomplex.utils.algebra.ExpressionCache;
import ivorius.reccomplex.utils.expression.BiomeExpression;
import java.lang.reflect.Type;
import java.util.regex.Matcher;
import net.minecraft.world.biome.Biome;

public class WeightedBiomeMatcher {
   private BiomeExpression biomeExpression;
   private Double generationWeight;

   public WeightedBiomeMatcher(String expression, Double generationWeight) {
      this.biomeExpression = (BiomeExpression)ExpressionCache.of(new BiomeExpression(), expression);
      this.generationWeight = generationWeight;
   }

   public Double getGenerationWeight() {
      return this.generationWeight;
   }

   public void setGenerationWeight(Double generationWeight) {
      this.generationWeight = generationWeight;
   }

   public double getActiveGenerationWeight() {
      return this.generationWeight != null ? this.generationWeight.doubleValue() : 1.0D;
   }

   public boolean hasDefaultWeight() {
      return this.generationWeight == null;
   }

   public boolean matches(Biome biome) {
      return this.biomeExpression.test(biome);
   }

   public BiomeExpression getBiomeExpression() {
      return this.biomeExpression;
   }

   public String getDisplayString() {
      return this.biomeExpression.getDisplayString((Object)null);
   }

   public static class Serializer implements JsonDeserializer<WeightedBiomeMatcher>, JsonSerializer<WeightedBiomeMatcher> {
      public Serializer() {
      }

      public WeightedBiomeMatcher deserialize(JsonElement jsonElement, Type par2Type, JsonDeserializationContext context) {
         JsonObject jsonobject = JsonUtils.asJsonObject(jsonElement, "generationInfo");
         String expression;
         if (jsonobject.has("biome")) {
            expression = JsonUtils.getString(jsonobject, "biome");
            if (expression.startsWith("Type:")) {
               expression = "$" + expression.substring(5).replaceAll(",", Matcher.quoteReplacement(" & $"));
            }
         } else {
            expression = JsonUtils.getString(jsonobject, "biomes");
         }

         Double weight = jsonobject.has("weight") ? JsonUtils.getDouble(jsonobject, "weight") : null;
         return new WeightedBiomeMatcher(expression, weight);
      }

      public JsonElement serialize(WeightedBiomeMatcher generationInfo, Type par2Type, JsonSerializationContext context) {
         JsonObject jsonobject = new JsonObject();
         jsonobject.addProperty("biomes", generationInfo.getBiomeExpression().getExpression());
         if (generationInfo.generationWeight != null) {
            jsonobject.addProperty("weight", generationInfo.generationWeight);
         }

         return jsonobject;
      }
   }
}
