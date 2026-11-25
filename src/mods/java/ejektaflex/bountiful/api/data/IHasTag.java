package ejektaflex.bountiful.api.data;

import kotlin.Metadata;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001R\u001a\u0010\u0002\u001a\u0004\u0018\u00010\u0003X¦\u000e¢\u0006\f\u001a\u0004\b\u0004\u0010\u0005\"\u0004\b\u0006\u0010\u0007R\u0016\u0010\b\u001a\u0004\u0018\u00010\t8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\n\u0010\u000b¨\u0006\f"},
   d2 = {"Lejektaflex/bountiful/api/data/IHasTag;", "", "nbtJson", "", "getNbtJson", "()Ljava/lang/String;", "setNbtJson", "(Ljava/lang/String;)V", "tag", "Lnet/minecraft/nbt/NBTTagCompound;", "getTag", "()Lnet/minecraft/nbt/NBTTagCompound;", "Bountiful_api"}
)
public interface IHasTag {
   @Nullable
   String getNbtJson();

   void setNbtJson(@Nullable String var1);

   @Nullable
   NBTTagCompound getTag();

   @Metadata(
      mv = {1, 1, 13},
      bv = {1, 0, 3},
      k = 3
   )
   public static final class DefaultImpls {
      @Nullable
      public static NBTTagCompound getTag(IHasTag $this) {
         String var1 = $this.getNbtJson();
         return var1 == null ? null : JsonToNBT.getTagFromJson(String.valueOf($this.getNbtJson()));
      }
   }
}
