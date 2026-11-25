package wiresegal.classyhats.capability.data;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0003J.\u0010\u0004\u001a\u00020\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00020\u00072\u0006\u0010\b\u001a\u00020\u00022\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0016J&\u0010\r\u001a\u00020\f2\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00020\u00072\u0006\u0010\b\u001a\u00020\u00022\u0006\u0010\t\u001a\u00020\nH\u0016\u00a8\u0006\u000e"},
   d2 = {"Lwiresegal/classyhats/capability/data/ClassyHatStorage;", "Lnet/minecraftforge/common/capabilities/Capability$IStorage;", "Lwiresegal/classyhats/capability/data/IHatContainer;", "()V", "readNBT", "", "capability", "Lnet/minecraftforge/common/capabilities/Capability;", "instance", "side", "Lnet/minecraft/util/EnumFacing;", "nbt", "Lnet/minecraft/nbt/NBTBase;", "writeNBT", "classyhats"}
)
public final class ClassyHatStorage implements IStorage<IHatContainer> {
   @NotNull
   public NBTBase writeNBT(@NotNull Capability<IHatContainer> capability, @NotNull IHatContainer instance, @NotNull EnumFacing side) {
      Intrinsics.checkParameterIsNotNull(capability, "capability");
      Intrinsics.checkParameterIsNotNull(instance, "instance");
      Intrinsics.checkParameterIsNotNull(side, "side");
      NBTBase var10000 = instance.serializeNBT();
      Intrinsics.checkExpressionValueIsNotNull(var10000, "instance.serializeNBT()");
      return var10000;
   }

   public void readNBT(@NotNull Capability<IHatContainer> capability, @NotNull IHatContainer instance, @NotNull EnumFacing side, @NotNull NBTBase nbt) {
      Intrinsics.checkParameterIsNotNull(capability, "capability");
      Intrinsics.checkParameterIsNotNull(instance, "instance");
      Intrinsics.checkParameterIsNotNull(side, "side");
      Intrinsics.checkParameterIsNotNull(nbt, "nbt");
      NBTBase var10001 = nbt;
      if (!(nbt instanceof NBTTagCompound)) {
         var10001 = null;
      }

      NBTTagCompound var5 = (NBTTagCompound)var10001;
      if (var5 == null) {
         var5 = new NBTTagCompound();
      }

      instance.deserializeNBT((NBTBase)var5);
   }

   public ClassyHatStorage() {
   }
}
