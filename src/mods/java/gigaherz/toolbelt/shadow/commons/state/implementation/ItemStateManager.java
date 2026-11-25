package gigaherz.toolbelt.shadow.commons.state.implementation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import gigaherz.toolbelt.shadow.commons.state.IItemState;
import gigaherz.toolbelt.shadow.commons.state.IItemStateManager;
import gigaherz.toolbelt.shadow.commons.state.StatefulItem;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import javax.annotation.Nullable;
import net.minecraft.block.properties.IProperty;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.ArrayUtils;

public class ItemStateManager implements IItemStateManager {
   private final Item item;
   private final ItemStateManager.ItemState[] stateTable;
   private final IProperty[] properties;
   private final Comparable[][] propertyValues;
   private IItemState defaultState;

   public <T extends Item & StatefulItem> ItemStateManager(T item, IProperty... properties) {
      this.item = item;
      this.properties = properties;
      this.propertyValues = new Comparable[properties.length][];
      Arrays.sort(properties);
      int combinations = 1;

      for(int i = 0; i < properties.length; ++i) {
         IProperty prop = properties[i];
         Collection<Comparable> allowed = prop.getAllowedValues();
         Comparable[] values = (Comparable[])allowed.toArray(new Comparable[allowed.size()]);
         this.propertyValues[i] = values;
         combinations *= values.length;
      }

      this.stateTable = new ItemStateManager.ItemState[combinations];
      Deque<Comparable> values = new ArrayDeque();
      int lastState = this.enumStates(properties, 0, 0, values);

      assert lastState == combinations;

      this.defaultState = this.stateTable[0];
   }

   private int enumStates(IProperty[] properties, int p, int state, Deque<Comparable> values) {
      Comparable[] propValues = this.propertyValues[p];

      for(Object o : propValues) {
         values.push((Comparable)o);
         if (p + 1 >= properties.length) {
            this.stateTable[state] = new ItemStateManager.ItemState(state, (Comparable[])values.toArray(new Comparable[values.size()]));
            ++state;
         } else {
            state = this.enumStates(properties, p + 1, state, values);
         }

         values.pop();
      }

      return state;
   }

   @Nullable
   public IItemState get(int metadata) {
      return metadata >= 0 && metadata < this.stateTable.length ? this.stateTable[metadata] : null;
   }

   public Item getItem() {
      return this.item;
   }

   public ImmutableList<IProperty> getProperties() {
      return ImmutableList.copyOf(this.properties);
   }

   public IItemState getDefaultState() {
      return this.defaultState;
   }

   public void setDefaultState(IItemState defaultState) {
      this.defaultState = defaultState;
   }

   public ImmutableList<IItemState> getStateTable() {
      return ImmutableList.copyOf(this.stateTable);
   }

   public class ItemState implements IItemState {
      private final int metadata;
      private Comparable[] values;

      ItemState(int metadata, Comparable[] values) {
         this.metadata = metadata;
         this.values = values;
      }

      public int getMetadata() {
         return this.metadata;
      }

      public ItemStack getStack() {
         return this.getStack(1);
      }

      public ItemStack getStack(int count) {
         return new ItemStack(ItemStateManager.this.item, count, this.metadata);
      }

      public <T extends Comparable<T>> IItemState withProperty(IProperty<T> property, T value) {
         int lastSize = 1;
         int meta = 0;

         for(int i = 0; i < ItemStateManager.this.properties.length; ++i) {
            Comparable[] values = ItemStateManager.this.propertyValues[i];
            int idx;
            if (ItemStateManager.this.properties[i] == property) {
               idx = ArrayUtils.indexOf(values, value);
            } else {
               idx = ArrayUtils.indexOf(values, this.values[i]);
            }

            meta = meta * lastSize + idx;
            lastSize = values.length;
         }

         return ItemStateManager.this.stateTable[meta];
      }

      public <T extends Comparable<T>> T getValue(IProperty<T> property) {
         int i = ArrayUtils.indexOf(ItemStateManager.this.properties, property);
         return (T)this.values[i];
      }

      public ImmutableList<Comparable> getValues() {
         return ImmutableList.copyOf(this.values);
      }

      public ImmutableMap<IProperty<?>, Comparable<?>> getProperties() {
         Builder<IProperty<?>, Comparable<?>> b = new Builder();

         for(int i = 0; i < ItemStateManager.this.properties.length; ++i) {
            IProperty prop = ItemStateManager.this.properties[i];
            Comparable value = this.values[i];
            b.put(prop, value);
         }

         return b.build();
      }

      public String toString() {
         StringBuilder sb = new StringBuilder();

         for(int i = 0; i < ItemStateManager.this.properties.length; ++i) {
            if (i != 0) {
               sb.append(",");
            }

            sb.append(ItemStateManager.this.properties[i].getName());
            sb.append("=");
            sb.append(ItemStateManager.this.properties[i].getName(this.values[i]));
         }

         return sb.toString();
      }
   }
}
