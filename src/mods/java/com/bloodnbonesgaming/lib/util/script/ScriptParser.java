package com.bloodnbonesgaming.lib.util.script;

import com.bloodnbonesgaming.lib.BNBGamingLib;
import com.bloodnbonesgaming.lib.util.data.ItemBlockData;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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

public class ScriptParser {
   private Object mainObj;
   private final Map<String, Object> localVariables = new HashMap();
   private final List<String> script;
   private final String fileName;
   private final Map<String, Class> classKeywords;
   private static final Object WRONG_SIDE = new Object();
   private boolean canceled = false;

   public ScriptParser(List<String> script, String fileName, Object mainObj, Map<String, Class> classKeywords, Map<String, Object> localVariables) {
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

   public Class<?> getClassForKeyword(String keyword) {
      return this.classKeywords != null && this.classKeywords.containsKey(keyword) ? (Class)this.classKeywords.get(keyword) : ScriptUtil.getClassForKeyword(keyword);
   }

   public boolean parseScript() {
      for(String line : this.script) {
         if (this.canceled) {
            break;
         }

         if (!line.isEmpty() && !line.startsWith("#") && !line.startsWith("//")) {
            try {
               if (!this.checkLocalVariable(line) && !this.checkMethod(line)) {
                  throw new Exception("Not a local variable or method call.");
               }
            } catch (Throwable var4) {
               BNBGamingLib.libInstance.getLog().error("Error reading script in file " + this.fileName + " on line " + (this.script.indexOf(line) + 1) + (var4.getMessage() != null ? ": " + var4.getMessage() : ": See stack trace below."));
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
      if (reference == WRONG_SIDE) {
         return WRONG_SIDE;
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
      List<Method> possibleMethods = new ArrayList();

      for(Method method : reference.getClass().getMethods()) {
         if (method.getName().equals(methodName)) {
            possibleMethods.add(method);
         }
      }

      if (possibleMethods.isEmpty()) {
         throw new Exception("No method of name " + methodName + " could be found.");
      } else if (methodBody == null) {
         for(Method method : possibleMethods) {
            if (method.getParameterCount() == 0) {
               try {
                  if (!BNBGamingLib.proxy.client() && method.isAnnotationPresent(ClientOnly.class)) {
                     return WRONG_SIDE;
                  }

                  return method.invoke(reference, (Object[])null);
               } catch (Exception var14) {
                  if (var14 instanceof InvocationTargetException) {
                     throw new Exception(((InvocationTargetException)var14).getCause().getLocalizedMessage());
                  }

                  throw new Exception(var14.getLocalizedMessage());
               }
            }
         }

         throw new Exception("No method of name " + methodName + " found with the correct number of arguments.");
      } else {
         String[] trimmed = this.trim(this.splitArguments(methodBody));
         Object[] arguments = this.parseArguments(trimmed);
         Method genericMethod = null;

         label227:
         for(Method method : possibleMethods) {
            if (method.getParameterCount() == 1) {
               Class<?> type = method.getParameterTypes()[0];
               if (type.isArray() && type.getComponentType() == Object.class) {
                  genericMethod = method;
                  continue;
               }
            }

            if (method.getParameterCount() == arguments.length) {
               if (method.isAnnotationPresent(ScriptArgs.class)) {
                  ScriptArgs anno = (ScriptArgs)method.getAnnotation(ScriptArgs.class);
                  ArgType[] argTypes = anno.args();
                  Object[] args = ArgType.transformArgs(arguments, argTypes);
                  if (args != null) {
                     try {
                        if (!BNBGamingLib.proxy.client() && method.isAnnotationPresent(ClientOnly.class)) {
                           return WRONG_SIDE;
                        }

                        return method.invoke(reference, args);
                     } catch (Exception var17) {
                        if (var17 instanceof InvocationTargetException) {
                           throw new Exception(((InvocationTargetException)var17).getCause().getLocalizedMessage());
                        }

                        throw new Exception(var17.getLocalizedMessage());
                     }
                  }

                  BNBGamingLib.libInstance.getLog().info("Args could not be cast using annotation");
               } else {
                  Parameter[] parameters = method.getParameters();
                  Object[] castArgs = new Object[arguments.length];
                  System.arraycopy(arguments, 0, castArgs, 0, arguments.length);

                  for(int i = 0; i < arguments.length; ++i) {
                     if (arguments[i] == null) {
                        if (parameters[i].getType().isPrimitive()) {
                           continue label227;
                        }
                     } else {
                        Class<?> type = parameters[i].getType();
                        if (type.isPrimitive()) {
                           if (type == Boolean.TYPE && arguments[i].getClass() != Boolean.class) {
                              continue label227;
                           }

                           if (type == Byte.TYPE) {
                              if (arguments[i].getClass() != BigDecimal.class) {
                                 continue label227;
                              }

                              castArgs[i] = ((BigDecimal)arguments[i]).byteValueExact();
                           } else if (type == Short.TYPE) {
                              if (arguments[i].getClass() != BigDecimal.class) {
                                 continue label227;
                              }

                              castArgs[i] = ((BigDecimal)arguments[i]).shortValueExact();
                           } else if (type == Integer.TYPE) {
                              if (arguments[i].getClass() != BigDecimal.class) {
                                 continue label227;
                              }

                              castArgs[i] = ((BigDecimal)arguments[i]).intValueExact();
                           } else if (type == Double.TYPE) {
                              if (arguments[i].getClass() != BigDecimal.class) {
                                 continue label227;
                              }

                              castArgs[i] = ((BigDecimal)arguments[i]).doubleValue();
                           } else if (type == Float.TYPE) {
                              if (arguments[i].getClass() != BigDecimal.class) {
                                 continue label227;
                              }

                              castArgs[i] = ((BigDecimal)arguments[i]).floatValue();
                           } else if (type == Long.TYPE) {
                              if (arguments[i].getClass() != BigDecimal.class) {
                                 continue label227;
                              }

                              castArgs[i] = ((BigDecimal)arguments[i]).longValueExact();
                           }
                        } else if (type == Byte.class) {
                           if (arguments[i].getClass() != BigDecimal.class) {
                              continue label227;
                           }

                           castArgs[i] = ((BigDecimal)arguments[i]).byteValueExact();
                        } else if (type == Short.class) {
                           if (arguments[i].getClass() != BigDecimal.class) {
                              continue label227;
                           }

                           castArgs[i] = ((BigDecimal)arguments[i]).shortValueExact();
                        } else if (type == Integer.class) {
                           if (arguments[i].getClass() != BigDecimal.class) {
                              continue label227;
                           }

                           castArgs[i] = ((BigDecimal)arguments[i]).intValueExact();
                        } else if (type == Double.class) {
                           if (arguments[i].getClass() != BigDecimal.class) {
                              continue label227;
                           }

                           castArgs[i] = ((BigDecimal)arguments[i]).doubleValue();
                        } else if (type == Float.class) {
                           if (arguments[i].getClass() != BigDecimal.class) {
                              continue label227;
                           }

                           castArgs[i] = ((BigDecimal)arguments[i]).floatValue();
                        } else if (type == Long.class) {
                           if (arguments[i].getClass() != BigDecimal.class) {
                              continue label227;
                           }

                           castArgs[i] = ((BigDecimal)arguments[i]).longValueExact();
                        } else if (!type.isAssignableFrom(arguments[i].getClass())) {
                           continue label227;
                        }
                     }
                  }

                  try {
                     if (!BNBGamingLib.proxy.client() && method.isAnnotationPresent(ClientOnly.class)) {
                        return WRONG_SIDE;
                     }

                     return method.invoke(reference, castArgs);
                  } catch (Exception var16) {
                     if (var16 instanceof InvocationTargetException) {
                        throw ((InvocationTargetException)var16).getCause();
                     }

                     throw new Exception(var16.getLocalizedMessage());
                  }
               }
            }
         }

         if (genericMethod != null) {
            try {
               if (!BNBGamingLib.proxy.client() && genericMethod.isAnnotationPresent(ClientOnly.class)) {
                  return WRONG_SIDE;
               } else {
                  return genericMethod.invoke(reference, (Object)arguments);
               }
            } catch (Exception var15) {
               if (var15 instanceof InvocationTargetException) {
                  throw new Exception(((InvocationTargetException)var15).getCause().getLocalizedMessage());
               } else {
                  throw new Exception(var15.getLocalizedMessage());
               }
            }
         } else {
            throw new Exception("No method of name " + methodName + " found with the correct parameters.");
         }
      }
   }

   public Object findAndCallConstructor(Class<?> clazz, String methodBody) throws Exception {
      if (methodBody != null && !methodBody.isEmpty()) {
         String[] trimmed = this.trim(this.splitArguments(methodBody));
         Object[] arguments = this.parseArguments(trimmed);
         Constructor<?> genericConstructor = null;

         label179:
         for(Constructor<?> constructor : clazz.getConstructors()) {
            if (constructor.getParameterCount() == 1) {
               Class<?> type = constructor.getParameterTypes()[0];
               if (type.isArray() && type.getComponentType() == Object.class) {
                  genericConstructor = constructor;
                  continue;
               }
            }

            if (constructor.getParameterCount() == arguments.length) {
               if (constructor.isAnnotationPresent(ScriptArgs.class)) {
                  ScriptArgs anno = (ScriptArgs)constructor.getAnnotation(ScriptArgs.class);
                  ArgType[] argTypes = anno.args();
                  Object[] args = ArgType.transformArgs(arguments, argTypes);
                  if (args != null) {
                     try {
                        if (!BNBGamingLib.proxy.client() && constructor.isAnnotationPresent(ClientOnly.class)) {
                           return WRONG_SIDE;
                        }

                        return constructor.newInstance(args);
                     } catch (Exception var17) {
                        if (var17 instanceof InvocationTargetException) {
                           throw new Exception(((InvocationTargetException)var17).getCause().getLocalizedMessage());
                        }

                        throw new Exception(var17.getLocalizedMessage());
                     }
                  }

                  BNBGamingLib.libInstance.getLog().info("Args could not be cast using annotation");
               } else {
                  Parameter[] parameters = constructor.getParameters();
                  Object[] castArgs = new Object[arguments.length];
                  System.arraycopy(arguments, 0, castArgs, 0, arguments.length);

                  for(int i = 0; i < parameters.length; ++i) {
                     if (arguments[i] == null) {
                        if (parameters[i].getType().isPrimitive()) {
                           continue label179;
                        }
                     } else {
                        Class<?> type = parameters[i].getType();
                        if (type.isPrimitive()) {
                           if (type == Boolean.TYPE && arguments[i].getClass() != Boolean.class) {
                              continue label179;
                           }

                           if (type == Byte.TYPE) {
                              if (arguments[i].getClass() != BigDecimal.class) {
                                 continue label179;
                              }

                              castArgs[i] = ((BigDecimal)arguments[i]).byteValueExact();
                           } else if (type == Short.TYPE) {
                              if (arguments[i].getClass() != BigDecimal.class) {
                                 continue label179;
                              }

                              castArgs[i] = ((BigDecimal)arguments[i]).shortValueExact();
                           } else if (type == Integer.TYPE) {
                              if (arguments[i].getClass() != BigDecimal.class) {
                                 continue label179;
                              }

                              castArgs[i] = ((BigDecimal)arguments[i]).intValueExact();
                           } else if (type == Double.TYPE) {
                              if (arguments[i].getClass() != BigDecimal.class) {
                                 continue label179;
                              }

                              castArgs[i] = ((BigDecimal)arguments[i]).doubleValue();
                           } else if (type == Float.TYPE) {
                              if (arguments[i].getClass() != BigDecimal.class) {
                                 continue label179;
                              }

                              castArgs[i] = ((BigDecimal)arguments[i]).floatValue();
                           } else if (type == Long.TYPE) {
                              if (arguments[i].getClass() != BigDecimal.class) {
                                 continue label179;
                              }

                              castArgs[i] = ((BigDecimal)arguments[i]).longValueExact();
                           }
                        } else if (!type.isAssignableFrom(arguments[i].getClass())) {
                           continue label179;
                        }
                     }
                  }

                  try {
                     if (!BNBGamingLib.proxy.client() && constructor.isAnnotationPresent(ClientOnly.class)) {
                        return WRONG_SIDE;
                     }

                     return constructor.newInstance(castArgs);
                  } catch (Exception var16) {
                     if (var16 instanceof InvocationTargetException) {
                        throw new Exception("Failed to create new instance of constructor: " + ((InvocationTargetException)var16).getCause().getLocalizedMessage());
                     }

                     throw new Exception("Failed to create new instance of constructor: " + var16.getLocalizedMessage());
                  }
               }
            }
         }

         if (genericConstructor != null) {
            try {
               if (!BNBGamingLib.proxy.client() && genericConstructor.isAnnotationPresent(ClientOnly.class)) {
                  return WRONG_SIDE;
               } else {
                  return genericConstructor.newInstance((Object)arguments);
               }
            } catch (Exception var15) {
               if (var15 instanceof InvocationTargetException) {
                  throw new Exception(((InvocationTargetException)var15).getCause().getLocalizedMessage());
               } else {
                  throw new Exception(var15.getLocalizedMessage());
               }
            }
         } else {
            throw new Exception("No constructor found with the correct parameters.");
         }
      } else {
         for(Constructor<?> constructor : clazz.getConstructors()) {
            if (constructor.getParameterCount() == 0) {
               try {
                  if (!BNBGamingLib.proxy.client() && constructor.isAnnotationPresent(ClientOnly.class)) {
                     return WRONG_SIDE;
                  }

                  return constructor.newInstance((Object[])null);
               } catch (Exception var14) {
                  if (var14 instanceof InvocationTargetException) {
                     throw new Exception(((InvocationTargetException)var14).getCause().getLocalizedMessage());
                  }

                  throw new Exception(var14.getLocalizedMessage());
               }
            }
         }

         throw new Exception("No constructor found with the correct number of arguments.");
      }
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
            Class<?> clazz = this.getClassForKeyword(methodSplit[0]);
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
