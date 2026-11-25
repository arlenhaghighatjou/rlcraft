package ejektaflex.bountiful.api.logic.picked;

import com.google.gson.annotations.Expose;
import ejektaflex.bountiful.api.BountifulAPI;
import ejektaflex.bountiful.api.ext.ExtStringKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.registry.EntityEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010!\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0010\u0006\n\u0002\b\u0003\b\u0086\b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\t\u0010-\u001a\u00020\u0012H\u0094\u0001J\t\u0010.\u001a\u00020\u0003HÆ\u0003J\u0013\u0010/\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÆ\u0001J\u0010\u00100\u001a\u0002012\u0006\u0010&\u001a\u00020'H\u0016J\u0013\u00102\u001a\u0002032\b\u00104\u001a\u0004\u0018\u00010\u0012HÖ\u0003J\t\u00105\u001a\u00020\u0006HÖ\u0001J\b\u00106\u001a\u000203H\u0016J\u000f\u00107\u001a\b\u0012\u0004\u0012\u00020\f0!H\u0096\u0001J\b\u00108\u001a\u00020'H\u0016J\b\u00109\u001a\u00020:H\u0016J\b\u0010;\u001a\u00020\fH\u0016J\t\u0010<\u001a\u00020\u0001H\u0096\u0001R\u0018\u0010\u0005\u001a\u00020\u0006X\u0096\u000f¢\u0006\f\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\nR\u0018\u0010\u000b\u001a\u00020\fX\u0096\u000f¢\u0006\f\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010R\u0016\u0010\u0011\u001a\u0004\u0018\u00010\u00128VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0013\u0010\u0014R\u0013\u0010\u0015\u001a\u0004\u0018\u00010\u00168F¢\u0006\u0006\u001a\u0004\b\u0017\u0010\u0018R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u001a\u0010\u001b\u001a\u00020\u0006X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001c\u0010\b\"\u0004\b\u001d\u0010\nR\u0014\u0010\u001e\u001a\u00020\f8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u001f\u0010\u000eR \u0010 \u001a\n\u0012\u0004\u0012\u00020\f\u0018\u00010!X\u0096\u000f¢\u0006\f\u001a\u0004\b\"\u0010#\"\u0004\b$\u0010%R\u0014\u0010&\u001a\u0004\u0018\u00010'X\u0096\u0005¢\u0006\u0006\u001a\u0004\b(\u0010)R\u0018\u0010*\u001a\u00020\u0006X\u0096\u000f¢\u0006\f\u001a\u0004\b+\u0010\b\"\u0004\b,\u0010\n¨\u0006="},
   d2 = {"Lejektaflex/bountiful/api/logic/picked/PickedEntryEntity;", "Lejektaflex/bountiful/api/logic/picked/IPickedEntry;", "genericPick", "Lejektaflex/bountiful/api/logic/picked/PickedEntry;", "(Lejektaflex/bountiful/api/logic/picked/PickedEntry;)V", "amount", "", "getAmount", "()I", "setAmount", "(I)V", "content", "", "getContent", "()Ljava/lang/String;", "setContent", "(Ljava/lang/String;)V", "contentObj", "", "getContentObj", "()Ljava/lang/Object;", "entityEntry", "Lnet/minecraftforge/fml/common/registry/EntityEntry;", "getEntityEntry", "()Lnet/minecraftforge/fml/common/registry/EntityEntry;", "getGenericPick", "()Lejektaflex/bountiful/api/logic/picked/PickedEntry;", "killedAmount", "getKilledAmount", "setKilledAmount", "prettyContent", "getPrettyContent", "stages", "", "getStages", "()Ljava/util/List;", "setStages", "(Ljava/util/List;)V", "tag", "Lnet/minecraft/nbt/NBTTagCompound;", "getTag", "()Lnet/minecraft/nbt/NBTTagCompound;", "weight", "getWeight", "setWeight", "clone", "component1", "copy", "deserializeNBT", "", "equals", "", "other", "hashCode", "isValid", "requiredStages", "serializeNBT", "timeMult", "", "toString", "typed", "Bountiful_api"}
)
public final class PickedEntryEntity implements IPickedEntry {
   private int killedAmount;
   @Expose(
      serialize = false,
      deserialize = false
   )
   @NotNull
   private final PickedEntry genericPick;

   public double timeMult() {
      return BountifulAPI.INSTANCE.getConfig().getEntityTimeMult();
   }

   public final int getKilledAmount() {
      return this.killedAmount;
   }

   public final void setKilledAmount(int var1) {
      this.killedAmount = <set-?>;
   }

   public void deserializeNBT(@NotNull NBTTagCompound tag) {
      Intrinsics.checkParameterIsNotNull(tag, "tag");
      this.genericPick.deserializeNBT(tag);
      this.killedAmount = tag.getInteger("killedAmount");
   }

   @NotNull
   public NBTTagCompound serializeNBT() {
      NBTTagCompound var1 = this.genericPick.serializeNBT();
      var1.setInteger("killedAmount", this.killedAmount);
      return var1;
   }

   @Nullable
   public final EntityEntry getEntityEntry() {
      return ExtStringKt.getToEntityEntry(this.getContent());
   }

   @Nullable
   public Object getContentObj() {
      return this.getEntityEntry();
   }

   @NotNull
   public String getPrettyContent() {
      StringBuilder var10000 = (new StringBuilder()).append('(').append(this.killedAmount).append('/').append(this.getAmount()).append(") §a");
      StringBuilder var10001 = (new StringBuilder()).append("entity.");
      EntityEntry var10002 = this.getEntityEntry();
      return var10000.append(I18n.format(var10001.append(var10002 != null ? var10002.getName() : null).append(".name").toString(), new Object[0])).append(" Kills§r").toString();
   }

   @NotNull
   public String toString() {
      return "PickedEntry (Entity) [Entity: " + this.getContent() + ", Amount: " + this.getAmount() + ", Weight: " + this.getWeight() + ']';
   }

   public boolean isValid() {
      return this.getContentObj() != null;
   }

   @NotNull
   public final PickedEntry getGenericPick() {
      return this.genericPick;
   }

   public PickedEntryEntity(@NotNull PickedEntry genericPick) {
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

   @NotNull
   public List<String> requiredStages() {
      return this.genericPick.requiredStages();
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
   public final PickedEntryEntity copy(@NotNull PickedEntry genericPick) {
      Intrinsics.checkParameterIsNotNull(genericPick, "genericPick");
      return new PickedEntryEntity(genericPick);
   }

   public int hashCode() {
      return this.genericPick != null ? this.genericPick.hashCode() : 0;
   }

   public boolean equals(@Nullable Object var1) {
      if (this != var1) {
         if (var1 instanceof PickedEntryEntity) {
            PickedEntryEntity var2 = (PickedEntryEntity)var1;
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
