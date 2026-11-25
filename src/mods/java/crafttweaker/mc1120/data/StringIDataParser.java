package crafttweaker.mc1120.data;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.data.DataBool;
import crafttweaker.api.data.DataByte;
import crafttweaker.api.data.DataDouble;
import crafttweaker.api.data.DataFloat;
import crafttweaker.api.data.DataInt;
import crafttweaker.api.data.DataList;
import crafttweaker.api.data.DataLong;
import crafttweaker.api.data.DataMap;
import crafttweaker.api.data.DataShort;
import crafttweaker.api.data.DataString;
import crafttweaker.api.data.IData;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import stanhebben.zenscript.IZenCompileEnvironment;
import stanhebben.zenscript.ZenTokener;
import stanhebben.zenscript.parser.Token;

public class StringIDataParser {
   public StringIDataParser() {
   }

   public static IData parse(String expression) {
      try {
         ZenTokener zenTokener = new ZenTokener(expression, (IZenCompileEnvironment)null, "", false);
         return parse(zenTokener);
      } catch (IOException var2) {
         var2.printStackTrace();
         return null;
      }
   }

   private static IData parse(ZenTokener tokener) {
      Token next = tokener.next();
      IData base;
      switch(next.getType()) {
      case 2:
         base = new DataLong(Long.parseLong(next.getValue(), 10));
         break;
      case 3:
         base = new DataDouble(Double.parseDouble(next.getValue()));
         break;
      case 4:
         base = new DataString(next.getValue());
         break;
      case 5:
         base = parseMap(tokener);
         break;
      case 7:
         base = parseList(tokener);
         break;
      case 141:
         base = new DataBool(true);
         break;
      case 142:
         base = new DataBool(false);
         break;
      default:
         CraftTweakerAPI.logError("Could not completely resolve Data near " + next.toString());
         return new DataMap(Collections.emptyMap(), true);
      }

      if (tokener.optional(120) != null) {
         Token token = tokener.next();
         switch(token.getType()) {
         case 1:
            return base;
         case 100:
            return new DataBool(base.asBool());
         case 101:
            return new DataByte(base.asByte());
         case 102:
            return new DataShort(base.asShort());
         case 103:
            return new DataInt(base.asInt());
         case 104:
            return new DataLong(base.asLong());
         case 105:
            return new DataFloat(base.asFloat());
         case 107:
            return new DataString(base.asString());
         }
      }

      return base;
   }

   private static IData parseList(ZenTokener tokener) {
      List<IData> result = new ArrayList();

      while(tokener.optional(8) == null) {
         result.add(parse(tokener));
         if (tokener.optional(11) == null) {
            tokener.required(8, "] expected");
            break;
         }
      }

      return new DataList(result, false);
   }

   private static IData parseMap(ZenTokener tokener) {
      Map<String, IData> result = new HashMap();

      while(tokener.optional(6) == null) {
         String key = tokener.required(1, "Identifier expected").getValue();
         tokener.required(29, ": expected");
         result.put(key, parse(tokener));
         if (tokener.optional(11) == null) {
            tokener.required(6, "} expected");
            break;
         }
      }

      return new DataMap(result, false);
   }
}
