package crafttweaker.socket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.socket.messages.IRequestMessage;
import crafttweaker.socket.messages.LintRequestMessage;
import crafttweaker.socket.messages.LintResponseMessage;
import crafttweaker.socket.messages.SocketMessage;
import io.netty.channel.ChannelHandlerContext;
import java.lang.reflect.Type;
import java.util.HashMap;

public class JsonMessageHandler {
   private static final HashMap<String, Type> TYPE_HASH_MAP = new HashMap();
   private static Gson gson = (new GsonBuilder()).create();
   private static JsonParser jsonParser = new JsonParser();

   public JsonMessageHandler() {
   }

   public static void registerType(String typeName, Type type) {
      TYPE_HASH_MAP.put(typeName, type);
   }

   public static Type getType(String typeName) {
      return (Type)TYPE_HASH_MAP.get(typeName);
   }

   public static String handleJson(String message, ChannelHandlerContext ctx) {
      JsonElement json = jsonParser.parse(message);
      String messageType = json.getAsJsonObject().get("messageType").getAsString();
      Type type = (Type)TYPE_HASH_MAP.get(messageType);
      if (type == null) {
         CraftTweakerAPI.logWarning("Invalid type in json element: " + json);
         return "Invalid Type.";
      } else {
         SocketMessage<?> obj = (SocketMessage)gson.fromJson(json, type);
         if (obj instanceof IRequestMessage) {
            SocketMessage<?> res = ((IRequestMessage)obj).handleReceive(ctx);
            if (res != null) {
               String jsonRes = gson.toJson(res, getType(res.messageType));
               CraftTweakerAPI.logInfo("jres = " + jsonRes);
               return jsonRes;
            } else {
               return "Null response!";
            }
         } else {
            return "INVALID MESSAGE!!!";
         }
      }
   }

   static {
      registerType("LintRequest", LintRequestMessage.class);
      registerType("LintResponse", LintResponseMessage.class);
   }
}
