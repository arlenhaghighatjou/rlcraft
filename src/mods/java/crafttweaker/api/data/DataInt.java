package crafttweaker.api.data;

import java.util.List;
import java.util.Map;

public class DataInt implements IData {
   private final int value;

   public DataInt(int value) {
      this.value = value;
   }

   public boolean asBool() {
      return this.value == 1;
   }

   public byte asByte() {
      return (byte)this.value;
   }

   public short asShort() {
      return (short)this.value;
   }

   public int asInt() {
      return this.value;
   }

   public long asLong() {
      return (long)this.value;
   }

   public float asFloat() {
      return (float)this.value;
   }

   public double asDouble() {
      return (double)this.value;
   }

   public String asString() {
      return Integer.toString(this.value);
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
      throw new RuntimeException("An int is not indexable");
   }

   public void setAt(int i, IData value) {
      throw new RuntimeException("An int is not indexable");
   }

   public IData memberGet(String name) {
      throw new RuntimeException("An int is not indexable");
   }

   public void memberSet(String name, IData data) {
      throw new RuntimeException("An int is not indexable");
   }

   public int length() {
      return 0;
   }

   public boolean contains(IData data) {
      return data.asInt() == this.value;
   }

   public boolean equals(IData data) {
      return this.value == data.asInt();
   }

   public int compareTo(IData data) {
      return Integer.compare(this.value, data.asInt());
   }

   public IData immutable() {
      return this;
   }

   public IData update(IData data) {
      return data;
   }

   public <T> T convert(IDataConverter<T> converter) {
      return converter.fromInt(this.value);
   }

   public IData add(IData other) {
      return new DataInt(this.value + other.asInt());
   }

   public IData sub(IData other) {
      return new DataInt(this.value - other.asInt());
   }

   public IData mul(IData other) {
      return new DataInt(this.value * other.asInt());
   }

   public IData div(IData other) {
      return new DataInt(this.value / other.asInt());
   }

   public IData mod(IData other) {
      return new DataInt(this.value % other.asInt());
   }

   public IData and(IData other) {
      return new DataInt(this.value & other.asInt());
   }

   public IData or(IData other) {
      return new DataInt(this.value | other.asInt());
   }

   public IData xor(IData other) {
      return new DataInt(this.value ^ other.asInt());
   }

   public IData neg() {
      return new DataInt(-this.value);
   }

   public IData not() {
      return new DataInt(~this.value);
   }

   public String toString() {
      return this.asString();
   }
}
