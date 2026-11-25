package crafttweaker.mc1120.oredict;

import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.oredict.IOreDict;
import crafttweaker.api.oredict.IOreDictEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import net.minecraftforge.oredict.OreDictionary;

public class MCOreDict implements IOreDict {
   public MCOreDict() {
   }

   public IOreDictEntry get(String name) {
      return CraftTweakerMC.getOreDict(name);
   }

   public List<IOreDictEntry> getEntries() {
      List<IOreDictEntry> entries = new ArrayList();

      for(String key : OreDictionary.getOreNames()) {
         if (!OreDictionary.getOres(key).isEmpty()) {
            entries.add(CraftTweakerMC.getOreDict(key));
         }
      }

      return entries;
   }

   public boolean contains(String name) {
      return OreDictionary.doesOreNameExist(name);
   }

   public Iterator<IOreDictEntry> iterator() {
      return Arrays.stream(OreDictionary.getOreNames()).map(CraftTweakerMC::getOreDict).iterator();
   }
}
