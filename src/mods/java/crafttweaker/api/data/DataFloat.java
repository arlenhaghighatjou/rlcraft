package crafttweaker.api.data;

import java.util.List;
import java.util.Map;

public class DataFloat implements IData {
   private final float value;

   public DataFloat(float value) {
      this.value = value;
   }

   public boolean asBool() {
      return this.value == 1.0F;
   }

   public byte asByte() {
      return (byte)((int)this.value);
   }

   public short asShort() {
      return (short)((int)this.value);
   }

   public int asInt() {
      return (int)this.value;
   }

   public long asLong() {
      return (long)this.value;
   }

   public float asFloat() {
      return this.value;
   }

   public double asDouble() {
      return (double)this.value;
   }

   public String asString() {
      return Float.toString(this.value);
   }

   public List<IData> asList() {
      return null;
   }

   public Map<String, IData> asMap() {
      return null;
   }

   public byte[] asByteArray() {
      return null;
   }

   public int[] asIntArray() {
      return null;
   }

   public IData getAt(int i) {
      throw new UnsupportedOperationException("A float is not indexable");
   }

   public void setAt(int i, IData value) {
      throw new UnsupportedOperationException("A float is not indexable");
   }

   public IData memberGet(String name) {
      throw new UnsupportedOperationException("A float is not indexable");
   }

   public void memberSet(String name, IData data) {
      throw new UnsupportedOperationException("A float is not indexable");
   }

   public int length() {
      return 0;
   }

   public boolean contains(IData data) {
      return data.asFloat() == this.value;
   }

   public boolean equals(IData data) {
      return this.value == data.asFloat();
   }

   public int compareTo(IData data) {
      return Float.compare(this.value, data.asFloat());
   }

   public IData immutable() {
      return this;
   }

   public IData update(IData data) {
      return data;
   }

   public <T> T convert(IDataConverter<T> converter) {
      return converter.fromFloat(this.value);
   }

   public IData add(IData other) {
      return new DataFloat(this.value + other.asFloat());
   }

   public IData sub(IData other) {
      return new DataFloat(this.value - other.asFloat());
   }

   public IData mul(IData other) {
      return new DataFloat(this.value * other.asFloat());
   }

   public IData div(IData other) {
      return new DataFloat(this.value / other.asFloat());
   }

   public IData mod(IData other) {
      return new DataFloat(this.value % other.asFloat());
   }

   public IData and(IData other) {
      throw new UnsupportedOperationException("Cannot perform bitwise operations on float");
   }

   public IData or(IData other) {
      throw new UnsupportedOperationException("Cannot perform bitwise operations on float");
   }

   public IData xor(IData other) {
      throw new UnsupportedOperationException("Cannot perform bitwise operations on float");
   }

   public IData neg() {
      return new DataFloat(-this.value);
   }

   public IData not() {
      throw new UnsupportedOperationException("Cannot perform bitwise operations on float");
   }

   public String toString() {
      return this.asString() + " as float";
   }
}
