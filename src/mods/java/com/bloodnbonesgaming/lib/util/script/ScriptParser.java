package com.bloodnbonesgaming.lib.util.script;

import com.bloodnbonesgaming.lib.BNBGamingLib;
import com.bloodnbonesgaming.lib.util.data.ItemBlockData;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.fml.common.Loader;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class ScriptParser implements ScriptTarget {
   private Object mainObj;
   private final Map<String, Object> localVariables = new HashMap();
   private final List<String> script;
   private final String fileName;
   private final Map<String, ScriptConstructor> classKeywords;
   private static final Object[] NO_ARGS = new Object[0];
   private boolean canceled = false;

   public ScriptParser(List<String> script, String fileName, Object mainObj, Map<String, ScriptConstructor> classKeywords, Map<String, Object> localVariables) {
      this.mainObj = mainObj;
      this.script = script;
      this.fileName = fileName;
      this.classKeywords = classKeywords;
      if (localVariables != null) {
         this.localVariables.putAll(localVariables);
      }

      this.localVariables.put("Parser", this);
   }

   public void setLocalVariable(String name, Object variable) {
      this.localVariables.put(name, variable);
   }

   public boolean hasLocalVariable(String name) {
      return this.localVariables.containsKey(name);
   }

   public Object getLocalVariable(String name) {
      return this.localVariables.get(name);
   }

   public ScriptConstructor getClassForKeyword(String keyword) {
      ScriptConstructor constructor = this.classKeywords != null ? this.classKeywords.get(keyword) : null;
      return constructor != null ? constructor : ScriptUtil.getClassForKeyword(keyword);
   }

   public Object call(String method, Object[] args) throws Exception {
      if (method.equals("addRequiredMod") && args.length == 1 && args[0] instanceof String) {
         this.addRequiredMod((String)args[0]);
         return null;
      }

      return NO_METHOD;
   }

   public boolean parseScript() {
      for(int lineNumber = 0, size = this.script.size(); lineNumber < size; ++lineNumber) {
         String line = this.script.get(lineNumber);
         if (this.canceled) {
            break;
         }

         if (!line.isEmpty() && !line.startsWith("#") && !line.startsWith("//")) {
            try {
               if (!this.checkLocalVariable(line) && !this.checkMethod(line)) {
                  throw new Exception("Not a local variable or method call.");
               }
            } catch (Throwable var4) {
               BNBGamingLib.libInstance.getLog().error("Error reading script in file " + this.fileName + " on line " + (lineNumber + 1) + (var4.getMessage() != null ? ": " + var4.getMessage() : ": See stack trace below."));
               if (var4.getMessage() == null) {
                  var4.printStackTrace();
               }
            }
         }
      }

      return this.canceled;
   }

   public boolean checkMethod(String line) throws Throwable {
      if (line.contains("(")) {
         this.handleMethod(line);
         return true;
      } else {
         return false;
      }
   }

   public Object handleMethod(String line) throws Throwable {
      Object reference = this.getMethodCallObject(line);
      if (reference == ScriptTarget.WRONG_SIDE) {
         return ScriptTarget.WRONG_SIDE;
      } else {
         String method;
         if (reference != this.mainObj) {
            method = this.splitMethodCall(line);
         } else {
            method = line;
         }

         return this.splitMethodNameAndBody(method, reference);
      }
   }

   public Object getMethodCallObject(String line) throws Exception {
      Object reference;
      if (line.contains(".") && line.indexOf(".") < line.indexOf("(")) {
         String[] split = line.split("\\.", 2);
         if (!this.hasLocalVariable(split[0])) {
            throw new Exception("Referenced variable " + split[0] + " cannot be found.");
         }

         reference = this.getLocalVariable(split[0]);
      } else {
         reference = this.mainObj;
      }

      return reference;
   }

   public String splitMethodCall(String line) throws Exception {
      String string;
      if (line.contains(".") && line.indexOf(".") < line.indexOf("(")) {
         String[] split = line.split("\\.", 2);
         if (!this.hasLocalVariable(split[0])) {
            throw new Exception("Referenced variable " + split[0] + " cannot be found.");
         }

         string = split[1];
      } else {
         string = line;
      }

      return string;
   }

   public Object splitMethodNameAndBody(String line, Object reference) throws Throwable {
      int start = line.indexOf("(");
      int end = StringUtils.lastIndexOf(line, ")");
      String[] split = new String[]{line.substring(0, start), line.substring(start + 1, end)};
      return split.length > 1 ? this.findAndCallMethod(reference, split[0], split[1]) : this.findAndCallMethod(reference, split[0], (String)null);
   }

   public String[] trim(String[] array) {
      String[] trimmed = new String[array.length];

      for(int i = 0; i < array.length; ++i) {
         trimmed[i] = array[i].trim();
      }

      return trimmed;
   }

   private String[] splitArguments(String body) {
      body = body.trim();
      if (body.isEmpty()) {
         return new String[0];
      } else if (!body.contains(",")) {
         return new String[]{body};
      } else {
         List<String> args = new ArrayList();
         int nestCurly = 0;
         int nestSquare = 0;
         int nestAngle = 0;
         int lastArg = -1;

         for(int i = 0; i < body.length(); ++i) {
            if (i == body.length() - 1) {
               args.add(body.substring(lastArg + 1, i + 1));
               break;
            }

            String current = Character.toString(body.charAt(i));
            if (current.equalsIgnoreCase("{")) {
               ++nestCurly;
            } else if (current.equalsIgnoreCase("}")) {
               --nestCurly;
            } else if (nestCurly <= 0) {
               if (current.equalsIgnoreCase("[")) {
                  ++nestSquare;
               } else if (current.equalsIgnoreCase("]")) {
                  --nestSquare;
               } else if (nestSquare <= 0) {
                  if (current.equalsIgnoreCase("<")) {
                     ++nestAngle;
                  } else if (current.equalsIgnoreCase(">")) {
                     --nestAngle;
                  } else if (nestAngle <= 0 && current.equalsIgnoreCase(",") && StringUtils.countMatches(body.substring(i), "\"") % 2 != 1) {
                     args.add(body.substring(lastArg + 1, i));
                     lastArg = i;
                  }
               }
            }
         }

         String[] argArray = new String[args.size()];

         for(int i = 0; i < argArray.length; ++i) {
            argArray[i] = (String)args.get(i);
         }

         return argArray;
      }
   }

   public Object findAndCallMethod(Object reference, String methodName, String methodBody) throws Throwable {
      if (!(reference instanceof ScriptTarget)) {
         throw new Exception("No method of name " + methodName + " could be found.");
      }

      Object[] arguments = methodBody == null ? NO_ARGS : this.parseArguments(this.trim(this.splitArguments(methodBody)));
      Object result = ((ScriptTarget)reference).call(methodName, arguments);
      if (result == ScriptTarget.NO_METHOD) {
         throw new Exception("No method of name " + methodName + " found with the correct parameters.");
      }

      return result;
   }

   public Object findAndCallConstructor(ScriptConstructor constructor, String methodBody) throws Exception {
      Object[] arguments = methodBody != null && !methodBody.isEmpty() ? this.parseArguments(this.trim(this.splitArguments(methodBody))) : NO_ARGS;
      Object result = constructor.create(arguments);
      if (result == ScriptTarget.NO_METHOD) {
         throw new Exception("No constructor found with the correct parameters.");
      }

      return result;
   }

   public Object[] parseArguments(String[] arguments) throws Exception {
      arguments = this.trim(arguments);
      Object[] args = new Object[arguments.length];

      for(int i = 0; i < arguments.length; ++i) {
         if (arguments[i].equalsIgnoreCase("null")) {
            args[i] = null;
         } else if (this.hasLocalVariable(arguments[i])) {
            args[i] = this.getLocalVariable(arguments[i]);
         } else {
            String string = this.parseToString(arguments[i]);
            if (string != null) {
               args[i] = string;
            } else {
               Boolean bool = this.parseToBoolean(arguments[i]);
               if (bool != null) {
                  args[i] = bool;
               } else {
                  BigDecimal numArg = this.parseToBigDecimal(arguments[i]);
                  if (numArg != null) {
                     args[i] = numArg;
                  } else {
                     int[] intArray = this.parseToIntArray(arguments[i]);
                     if (intArray != null) {
                        args[i] = intArray;
                     } else {
                        float[] floatArray = this.parseToFloatArray(arguments[i]);
                        if (floatArray != null) {
                           args[i] = floatArray;
                        } else {
                           String[] stringArray = this.parseToStringArray(arguments[i]);
                           if (stringArray != null) {
                              args[i] = stringArray;
                           } else {
                              ItemBlockData itemBlockData = this.parseToItemBlock(arguments[i]);
                              if (itemBlockData != null) {
                                 args[i] = itemBlockData;
                              } else {
                                 MinMaxBounds bounds = this.parseToMinMaxBounds(arguments[i]);
                                 if (bounds == null) {
                                    throw new Exception("Argument " + (i + 1) + " could not be cast to anything.");
                                 }

                                 args[i] = bounds;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return args;
   }

   private String parseToString(String arg) {
      if (arg.startsWith("\"") && arg.endsWith("\"")) {
         return arg.substring(1, arg.length() - 1);
      } else {
         return arg.startsWith("{") && arg.endsWith("}") ? arg : null;
      }
   }

   private BigDecimal parseToBigDecimal(String arg) {
      try {
         if (arg.startsWith("0x")) {
            return new BigDecimal(Integer.parseUnsignedInt(arg.substring(2), 16));
         } else {
            return arg.startsWith("#") ? new BigDecimal(Integer.parseUnsignedInt(arg.substring(1), 16)) : new BigDecimal(arg);
         }
      } catch (NumberFormatException var3) {
         return null;
      }
   }

   private Boolean parseToBoolean(String arg) {
      if (arg.equalsIgnoreCase("true")) {
         return true;
      } else {
         return arg.equalsIgnoreCase("false") ? false : null;
      }
   }

   private int[] parseToIntArray(String arg) {
      if (arg.startsWith("[") && arg.endsWith("]")) {
         String array = new String(arg);
         array = StringUtils.removeStart(array, "[");
         array = StringUtils.removeEnd(array, "]");
         String[] sections = this.splitArguments(array);
         sections = this.trim(sections);
         int[] ints = new int[sections.length];

         for(int ii = 0; ii < sections.length; ++ii) {
            String section = sections[ii];

            try {
               ints[ii] = Integer.parseInt(section);
            } catch (NumberFormatException var8) {
               return null;
            }
         }

         return ints;
      } else {
         return null;
      }
   }

   private float[] parseToFloatArray(String arg) {
      if (arg.startsWith("[") && arg.endsWith("]")) {
         String array = new String(arg);
         array = StringUtils.removeStart(array, "[");
         array = StringUtils.removeEnd(array, "]");
         String[] sections = this.splitArguments(array);
         sections = this.trim(sections);
         float[] floats = new float[sections.length];

         for(int ii = 0; ii < sections.length; ++ii) {
            String section = sections[ii];

            try {
               floats[ii] = Float.parseFloat(section);
            } catch (NumberFormatException var8) {
               return null;
            }
         }

         return floats;
      } else {
         return null;
      }
   }

   private String[] parseToStringArray(String arg) {
      if (arg.startsWith("[") && arg.endsWith("]")) {
         String array = new String(arg);
         array = StringUtils.removeStart(array, "[");
         array = StringUtils.removeEnd(array, "]");
         String[] sections = this.splitArguments(array);
         sections = this.trim(sections);
         String[] strings = new String[sections.length];

         for(int i = 0; i < sections.length; ++i) {
            if (!sections[i].startsWith("\"") || !sections[i].endsWith("\"")) {
               return null;
            }

            strings[i] = sections[i].substring(1, sections[i].length() - 1);
         }

         return strings;
      } else {
         return null;
      }
   }

   private ItemBlockData parseToItemBlock(String arg) throws Exception {
      if (arg.startsWith("<") && arg.endsWith(">")) {
         ItemBlockData data = new ItemBlockData();
         String[] trimmed = this.trim(this.splitArguments(arg.substring(1, arg.length() - 1)));

         for(int i = 0; i < trimmed.length; ++i) {
            if (trimmed[i].startsWith("ore:")) {
               data.setOre(trimmed[i].substring(4));
            } else if (trimmed[i].startsWith("state:")) {
               data.setStateJson(trimmed[i].substring(6));
            } else if (trimmed[i].startsWith("meta:")) {
               data.setMeta(this.parseToMinMaxBounds(trimmed[i].substring(5)));
            } else if (trimmed[i].startsWith("count:")) {
               data.setCount(this.parseToMinMaxBounds(trimmed[i].substring(6)));
            } else if (trimmed[i].startsWith("durability:")) {
               data.setCount(this.parseToMinMaxBounds(trimmed[i].substring(11)));
            } else if (trimmed[i].startsWith("enchant:")) {
               String sub = trimmed[i].substring(8);
               if (sub.startsWith("[") && sub.endsWith("]")) {
                  String[] splitArray = this.trim(this.splitArguments(sub.substring(1, sub.length() - 1)));

                  for(String split : splitArray) {
                     data.addEnchantment(this.parseToEnchantPredicate(split));
                  }
               } else {
                  data.addEnchantment(this.parseToEnchantPredicate(sub));
               }
            } else if (trimmed[i].startsWith("potion:")) {
               data.setPotion(trimmed[i].substring(7));
            } else if (trimmed[i].startsWith("nbt:")) {
               data.setNBT(trimmed[i].substring(4));
            } else if (trimmed[i].startsWith("state:")) {
               data.setStateJson(trimmed[i].substring(6));
            } else if (trimmed[i].startsWith("tile:")) {
               data.setTileNBT(trimmed[i].substring(5));
            } else {
               String[] split = trimmed[i].split(":");
               if (split.length > 2) {
                  data.setLocation(split[0] + ":" + split[1]);
                  data.setMeta(this.parseToMinMaxBounds(split[2]));
               } else {
                  data.setLocation(trimmed[i]);
               }
            }
         }

         return data;
      } else {
         return null;
      }
   }

   private EnchantmentPredicate parseToEnchantPredicate(String arg) throws Exception {
      Enchantment enchant = null;
      MinMaxBounds levels = MinMaxBounds.UNBOUNDED;
      if (Character.isAlphabetic(arg.charAt(0))) {
         String[] split = this.trim(arg.split(":", 3));
         MinMaxBounds bounds = this.parseToMinMaxBounds(split[split.length - 1]);
         if (bounds != null) {
            if (split.length > 2) {
               String location = split[0] + ":" + split[1];
               enchant = Enchantment.getEnchantmentByLocation(location);
               if (enchant == null) {
                  throw new Exception(location + " is not a valid enchantment id!");
               }
            } else {
               String location = split[0];
               enchant = Enchantment.getEnchantmentByLocation(location);
               if (enchant == null) {
                  throw new Exception(location + " is not a valid enchantment id!");
               }
            }

            return new EnchantmentPredicate(enchant, bounds);
         } else {
            if (split.length > 1) {
               String location = split[0] + ":" + split[1];
               enchant = Enchantment.getEnchantmentByLocation(location);
               if (enchant == null) {
                  throw new Exception(location + " is not a valid enchantment id!");
               }
            } else {
               String location = split[0];
               enchant = Enchantment.getEnchantmentByLocation(location);
               if (enchant == null) {
                  throw new Exception(location + " is not a valid enchantment id!");
               }
            }

            return new EnchantmentPredicate(enchant, levels);
         }
      } else {
         return new EnchantmentPredicate((Enchantment)null, this.parseToMinMaxBounds(arg));
      }
   }

   private MinMaxBounds parseToMinMaxBounds(String arg) throws Exception {
      if (arg.contains("~")) {
         if (arg.startsWith("~")) {
            String max = arg.substring(1).trim();
            return NumberUtils.isCreatable(max) ? new MinMaxBounds((Float)null, NumberUtils.createNumber(max).floatValue()) : null;
         } else if (arg.endsWith("~")) {
            String min = arg.substring(0, arg.length() - 1);
            return NumberUtils.isCreatable(min) ? new MinMaxBounds(NumberUtils.createNumber(min).floatValue(), (Float)null) : null;
         } else {
            String[] split = arg.split("~");
            return NumberUtils.isCreatable(split[0].trim()) && NumberUtils.isCreatable(split[1].trim()) ? new MinMaxBounds(NumberUtils.createNumber(split[0]).floatValue(), NumberUtils.createNumber(split[1]).floatValue()) : null;
         }
      } else {
         return NumberUtils.isCreatable(arg) ? new MinMaxBounds(NumberUtils.createNumber(arg).floatValue(), NumberUtils.createNumber(arg).floatValue()) : null;
      }
   }

   public boolean checkLocalVariable(String line) throws Throwable {
      if (line.contains("=")) {
         String[] split = line.split("=");
         String trimName = split[0].trim();
         String trimMethod = split[1].trim();
         if (!trimMethod.startsWith("new")) {
            Object reference = this.handleMethod(trimMethod);
            if (reference != null) {
               this.setLocalVariable(trimName, reference);
               return true;
            } else {
               throw new Exception("Reference Null");
            }
         } else {
            trimMethod = trimMethod.replace("new ", "").trim();
            int start = trimMethod.indexOf("(");
            int end = StringUtils.lastIndexOf(trimMethod, ")");
            String[] methodSplit = new String[]{trimMethod.substring(0, start), trimMethod.substring(start + 1, end)};
            ScriptConstructor clazz = this.getClassForKeyword(methodSplit[0]);
            if (clazz != null) {
               Object returnObj;
               if (methodSplit.length == 1) {
                  returnObj = this.findAndCallConstructor(clazz, "");
               } else {
                  methodSplit[1] = methodSplit[1].trim();
                  returnObj = this.findAndCallConstructor(clazz, methodSplit[1]);
               }

               if (returnObj != null) {
                  this.setLocalVariable(trimName, returnObj);
                  return true;
               } else {
                  throw new Exception("Reference Null");
               }
            } else {
               throw new Exception("No class keyword found for '" + methodSplit[0] + "'");
            }
         }
      } else {
         return false;
      }
   }

   public void addRequiredMod(String modID) {
      if (!Loader.isModLoaded(modID)) {
         this.canceled = true;
         BNBGamingLib.libInstance.getLog().debug("Canceled reading script in file " + this.fileName + " due to: missing mod `" + modID + "`");
      }

   }
}
