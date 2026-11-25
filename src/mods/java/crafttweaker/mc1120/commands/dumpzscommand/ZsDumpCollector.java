package crafttweaker.mc1120.commands.dumpzscommand;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import crafttweaker.mc1120.commands.dumpzscommand.types.DumpBracketHandler;
import crafttweaker.zenscript.IBracketHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import stanhebben.zenscript.compiler.TypeRegistry;
import stanhebben.zenscript.dump.GSONDumpableSerializer;
import stanhebben.zenscript.dump.IDumpable;
import stanhebben.zenscript.dump.types.DumpClassBase;
import stanhebben.zenscript.dump.types.DumpDummy;
import stanhebben.zenscript.dump.types.DumpIJavaMethod;
import stanhebben.zenscript.dump.types.DumpZenType;
import stanhebben.zenscript.dump.types.DumpZenTypeNative;
import stanhebben.zenscript.symbols.SymbolPackage;
import stanhebben.zenscript.util.Pair;

public class ZsDumpCollector {
   private final transient Gson gson;
   private List<DumpBracketHandler> bracketHandlerDumps = new ArrayList();
   private List<DumpZenType> zenTypeDumps = new ArrayList();
   private List<IDumpable> rootDumps = new ArrayList();

   public ZsDumpCollector() {
      Class<?>[] serializerClasses = new Class[]{DumpDummy.class, DumpZenType.class, DumpClassBase.class, DumpBracketHandler.class, DumpZenTypeNative.class, DumpIJavaMethod.class};
      GsonBuilder builder = (new GsonBuilder()).setPrettyPrinting();

      for(Class<?> serializerClass : serializerClasses) {
         builder.registerTypeAdapter(serializerClass, GSONDumpableSerializer.INSTANCE);
      }

      this.gson = builder.create();
   }

   void collectTypeRegistry(TypeRegistry typeRegistry) {
      typeRegistry.getTypeMap().forEach((clazz, type) -> {
         this.zenTypeDumps.add(type.asDumpedObject().get(0));
      });
   }

   void collectBracketHandlers(Set<Pair<Integer, IBracketHandler>> bracketHandlers) {
      for(Pair<Integer, IBracketHandler> bracketHandler : bracketHandlers) {
         this.bracketHandlerDumps.add(new DumpBracketHandler(bracketHandler.getValue(), ((Integer)bracketHandler.getKey()).intValue()));
      }

   }

   void collectRoot(SymbolPackage symbolPackage) {
      this.rootDumps.addAll(symbolPackage.asDumpedObject());
   }

   String toJson() {
      return this.gson.toJson(this);
   }
}
