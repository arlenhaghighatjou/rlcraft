package svenhjol.charm.crafting.block;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmSounds;
import svenhjol.charm.crafting.feature.Crate;
import svenhjol.charm.crafting.tile.TileCrate;
import svenhjol.meson.Meson;
import svenhjol.meson.MesonBlock;
import svenhjol.meson.MesonBlockTE;
import svenhjol.meson.helper.EntityHelper;
import svenhjol.meson.helper.SoundHelper;
import svenhjol.meson.iface.IMesonBlock;
import svenhjol.meson.iface.IMesonEnum;

public class BlockCrate extends MesonBlockTE<TileCrate> implements IMesonBlock {
   public static PropertyEnum<MesonBlock.WoodVariant> VARIANT = PropertyEnum.create("variant", MesonBlock.WoodVariant.class);
   public BlockCrate.Type type;

   public BlockCrate(BlockCrate.Type type) {
      super(Material.WOOD, type.getName());
      this.setHardness(Crate.hardness);
      this.setResistance(10.0F);
      this.setSoundType(SoundType.WOOD);
      this.setCreativeTab(CreativeTabs.DECORATIONS);
      this.type = type;
   }

   public String getModId() {
      return "charm";
   }

   public String[] getVariants() {
      List<String> variants = new ArrayList();

      for(MesonBlock.WoodVariant variant : MesonBlock.WoodVariant.values()) {
         variants.add(variant.toString().toLowerCase());
      }

      return (String[])variants.toArray(new String[0]);
   }

   public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
   }

   public int getMaxStackSize() {
      return 1;
   }

   public void doItemSpawn(ItemStack stack, World world, BlockPos pos) {
      if (EntityHelper.itemHasEntityTag(stack)) {
         EntityHelper.spawnEntityFromItem(stack, world, pos);
      } else if (Block.getBlockFromItem(stack.getItem()) instanceof BlockTNT) {
         EntityTNTPrimed tnt = new EntityTNTPrimed(world, (double)((float)pos.getX() + 0.5F), (double)pos.getY(), (double)((float)pos.getZ() + 0.5F), (EntityLivingBase)null);
         if (world.spawnEntity(tnt)) {
            SoundHelper.playSoundAtPos(world, tnt.getPosition(), SoundEvents.ENTITY_TNT_PRIMED, 1.0F, 1.0F);
         }
      } else {
         super.doItemSpawn(stack, world, pos);
      }

   }

   public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
      if (world.isRemote && this.validTileEntity(this.getTileEntity(world, pos)) && this.isSealedCrate()) {
         SoundHelper.playSoundAtPos(world, pos, CharmSounds.WOOD_SMASH, 0.6F, 1.0F);
      }

   }

   public void breakBlock(World world, BlockPos pos, IBlockState state) {
      TileCrate crate = (TileCrate)this.getTileEntity(world, pos);
      if (this.validTileEntity(crate)) {
         if (this.isSealedCrate()) {
            this.dropsInventory(crate, 9, world, pos);
         } else {
            ItemStack stack = new ItemStack(Item.getItemFromBlock(this), 1, this.getMetaFromState(state));
            NBTTagCompound tag = new NBTTagCompound();
            crate.writeToNBT(tag);
            stack.setTagInfo("BlockEntityTag", tag);
            tag.removeTag("x");
            tag.removeTag("y");
            tag.removeTag("z");
            if (crate.hasCustomName()) {
               stack.setStackDisplayName(crate.getName());
            }

            spawnAsEntity(world, pos, stack);
         }
      }

      super.breakBlock(world, pos, state);
   }

   public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      TileCrate crate = (TileCrate)this.getTileEntity(world, pos);
      if (this.validTileEntity(crate)) {
         world.setBlockState(pos, state, 2);
         if (stack.getTagCompound() != null) {
            crate.setLootTable(stack.getTagCompound().getString("lootTable"));
            String name = stack.getDisplayName();
            if (!name.isEmpty()) {
               crate.setName(name);
            }

            this.onBlockAdded(world, pos, state);
         }
      } else {
         super.onBlockPlacedBy(world, pos, state, placer, stack);
      }

   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      TileCrate crate = (TileCrate)this.getTileEntity(world, pos);
      if (!this.validTileEntity(crate)) {
         return false;
      } else if (Meson.DEBUG && player.isCreative() && player.isSneaking() && player.getHeldItem(hand) == ItemStack.EMPTY) {
         this.setCreativeLootTable(world, pos);
         world.notifyBlockUpdate(pos, state, state, 2);
         return false;
      } else {
         if (crate.hasLootTable()) {
            crate.generateLoot(player);
         }

         if (this.isSealedCrate()) {
            return true;
         } else if (player.isSneaking()) {
            return false;
         } else {
            if (!world.isRemote) {
               player.openGui(Charm.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
            }

            if (world.isRemote && !this.isSealedCrate()) {
               player.playSound(CharmSounds.WOOD_OPEN, 1.0F, 1.0F);
            }

            return true;
         }
      }
   }

   public Class<TileCrate> getTileEntityClass() {
      return TileCrate.class;
   }

   @Nullable
   public TileCrate createTileEntity(World world, IBlockState state) {
      return new TileCrate();
   }

   public boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return !this.isSealedCrate();
   }

   public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return this.isSealedCrate() ? 0 : 50;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return true;
   }

   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.MODEL;
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{VARIANT});
   }

   public int getMetaFromState(IBlockState state) {
      return ((MesonBlock.WoodVariant)state.getValue(VARIANT)).ordinal();
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(VARIANT, MesonBlock.WoodVariant.byMetadata(meta));
   }

   public int damageDropped(IBlockState state) {
      return this.getMetaFromState(state);
   }

   public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
      return new ItemStack(this, 1, this.getMetaFromState(world.getBlockState(pos)));
   }

   public boolean isSealedCrate() {
      return this.type == BlockCrate.Type.CRATE_SEALED;
   }

   public void setCreativeLootTable(World world, BlockPos pos) {
      if (!world.isRemote) {
         float f = world.rand.nextFloat();
         Crate.RARITY rarity = Crate.RARITY.COMMON;
         if ((double)f <= 0.25D) {
            rarity = Crate.RARITY.RARE;
         } else if ((double)f <= 0.5D) {
            rarity = Crate.RARITY.VALUABLE;
         } else if ((double)f <= 0.75D) {
            rarity = Crate.RARITY.UNCOMMON;
         }

         TileCrate tile = (TileCrate)this.getTileEntity(world, pos);
         List<Crate.CrateType> types = (List)Crate.types.get(rarity);
         Crate.CrateType type = (Crate.CrateType)types.get(world.rand.nextInt(types.size()));
         int slots = tile.getInventory().getSlots();

         for(int i = 0; i < slots; ++i) {
            tile.getInventory().getStackInSlot(i).setCount(0);
         }

         tile.setLootTable(type.pool);
         tile.setName(type.pool.toString());
         tile.setShowName(true);
      } else {
         for(int i = 0; i < 8; ++i) {
            double d0 = world.rand.nextGaussian() * 0.02D;
            double d1 = world.rand.nextGaussian() * 0.02D;
            double d2 = world.rand.nextGaussian() * 0.02D;
            double dx = (double)((float)pos.getX() + MathHelper.clamp(world.rand.nextFloat(), 0.25F, 0.75F));
            double dy = (double)((float)pos.getY() + 1.35F);
            double dz = (double)((float)pos.getZ() + MathHelper.clamp(world.rand.nextFloat(), 0.25F, 0.75F));
            world.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, dx, dy, dz, d0, d1, d2, new int[0]);
         }
      }

   }

   public static enum Type implements IMesonEnum {
      CRATE,
      CRATE_SEALED;

      private Type() {
      }
   }
}
