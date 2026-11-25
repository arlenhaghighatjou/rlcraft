package wiresegal.classyhats.capability.data;

import kotlin.Metadata;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\bf\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001R\u0018\u0010\u0003\u001a\u00020\u0004X¦\u000e¢\u0006\f\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\bR\u0018\u0010\t\u001a\u00020\nX¦\u000e¢\u0006\f\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR\u0012\u0010\u000f\u001a\u00020\u0010X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0011\u0010\u0012R\u001a\u0010\u0013\u001a\u0004\u0018\u00010\u0014X¦\u000e¢\u0006\f\u001a\u0004\b\u0015\u0010\u0016\"\u0004\b\u0017\u0010\u0018¨\u0006\u0019"},
   d2 = {"Lwiresegal/classyhats/capability/data/IHatContainer;", "Lnet/minecraftforge/common/util/INBTSerializable;", "Lnet/minecraft/nbt/NBTTagCompound;", "currentHatSection", "", "getCurrentHatSection", "()I", "setCurrentHatSection", "(I)V", "equipped", "Lnet/minecraft/item/ItemStack;", "getEquipped", "()Lnet/minecraft/item/ItemStack;", "setEquipped", "(Lnet/minecraft/item/ItemStack;)V", "hats", "Lnet/minecraftforge/items/ItemStackHandler;", "getHats", "()Lnet/minecraftforge/items/ItemStackHandler;", "player", "Lnet/minecraft/entity/player/EntityPlayer;", "getPlayer", "()Lnet/minecraft/entity/player/EntityPlayer;", "setPlayer", "(Lnet/minecraft/entity/player/EntityPlayer;)V", "classyhats"}
)
public interface IHatContainer extends INBTSerializable<NBTTagCompound> {
   @NotNull
   ItemStackHandler getHats();

   @Nullable
   EntityPlayer getPlayer();

   void setPlayer(@Nullable EntityPlayer var1);

   @NotNull
   ItemStack getEquipped();

   void setEquipped(@NotNull ItemStack var1);

   int getCurrentHatSection();

   void setCurrentHatSection(int var1);
}
