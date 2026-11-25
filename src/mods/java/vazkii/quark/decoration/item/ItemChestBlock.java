package vazkii.quark.decoration.item;

import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.interf.IExtraVariantHolder;
import vazkii.arl.item.ItemModBlock;
import vazkii.arl.util.ModelHandler;
import vazkii.quark.decoration.block.BlockCustomChest;
import vazkii.quark.decoration.feature.VariedChests;
import vazkii.quark.decoration.tile.TileCustomChest;

public class ItemChestBlock extends ItemModBlock implements IExtraVariantHolder {
   public ItemChestBlock(Block block, ResourceLocation res) {
      super(block, res);
      this.setHasSubtypes(true);
   }

   @SideOnly(Side.CLIENT)
   public ItemMeshDefinition getCustomMeshDefinition() {
      return (stack) -> {
         return (ModelResourceLocation)ModelHandler.resourceLocations.get(this.getVariants()[0]);
      };
   }

   public String[] getExtraVariants() {
      return this.getVariants();
   }

   @Nonnull
   public String getUnlocalizedName(ItemStack stack) {
      VariedChests.ChestType type = VariedChests.custom_chest.getCustomType(stack);
      String name = type.name + (this.getBlock() == VariedChests.custom_chest_trap ? "_trap" : "");
      return "tile.quark:custom_chest_" + name;
   }

   public boolean placeBlockAt(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, World world, @Nonnull BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, @Nonnull IBlockState newState) {
      int typeCnt = 0;
      BlockPos posN = pos.north();
      BlockPos posS = pos.south();
      BlockPos posW = pos.west();
      BlockPos posE = pos.east();
      BlockCustomChest cChest = (BlockCustomChest)this.getBlock();
      VariedChests.ChestType myType = cChest.getCustomType(stack);
      if (world.getBlockState(posN).getBlock() == this.block && cChest.getCustomType(world, posN) == myType) {
         typeCnt += cChest.isDoubleChest(world, posN, myType) ? 2 : 1;
      }

      if (world.getBlockState(posS).getBlock() == this.block && cChest.getCustomType(world, posS) == myType) {
         typeCnt += cChest.isDoubleChest(world, posS, myType) ? 2 : 1;
      }

      if (world.getBlockState(posW).getBlock() == this.block && cChest.getCustomType(world, posW) == myType) {
         typeCnt += cChest.isDoubleChest(world, posW, myType) ? 2 : 1;
      }

      if (world.getBlockState(posE).getBlock() == this.block && cChest.getCustomType(world, posE) == myType) {
         typeCnt += cChest.isDoubleChest(world, posE, myType) ? 2 : 1;
      }

      if (typeCnt <= 1 && super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState)) {
         TileEntity te = world.getTileEntity(pos);
         if (te instanceof TileCustomChest) {
            ((TileCustomChest)te).chestType = myType;
            return true;
         }
      }

      return false;
   }

   public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> subItems) {
      BlockCustomChest chest = (BlockCustomChest)Block.getBlockFromItem(this);
      if (this.isInCreativeTab(tab)) {
         for(VariedChests.ChestType type : VariedChests.ChestType.VALID_TYPES) {
            subItems.add(chest.setCustomType(new ItemStack(this, 1), type));
         }
      }

   }
}
