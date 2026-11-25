package vazkii.quark.misc.item;

import javax.annotation.Nonnull;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import vazkii.arl.item.ItemMod;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.base.item.IQuarkItem;

public class ItemSlimeBucket extends ItemMod implements IQuarkItem {
   public static final String TAG_ENTITY_DATA = "slime_nbt";
   public static final String[] VARIANTS = new String[]{"slime_bucket_normal", "slime_bucket_excited"};

   public ItemSlimeBucket() {
      super("slime_bucket", VARIANTS);
      this.setMaxStackSize(1);
      this.setCreativeTab(CreativeTabs.MISC);
      this.setContainerItem(Items.BUCKET);
   }

   public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> subItems) {
      if (this.isInCreativeTab(tab)) {
         subItems.add(new ItemStack(this));
      }

   }

   public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
      if (!world.isRemote) {
         int x = MathHelper.floor(entity.posX);
         int z = MathHelper.floor(entity.posZ);
         boolean slime = isSlimeChunk(world, x, z);
         int meta = stack.getItemDamage();
         int newMeta = slime ? 1 : 0;
         if (meta != newMeta) {
            stack.setItemDamage(newMeta);
         }
      }

   }

   @Nonnull
   public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      double x = (double)pos.getX() + 0.5D + (double)facing.getFrontOffsetX();
      double y = (double)pos.getY() + 0.5D + (double)facing.getFrontOffsetY();
      double z = (double)pos.getZ() + 0.5D + (double)facing.getFrontOffsetZ();
      if (!worldIn.isRemote) {
         EntitySlime slime = new EntitySlime(worldIn);
         NBTTagCompound data = ItemNBTHelper.getCompound(playerIn.getHeldItem(hand), "slime_nbt", true);
         if (data != null) {
            slime.readFromNBT(data);
         } else {
            slime.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(1.0D);
            slime.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
            slime.setHealth(slime.getMaxHealth());
         }

         slime.setPosition(x, y, z);
         worldIn.spawnEntity(slime);
         playerIn.swingArm(hand);
      }

      playerIn.setHeldItem(hand, new ItemStack(Items.BUCKET));
      return EnumActionResult.SUCCESS;
   }

   @Nonnull
   public String getItemStackDisplayName(@Nonnull ItemStack stack) {
      if (stack.hasTagCompound()) {
         NBTTagCompound cmp = ItemNBTHelper.getCompound(stack, "slime_nbt", false);
         if (cmp != null && cmp.hasKey("CustomName")) {
            return I18n.format("item.quark:slime_bucket_named.name", new Object[]{cmp.getString("CustomName")});
         }
      }

      return super.getItemStackDisplayName(stack);
   }

   public static boolean isSlimeChunk(World world, int x, int z) {
      Chunk chunk = world.getChunkFromBlockCoords(new BlockPos(x, 0, z));
      return chunk.getRandomWithSeed(987234911L).nextInt(10) == 0;
   }
}
