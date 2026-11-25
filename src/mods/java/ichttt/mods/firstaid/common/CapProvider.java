package ichttt.mods.firstaid.common;

import ichttt.mods.firstaid.api.CapabilityExtendedHealthSystem;
import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class CapProvider implements ICapabilitySerializable<NBTTagCompound> {
   public static final ResourceLocation IDENTIFIER = new ResourceLocation("firstaid", "capExtendedHealthSystem");
   public static final Set<String> tutorialDone = new HashSet();
   private final AbstractPlayerDamageModel damageModel;

   public CapProvider(AbstractPlayerDamageModel damageModel) {
      this.damageModel = damageModel;
   }

   public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
      return capability == CapabilityExtendedHealthSystem.INSTANCE;
   }

   @Nullable
   public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
      return (T)(capability == CapabilityExtendedHealthSystem.INSTANCE ? CapabilityExtendedHealthSystem.INSTANCE.cast(this.damageModel) : null);
   }

   public NBTTagCompound serializeNBT() {
      return (NBTTagCompound)this.damageModel.serializeNBT();
   }

   public void deserializeNBT(NBTTagCompound nbt) {
      this.damageModel.deserializeNBT(nbt);
   }
}
