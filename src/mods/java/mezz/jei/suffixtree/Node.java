package mezz.jei.suffixtree;

import it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectMaps;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import mezz.jei.util.Log;

class Node {
   private final IntList data = new IntArrayList(0);
   private Char2ObjectMap<Edge> edges = new Char2ObjectOpenHashMap();
   @Nullable
   private Node suffix = null;

   Node() {
   }

   void getData(IntSet ret) {
      ret.addAll(this.data);
      ObjectIterator var2 = this.edges.values().iterator();

      while(var2.hasNext()) {
         Edge e = (Edge)var2.next();
         e.getDest().getData(ret);
      }

   }

   boolean addRef(int index) {
      if (this.contains(index)) {
         return false;
      } else {
         this.addIndex(index);

         for(Node iter = this.suffix; iter != null && !iter.contains(index); iter = iter.suffix) {
            iter.addIndex(index);
         }

         return true;
      }
   }

   private boolean contains(int index) {
      return this.data.contains(index);
   }

   void addEdge(char ch, Edge e) {
      try {
         this.edges.put(ch, e);
      } catch (UnsupportedOperationException var4) {
         Log.get().warn("Mod added a tree entry after memory optimization!", var4);
         this.edges = new Char2ObjectOpenHashMap(this.edges);
         this.edges.put(ch, e);
      }

   }

   @Nullable
   Edge getEdge(char ch) {
      return (Edge)this.edges.get(ch);
   }

   @Nullable
   Node getSuffix() {
      return this.suffix;
   }

   void setSuffix(Node suffix) {
      this.suffix = suffix;
   }

   private void addIndex(int index) {
      this.data.add(index);
   }

   public String toString() {
      return "Node: size:" + this.data.size() + " Edges: " + this.edges.toString();
   }

   ObjectCollection<Edge> edges() {
      return this.edges.values();
   }

   void trimToSize() {
      if (this.data instanceof IntArrayList) {
         ((IntArrayList)this.data).trim();
      }

      switch(this.edges.size()) {
      case 0:
         this.edges = Char2ObjectMaps.emptyMap();
         break;
      case 1:
         Entry<Character, Edge> entry = (Entry)this.edges.entrySet().iterator().next();
         this.edges = Char2ObjectMaps.singleton((Character)entry.getKey(), entry.getValue());
         break;
      case 2:
      case 3:
      case 4:
         this.edges = new Char2ObjectArrayMap(this.edges);
      }

   }
}
