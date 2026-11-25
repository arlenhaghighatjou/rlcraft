package net.silentchaos512.lib.block;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import net.minecraft.block.BlockOre;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;

public class BlockOreSL extends BlockOre {
   private final Item droppedItem;
   private final int quantityMin;
   private final int quantityMax;
   private final int xpMin;
   private final int xpMax;

   public BlockOreSL(Item droppedItem, int harvestLevel, int quantityMin, int quantityMax, int xpMin, int xpMax) {
      this.droppedItem = droppedItem;
      this.quantityMin = quantityMin;
      this.quantityMax = quantityMax;
      this.xpMin = xpMin;
      this.xpMax = xpMax;
      this.setHardness(3.0F);
      this.setResistance(15.0F);
      this.setSoundType(SoundType.STONE);
      this.setHarvestLevel("pickaxe", harvestLevel);
   }

   @Nonnull
   public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune) {
      return this.droppedItem;
   }

   public int quantityDropped(EaglercraftRandom random) {
      return this.quantityMin + random.nextInt(this.quantityMax - this.quantityMin + 1);
   }

   public int quantityDroppedWithBonus(int fortune, EaglercraftRandom random) {
      int quantity = this.quantityDropped(random);
      if (fortune > 0 && this.getItemDropped(this.getDefaultState(), random, fortune) != Item.getItemFromBlock(this)) {
         float bonus = this.bonusAmount(fortune, random);
         if (bonus < 0.0F) {
            bonus = 0.0F;
         }

         quantity = Math.round((float)quantity * (bonus + 1.0F));
      }

      return MathHelper.clamp(quantity, 0, 64);
   }

   public float bonusAmount(int fortune, EaglercraftRandom random) {
      return (float)(random.nextInt(fortune + 2) - 1);
   }

   public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune) {
      Item item = this.getItemDropped(world.getBlockState(pos), RANDOM, fortune);
      return item != Item.getItemFromBlock(this) ? this.xpMin + RANDOM.nextInt(this.xpMax - this.xpMin + 1) : 0;
   }
}
