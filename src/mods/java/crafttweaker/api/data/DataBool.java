package crafttweaker.api.data;

import java.util.List;
import java.util.Map;

public class DataBool implements IData {
   private final boolean value;

   public DataBool(boolean value) {
      this.value = value;
   }

   public boolean asBool() {
      return this.value;
   }

   public byte asByte() {
      return (byte)(this.value ? 1 : 0);
   }

   public short asShort() {
      return (short)(this.value ? 1 : 0);
   }

   public int asInt() {
      return this.value ? 1 : 0;
   }

   public long asLong() {
      return this.value ? 1L : 0L;
   }

   public float asFloat() {
      return this.value ? 1.0F : 0.0F;
   }

   public double asDouble() {
      return this.value ? 1.0D : 0.0D;
   }

   public String asString() {
      return Boolean.toString(this.value);
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
      throw new UnsupportedOperationException("a bool is not indexable");
   }

   public void setAt(int i, IData value) {
      throw new UnsupportedOperationException("a bool is not indexable");
   }

   public IData memberGet(String name) {
      throw new UnsupportedOperationException("a bool is not indexable");
   }

   public void memberSet(String name, IData data) {
      throw new UnsupportedOperationException("a bool is not indexable");
   }

   public int length() {
      return 0;
   }

   public boolean contains(IData data) {
      return data.asBool() == this.value;
   }

   public boolean equals(IData other) {
      return this.value == other.asBool();
   }

   public int compareTo(IData other) {
      throw new UnsupportedOperationException("Cannot compare bool values");
   }

   public IData immutable() {
      return this;
   }

   public IData update(IData data) {
      return data;
   }

   public <T> T convert(IDataConverter<T> converter) {
      return converter.fromBool(this.value);
   }

   public IData add(IData other) {
      throw new UnsupportedOperationException("Cannot add bool values");
   }

   public IData sub(IData other) {
      throw new UnsupportedOperationException("Cannot subtract bool values");
   }

   public IData mul(IData other) {
      throw new UnsupportedOperationException("Cannot multiply bool values");
   }

   public IData div(IData other) {
      throw new UnsupportedOperationException("Cannot divide bool values");
   }

   public IData mod(IData other) {
      throw new UnsupportedOperationException("Cannot perform modulo on bool values");
   }

   public IData and(IData other) {
      return new DataBool(this.value & other.asBool());
   }

   public IData or(IData other) {
      return new DataBool(this.value | other.asBool());
   }

   public IData xor(IData other) {
      return new DataBool(this.value ^ other.asBool());
   }

   public IData neg() {
      throw new UnsupportedOperationException("Cannot negate a bool value");
   }

   public IData not() {
      return new DataBool(!this.value);
   }

   public String toString() {
      return this.asString();
   }
}
