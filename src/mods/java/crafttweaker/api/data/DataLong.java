package crafttweaker.api.data;

import java.util.List;
import java.util.Map;

public class DataLong implements IData {
   private final long value;

   public DataLong(long value) {
      this.value = value;
   }

   public boolean asBool() {
      return this.value == 1L;
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
      return this.value;
   }

   public float asFloat() {
      return (float)this.value;
   }

   public double asDouble() {
      return (double)this.value;
   }

   public String asString() {
      return Long.toString(this.value);
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
      throw new UnsupportedOperationException("A long is not indexable");
   }

   public void setAt(int i, IData value) {
      throw new UnsupportedOperationException("A long is not indexable");
   }

   public IData memberGet(String name) {
      throw new UnsupportedOperationException("A long is not indexable");
   }

   public void memberSet(String name, IData data) {
      throw new UnsupportedOperationException("A long is not indexable");
   }

   public int length() {
      return 0;
   }

   public boolean contains(IData data) {
      return data.asLong() == this.value;
   }

   public boolean equals(IData data) {
      return this.value == data.asLong();
   }

   public int compareTo(IData data) {
      return Long.compare(this.value, data.asLong());
   }

   public IData immutable() {
      return this;
   }

   public IData update(IData data) {
      return data;
   }

   public <T> T convert(IDataConverter<T> converter) {
      return converter.fromLong(this.value);
   }

   public IData add(IData other) {
      return new DataLong(this.value + other.asLong());
   }

   public IData sub(IData other) {
      return new DataLong(this.value - other.asLong());
   }

   public IData mul(IData other) {
      return new DataLong(this.value * other.asLong());
   }

   public IData div(IData other) {
      return new DataLong(this.value / other.asLong());
   }

   public IData mod(IData other) {
      return new DataLong(this.value % other.asLong());
   }

   public IData and(IData other) {
      return new DataLong(this.value & other.asLong());
   }

   public IData or(IData other) {
      return new DataLong(this.value | other.asLong());
   }

   public IData xor(IData other) {
      return new DataLong(this.value ^ other.asLong());
   }

   public IData neg() {
      return new DataLong(-this.value);
   }

   public IData not() {
      return new DataLong(~this.value);
   }

   public String toString() {
      return this.asString() + " as long";
   }
}
