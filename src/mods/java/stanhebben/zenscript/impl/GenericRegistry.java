package stanhebben.zenscript.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import stanhebben.zenscript.IZenCompileEnvironment;
import stanhebben.zenscript.IZenErrorLogger;
import stanhebben.zenscript.IZenLogger;
import stanhebben.zenscript.IZenRegistry;
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

public class GenericRegistry implements IZenRegistry {
   private IZenCompileEnvironment compileEnvironment;
   private Map<String, IZenSymbol> globals = new HashMap();
   private Set<Pair<Integer, IBracketHandler>> bracketHandlers = new TreeSet(Comparator.comparingInt(Pair::getKey).thenComparing((o) -> {
      return ((IBracketHandler)o.getValue()).getClass().getName();
   }));
   private TypeRegistry types = new TypeRegistry();
   private SymbolPackage root = new SymbolPackage("<root>");
   private Map<String, TypeExpansion> expansions = new HashMap();
   private IZenErrorLogger errorLogger;
   private IZenLogger logger = new GenericLogger();

   public GenericRegistry(IZenCompileEnvironment compileEnvironment, IZenErrorLogger errorLogger) {
      this.compileEnvironment = compileEnvironment;
      this.errorLogger = errorLogger;
      this.compileEnvironment.setRegistry(this);
      this.root.put(ZenTypeIntRange.INSTANCE.getName(), new SymbolType(ZenTypeIntRange.INSTANCE), this.errorLogger);
   }

   public void registerGlobal(String name, IZenSymbol symbol) {
      if (this.globals.containsKey(name)) {
         throw new IllegalArgumentException("symbol already exists: " + name);
      } else {
         this.globals.put(name, symbol);
      }
   }

   public void registerExpansion(Class<?> cls) {
      try {
         for(Annotation annotation : cls.getAnnotations()) {
            if (annotation instanceof ZenExpansion) {
               ZenExpansion eAnnotation = (ZenExpansion)annotation;
               if (!this.expansions.containsKey(eAnnotation.value())) {
                  this.expansions.put(eAnnotation.value(), new TypeExpansion(eAnnotation.value()));
               }

               ((TypeExpansion)this.expansions.get(eAnnotation.value())).expand(cls, this.types);
            }
         }
      } catch (Throwable var7) {
         var7.printStackTrace();
      }

   }

   public void registerBracketHandler(IBracketHandler handler) {
      int prio = 10;
      if (handler.getClass().getAnnotation(BracketHandler.class) != null) {
         prio = ((BracketHandler)handler.getClass().getAnnotation(BracketHandler.class)).priority();
      } else {
         this.getLogger().info(handler.getClass().getName() + " is missing a BracketHandler annotation, setting the priority to " + prio);
      }

      this.bracketHandlers.add(new Pair(prio, handler));
   }

   public void removeBracketHandler(IBracketHandler handler) {
      Pair<Integer, IBracketHandler> prioPair = null;

      for(Pair<Integer, IBracketHandler> pair : this.bracketHandlers) {
         if (((IBracketHandler)pair.getValue()).equals(handler)) {
            prioPair = pair;
         }
      }

      this.bracketHandlers.remove(prioPair);
   }

   public void registerNativeClass(Class<?> cls) {
      try {
         ZenTypeNative type = new ZenTypeNative(cls);
         type.complete(this.types);
         this.root.put(type.getName(), new SymbolType(type), this.errorLogger);
      } catch (Throwable var3) {
         var3.printStackTrace();
      }

   }

   public IZenSymbol getStaticFunction(Class cls, String name, Class... arguments) {
      IJavaMethod method = JavaMethod.get(this.types, cls, name, arguments);
      return new SymbolJavaStaticMethod(method);
   }

   public IZenSymbol getStaticField(Class cls, String name) {
      try {
         Field field = cls.getDeclaredField(name);
         return new SymbolJavaStaticField(cls, field, this.types);
      } catch (SecurityException | NoSuchFieldException var4) {
         this.getLogger().error("Unable to get static field: " + name + " from class " + cls.getName(), var4);
         return null;
      }
   }

   public IZenSymbol resolveBracket(IEnvironmentGlobal environment, List<Token> tokens) {
      for(Pair<Integer, IBracketHandler> pair : this.bracketHandlers) {
         IZenSymbol symbol = (pair.getValue()).resolve(environment, tokens);
         if (symbol != null) {
            return symbol;
         }
      }

      return null;
   }

   public IEnvironmentGlobal makeGlobalEnvironment(Map<String, byte[]> classes) {
      return new GenericGlobalEnvironment(classes, this);
   }

   public IZenCompileEnvironment getCompileEnvironment() {
      return this.compileEnvironment;
   }

   public Map<String, IZenSymbol> getGlobals() {
      return this.globals;
   }

   public Set<Pair<Integer, IBracketHandler>> getBracketHandlers() {
      return this.bracketHandlers;
   }

   public TypeRegistry getTypes() {
      return this.types;
   }

   public SymbolPackage getRoot() {
      return this.root;
   }

   public Map<String, TypeExpansion> getExpansions() {
      return this.expansions;
   }

   public void setCompileEnvironment(IZenCompileEnvironment compileEnvironment) {
      this.compileEnvironment = compileEnvironment;
   }

   public void setGlobals(Map<String, IZenSymbol> globals) {
      this.globals = globals;
   }

   public void setBracketHandlers(Set<Pair<Integer, IBracketHandler>> bracketHandlers) {
      this.bracketHandlers = bracketHandlers;
   }

   public void setTypes(TypeRegistry types) {
      this.types = types;
   }

   public void setRoot(SymbolPackage root) {
      this.root = root;
   }

   public void setExpansions(Map<String, TypeExpansion> expansions) {
      this.expansions = expansions;
   }

   public IZenErrorLogger getErrorLogger() {
      return this.errorLogger;
   }

   public void setErrorLogger(IZenErrorLogger errorLogger) {
      this.errorLogger = errorLogger;
   }

   public IZenLogger getLogger() {
      return this.logger;
   }

   public void setLogger(IZenLogger logger) {
      this.logger = logger;
   }
}
