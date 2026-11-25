package goblinbob.mobends.core.supporters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import goblinbob.mobends.core.client.model.IModelPart;
import goblinbob.mobends.standard.data.PlayerData;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public enum BindPoint {
   HEAD("head", (data) -> {
      return data.head;
   }),
   BODY("body", (data) -> {
      return data.body;
   }),
   LEFT_ARM("leftArm", (data) -> {
      return data.leftArm;
   }),
   LEFT_FOREARM("leftForearm", (data) -> {
      return data.leftForeArm;
   }),
   RIGHT_ARM("rightArm", (data) -> {
      return data.rightArm;
   }),
   RIGHT_FOREARM("rightForearm", (data) -> {
      return data.rightForeArm;
   }),
   LEFT_THIGH("leftThigh", (data) -> {
      return data.leftLeg;
   }),
   LEFT_SHIN("leftShin", (data) -> {
      return data.leftForeLeg;
   }),
   RIGHT_THIGH("rightThigh", (data) -> {
      return data.rightLeg;
   }),
   RIGHT_SHIN("rightShin", (data) -> {
      return data.rightForeLeg;
   });

   private static Map<String, BindPoint> KEY_TO_VALUE = new HashMap();
   private String key;
   private Function<PlayerData, IModelPart> partSelector;

   private BindPoint(String key, Function<PlayerData, IModelPart> partSelector) {
      this.key = key;
      this.partSelector = partSelector;
   }

   public String getKey() {
      return this.key;
   }

   public Function<PlayerData, IModelPart> getPartSelector() {
      return this.partSelector;
   }

   public static BindPoint fromKey(String key) {
      return (BindPoint)KEY_TO_VALUE.get(key);
   }

   static {
      for(BindPoint p : values()) {
         KEY_TO_VALUE.put(p.getKey(), p);
      }

   }

   public static class Adapter extends TypeAdapter<BindPoint> {
      public Adapter() {
      }

      public BindPoint read(JsonReader in) throws IOException {
         String key = in.nextString();
         return BindPoint.fromKey(key);
      }

      public void write(JsonWriter out, BindPoint value) throws IOException {
         out.value(value.getKey());
      }
   }
}
