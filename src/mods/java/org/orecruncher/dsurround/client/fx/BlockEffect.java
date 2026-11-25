package org.orecruncher.dsurround.client.fx;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.expression.ExpressionEngine;
import org.orecruncher.lib.chunk.IBlockAccessEx;

@SideOnly(Side.CLIENT)
public abstract class BlockEffect implements ISpecialEffect {
   private int chance;
   protected String conditions;

   public BlockEffect() {
      this(100);
   }

   protected BlockEffect(int chance) {
      this.conditions = "";
      this.chance = chance;
   }

   @Nonnull
   public abstract BlockEffectType getEffectType();

   public void setConditions(@Nullable String conditions) {
      this.conditions = conditions == null ? "" : conditions.intern();
   }

   @Nonnull
   public String getConditions() {
      return this.conditions;
   }

   public void setChance(int chance) {
      this.chance = chance;
   }

   public int getChance() {
      return this.chance;
   }

   public boolean alwaysExecute() {
      return this.chance == 0;
   }

   public boolean canTrigger(@Nonnull IBlockAccessEx provider, @Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull Random random) {
      return !this.alwaysExecute() && random.nextInt(this.getChance()) != 0 ? false : ExpressionEngine.instance().check(this.getConditions());
   }

   public abstract void doEffect(@Nonnull IBlockAccessEx var1, @Nonnull IBlockState var2, @Nonnull BlockPos var3, @Nonnull Random var4);

   @Nonnull
   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("type: ").append(this.getEffectType().getName());
      builder.append(" conditions: [").append(this.getConditions()).append(']');
      builder.append("; chance:").append(this.getChance());
      builder.append(' ').append(this.getClass().getSimpleName());
      return builder.toString();
   }
}
