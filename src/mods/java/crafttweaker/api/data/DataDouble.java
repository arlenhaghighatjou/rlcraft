package crafttweaker.api.data;

import java.util.List;
import java.util.Map;

public class DataDouble implements IData {
   private final double value;

   public DataDouble(double value) {
      this.value = value;
   }

   public boolean asBool() {
      return this.value == 1.0D;
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
      return (float)this.value;
   }

   public double asDouble() {
      return this.value;
   }

   public String asString() {
      return Double.toString(this.value);
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
      throw new UnsupportedOperationException("A double is not indexable");
   }

   public void setAt(int i, IData value) {
      throw new UnsupportedOperationException("A double is not indexable");
   }

   public IData memberGet(String name) {
      throw new UnsupportedOperationException("A double is not indexable");
   }

   public void memberSet(String name, IData data) {
      throw new UnsupportedOperationException("A double is not indexable");
   }

   public int length() {
      return 0;
   }

   public boolean contains(IData data) {
      return data.asDouble() == this.value;
   }

   public boolean equals(IData other) {
      return this.value == other.asDouble();
   }

   public int compareTo(IData other) {
      return Double.compare(this.value, other.asDouble());
   }

   public IData immutable() {
      return this;
   }

   public IData update(IData data) {
      return data;
   }

   public <T> T convert(IDataConverter<T> converter) {
      return converter.fromDouble(this.value);
   }

   public IData add(IData other) {
      return new DataDouble(this.value + other.asDouble());
   }

   public IData sub(IData other) {
      return new DataDouble(this.value - other.asDouble());
   }

   public IData mul(IData other) {
      return new DataDouble(this.value * other.asDouble());
   }

   public IData div(IData other) {
      return new DataDouble(this.value / other.asDouble());
   }

   public IData mod(IData other) {
      return new DataDouble(this.value % other.asDouble());
   }

   public IData and(IData other) {
      throw new UnsupportedOperationException("Cannot perform bitwise operations on double");
   }

   public IData or(IData other) {
      throw new UnsupportedOperationException("Cannot perform bitwise operations on double");
   }

   public IData xor(IData other) {
      throw new UnsupportedOperationException("Cannot perform bitwise operations on double");
   }

   public IData neg() {
      return new DataDouble(-this.value);
   }

   public IData not() {
      throw new UnsupportedOperationException("Cannot perform bitwise operations on double");
   }

   public String toString() {
      return this.asString();
   }
}
