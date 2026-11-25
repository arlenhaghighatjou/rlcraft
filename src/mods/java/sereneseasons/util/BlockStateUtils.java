package sereneseasons.util;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.UnmodifiableIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import sereneseasons.api.ISSBlock;

public class BlockStateUtils {
   public BlockStateUtils() {
   }

   public static String getStateInfoAsString(IBlockState state) {
      String desc = state.getBlock().getClass().getName() + "[";
      Iterator it = state.getProperties().entrySet().iterator();

      for(boolean first = true; it.hasNext(); first = false) {
         if (!first) {
            desc = desc + ",";
         }

         Entry entry = (Entry)it.next();
         IProperty iproperty = (IProperty)entry.getKey();
         Comparable comparable = (Comparable)entry.getValue();
         desc = desc + iproperty.getName() + "=" + iproperty.getName(comparable);
      }

      desc = desc + "]";
      return desc;
   }

   public static ImmutableSet<IBlockState> getStatesSet(IBlockState baseState, IProperty... properties) {
      Stack<IProperty> propStack = new Stack();
      List<IBlockState> states = new ArrayList();

      for(IProperty prop : properties) {
         propStack.push(prop);
      }

      if (!propStack.isEmpty()) {
         addStatesToList(baseState, states, propStack);
      }

      ImmutableSet<IBlockState> ret = ImmutableSet.copyOf(states);
      return ret;
   }

   private static void addStatesToList(IBlockState state, List<IBlockState> list, Stack<IProperty> stack) {
      if (stack.empty()) {
         list.add(state);
      } else {
         IProperty prop = (IProperty)stack.pop();

         for(Object value : prop.getAllowedValues()) {
            addStatesToList(state.withProperty(prop, (Comparable)value), list, stack);
         }

         stack.push(prop);
      }
   }

   public static ImmutableSet<IBlockState> getBlockPresets(Block block) {
      if (!(block instanceof ISSBlock)) {
         return ImmutableSet.of();
      } else {
         IBlockState defaultState = block.getDefaultState();
         if (defaultState == null) {
            defaultState = block.getBlockState().getBaseState();
         }

         return getStatesSet(defaultState, ((ISSBlock)block).getPresetProperties());
      }
   }

   public static IBlockState getPresetState(IBlockState state) {
      IBlockState outState = state.getBlock().getDefaultState();
      if (state.getBlock() instanceof ISSBlock) {
         ISSBlock bopBlock = (ISSBlock)state.getBlock();

         for(IProperty property : bopBlock.getPresetProperties()) {
            outState = outState.withProperty(property, state.getValue(property));
         }
      }

      return outState;
   }

   public static IProperty getPropertyByName(IBlockState blockState, String propertyName) {
      UnmodifiableIterator var2 = blockState.getProperties().keySet().iterator();

      while(var2.hasNext()) {
         IProperty property = (IProperty)var2.next();
         if (property.getName().equals(propertyName)) {
            return property;
         }
      }

      return null;
   }

   public static boolean isValidPropertyName(IBlockState blockState, String propertyName) {
      return getPropertyByName(blockState, propertyName) != null;
   }

   public static Comparable getPropertyValueByName(IBlockState blockState, IProperty property, String valueName) {
      UnmodifiableIterator var3 = ((ImmutableSet)property.getAllowedValues()).iterator();

      while(var3.hasNext()) {
         Comparable value = (Comparable)var3.next();
         if (value.toString().equals(valueName)) {
            return value;
         }
      }

      return null;
   }
}
