package crafttweaker.api.data;

import crafttweaker.CraftTweakerAPI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DataString implements IData {
   private final String value;

   public DataString(String value) {
      this.value = value;
   }

   public boolean asBool() {
      return Boolean.parseBoolean(this.value);
   }

   public byte asByte() {
      try {
         return Byte.parseByte(this.value);
      } catch (NumberFormatException var2) {
         CraftTweakerAPI.logError("DataString " + this.value + " cannot be parsed to Byte, substituting 0!");
         return 0;
      }
   }

   public short asShort() {
      try {
         return Short.parseShort(this.value);
      } catch (NumberFormatException var2) {
         CraftTweakerAPI.logError("DataString " + this.value + " cannot be parsed to Short, substituting 0!");
         return 0;
      }
   }

   public int asInt() {
      try {
         return Integer.parseInt(this.value);
      } catch (NumberFormatException var2) {
         CraftTweakerAPI.logError("DataString " + this.value + " cannot be parsed to Int, substituting 0!");
         return 0;
      }
   }

   public long asLong() {
      try {
         return Long.parseLong(this.value);
      } catch (NumberFormatException var2) {
         CraftTweakerAPI.logError("DataString " + this.value + " cannot be parsed to Long, substituting 0!");
         return 0L;
      }
   }

   public float asFloat() {
      try {
         return Float.parseFloat(this.value);
      } catch (NumberFormatException var2) {
         CraftTweakerAPI.logError("DataString " + this.value + " cannot be parsed to Float, substituting 0!");
         return 0.0F;
      }
   }

   public double asDouble() {
      try {
         return Double.parseDouble(this.value);
      } catch (NumberFormatException var2) {
         CraftTweakerAPI.logError("DataString " + this.value + " cannot be parsed to Double, substituting 0!");
         return 0.0D;
      }
   }

   public String asString() {
      return this.value;
   }

   public List<IData> asList() {
      return null;
   }

   public Map<String, IData> asMap() {
      return Collections.singletonMap(this.value, this);
   }

   public byte[] asByteArray() {
      return null;
   }

   public int[] asIntArray() {
      return null;
   }

   public IData getAt(int i) {
      return new DataString(this.value.substring(i, i + 1));
   }

   public void setAt(int i, IData value) {
      throw new UnsupportedOperationException("Strings are immutable");
   }

   public IData memberGet(String name) {
      if (name.equals("length")) {
         return new DataInt(this.value.length());
      } else {
         throw new UnsupportedOperationException("no such member: " + name);
      }
   }

   public void memberSet(String name, IData data) {
      throw new UnsupportedOperationException("Strings are immutable");
   }

   public int length() {
      return this.value.length();
   }

   public boolean contains(IData data) {
      return data.asString().equals(this.value);
   }

   public boolean equals(IData data) {
      return this.value.equals(data.asString());
   }

   public int compareTo(IData data) {
      return this.value.compareTo(data.asString());
   }

   public IData immutable() {
      return this;
   }

   public IData update(IData data) {
      return data;
   }

   public <T> T convert(IDataConverter<T> converter) {
      return converter.fromString(this.value);
   }

   public IData add(IData other) {
      return new DataString(this.value + other.asString());
   }

   public IData sub(IData other) {
      throw new UnsupportedOperationException("Cannot subtract from a string");
   }

   public IData mul(IData other) {
      throw new UnsupportedOperationException("Cannot multiply a string");
   }

   public IData div(IData other) {
      throw new UnsupportedOperationException("Cannot divide a string");
   }

   public IData mod(IData other) {
      throw new UnsupportedOperationException("Cannot perform modulo on a string");
   }

   public IData and(IData other) {
      throw new UnsupportedOperationException("Cannot perform bitwise arithmetic on a string");
   }

   public IData or(IData other) {
      throw new UnsupportedOperationException("Cannot perform bitwise arithmetic on a string");
   }

   public IData xor(IData other) {
      throw new UnsupportedOperationException("Cannot perform bitwise arithmetic on a string");
   }

   public IData neg() {
      throw new UnsupportedOperationException("Cannot negate a string");
   }

   public IData not() {
      throw new UnsupportedOperationException("Cannot perform bitwise arithmetic on a string");
   }

   public String toString() {
      return quoteAndEscape(this.value);
   }

   public static String quoteAndEscape(String p_193588_0_) {
      StringBuilder stringbuilder = new StringBuilder("\"");

      for(int i = 0; i < p_193588_0_.length(); ++i) {
         char c0 = p_193588_0_.charAt(i);
         if (c0 == '\\' || c0 == '"') {
            stringbuilder.append('\\');
         }

         stringbuilder.append(c0);
      }

      return stringbuilder.append('"').toString();
   }
}
