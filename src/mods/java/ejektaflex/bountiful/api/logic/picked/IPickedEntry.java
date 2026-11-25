package ejektaflex.bountiful.api.logic.picked;

import ejektaflex.bountiful.api.data.IValidatable;
import ejektaflex.bountiful.api.data.IWeighted;
import ejektaflex.bountiful.api.logic.IPickCommon;
import java.util.List;
import kotlin.Metadata;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u001a\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\u0006\n\u0002\b\u0002\bf\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u00012\u00020\u00032\u00020\u00042\u00020\u00052\u00020\u0006J\b\u0010\u0018\u001a\u00020\u0019H&J\b\u0010\u001a\u001a\u00020\u0000H&R\u0018\u0010\u0007\u001a\u00020\bX¦\u000e¢\u0006\f\u001a\u0004\b\t\u0010\n\"\u0004\b\u000b\u0010\fR\u0014\u0010\r\u001a\u0004\u0018\u00010\u000eX¦\u0004¢\u0006\u0006\u001a\u0004\b\u000f\u0010\u0010R\u0012\u0010\u0011\u001a\u00020\u0012X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0013\u0010\u0014R\u0014\u0010\u0015\u001a\u0004\u0018\u00010\u0002X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0016\u0010\u0017¨\u0006\u001b"},
   d2 = {"Lejektaflex/bountiful/api/logic/picked/IPickedEntry;", "Lnet/minecraftforge/common/util/INBTSerializable;", "Lnet/minecraft/nbt/NBTTagCompound;", "Lejektaflex/bountiful/api/logic/IPickCommon;", "Lejektaflex/bountiful/api/data/IValidatable;", "", "Lejektaflex/bountiful/api/data/IWeighted;", "amount", "", "getAmount", "()I", "setAmount", "(I)V", "contentObj", "", "getContentObj", "()Ljava/lang/Object;", "prettyContent", "", "getPrettyContent", "()Ljava/lang/String;", "tag", "getTag", "()Lnet/minecraft/nbt/NBTTagCompound;", "timeMult", "", "typed", "Bountiful_api"}
)
public interface IPickedEntry extends INBTSerializable<NBTTagCompound>, IPickCommon, IValidatable, Cloneable, IWeighted {
   int getAmount();

   void setAmount(int var1);

   @Nullable
   NBTTagCompound getTag();

   @NotNull
   IPickedEntry typed();

   @Nullable
   Object getContentObj();

   @NotNull
   String getPrettyContent();

   double timeMult();

   @Metadata(
      mv = {1, 1, 13},
      bv = {1, 0, 3},
      k = 3
   )
   public static final class DefaultImpls {
      @NotNull
      public static List<String> requiredStages(IPickedEntry $this) {
         return IPickCommon.DefaultImpls.requiredStages($this);
      }

      @NotNull
      public static Object clone(IPickedEntry $this) {
         return java.lang.Cloneable.DefaultImpls.clone($this);
      }
   }
}
