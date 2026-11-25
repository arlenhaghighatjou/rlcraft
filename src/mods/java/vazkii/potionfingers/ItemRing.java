package vazkii.potionfingers;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.interf.IItemColorProvider;
import vazkii.arl.item.ItemMod;
import vazkii.arl.util.ItemNBTHelper;

public class ItemRing extends ItemMod implements IBauble, IItemColorProvider {
   private static final String TAG_POTION_EFFECT = "effect";
   private static final String[] VARIANTS = new String[]{"ring_disabled", "ring_enabled"};
   public static final int EFFECT_DURATION = 199;
   public static final int EFFECT_REFRESH_RATE = 99;

   public ItemRing() {
      super("ring", VARIANTS);
      this.setCreativeTab(CreativeTabs.BREWING);
      this.setMaxStackSize(1);
      this.setHasSubtypes(true);
   }

   public String getModNamespace() {
      return "potionfingers";
   }

   public String getUniqueModel() {
      return "ring";
   }

   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
      if (tab == this.getCreativeTab()) {
         subItems.add(new ItemStack(this));

         for(Potion p : PotionFingers.DEFAULT_EFFECTS) {
            subItems.add(getRingForPotion(p));
         }
      }

   }

   public boolean hasEffect(ItemStack stack) {
      return getPotion(stack) != null;
   }

   public String getItemStackDisplayName(ItemStack stack) {
      Potion p = getPotion(stack);
      String potionName = p == null ? "N/A" : I18n.translateToLocal(p.getName());
      return I18n.translateToLocalFormatted(this.getUnlocalizedNameInefficiently(stack) + ".name", potionName).trim();
   }

   public static ItemStack getRingForPotion(Potion potion) {
      String id = potion.getRegistryName().toString();
      ItemStack stack = new ItemStack(PotionFingers.ring, 1, 1);
      ItemNBTHelper.setString(stack, "effect", id);
      return stack;
   }

   public static Potion getPotion(ItemStack stack) {
      if (stack == null) {
         return null;
      } else {
         String effect = ItemNBTHelper.getString(stack, "effect", "");
         return effect.isEmpty() ? null : (Potion)Potion.REGISTRY.getObject(new ResourceLocation(effect));
      }
   }

   @SideOnly(Side.CLIENT)
   public IItemColor getItemColor() {
      return (stack, i) -> {
         if (i != 0) {
            Potion p = getPotion(stack);
            if (p != null) {
               return p.getLiquidColor();
            }
         }

         return 16777215;
      };
   }

   public void onEquipped(ItemStack itemstack, EntityLivingBase player) {
      this.updatePotionStatus(player, getPotion(itemstack), itemstack, false);
   }

   public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
      this.updatePotionStatus(player, getPotion(itemstack), itemstack, true);
   }

   public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
      Potion potion = getPotion(itemstack);
      if (potion != null && (player.ticksExisted % 99 == 0 || !player.isPotionActive(potion))) {
         this.updatePotionStatus(player, potion, itemstack, false);
      }

   }

   public void updatePotionStatus(EntityLivingBase player, Potion potion, ItemStack ring, boolean unequipping) {
      if (potion != null && !player.world.isRemote && player instanceof EntityPlayer) {
         IBaublesItemHandler inv = BaublesApi.getBaublesHandler((EntityPlayer)player);
         int level = -1;

         for(int slot : BaubleType.RING.getValidSlots()) {
            ItemStack ring1 = inv.getStackInSlot(slot);
            Potion potion1 = unequipping && ring == ring1 ? null : getPotion(ring1);
            if (potion1 == potion) {
               ++level;
            }
         }

         if (level > 1) {
            level = 1;
         }

         PotionEffect currentEffect = player.getActivePotionEffect(potion);
         int currentLevel = currentEffect != null ? currentEffect.getAmplifier() : -1;

         assert currentLevel <= level || currentEffect != null;

         if (unequipping && currentLevel > level && currentEffect.getDuration() <= 199) {
            player.removePotionEffect(potion);
         }

         if (level != -1) {
            player.addPotionEffect(new PotionEffect(potion, 199, level, true, false));
         }

      }
   }

   public BaubleType getBaubleType(ItemStack arg0) {
      return BaubleType.RING;
   }
}
