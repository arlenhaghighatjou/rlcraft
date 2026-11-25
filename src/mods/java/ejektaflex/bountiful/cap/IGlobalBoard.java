package ejektaflex.bountiful.cap;

import ejektaflex.bountiful.logic.BountyHolder;
import kotlin.Metadata;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\bf\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001R\u0012\u0010\u0003\u001a\u00020\u0004X\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0007"},
   d2 = {"Lejektaflex/bountiful/cap/IGlobalBoard;", "Lnet/minecraftforge/common/util/INBTSerializable;", "Lnet/minecraft/nbt/NBTTagCompound;", "holder", "Lejektaflex/bountiful/logic/BountyHolder;", "getHolder", "()Lejektaflex/bountiful/logic/BountyHolder;", "Bountiful"}
)
public interface IGlobalBoard extends INBTSerializable<NBTTagCompound> {
   @NotNull
   BountyHolder getHolder();
}
