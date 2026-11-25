package me.ichun.mods.ichunutil.common.block;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;

public class BlockCompactPorkchop extends Block {
   public BlockCompactPorkchop() {
      super(Material.CAKE);
      this.blockSoundType = new SoundType(0.8F, 1.0F, SoundEvents.ENTITY_PIG_AMBIENT, SoundEvents.ENTITY_PIG_AMBIENT, SoundEvents.ENTITY_PIG_AMBIENT, SoundEvents.ENTITY_PIG_AMBIENT, SoundEvents.ENTITY_PIG_AMBIENT) {
         public EaglercraftRandom rand = new EaglercraftRandom();

         public float getPitch() {
            return (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F;
         }
      };
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
      this.setHardness(0.8F);
      this.setRegistryName(new ResourceLocation("ichunutil", "compact_porkchop"));
      this.setUnlocalizedName("ichunutil.block.compactporkchop");
   }

   public Block setBlockUnbreakable() {
      return super.setBlockUnbreakable();
   }
}
