package crafttweaker.api.block;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.util.ArrayUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class BlockStateMatcherOr implements IBlockStateMatcher {
   private final IBlockStateMatcher[] elements;

   public BlockStateMatcherOr(IBlockStateMatcher[] elements) {
      this.elements = elements;
   }

   public BlockStateMatcherOr(IBlockStateMatcher a, IBlockStateMatcher b) {
      this.elements = new IBlockStateMatcher[]{a, b};
   }

   public boolean matches(IBlockState blockState) {
      for(IBlockStateMatcher matcher : this.elements) {
         if (matcher.matches(blockState)) {
            return true;
         }
      }

      return false;
   }

   public IBlockStateMatcher or(IBlockStateMatcher matcher) {
      return new BlockStateMatcherOr(ArrayUtil.append(this.elements, matcher));
   }

   public Collection<IBlockState> getMatchingBlockStates() {
      Collection<IBlockState> matchingStates = new ArrayList();

      for(IBlockStateMatcher matcher : this.elements) {
         Collection<IBlockState> states = matcher.getMatchingBlockStates();
         if (states != null) {
            matchingStates.addAll(states);
         }
      }

      return matchingStates;
   }

   public IBlockStateMatcher allowValuesForProperty(String name, String... values) {
      CraftTweakerAPI.logWarning("IBlockStateMatcher#allowValuesForProperty is deprecated. Please use IBlockStateMatcher#withMatchedValuesForProperty instead.");
      return this.withMatchedValuesForProperty(name, values);
   }

   public IBlockStateMatcher withMatchedValuesForProperty(String name, String... values) {
      CraftTweakerAPI.logWarning("Cannot change matched values for a compound blockstate matcher");
      return this;
   }

   public List<String> getMatchedValuesForProperty(String name) {
      CraftTweakerAPI.logWarning("Cannot retrieve matched values for a compound blockstate matcher");
      return new ArrayList();
   }

   public Map<String, List<String>> getMatchedProperties() {
      CraftTweakerAPI.logWarning("Cannot retrieve matched values for a compound blockstate matcher");
      return new HashMap();
   }

   public String toString() {
      StringJoiner joiner = new StringJoiner(" | ", "Matcher (", ")");

      for(IBlockStateMatcher element : this.elements) {
         joiner.add(element.toString());
      }

      return joiner.toString();
   }

   public boolean isCompound() {
      return true;
   }

   public String toCommandString() {
      StringJoiner joiner = new StringJoiner(" | ");

      for(IBlockStateMatcher element : this.elements) {
         joiner.add(element.toCommandString());
      }

      return joiner.toString();
   }
}
