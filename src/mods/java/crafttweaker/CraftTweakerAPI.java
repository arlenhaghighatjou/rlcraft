package crafttweaker;

import crafttweaker.annotations.BracketHandler;
import crafttweaker.annotations.OnRegister;
import crafttweaker.api.client.IClient;
import crafttweaker.api.event.IEventManager;
import crafttweaker.api.formatting.IFormatter;
import crafttweaker.api.game.IGame;
import crafttweaker.api.item.IItemUtils;
import crafttweaker.api.mods.ILoadedMods;
import crafttweaker.api.oredict.IOreDict;
import crafttweaker.api.recipes.IBrewingManager;
import crafttweaker.api.recipes.IFurnaceManager;
import crafttweaker.api.recipes.IRecipeManager;
import crafttweaker.api.server.IServer;
import crafttweaker.api.vanilla.IVanilla;
import crafttweaker.runtime.CrTTweaker;
import crafttweaker.runtime.ILogger;
import crafttweaker.runtime.ITweaker;
import crafttweaker.util.SuppressErrorFlag;
import crafttweaker.zenscript.GlobalRegistry;
import crafttweaker.zenscript.IBracketHandler;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.symbols.SymbolJavaStaticField;
import stanhebben.zenscript.symbols.SymbolJavaStaticGetter;
import stanhebben.zenscript.symbols.SymbolJavaStaticMethod;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.type.natives.JavaMethod;

public class CraftTweakerAPI {
   public static final ITweaker tweaker = new CrTTweaker();
   public static final IEventManager events = CrafttweakerImplementationAPI.events;
   public static IOreDict oreDict = null;
   public static IRecipeManager recipes = null;
   public static IFurnaceManager furnace = null;
   public static IServer server = null;
   public static IClient client = null;
   public static IGame game = null;
   public static ILoadedMods loadedMods = null;
   public static IFormatter format = null;
   public static IVanilla vanilla = null;
   public static IItemUtils itemUtils = null;
   public static IBrewingManager brewingManager = null;
   private static SuppressErrorFlag suppressErrorFlag = SuppressErrorFlag.DEFAULT;
   public static boolean ENABLE_SEARCH_TREE_RECALCULATION = true;
   public static boolean profile = false;

   private CraftTweakerAPI() {
   }

   public static ILogger getLogger() {
      return CrafttweakerImplementationAPI.logger;
   }

   public static void apply(IAction action) {
      long time = 0L;
      if (profile) {
         time = System.nanoTime();
      }

      tweaker.apply(action);
      if (profile) {
         logInfo("Took " + (double)(System.nanoTime() - time) / 1000000.0D + " ms");
      }

   }

   public static void logCommand(String message) {
      getLogger().logCommand(message);
   }

   public static void logInfo(String message) {
      getLogger().logInfo(message);
   }

   public static void logWarning(String message) {
      getLogger().logWarning(message);
   }

   public static void logError(String message) {
      getLogger().logError(message);
   }

   public static void logError(String message, Throwable exception) {
      getLogger().logError(message, exception);
   }

   public static void logDefault(String message) {
      getLogger().logDefault(message);
   }

   public static void setSuppressErrorFlag(SuppressErrorFlag flag) {
      if (!suppressErrorFlag.isForced()) {
         suppressErrorFlag = flag;
      }
   }

   public static boolean isSuppressingErrors() {
      return suppressErrorFlag.isSuppressingErrors();
   }

   public static boolean isSuppressingWarnings() {
      return suppressErrorFlag.isSuppressingWarnings();
   }

   public static void registerClass(Class<?> annotatedClass) {
      boolean registered = false;

      for(Annotation annotation : annotatedClass.getAnnotations()) {
         if (annotation instanceof ZenExpansion) {
            GlobalRegistry.registerExpansion(annotatedClass);
            registered = true;
         }

         if (annotation instanceof ZenClass) {
            GlobalRegistry.registerNativeClass(annotatedClass);
            registered = true;
         }

         if (annotation instanceof BracketHandler && IBracketHandler.class.isAssignableFrom(annotatedClass)) {
            try {
               IBracketHandler bracketHandler = (IBracketHandler)annotatedClass.newInstance();
               registerBracketHandler(bracketHandler);
            } catch (IllegalAccessException | InstantiationException var8) {
               Logger.getLogger(CraftTweakerAPI.class.getName()).log(Level.SEVERE, (String)null, var8);
            }

            registered = true;
         }
      }

      if (registered) {
         for(Method method : annotatedClass.getDeclaredMethods()) {
            if (Modifier.isStatic(method.getModifiers()) && Modifier.isPublic(method.getModifiers()) && method.isAnnotationPresent(OnRegister.class)) {
               try {
                  method.invoke((Object)null);
               } catch (InvocationTargetException | IllegalAccessException var7) {
                  var7.printStackTrace();
               }
            }
         }
      }

   }

   public static void registerGlobalSymbol(String name, IZenSymbol symbol) {
      GlobalRegistry.registerGlobal(name, symbol);
   }

   public static void registerBracketHandler(IBracketHandler handler) {
      GlobalRegistry.registerBracketHandler(handler);
   }

   public static IZenSymbol getJavaStaticMethodSymbol(Class<?> cls, String name, Class<?>... arguments) {
      IJavaMethod method = JavaMethod.get(GlobalRegistry.getTypes(), cls, name, arguments);
      return new SymbolJavaStaticMethod(method);
   }

   public static IZenSymbol getJavaStaticGetterSymbol(Class<? extends CraftTweakerAPI> cls, String name) {
      IJavaMethod method = JavaMethod.get(GlobalRegistry.getTypes(), cls, name);
      return new SymbolJavaStaticGetter(method);
   }

   public static IZenSymbol getJavaStaticFieldSymbol(Class<?> cls, String name) {
      try {
         Field field = cls.getField(name);
         return new SymbolJavaStaticField(cls, field, GlobalRegistry.getTypes());
      } catch (SecurityException | NoSuchFieldException var3) {
         return null;
      }
   }

   public static IJavaMethod getJavaMethod(Class<? extends IBracketHandler> cls, String name, Class<?>... arguments) {
      return JavaMethod.get(GlobalRegistry.getTypes(), cls, name, arguments);
   }

   public static String getScriptFileAndLine() {
      return (String)Arrays.stream(Thread.currentThread().getStackTrace()).filter((s) -> {
         return s != null && s.getFileName() != null && s.getFileName().endsWith(".zs");
      }).findFirst().map((s) -> {
         return s.getFileName() + ":" + s.getLineNumber();
      }).orElse("<?>");
   }

   static {
      registerGlobalSymbol("logger", getJavaStaticGetterSymbol(CraftTweakerAPI.class, "getLogger"));
      registerGlobalSymbol("recipes", getJavaStaticFieldSymbol(CraftTweakerAPI.class, "recipes"));
      registerGlobalSymbol("furnace", getJavaStaticFieldSymbol(CraftTweakerAPI.class, "furnace"));
      registerGlobalSymbol("oreDict", getJavaStaticFieldSymbol(CraftTweakerAPI.class, "oreDict"));
      registerGlobalSymbol("events", getJavaStaticFieldSymbol(CraftTweakerAPI.class, "events"));
      registerGlobalSymbol("server", getJavaStaticFieldSymbol(CraftTweakerAPI.class, "server"));
      registerGlobalSymbol("client", getJavaStaticFieldSymbol(CraftTweakerAPI.class, "client"));
      registerGlobalSymbol("game", getJavaStaticFieldSymbol(CraftTweakerAPI.class, "game"));
      registerGlobalSymbol("loadedMods", getJavaStaticFieldSymbol(CraftTweakerAPI.class, "loadedMods"));
      registerGlobalSymbol("format", getJavaStaticFieldSymbol(CraftTweakerAPI.class, "format"));
      registerGlobalSymbol("vanilla", getJavaStaticFieldSymbol(CraftTweakerAPI.class, "vanilla"));
      registerGlobalSymbol("itemUtils", getJavaStaticFieldSymbol(CraftTweakerAPI.class, "itemUtils"));
      registerGlobalSymbol("brewing", getJavaStaticFieldSymbol(CraftTweakerAPI.class, "brewingManager"));
   }
}
