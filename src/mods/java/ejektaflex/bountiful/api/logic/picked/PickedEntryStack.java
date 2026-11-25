package ejektaflex.bountiful.api.logic.picked;

import com.google.gson.annotations.Expose;
import ejektaflex.bountiful.api.ext.ExtStringKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000Z\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u0000\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010!\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0010\u0006\n\u0002\b\u0003\b\u0086\b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\t\u0010*\u001a\u00020\u0012H\u0094\u0001J\t\u0010+\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010,\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0019\u0010-\u001a\u00020.2\u000e\u0010/\u001a\n 0*\u0004\u0018\u00010$0$H\u0096\u0001J\u0013\u00101\u001a\u0002022\b\u00103\u001a\u0004\u0018\u00010\u0012H\u00d6\u0003J\t\u00104\u001a\u00020\u0006H\u00d6\u0001J\b\u00105\u001a\u000202H\u0016J\u000f\u00106\u001a\b\u0012\u0004\u0012\u00020\f0\u001eH\u0096\u0001J\u0011\u00107\u001a\n 0*\u0004\u0018\u00010$0$H\u0096\u0001J\b\u00108\u001a\u000209H\u0016J\b\u0010:\u001a\u00020\fH\u0016J\t\u0010;\u001a\u00020\u0001H\u0096\u0001R\u0018\u0010\u0005\u001a\u00020\u0006X\u0096\u000f\u00a2\u0006\f\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\nR\u0018\u0010\u000b\u001a\u00020\fX\u0096\u000f\u00a2\u0006\f\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010R\u0016\u0010\u0011\u001a\u0004\u0018\u00010\u00128VX\u0096\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0013\u0010\u0014R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0013\u0010\u0017\u001a\u0004\u0018\u00010\u00188F\u00a2\u0006\u0006\u001a\u0004\b\u0019\u0010\u001aR\u0014\u0010\u001b\u001a\u00020\f8VX\u0096\u0004\u00a2\u0006\u0006\u001a\u0004\b\u001c\u0010\u000eR \u0010\u001d\u001a\n\u0012\u0004\u0012\u00020\f\u0018\u00010\u001eX\u0096\u000f\u00a2\u0006\f\u001a\u0004\b\u001f\u0010 \"\u0004\b!\u0010\"R\u0014\u0010#\u001a\u0004\u0018\u00010$X\u0096\u0005\u00a2\u0006\u0006\u001a\u0004\b%\u0010&R\u0018\u0010'\u001a\u00020\u0006X\u0096\u000f\u00a2\u0006\f\u001a\u0004\b(\u0010\b\"\u0004\b)\u0010\n\u00a8\u0006<"},
   d2 = {"Lejektaflex/bountiful/api/logic/picked/PickedEntryStack;", "Lejektaflex/bountiful/api/logic/picked/IPickedEntry;", "genericPick", "Lejektaflex/bountiful/api/logic/picked/PickedEntry;", "(Lejektaflex/bountiful/api/logic/picked/PickedEntry;)V", "amount", "", "getAmount", "()I", "setAmount", "(I)V", "content", "", "getContent", "()Ljava/lang/String;", "setContent", "(Ljava/lang/String;)V", "contentObj", "", "getContentObj", "()Ljava/lang/Object;", "getGenericPick", "()Lejektaflex/bountiful/api/logic/picked/PickedEntry;", "itemStack", "Lnet/minecraft/item/ItemStack;", "getItemStack", "()Lnet/minecraft/item/ItemStack;", "prettyContent", "getPrettyContent", "stages", "", "getStages", "()Ljava/util/List;", "setStages", "(Ljava/util/List;)V", "tag", "Lnet/minecraft/nbt/NBTTagCompound;", "getTag", "()Lnet/minecraft/nbt/NBTTagCompound;", "weight", "getWeight", "setWeight", "clone", "component1", "copy", "deserializeNBT", "", "p0", "kotlin.jvm.PlatformType", "equals", "", "other", "hashCode", "isValid", "requiredStages", "serializeNBT", "timeMult", "", "toString", "typed", "Bountiful_api"}
)
public final class PickedEntryStack implements IPickedEntry {
   @Expose(
      serialize = false,
      deserialize = false
   )
   @NotNull
   private final PickedEntry genericPick;

   public double timeMult() {
      return 1.0D;
   }

   @Nullable
   public final ItemStack getItemStack() {
      ItemStack stack = ExtStringKt.getToItemStack(this.getContent());
      NBTTagCompound var10000 = this.getTag();
      if (var10000 != null) {
         NBTTagCompound var2 = var10000;
         if (stack != null) {
            stack.setTagCompound(var2);
         }
      }

      return stack;
   }

   @Nullable
   public Object getContentObj() {
      return this.getItemStack();
   }

   @NotNull
   public String getPrettyContent() {
      StringBuilder var10000 = (new StringBuilder()).append("\u00a7f").append(this.getAmount()).append("x \u00a7a");
      ItemStack var10001 = this.getItemStack();
      return var10000.append(var10001 != null ? var10001.getDisplayName() : null).append("\u00a7r").toString();
   }

   public boolean isValid() {
      return this.getContentObj() != null;
   }

   @NotNull
   public String toString() {
      return "PickedEntry (Stack) [Item: " + this.getContent() + ", Amount: " + this.getAmount() + ", NBT: " + this.getTag() + ", Weight: " + this.getWeight() + ", Stages: " + this.genericPick.getStages() + ']';
   }

   @NotNull
   public final PickedEntry getGenericPick() {
      return this.genericPick;
   }

   public PickedEntryStack(@NotNull PickedEntry genericPick) {
      Intrinsics.checkParameterIsNotNull(genericPick, "genericPick");
      super();
      this.genericPick = genericPick;
   }

   public int getAmount() {
      return this.genericPick.getAmount();
   }

   public void setAmount(int var1) {
      this.genericPick.setAmount(<set-?>);
   }

   @NotNull
   public String getContent() {
      return this.genericPick.getContent();
   }

   public void setContent(@NotNull String var1) {
      Intrinsics.checkParameterIsNotNull(<set-?>, "<set-?>");
      this.genericPick.setContent(<set-?>);
   }

   @Nullable
   public List<String> getStages() {
      return this.genericPick.getStages();
   }

   public void setStages(@Nullable List<String> var1) {
      this.genericPick.setStages(<set-?>);
   }

   @Nullable
   public NBTTagCompound getTag() {
      return this.genericPick.getTag();
   }

   public int getWeight() {
      return this.genericPick.getWeight();
   }

   public void setWeight(int var1) {
      this.genericPick.setWeight(<set-?>);
   }

   @NotNull
   public Object clone() {
      return this.genericPick.clone();
   }

   public void deserializeNBT(NBTTagCompound p0) {
      this.genericPick.deserializeNBT(p0);
   }

   @NotNull
   public List<String> requiredStages() {
      return this.genericPick.requiredStages();
   }

   public NBTTagCompound serializeNBT() {
      return this.genericPick.serializeNBT();
   }

   @NotNull
   public IPickedEntry typed() {
      return this.genericPick.typed();
   }

   @NotNull
   public final PickedEntry component1() {
      return this.genericPick;
   }

   @NotNull
   public final PickedEntryStack copy(@NotNull PickedEntry genericPick) {
      Intrinsics.checkParameterIsNotNull(genericPick, "genericPick");
      return new PickedEntryStack(genericPick);
   }

   public int hashCode() {
      return this.genericPick != null ? this.genericPick.hashCode() : 0;
   }

   public boolean equals(@Nullable Object var1) {
      if (this != var1) {
         if (var1 instanceof PickedEntryStack) {
            PickedEntryStack var2 = (PickedEntryStack)var1;
            if (Intrinsics.areEqual(this.genericPick, var2.genericPick)) {
               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }
}
