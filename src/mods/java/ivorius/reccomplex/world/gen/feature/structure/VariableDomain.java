package ivorius.reccomplex.world.gen.feature.structure;

import gnu.trove.impl.unmodifiable.TUnmodifiableObjectByteMap;
import gnu.trove.map.TObjectByteMap;
import gnu.trove.map.hash.TObjectByteHashMap;
import gnu.trove.procedure.TObjectByteProcedure;
import ivorius.ivtoolkit.tools.NBTCompoundObject;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.nbt.NBTTagCompound;

public class VariableDomain implements NBTCompoundObject {
   protected final TObjectByteMap<String> variables = new TObjectByteHashMap();

   public VariableDomain() {
   }

   public VariableDomain(TObjectByteMap<String> variables) {
      this.variables.putAll(variables);
   }

   public void fill(VariableDomain domain) {
      this.variables.forEachEntry((a, b) -> {
         domain.variables.putIfAbsent(a, b);
         return true;
      });
   }

   public boolean get(String variable) {
      return this.variables.get(variable) != 0;
   }

   public boolean isSet(String variable) {
      return this.variables.containsKey(variable);
   }

   public VariableDomain set(String variable, boolean value) {
      this.variables.put(variable, (byte)(value ? 1 : 0));
      return this;
   }

   public Stream<VariableDomain> split(String variable) {
      return IntStream.of(new int[]{0, 1}).mapToObj((i) -> {
         VariableDomain copy = this.copy();
         copy.set(variable, i != 0);
         return copy;
      });
   }

   public TObjectByteMap<String> all() {
      return new TUnmodifiableObjectByteMap(this.variables);
   }

   public VariableDomain copy() {
      return new VariableDomain(this.variables);
   }

   public void readFromNBT(NBTTagCompound compound) {
      this.variables.clear();

      for(String s : compound.getKeySet()) {
         this.set(s, compound.getBoolean(s));
      }

   }

   public void writeToNBT(NBTTagCompound compound) {
      this.variables.forEachEntry((a, b) -> {
         compound.setBoolean(a, b != 0);
         return true;
      });
   }
}
