package slimeknights.mantle.item;

import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.BitSet;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemEdible extends ItemFood {
   private ItemMetaDynamic dynamic;
   protected Int2IntOpenHashMap foodLevels;
   protected Int2FloatOpenHashMap saturations;
   protected Int2ObjectOpenHashMap<PotionEffect[]> potionEffects;
   protected BitSet alwaysEdible;
   public boolean displayEffectsTooltip;

   public ItemEdible() {
      super(0, 0.0F, false);
      this.setHasSubtypes(true);
      this.dynamic = new ItemMetaDynamic();
      this.foodLevels = new Int2IntOpenHashMap();
      this.saturations = new Int2FloatOpenHashMap();
      this.potionEffects = new Int2ObjectOpenHashMap();
      this.alwaysEdible = new BitSet();
      this.displayEffectsTooltip = true;
   }

   public ItemStack addFood(int meta, int food, float saturation, String name, PotionEffect... effects) {
      return this.addFood(meta, food, saturation, name, effects.length > 0, effects);
   }

   public ItemStack addFood(int meta, int food, float saturation, String name, boolean alwaysEdible, PotionEffect... effects) {
      this.dynamic.addMeta(meta, name);
      this.foodLevels.put(meta, food);
      this.saturations.put(meta, saturation);
      this.potionEffects.put(meta, effects);
      this.alwaysEdible.set(meta, alwaysEdible);
      return new ItemStack(this, 1, meta);
   }

   public float getSaturationModifier(ItemStack stack) {
      return this.saturations.get(stack.getMetadata());
   }

   public int getHealAmount(ItemStack stack) {
      return this.foodLevels.get(stack.getMetadata());
   }

   protected void onFoodEaten(ItemStack stack, World worldIn, @Nonnull EntityPlayer player) {
      if (!worldIn.isRemote) {
         for(PotionEffect potion : (PotionEffect[])this.potionEffects.get(stack.getMetadata())) {
            player.addPotionEffect(new PotionEffect(potion.getPotion(), potion.getDuration(), potion.getAmplifier(), potion.getIsAmbient(), potion.doesShowParticles()));
         }
      }

   }

   @Nonnull
   public Item setUnlocalizedName(@Nonnull String unlocalizedName) {
      this.dynamic.setUnlocalizedName(unlocalizedName);
      return super.setUnlocalizedName(unlocalizedName);
   }

   @Nonnull
   public String getUnlocalizedName(ItemStack stack) {
      return this.dynamic.getUnlocalizedName(stack);
   }

   public int getMetadata(ItemStack stack) {
      return this.dynamic.getMetadata(stack);
   }

   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      this.dynamic.addInformation(stack, worldIn, tooltip, flagIn);
      int meta = stack.getMetadata();
      if (this.dynamic.isValid(meta) && this.displayEffectsTooltip) {
         for(PotionEffect potionEffect : (PotionEffect[])this.potionEffects.get(meta)) {
            tooltip.add(I18n.translateToLocal(potionEffect.getEffectName()).trim());
         }
      }

   }

   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
      if (this.isInCreativeTab(tab)) {
         for(int i = 0; i <= this.dynamic.availabilityMask.length; ++i) {
            if (this.dynamic.isValid(i)) {
               subItems.add(new ItemStack(this, 1, i));
            }
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public void registerItemModels() {
      this.dynamic.registerItemModels(this);
   }

   @Nonnull
   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull EnumHand hand) {
      ItemStack itemStackIn = playerIn.getHeldItem(hand);
      int meta = itemStackIn.getMetadata();
      if (this.dynamic.isValid(meta) && playerIn.canEat(this.alwaysEdible.get(itemStackIn.getMetadata()))) {
         playerIn.setActiveHand(hand);
         return new ActionResult(EnumActionResult.SUCCESS, itemStackIn);
      } else {
         return new ActionResult(EnumActionResult.FAIL, itemStackIn);
      }
   }
}
