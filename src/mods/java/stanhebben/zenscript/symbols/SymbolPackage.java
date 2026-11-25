package stanhebben.zenscript.symbols;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import stanhebben.zenscript.IZenErrorLogger;
import stanhebben.zenscript.dump.IDumpable;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.expression.partial.PartialPackage;
import stanhebben.zenscript.util.StringUtil;
import stanhebben.zenscript.util.ZenPosition;

public class SymbolPackage implements IZenSymbol {
   private final HashMap<String, IZenSymbol> members;
   private final String name;

   public SymbolPackage(String name) {
      this.name = name;
      this.members = new HashMap();
   }

   public String getName() {
      return this.name;
   }

   public Map<String, IZenSymbol> getPackages() {
      return this.members;
   }

   public IZenSymbol get(String name) {
      return (IZenSymbol)this.members.get(name);
   }

   public void put(String name, IZenSymbol symbol, IZenErrorLogger errors) {
      String[] parts = StringUtil.split(name, '.');
      String[] pkgParts = (String[])Arrays.copyOf(parts, parts.length - 1);
      SymbolPackage pkgCurrent = this;
      String pkgName = null;

      for(String part : pkgParts) {
         if (pkgName == null) {
            pkgName = part;
         } else {
            pkgName = pkgName + '.' + part;
         }

         if (pkgCurrent.members.containsKey(part)) {
            IZenSymbol member = (IZenSymbol)pkgCurrent.members.get(part);
            if (!(member instanceof SymbolPackage)) {
               errors.error((ZenPosition)null, part + " is not a package");
               return;
            }

            pkgCurrent = (SymbolPackage)member;
         } else {
            SymbolPackage child = new SymbolPackage(pkgName);
            pkgCurrent.members.put(part, child);
            pkgCurrent = child;
         }
      }

      if (pkgCurrent.members.containsKey(parts[parts.length - 1])) {
         errors.error((ZenPosition)null, parts[parts.length - 1] + " is already defined in that package");
      } else {
         pkgCurrent.members.put(parts[parts.length - 1], symbol);
      }

   }

   public IPartialExpression instance(ZenPosition position) {
      return new PartialPackage(position, this);
   }

   public String toString() {
      return "SymbolPackage: " + this.name;
   }

   public List<? extends IDumpable> asDumpedObject() {
      List<IDumpable> list = new ArrayList();

      for(Entry<String, IZenSymbol> entry : this.members.entrySet()) {
         list.addAll(((IZenSymbol)entry.getValue()).asDumpedObject());
      }

      return list;
   }
}
