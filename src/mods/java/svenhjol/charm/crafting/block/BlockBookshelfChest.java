package svenhjol.charm.crafting.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmSounds;
import svenhjol.charm.crafting.feature.BookshelfChest;
import svenhjol.charm.crafting.tile.TileBookshelfChest;
import svenhjol.meson.MesonBlockTE;
import svenhjol.meson.iface.IMesonBlock;

public class BlockBookshelfChest extends MesonBlockTE<TileBookshelfChest> implements IMesonBlock {
   public static final PropertyInteger SLOTS = PropertyInteger.create("slots", 0, 9);

   public BlockBookshelfChest() {
      super(Material.WOOD, "bookshelf_chest");
      this.setHardness(BookshelfChest.hardness);
      this.setSoundType(SoundType.WOOD);
      this.setCreativeTab(CreativeTabs.DECORATIONS);
      this.setDefaultState(this.getBlockState().getBaseState().withProperty(SLOTS, Integer.valueOf(0)));
   }

   public String getModId() {
      return "charm";
   }

   public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
      ItemStack thisChest = new ItemStack(this, 1, 0);
      TileBookshelfChest chest = (TileBookshelfChest)this.getTileEntity(world, pos);
      if (this.validTileEntity(chest)) {
         this.dropsInventory(chest, 9, (World)world, pos);
         thisChest.setStackDisplayName(chest.getName());
      }

      drops.add(thisChest);
   }

   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      TileBookshelfChest chest = (TileBookshelfChest)this.getTileEntity(worldIn, pos);
      if (this.validTileEntity(chest)) {
         chest.setName(stack.getDisplayName());
      }

      super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      if (!world.isRemote) {
         TileBookshelfChest bookshelf = (TileBookshelfChest)this.getTileEntity(world, pos);
         if (!this.validTileEntity(bookshelf)) {
            return false;
         }

         if (player.isSneaking()) {
            return false;
         }

         if (bookshelf.hasLootTable()) {
            bookshelf.generateLoot(player);
         }

         player.openGui(Charm.instance, 1, world, pos.getX(), pos.getY(), pos.getZ());
      }

      if (world.isRemote) {
         player.playSound(CharmSounds.BOOKSHELF_OPEN, 1.0F, 1.0F);
      }

      return true;
   }

   public boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return true;
   }

   public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return 50;
   }

   public Class<TileBookshelfChest> getTileEntityClass() {
      return TileBookshelfChest.class;
   }

   @Nullable
   public TileBookshelfChest createTileEntity(World world, IBlockState state) {
      return new TileBookshelfChest();
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{SLOTS});
   }

   public int getMetaFromState(IBlockState state) {
      return ((Integer)state.getValue(SLOTS)).intValue();
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(SLOTS, Integer.valueOf(meta & 9));
   }

   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      int slots = this.getNumberOfOccupiedSlots(worldIn, pos);
      return state.withProperty(SLOTS, Integer.valueOf(slots));
   }

   public float getEnchantPowerBonus(World world, BlockPos pos) {
      return this.getNumberOfOccupiedSlots(world, pos) > 0 ? 1.0F : 0.0F;
   }

   @Nullable
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Items.BOOK;
   }

   public int quantityDropped(Random random) {
      return 3;
   }

   private int getNumberOfOccupiedSlots(IBlockAccess world, BlockPos pos) {
      TileBookshelfChest chest = (TileBookshelfChest)world.getTileEntity(pos);
      return this.getNumberOfOccupiedSlots(chest);
   }

   private int getNumberOfOccupiedSlots(TileBookshelfChest chest) {
      int slots = 0;
      if (this.validTileEntity(chest) && chest != null) {
         slots = chest.getNumberOfFilledSlots();
      }

      return slots;
   }
}
