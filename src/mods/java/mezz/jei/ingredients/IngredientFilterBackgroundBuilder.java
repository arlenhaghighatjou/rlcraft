package mezz.jei.ingredients;

import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Collection;
import mezz.jei.config.Config;
import mezz.jei.gui.ingredients.IIngredientListElement;
import mezz.jei.suffixtree.GeneralizedSuffixTree;
import net.minecraft.client.Minecraft;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class IngredientFilterBackgroundBuilder {
   private final Char2ObjectMap<PrefixedSearchTree> prefixedSearchTrees;
   private final NonNullList<IIngredientListElement> elementList;

   public IngredientFilterBackgroundBuilder(Char2ObjectMap<PrefixedSearchTree> prefixedSearchTrees, NonNullList<IIngredientListElement> elementList) {
      this.prefixedSearchTrees = prefixedSearchTrees;
      this.elementList = elementList;
   }

   public void start() {
      boolean finished = this.run(10000);
      if (!finished) {
         MinecraftForge.EVENT_BUS.register(this);
      }

   }

   @SubscribeEvent
   public void onClientTick(ClientTickEvent event) {
      if (event.side == Side.CLIENT && Minecraft.getMinecraft().player != null) {
         boolean finished = this.run(20);
         if (!finished) {
            return;
         }
      }

      MinecraftForge.EVENT_BUS.unregister(this);
   }

   private boolean run(int timeoutMs) {
      long startTime = System.currentTimeMillis();
      ObjectIterator var4 = this.prefixedSearchTrees.values().iterator();

      while(var4.hasNext()) {
         PrefixedSearchTree prefixedTree = (PrefixedSearchTree)var4.next();
         Config.SearchMode mode = prefixedTree.getMode();
         if (mode != Config.SearchMode.DISABLED) {
            PrefixedSearchTree.IStringsGetter stringsGetter = prefixedTree.getStringsGetter();
            GeneralizedSuffixTree tree = prefixedTree.getTree();

            for(int i = tree.getHighestIndex() + 1; i < this.elementList.size(); ++i) {
               IIngredientListElement element = (IIngredientListElement)this.elementList.get(i);
               Collection<String> strings = stringsGetter.getStrings(element);
               if (strings.isEmpty()) {
                  tree.put("", i);
               } else {
                  for(String string : strings) {
                     tree.put(string, i);
                  }
               }

               if (System.currentTimeMillis() - startTime >= (long)timeoutMs) {
                  return false;
               }
            }
         }
      }

      return true;
   }
}
