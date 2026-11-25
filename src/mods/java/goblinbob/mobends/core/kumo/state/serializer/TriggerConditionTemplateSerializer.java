package goblinbob.mobends.core.kumo.state.serializer;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import goblinbob.mobends.core.kumo.KumoSerializer;
import goblinbob.mobends.core.kumo.state.condition.TriggerConditionRegistry;
import goblinbob.mobends.core.kumo.state.template.TriggerConditionTemplate;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class TriggerConditionTemplateSerializer implements JsonSerializer<TriggerConditionTemplate>, JsonDeserializer<TriggerConditionTemplate> {
   private final Map<JsonElement, Type> toDeserialize = new HashMap();

   public TriggerConditionTemplateSerializer() {
   }

   public JsonElement serialize(TriggerConditionTemplate src, Type typeOfSrc, JsonSerializationContext context) {
      return KumoSerializer.INSTANCE.gson.toJsonTree(src);
   }

   public TriggerConditionTemplate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      Gson gson = new Gson();
      TriggerConditionTemplate abstractTriggerCondition = (TriggerConditionTemplate)gson.fromJson(json, TriggerConditionTemplate.class);
      String typeName = abstractTriggerCondition.getType();
      if (typeName == null) {
         return null;
      } else {
         Type templateType = TriggerConditionRegistry.instance.getTemplateClass(typeName);
         if (templateType == null) {
            return null;
         } else {
            return templateType.equals(typeOfT) ? abstractTriggerCondition : (TriggerConditionTemplate)KumoSerializer.INSTANCE.gson.fromJson(json, templateType);
         }
      }
   }
}
