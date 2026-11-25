package com.creativemd.creativecore.common.utils.type;

import com.creativemd.creativecore.common.config.api.CreativeConfig;
import com.creativemd.creativecore.common.utils.stack.InfoBlock;
import com.creativemd.creativecore.common.utils.stack.InfoContainOre;
import com.creativemd.creativecore.common.utils.stack.InfoItem;
import com.creativemd.creativecore.common.utils.stack.InfoItemStack;
import com.creativemd.creativecore.common.utils.stack.InfoMaterial;
import com.creativemd.creativecore.common.utils.stack.InfoName;
import com.creativemd.creativecore.common.utils.stack.InfoOre;
import com.creativemd.creativecore.common.utils.stack.InfoStack;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@CreativeConfig
public class SortingList implements List<InfoStack> {
   @CreativeConfig
   public List<InfoStack> entries;
   @CreativeConfig
   public boolean isWhitelist;

   public SortingList(SortingList list) {
      this.entries = new ArrayList();
      this.isWhitelist = list.isWhitelist;
      this.entries = new ArrayList(list.entries);
   }

   public SortingList() {
      this(true);
   }

   public SortingList(boolean isWhitelist) {
      this.entries = new ArrayList();
      this.isWhitelist = isWhitelist;
   }

   public boolean isBlacklist() {
      return !this.isWhitelist;
   }

   public boolean isWhitelist() {
      return this.isWhitelist;
   }

   public void setListType(boolean isWhitelist) {
      this.isWhitelist = isWhitelist;
   }

   public void setWhitelist() {
      this.isWhitelist = true;
   }

   public void setBlacklist() {
      this.isWhitelist = false;
   }

   public void addSortingObjects(Object... objects) {
      for(int i = 0; i < objects.length; ++i) {
         this.addSortingObject(objects[i]);
      }

   }

   public void addSortingObject(Object object) {
      if (object instanceof InfoStack) {
         this.add((InfoStack)object);
      } else {
         InfoStack info = InfoStack.parseObject(object);
         if (info != null) {
            this.add(info);
         }

      }
   }

   public void addSortingByNameContains(String name) {
      this.add(new InfoName(name));
   }

   public void addSortingByOreContains(String name) {
      this.add(new InfoContainOre(name));
   }

   public void addSortingByOre(String ore) {
      this.add(new InfoOre(ore));
   }

   public void addSortingByBlock(Block block) {
      this.add(new InfoBlock(block));
   }

   public void addSortingByItem(Item item) {
      this.add(new InfoItem(item));
   }

   public void addSortingByItemStack(ItemStack stack) {
      this.add(new InfoItemStack(stack));
   }

   public void addSortingByMaterial(Material material) {
      this.add(new InfoMaterial(material));
   }

   protected boolean canBeFoundInList(Object object) {
      InfoStack info = InfoStack.parseObject(object);
      if (info == null) {
         return false;
      } else {
         for(InfoStack infoStack : this.entries) {
            if (infoStack.isInstanceIgnoreSize(infoStack)) {
               return true;
            }
         }

         return false;
      }
   }

   protected boolean canBeFoundInList(ItemStack stack) {
      for(InfoStack infoStack : this.entries) {
         if (infoStack.isInstanceIgnoreSize(stack)) {
            return true;
         }
      }

      return false;
   }

   public boolean canPass(Object object) {
      return this.canBeFoundInList(object) == this.isWhitelist;
   }

   public boolean canPass(ItemStack stack) {
      return this.canBeFoundInList(stack) == this.isWhitelist;
   }

   public boolean add(InfoStack arg0) {
      return this.entries.add(arg0);
   }

   public void add(int arg0, InfoStack arg1) {
      this.entries.add(arg0, arg1);
   }

   public boolean addAll(Collection<? extends InfoStack> arg0) {
      return this.addAll(arg0);
   }

   public boolean addAll(int arg0, Collection<? extends InfoStack> arg1) {
      return this.addAll(arg0, arg1);
   }

   public void clear() {
      this.entries = new ArrayList();
   }

   public boolean contains(Object arg0) {
      return this.entries.contains(arg0);
   }

   public boolean containsAll(Collection<?> arg0) {
      return this.entries.containsAll(arg0);
   }

   public InfoStack get(int arg0) {
      return (InfoStack)this.entries.get(arg0);
   }

   public int indexOf(Object arg0) {
      return this.entries.indexOf(arg0);
   }

   public boolean isEmpty() {
      return this.entries.isEmpty();
   }

   public Iterator<InfoStack> iterator() {
      return this.entries.iterator();
   }

   public int lastIndexOf(Object arg0) {
      return this.entries.lastIndexOf(arg0);
   }

   public ListIterator<InfoStack> listIterator() {
      return this.entries.listIterator();
   }

   public ListIterator<InfoStack> listIterator(int arg0) {
      return this.entries.listIterator(arg0);
   }

   public boolean remove(Object arg0) {
      return this.entries.remove(arg0);
   }

   public InfoStack remove(int arg0) {
      return (InfoStack)this.entries.remove(arg0);
   }

   public boolean removeAll(Collection<?> arg0) {
      return this.entries.removeAll(arg0);
   }

   public boolean retainAll(Collection<?> arg0) {
      return this.entries.retainAll(arg0);
   }

   public InfoStack set(int index, InfoStack element) {
      return (InfoStack)this.entries.set(index, element);
   }

   public int size() {
      return this.entries.size();
   }

   public List<InfoStack> subList(int fromIndex, int toIndex) {
      return this.entries.subList(fromIndex, toIndex);
   }

   public Object[] toArray() {
      return this.entries.toArray();
   }

   public <T> T[] toArray(T[] a) {
      return (T[])this.entries.toArray(a);
   }
}
