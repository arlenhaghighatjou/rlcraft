package crafttweaker.api.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DataList implements IData {
   private final List<IData> values;
   private final boolean immutable;

   public DataList(List<IData> values, boolean immutable) {
      this.values = values;
      this.immutable = immutable;
   }

   public boolean asBool() {
      throw new IllegalDataException("Cannot convert a list to a bool");
   }

   public byte asByte() {
      throw new IllegalDataException("Cannot convert a list to a byte");
   }

   public short asShort() {
      throw new IllegalDataException("Cannot convert a list to a short");
   }

   public int asInt() {
      throw new IllegalDataException("Cannot convert a list to an int");
   }

   public long asLong() {
      throw new IllegalDataException("Cannot convert a list to a long");
   }

   public float asFloat() {
      throw new IllegalDataException("Cannot convert a list to a float");
   }

   public double asDouble() {
      throw new IllegalDataException("Cannot convert a list to a double");
   }

   public List<IData> asList() {
      return this.immutable ? Collections.unmodifiableList(this.values) : this.values;
   }

   public Map<String, IData> asMap() {
      return null;
   }

   public String asString() {
      StringBuilder output = new StringBuilder();
      output.append('[');
      boolean first = true;

      for(IData value : this.values) {
         if (first) {
            first = false;
         } else {
            output.append(", ");
         }

         output.append(value.toString());
      }

      output.append(']');
      return output.toString();
   }

   public byte[] asByteArray() {
      try {
         byte[] result = new byte[this.values.size()];

         for(int i = 0; i < this.values.size(); ++i) {
            result[i] = ((IData)this.values.get(i)).asByte();
         }

         return result;
      } catch (IllegalDataException var3) {
         return null;
      }
   }

   public int[] asIntArray() {
      try {
         int[] result = new int[this.values.size()];

         for(int i = 0; i < this.values.size(); ++i) {
            result[i] = ((IData)this.values.get(i)).asInt();
         }

         return result;
      } catch (IllegalDataException var3) {
         return null;
      }
   }

   public IData getAt(int i) {
      return (IData)this.values.get(i);
   }

   public void setAt(int i, IData value) {
      if (this.immutable) {
         throw new UnsupportedOperationException("this list is immutable");
      } else {
         this.values.set(i, value);
      }
   }

   public IData memberGet(String name) {
      throw new UnsupportedOperationException("Lists don't have members");
   }

   public void memberSet(String name, IData data) {
      throw new UnsupportedOperationException("Lists don't have members");
   }

   public int length() {
      return this.values.size();
   }

   public boolean contains(IData data) {
      List<IData> dataValues = data.asList();
      if (dataValues != null && this.containsList(dataValues)) {
         return true;
      } else {
         for(IData value : this.values) {
            if (value.contains(data)) {
               return true;
            }
         }

         return false;
      }
   }

   private boolean containsList(List<IData> dataValues) {
      label21:
      for(IData dataValue : dataValues) {
         for(IData value : this.values) {
            if (value.contains(dataValue)) {
               continue label21;
            }
         }

         return false;
      }

      return true;
   }

   public boolean equals(IData data) {
      List<IData> otherValues = data.asList();
      if (otherValues.size() != this.values.size()) {
         return false;
      } else {
         for(int i = 0; i < this.values.size(); ++i) {
            if (!((IData)this.values.get(i)).equals((IData)otherValues.get(i))) {
               return false;
            }
         }

         return true;
      }
   }

   public int compareTo(IData data) {
      throw new UnsupportedOperationException("Cannot compare with a list");
   }

   public IData immutable() {
      if (this.immutable) {
         return this;
      } else {
         List<IData> copy = new ArrayList();

         for(IData value : this.values) {
            copy.add(value.immutable());
         }

         return new DataList(copy, true);
      }
   }

   public IData update(IData data) {
      if (this.immutable) {
         data = data.immutable();
      }

      List<IData> result = new ArrayList();
      result.addAll(this.values);
      result.addAll(data.asList());
      return new DataList(result, this.immutable);
   }

   public <T> T convert(IDataConverter<T> converter) {
      return converter.fromList(this.values);
   }

   public IData add(IData other) {
      return this.update(other);
   }

   public IData sub(IData other) {
      throw new UnsupportedOperationException("Cannot subtract from a list");
   }

   public IData mul(IData other) {
      throw new UnsupportedOperationException("Cannot multiply a list");
   }

   public IData div(IData other) {
      throw new UnsupportedOperationException("Cannot divide a list");
   }

   public IData mod(IData other) {
      throw new UnsupportedOperationException("Cannot perform modulo on a list");
   }

   public IData and(IData other) {
      throw new UnsupportedOperationException("Lists don't support bitwise operators");
   }

   public IData or(IData other) {
      throw new UnsupportedOperationException("Lists don't support bitwise operators");
   }

   public IData xor(IData other) {
      throw new UnsupportedOperationException("Lists don't support bitwise operators");
   }

   public IData neg() {
      throw new UnsupportedOperationException("Cannot negate a list");
   }

   public IData not() {
      throw new UnsupportedOperationException("Lists don't support bitwise operators");
   }

   public String toString() {
      return this.asString();
   }
}
