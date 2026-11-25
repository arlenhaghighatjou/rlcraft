package ivorius.reccomplex.world.gen.feature.structure.generic;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;
import ivorius.reccomplex.json.JsonUtils;
import ivorius.reccomplex.utils.algebra.ExpressionCache;
import ivorius.reccomplex.utils.expression.EnvironmentExpression;
import ivorius.reccomplex.world.gen.feature.structure.Environment;
import ivorius.reccomplex.world.gen.feature.structure.VariableDomain;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

public class GenericVariableDomain {
   @SerializedName("variables")
   protected final List<GenericVariableDomain.Variable> variables = new ArrayList();

   public GenericVariableDomain() {
   }

   public List<GenericVariableDomain.Variable> variables() {
      return this.variables;
   }

   public void fill(VariableDomain domain, Environment environment, Random random) {
      for(GenericVariableDomain.Variable variable : this.variables) {
         if (environment.variables.isSet(variable.id)) {
            domain.set(variable.id, environment.variables.get(variable.id));
            return;
         }

         if (domain.isSet(variable.id)) {
            environment.variables.set(variable.id, domain.get(variable.id));
            return;
         }

         boolean value = random.nextFloat() < variable.chance && variable.condition.test(environment);
         domain.set(variable.id, value);
         environment.variables.set(variable.id, value);
      }

   }

   public float chance(Environment environment) {
      float chance = 1.0F;

      for(GenericVariableDomain.Variable variable : this.variables) {
         chance *= this.chance(variable, environment.variables.get(variable.id), environment);
      }

      return chance;
   }

   public float chance(GenericVariableDomain.Variable variable, boolean value, Environment environment) {
      return variable.condition.test(environment) ? (value ? variable.chance : 1.0F - variable.chance) : (float)(value ? 0 : 1);
   }

   public Stream<VariableDomain> omega(Environment environment, boolean logical) {
      Stream<VariableDomain> stream = Stream.of(environment.variables.copy());

      for(GenericVariableDomain.Variable variable : this.variables) {
         if ((!logical || variable.affectsLogic) && !environment.variables.isSet(variable.id)) {
            stream = stream.flatMap((d) -> {
               VariableDomain cached = environment.variables;
               environment.variables = d;
               boolean top = variable.chance > 0.0F && variable.condition.test(environment);
               boolean bottom = variable.chance < 1.0F;
               environment.variables = cached;
               return top && bottom ? d.split(variable.id) : Stream.of(d.set(variable.id, top));
            });
         }
      }

      return stream;
   }

   public static class Variable {
      public String id = "";
      public EnvironmentExpression condition = (EnvironmentExpression)ExpressionCache.of(new EnvironmentExpression(), "");
      public float chance = 0.5F;
      public boolean affectsLogic = false;

      public Variable() {
      }

      public GenericVariableDomain.Variable copy() {
         GenericVariableDomain.Variable var = new GenericVariableDomain.Variable();
         var.id = this.id;
         var.condition.setExpression(this.condition.getExpression());
         var.chance = this.chance;
         var.affectsLogic = this.affectsLogic;
         return var;
      }

      public static class Serializer implements JsonSerializer<GenericVariableDomain.Variable>, JsonDeserializer<GenericVariableDomain.Variable> {
         public Serializer() {
         }

         public GenericVariableDomain.Variable deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = JsonUtils.asJsonObject(json, "Generic Variable");
            GenericVariableDomain.Variable variable = new GenericVariableDomain.Variable();
            variable.id = JsonUtils.getString(jsonObject, "id");
            variable.condition.setExpression(JsonUtils.getString(jsonObject, "condition", ""));
            variable.chance = JsonUtils.getFloat(jsonObject, "chance");
            variable.affectsLogic = JsonUtils.getBoolean(jsonObject, "affectsLogic");
            return variable;
         }

         public JsonElement serialize(GenericVariableDomain.Variable src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", src.id);
            jsonObject.addProperty("condition", src.condition.getExpression());
            jsonObject.addProperty("chance", Float.valueOf(src.chance));
            jsonObject.addProperty("affectsLogic", Boolean.valueOf(src.affectsLogic));
            return jsonObject;
         }
      }
   }
}
