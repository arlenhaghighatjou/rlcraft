package crafttweaker.api.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DataByteArray implements IData {
   private final byte[] data;
   private final boolean immutable;

   public DataByteArray(byte[] data, boolean immutable) {
      this.data = data;
      this.immutable = immutable;
   }

   public IData add(IData other) {
      byte[] otherData = other.asByteArray();
      byte[] result = Arrays.copyOf(this.data, this.data.length + otherData.length);
      System.arraycopy(otherData, 0, result, this.data.length, otherData.length);
      return new DataByteArray(result, this.immutable);
   }

   public IData sub(IData other) {
      throw new UnsupportedOperationException("Cannot subtract from a byte array");
   }

   public IData mul(IData other) {
      throw new UnsupportedOperationException("Cannot multiply with an array");
   }

   public IData div(IData other) {
      throw new UnsupportedOperationException("Cannot divide an array");
   }

   public IData mod(IData other) {
      throw new UnsupportedOperationException("Cannot perform modulo on an array");
   }

   public IData and(IData other) {
      throw new UnsupportedOperationException("Arrays don't support bitwise operations");
   }

   public IData or(IData other) {
      throw new UnsupportedOperationException("Arrays don't support bitwise operations");
   }

   public IData xor(IData other) {
      throw new UnsupportedOperationException("Arrays don't support bitwise operations");
   }

   public IData neg() {
      throw new UnsupportedOperationException("Cannot negate arrays");
   }

   public IData not() {
      throw new UnsupportedOperationException("Arrays don't support bitwise operations");
   }

   public boolean asBool() {
      throw new IllegalDataException("Cannot cast an array to bool");
   }

   public byte asByte() {
      throw new IllegalDataException("Cannot cast an array to byte");
   }

   public short asShort() {
      throw new IllegalDataException("Cannot cast an array to short");
   }

   public int asInt() {
      throw new IllegalDataException("Cannot cast an array to int");
   }

   public long asLong() {
      throw new IllegalDataException("Cannot cast an array to long");
   }

   public float asFloat() {
      throw new IllegalDataException("Cannot cast an array to float");
   }

   public double asDouble() {
      throw new IllegalDataException("Cannot cast an array to double");
   }

   public String asString() {
      StringBuilder result = new StringBuilder();
      result.append("[");
      boolean first = true;

      for(byte value : this.data) {
         if (first) {
            first = false;
         } else {
            result.append(", ");
         }

         result.append(value);
      }

      result.append("] as byte[]");
      return result.toString();
   }

   public List<IData> asList() {
      List<IData> result = new ArrayList();

      for(byte value : this.data) {
         result.add(new DataByte(value));
      }

      return result;
   }

   public Map<String, IData> asMap() {
      return null;
   }

   public byte[] asByteArray() {
      return this.data;
   }

   public int[] asIntArray() {
      int[] result = new int[this.data.length];

      for(int i = 0; i < result.length; ++i) {
         result[i] = this.data[i];
      }

      return result;
   }

   public IData getAt(int i) {
      return new DataByte(this.data[i]);
   }

   public void setAt(int i, IData value) {
      if (this.immutable) {
         throw new UnsupportedOperationException("Cannot modify this byte array");
      } else {
         this.data[i] = value.asByte();
      }
   }

   public IData memberGet(String name) {
      throw new UnsupportedOperationException("byte[] doesn't have members");
   }

   public void memberSet(String name, IData data) {
      throw new UnsupportedOperationException("cannot set byte[] members");
   }

   public int length() {
      return this.data.length;
   }

   public boolean contains(IData data) {
      return this.equals(data);
   }

   public int compareTo(IData data) {
      throw new UnsupportedOperationException("Cannot compare arrays");
   }

   public boolean equals(IData data) {
      return Arrays.equals(this.data, data.asByteArray());
   }

   public IData immutable() {
      return this.immutable ? this : new DataByteArray(Arrays.copyOf(this.data, this.data.length), true);
   }

   public IData update(IData data) {
      return data;
   }

   public <T> T convert(IDataConverter<T> converter) {
      return converter.fromByteArray(this.data);
   }

   public String toString() {
      return this.asString() + " as byte[]";
   }
}
