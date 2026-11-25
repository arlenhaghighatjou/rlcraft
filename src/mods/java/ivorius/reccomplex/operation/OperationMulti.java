package ivorius.reccomplex.operation;

import ivorius.ivtoolkit.tools.NBTTagLists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class OperationMulti implements Operation {
   public final List<Operation> operations = new ArrayList();

   public OperationMulti() {
   }

   public OperationMulti(List<Operation> operations) {
      this.operations.addAll(operations);
   }

   public OperationMulti(Operation... operations) {
      Collections.addAll(this.operations, operations);
   }

   public void perform(WorldServer world) {
      this.operations.forEach((o) -> {
         o.perform(world);
      });
   }

   public void update(World world, int ticks) {
      this.operations.forEach((o) -> {
         o.update(world, ticks);
      });
   }

   public boolean checkDead(ICommandSender target) {
      return this.operations.stream().anyMatch((o) -> {
         return o.checkDead(target);
      });
   }

   public void renderPreview(Operation.PreviewType previewType, World world, int ticks, float partialTicks) {
      for(int i = this.operations.size() - 1; i >= 0; --i) {
         ((Operation)this.operations.get(i)).renderPreview(previewType, world, ticks, partialTicks);
      }

   }

   public void readFromNBT(NBTTagCompound compound) {
      this.operations.clear();
      Stream var10000 = NBTTagLists.compoundsFrom(compound, "operations").stream().map(OperationRegistry::readOperation);
      List var10001 = this.operations;
      this.operations.getClass();
      var10000.forEach(var10001::add);
   }

   public void writeToNBT(NBTTagCompound compound) {
      NBTTagLists.writeTo(compound, "operations", (List)this.operations.stream().map(OperationRegistry::writeOperation).collect(Collectors.toList()));
   }
}
