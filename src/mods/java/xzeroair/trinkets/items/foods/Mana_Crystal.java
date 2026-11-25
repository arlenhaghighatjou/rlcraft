package xzeroair.trinkets.items.foods;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.capabilities.magic.MagicStats;
import xzeroair.trinkets.init.ModItems;
import xzeroair.trinkets.items.base.FoodBase;
import xzeroair.trinkets.util.Reference;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.helpers.TranslationHelper;

public class Mana_Crystal extends FoodBase {
   public Mana_Crystal(String name) {
      super(name, 2, 1.0F);
      this.setAlwaysEdible();
      this.setUUID("5569090a-43b2-468e-918a-05df8570277c");
   }

   public int getMaxItemUseDuration(ItemStack stack) {
      return 32;
   }

   @SideOnly(Side.CLIENT)
   protected String customItemInformation(ItemStack stack, World world, ITooltipFlag flagIn, int index, String translation) {
      TranslationHelper helper = TranslationHelper.INSTANCE;
      TranslationHelper.KeyEntry key = new TranslationHelper.OptionEntry("mpmax", "10");
      return helper.formatAddVariables(translation, key);
   }

   public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entity) {
      MagicStats magic = Capabilities.getMagicStats(entity);
      if (magic != null) {
         double currentBonus = magic.getBonusMana();
         magic.setBonusMana(currentBonus + 1.0D);
      }

      return super.onItemUseFinish(stack, worldIn, entity);
   }

   public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      if (TrinketsConfig.SERVER.mana.crystalExplodes) {
         IBlockState state = worldIn.getBlockState(pos);
         if (Reference.random.nextInt(10) == 0 && state.getMaterial() == Material.ROCK && state.isNormalCube() && facing == EnumFacing.UP) {
            if (!worldIn.isRemote) {
               ItemStack itemstack = player.getHeldItem(hand);
               itemstack.shrink(1);
               worldIn.spawnEntity(new EntityItem(worldIn, (double)pos.getX() + 0.5D, (double)(pos.getY() + 1), (double)pos.getZ() + 0.5D, new ItemStack(ModItems.foods.mana_reagent, 1)));
               worldIn.createExplosion((Entity)null, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), 3.0F, false);
               worldIn.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }

            return EnumActionResult.SUCCESS;
         } else {
            if (!worldIn.isRemote) {
               worldIn.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_GLASS_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }

            return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
         }
      } else {
         return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
      }
   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand handIn) {
      return super.onItemRightClick(worldIn, player, handIn);
   }

   public EnumAction getItemUseAction(ItemStack stack) {
      return EnumAction.EAT;
   }

   @SideOnly(Side.CLIENT)
   public void registerModels() {
      super.registerModels();
   }
}
