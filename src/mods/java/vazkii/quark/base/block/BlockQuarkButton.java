package vazkii.quark.base.block;

import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.item.ItemModBlock;

public class BlockQuarkButton extends BlockButton implements IQuarkBlock {
   private final String[] variants;
   private final String bareName;
   private final boolean wooden;

   public BlockQuarkButton(String name, boolean wooden) {
      super(wooden);
      this.wooden = wooden;
      this.bareName = name;
      this.variants = new String[]{this.bareName};
      this.setUnlocalizedName(this.bareName);
      this.setHardness(0.5F);
      this.setSoundType(wooden ? SoundType.WOOD : SoundType.STONE);
   }

   @Nonnull
   public Block setUnlocalizedName(@Nonnull String name) {
      super.setUnlocalizedName(name);
      this.register(name);
      return this;
   }

   public ItemBlock createItemInstance(ResourceLocation regName) {
      return new ItemModBlock(this, regName) {
         private final ItemStack button = new ItemStack(Blocks.WOODEN_BUTTON);

         public int getItemBurnTime(ItemStack itemStack) {
            return BlockQuarkButton.this.wooden ? TileEntityFurnace.getItemBurnTime(this.button) : super.getItemBurnTime(itemStack);
         }
      };
   }

   public String getBareName() {
      return this.bareName;
   }

   public String[] getVariants() {
      return this.variants;
   }

   @SideOnly(Side.CLIENT)
   public ItemMeshDefinition getCustomMeshDefinition() {
      return null;
   }

   public EnumRarity getBlockRarity(ItemStack stack) {
      return EnumRarity.COMMON;
   }

   public IProperty[] getIgnoredProperties() {
      return new IProperty[0];
   }

   public IProperty getVariantProp() {
      return null;
   }

   public Class getVariantEnum() {
      return null;
   }

   protected void playClickSound(EntityPlayer player, @Nonnull World worldIn, @Nonnull BlockPos pos) {
      if (this.wooden) {
         worldIn.playSound(player, pos, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.6F);
      } else {
         worldIn.playSound(player, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.6F);
      }

   }

   protected void playReleaseSound(@Nonnull World worldIn, @Nonnull BlockPos pos) {
      if (this.wooden) {
         worldIn.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.5F);
      } else {
         worldIn.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.5F);
      }

   }
}
