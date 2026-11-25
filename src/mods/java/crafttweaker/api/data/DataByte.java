package crafttweaker.api.data;

import java.util.List;
import java.util.Map;

public class DataByte implements IData {
   private final byte value;

   public DataByte(byte value) {
      this.value = value;
   }

   public boolean asBool() {
      return this.value == 1;
   }

   public byte asByte() {
      return this.value;
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
      return Byte.toString(this.value);
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
      throw new UnsupportedOperationException("A byte is not indexable");
   }

   public void setAt(int i, IData value) {
      throw new UnsupportedOperationException("A byte is not indexable");
   }

   public IData memberGet(String name) {
      throw new UnsupportedOperationException("A byte is not indexable");
   }

   public void memberSet(String name, IData data) {
      throw new UnsupportedOperationException("A byte is not indexable");
   }

   public int length() {
      return 0;
   }

   public boolean contains(IData data) {
      return data.asByte() == this.value;
   }

   public boolean equals(IData data) {
      return this.value == data.asByte();
   }

   public int compareTo(IData data) {
      return Byte.compare(this.value, data.asByte());
   }

   public IData immutable() {
      return this;
   }

   public IData update(IData data) {
      return data;
   }

   public <T> T convert(IDataConverter<T> converter) {
      return converter.fromByte(this.value);
   }

   public IData add(IData other) {
      return new DataByte((byte)(this.value + other.asByte()));
   }

   public IData sub(IData other) {
      return new DataByte((byte)(this.value - other.asByte()));
   }

   public IData mul(IData other) {
      return new DataByte((byte)(this.value * other.asByte()));
   }

   public IData div(IData other) {
      return new DataByte((byte)(this.value / other.asByte()));
   }

   public IData mod(IData other) {
      return new DataByte((byte)(this.value % other.asByte()));
   }

   public IData and(IData other) {
      return new DataByte((byte)(this.value & other.asByte()));
   }

   public IData or(IData other) {
      return new DataByte((byte)(this.value | other.asByte()));
   }

   public IData xor(IData other) {
      return new DataByte((byte)(this.value ^ other.asByte()));
   }

   public IData neg() {
      return new DataByte((byte)(-this.value));
   }

   public IData not() {
      return new DataByte((byte)(~this.value));
   }

   public String toString() {
      return this.asString() + " as byte";
   }
}
