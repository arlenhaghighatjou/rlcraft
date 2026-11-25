package ichttt.mods.firstaid.api.damagesystem;

import ichttt.mods.firstaid.api.enums.EnumPlayerPart;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class AbstractDamageablePart implements INBTSerializable<NBTTagCompound> {
   public final int initialMaxHealth;
   public final boolean canCauseDeath;
   @Nonnull
   public final EnumPlayerPart part;
   @Nullable
   public AbstractPartHealer activeHealer;
   public float currentHealth;

   public AbstractDamageablePart(int maxHealth, boolean canCauseDeath, @Nonnull EnumPlayerPart playerPart) {
      this.initialMaxHealth = maxHealth;
      this.canCauseDeath = canCauseDeath;
      this.part = playerPart;
   }

   public abstract float heal(float var1, @Nullable EntityPlayer var2, boolean var3);

   public abstract float damage(float var1, @Nullable EntityPlayer var2, boolean var3);

   public abstract float damage(float var1, @Nullable EntityPlayer var2, boolean var3, float var4);

   public abstract void tick(World var1, EntityPlayer var2, boolean var3);

   public abstract void setAbsorption(float var1);

   public abstract float getAbsorption();

   public abstract void setMaxHealth(int var1);

   public abstract int getMaxHealth();
}
