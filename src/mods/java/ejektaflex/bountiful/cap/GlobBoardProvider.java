package ejektaflex.bountiful.cap;

import ejektaflex.bountiful.logic.BountyHolder;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0003J\u0010\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u0002H\u0016J/\u0010\t\u001a\u0004\u0018\u0001H\n\"\u0004\b\u0000\u0010\n2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u0002H\n0\f2\n\b\u0001\u0010\r\u001a\u0004\u0018\u00010\u000eH\u0016\u00a2\u0006\u0002\u0010\u000fJ\u001e\u0010\u0010\u001a\u00020\u00112\n\u0010\u000b\u001a\u0006\u0012\u0002\b\u00030\f2\b\u0010\r\u001a\u0004\u0018\u00010\u000eH\u0016J\b\u0010\u0012\u001a\u00020\u0002H\u0016R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0013"},
   d2 = {"Lejektaflex/bountiful/cap/GlobBoardProvider;", "Lnet/minecraftforge/common/capabilities/ICapabilitySerializable;", "Lnet/minecraft/nbt/NBTTagCompound;", "()V", "inst", "Lejektaflex/bountiful/cap/GlobalBoard;", "deserializeNBT", "", "tag", "getCapability", "T", "capability", "Lnet/minecraftforge/common/capabilities/Capability;", "facing", "Lnet/minecraft/util/EnumFacing;", "(Lnet/minecraftforge/common/capabilities/Capability;Lnet/minecraft/util/EnumFacing;)Ljava/lang/Object;", "hasCapability", "", "serializeNBT", "Bountiful"}
)
public final class GlobBoardProvider implements ICapabilitySerializable<NBTTagCompound> {
   private final GlobalBoard inst = new GlobalBoard((BountyHolder)null, 1, (DefaultConstructorMarker)null);

   @NotNull
   public NBTTagCompound serializeNBT() {
      Capability var10000 = CapManager.INSTANCE.getCAP_BOARD();
      IStorage var1 = var10000 != null ? var10000.getStorage() : null;
      if (var1 == null) {
         Intrinsics.throwNpe();
      }

      NBTBase var2 = var1.writeNBT(CapManager.INSTANCE.getCAP_BOARD(), this.inst, EnumFacing.UP);
      if (var2 == null) {
         throw new TypeCastException("null cannot be cast to non-null type net.minecraft.nbt.NBTTagCompound");
      } else {
         return (NBTTagCompound)var2;
      }
   }

   public void deserializeNBT(@NotNull NBTTagCompound tag) {
      Intrinsics.checkParameterIsNotNull(tag, "tag");
      Capability var10000 = CapManager.INSTANCE.getCAP_BOARD();
      IStorage var2 = var10000 != null ? var10000.getStorage() : null;
      if (var2 == null) {
         Intrinsics.throwNpe();
      }

      var2.readNBT(CapManager.INSTANCE.getCAP_BOARD(), this.inst, EnumFacing.UP, (NBTBase)tag);
   }

   @Nullable
   public <T> T getCapability(@NotNull Capability<T> capability, @javax.annotation.Nullable @Nullable EnumFacing facing) {
      Intrinsics.checkParameterIsNotNull(capability, "capability");
      return (T)(capability == CapManager.INSTANCE.getCAP_BOARD() ? CapManager.INSTANCE.getCAP_BOARD().cast(this.inst) : null);
   }

   public boolean hasCapability(@NotNull Capability<?> capability, @Nullable EnumFacing facing) {
      Intrinsics.checkParameterIsNotNull(capability, "capability");
      return capability == CapManager.INSTANCE.getCAP_BOARD();
   }

   public GlobBoardProvider() {
   }
}
