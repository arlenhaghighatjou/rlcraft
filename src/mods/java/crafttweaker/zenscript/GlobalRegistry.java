package crafttweaker.zenscript;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.BracketHandler;
import crafttweaker.runtime.GlobalFunctions;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import stanhebben.zenscript.IZenCompileEnvironment;
import stanhebben.zenscript.IZenErrorLogger;
import stanhebben.zenscript.TypeExpansion;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.TypeRegistry;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.symbols.SymbolJavaStaticField;
import stanhebben.zenscript.symbols.SymbolJavaStaticMethod;
import stanhebben.zenscript.symbols.SymbolPackage;
import stanhebben.zenscript.symbols.SymbolType;
import stanhebben.zenscript.type.ZenTypeIntRange;
import stanhebben.zenscript.type.ZenTypeNative;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.type.natives.JavaMethod;
import stanhebben.zenscript.util.Pair;

public class GlobalRegistry {
   private static final Map<String, IZenSymbol> globals = new HashMap();
   private static final Set<Pair<Integer, IBracketHandler>> bracketHandlers = new TreeSet(Comparator.comparingInt(Pair::getKey).thenComparing((o) -> {
      return ((IBracketHandler)o.getValue()).getClass().getName();
   }));
   private static final TypeRegistry types = new TypeRegistry();
   private static final SymbolPackage root = new SymbolPackage("<root>");
   private static final IZenErrorLogger errors = new CrtStoringErrorLogger();
   private static final IZenCompileEnvironment environment = new CrTCompileEnvironment();
   private static final Map<String, TypeExpansion> expansions = new HashMap();

   private GlobalRegistry() {
   }

   public static void registerGlobal(String name, IZenSymbol symbol) {
      if (globals.containsKey(name)) {
         throw new IllegalArgumentException("symbol already exists: " + name);
      } else {
         globals.put(name, symbol);
      }
   }

   public static void registerExpansion(Class<?> cls) {
      try {
         for(Annotation annotation : cls.getAnnotations()) {
            if (annotation instanceof ZenExpansion) {
               ZenExpansion eAnnotation = (ZenExpansion)annotation;
               if (!expansions.containsKey(eAnnotation.value())) {
                  expansions.put(eAnnotation.value(), new TypeExpansion(eAnnotation.value()));
               }

               ((TypeExpansion)expansions.get(eAnnotation.value())).expand(cls, types);
            }
         }
      } catch (Throwable var6) {
         var6.printStackTrace();
      }

   }

   public static void registerBracketHandler(IBracketHandler handler) {
      int prio = 10;
      if (handler.getClass().getAnnotation(BracketHandler.class) != null) {
         prio = ((BracketHandler)handler.getClass().getAnnotation(BracketHandler.class)).priority();
      } else {
         CraftTweakerAPI.logInfo(handler.getClass().getName() + " is missing a BracketHandler annotation, setting the priority to " + prio);
      }

      bracketHandlers.add(new Pair(prio, handler));
   }

   public static void removeBracketHandler(IBracketHandler handler) {
      Pair<Integer, IBracketHandler> prioPair = null;

      for(Pair<Integer, IBracketHandler> pair : bracketHandlers) {
         if (((IBracketHandler)pair.getValue()).equals(handler)) {
            prioPair = pair;
         }
      }

      bracketHandlers.remove(prioPair);
   }

   public static void registerNativeClass(Class<?> cls) {
      try {
         ZenTypeNative type = new ZenTypeNative(cls);
         type.complete(types);
         root.put(type.getName(), new SymbolType(type), errors);
      } catch (Throwable var2) {
         var2.printStackTrace();
      }

   }

   public static IZenSymbol resolveBracket(IEnvironmentGlobal environment, List<Token> tokens) {
      for(Pair<Integer, IBracketHandler> pair : bracketHandlers) {
         IZenSymbol symbol = (pair.getValue()).resolve(environment, tokens);
         if (symbol != null) {
            return symbol;
         }
      }

      return null;
   }

   public static IZenSymbol getStaticFunction(Class<?> cls, String name, Class<?>... arguments) {
      IJavaMethod method = JavaMethod.get(types, cls, name, arguments);
      return new SymbolJavaStaticMethod(method);
   }

   public static IZenSymbol getStaticField(Class<?> cls, String name) {
      try {
         Field field = cls.getDeclaredField(name);
         return new SymbolJavaStaticField(cls, field, types);
      } catch (SecurityException | NoSuchFieldException var3) {
         Logger.getLogger(GlobalRegistry.class.getName()).log(Level.SEVERE, (String)null, var3);
         return null;
      }
   }

   public static IEnvironmentGlobal makeGlobalEnvironment(Map<String, byte[]> classes) {
      return new CrTGlobalEnvironment(classes);
   }

   public static IEnvironmentGlobal makeGlobalEnvironment(Map<String, byte[]> classes, String loaderMainName) {
      return new CrTGlobalEnvironment(classes, capitalize(loaderMainName));
   }

   private static String capitalize(String in) {
      return in != null && in.length() >= 1 ? Character.toUpperCase(in.charAt(0)) + in.substring(1) : "";
   }

   public static Map<String, IZenSymbol> getGlobals() {
      return globals;
   }

   public static Set<Pair<Integer, IBracketHandler>> getPrioritizedBracketHandlers() {
      return bracketHandlers;
   }

   /** @deprecated */
   @Deprecated
   public static List<IBracketHandler> getBracketHandlers() {
      List<IBracketHandler> handlers = new LinkedList();

      for(Pair<Integer, IBracketHandler> pair : getPrioritizedBracketHandlers()) {
         handlers.add(pair.getValue());
      }

      return handlers;
   }

   public static TypeRegistry getTypes() {
      return types;
   }

   public static SymbolPackage getRoot() {
      return root;
   }

   public static IZenErrorLogger getErrors() {
      return errors;
   }

   public static IZenCompileEnvironment getEnvironment() {
      return environment;
   }

   public static Map<String, TypeExpansion> getExpansions() {
      return expansions;
   }

   static {
      registerGlobal("print", getStaticFunction(GlobalFunctions.class, "print", String.class));
      registerGlobal("totalActions", getStaticFunction(GlobalFunctions.class, "totalActions"));
      registerGlobal("enableDebug", getStaticFunction(GlobalFunctions.class, "enableDebug"));
      registerGlobal("isNull", getStaticFunction(GlobalFunctions.class, "isNull", Object.class));
      registerGlobal("max", getStaticFunction(Math.class, "max", Integer.TYPE, Integer.TYPE));
      registerGlobal("min", getStaticFunction(Math.class, "min", Integer.TYPE, Integer.TYPE));
      registerGlobal("pow", getStaticFunction(Math.class, "pow", Double.TYPE, Double.TYPE));
      root.put(ZenTypeIntRange.INTRANGE.getName(), new SymbolType(ZenTypeIntRange.INSTANCE), errors);
   }
}
