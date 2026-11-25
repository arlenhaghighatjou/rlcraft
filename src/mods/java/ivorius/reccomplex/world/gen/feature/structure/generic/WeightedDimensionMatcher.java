package ivorius.reccomplex.world.gen.feature.structure.generic;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ivorius.reccomplex.json.JsonUtils;
import ivorius.reccomplex.utils.algebra.ExpressionCache;
import ivorius.reccomplex.utils.expression.DimensionExpression;
import java.lang.reflect.Type;
import java.util.regex.Matcher;
import net.minecraft.world.WorldProvider;

public class WeightedDimensionMatcher {
   private DimensionExpression dimensionExpression;
   private Double generationWeight;

   public WeightedDimensionMatcher(String expression, Double generationWeight) {
      this.dimensionExpression = (DimensionExpression)ExpressionCache.of(new DimensionExpression(), expression);
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

   public boolean matches(WorldProvider provider) {
      return this.dimensionExpression.test(provider);
   }

   public DimensionExpression getDimensionExpression() {
      return this.dimensionExpression;
   }

   public String getDisplayString() {
      return this.dimensionExpression.getDisplayString((Object)null);
   }

   public static class Serializer implements JsonDeserializer<WeightedDimensionMatcher>, JsonSerializer<WeightedDimensionMatcher> {
      public Serializer() {
      }

      public WeightedDimensionMatcher deserialize(JsonElement jsonElement, Type par2Type, JsonDeserializationContext context) {
         JsonObject jsonobject = JsonUtils.asJsonObject(jsonElement, "generationInfo");
         String expression;
         if (jsonobject.has("dimensionID")) {
            expression = JsonUtils.getString(jsonobject, "dimensionID");
            if (expression.startsWith("Type:")) {
               expression = "$" + expression.substring(5).replaceAll(",", Matcher.quoteReplacement(" & $"));
            }
         } else {
            expression = JsonUtils.getString(jsonobject, "dimensions");
         }

         Double weight = jsonobject.has("weight") ? JsonUtils.getDouble(jsonobject, "weight") : null;
         return new WeightedDimensionMatcher(expression, weight);
      }

      public JsonElement serialize(WeightedDimensionMatcher generationInfo, Type par2Type, JsonSerializationContext context) {
         JsonObject jsonobject = new JsonObject();
         jsonobject.addProperty("dimensions", generationInfo.getDimensionExpression().getExpression());
         if (generationInfo.generationWeight != null) {
            jsonobject.addProperty("weight", generationInfo.generationWeight);
         }

         return jsonobject;
      }
   }
}
