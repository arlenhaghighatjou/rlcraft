package ichttt.mods.firstaid.api.debuff.builder;

import ichttt.mods.firstaid.api.enums.EnumDebuffSlot;
import java.util.function.BooleanSupplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.SoundEvent;

public interface IDebuffBuilder {
   @Nonnull
   IDebuffBuilder addSoundEffect(@Nullable SoundEvent var1);

   @Nonnull
   IDebuffBuilder addBound(float var1, int var2);

   @Nonnull
   IDebuffBuilder addEnableCondition(@Nullable BooleanSupplier var1);

   void register(@Nonnull EnumDebuffSlot var1);
}
