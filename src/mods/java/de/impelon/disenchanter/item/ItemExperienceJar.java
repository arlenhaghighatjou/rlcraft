package de.impelon.disenchanter.item;

import de.impelon.disenchanter.DisenchanterConfig;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemExperienceJar extends Item {
   public static final String STORED_EXPERIENCE_KEY = "StoredExperience";
   public static final String EXPERIENCE_CAPACITY_KEY = "ExperienceCapacity";
   public static final String OVERLOAD_MODE_KEY = "Overload";

   public ItemExperienceJar() {
      this.setCreativeTab(CreativeTabs.MISC);
      this.setRegistryName("disenchanter", "experience_jar");
      this.setUnlocalizedName(this.getRegistryName().toString().toLowerCase());
      this.setMaxStackSize(1);
      this.addPropertyOverride(new ResourceLocation("disenchanter", "fill_level"), new IItemPropertyGetter() {
         @SideOnly(Side.CLIENT)
         public float apply(ItemStack stack, World world, EntityLivingBase entityIn) {
            ItemExperienceJar.ensureValidTag(stack);
            return ItemExperienceJar.getExperienceFillLevel(stack);
         }
      });
      this.addPropertyOverride(new ResourceLocation("disenchanter", "overloaded"), new IItemPropertyGetter() {
         @SideOnly(Side.CLIENT)
         public float apply(ItemStack stack, World world, EntityLivingBase entityIn) {
            ItemExperienceJar.ensureValidTag(stack);
            return ItemExperienceJar.isOverloadActive(stack) ? 1.0F : 0.0F;
         }
      });
   }

   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
      super.getSubItems(tab, subItems);
      if (tab == CreativeTabs.SEARCH) {
         ItemStack fullJar = new ItemStack(this);
         ensureValidTag(fullJar);
         setStoredExperience(fullJar, getExperienceCapacity(fullJar));
         subItems.add(fullJar);
         ItemStack fullOverloadedJar = fullJar.copy();
         setOverload(fullOverloadedJar, true);
         subItems.add(fullOverloadedJar);
      }

   }

   public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entityLiving) {
      if (entityLiving instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entityLiving;
         giveExperienceTo(player, stack, player.isSneaking());
      }

      return stack;
   }

   public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
      if (DisenchanterConfig.experienceJar.playerCanInsertXPInJar && entityLiving instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entityLiving;
         if (player.isSneaking()) {
            takeExperienceFrom(player, stack);
         }
      }

      return super.onEntitySwing(entityLiving, stack);
   }

   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      ItemStack stack = player.getHeldItem(hand);
      if (DisenchanterConfig.experienceJar.playerCanExtractXPFromJar && hasStoredExperience(stack)) {
         player.setActiveHand(hand);
         return new ActionResult(EnumActionResult.SUCCESS, stack);
      } else {
         return new ActionResult(EnumActionResult.FAIL, stack);
      }
   }

   public EnumAction getItemUseAction(ItemStack stack) {
      return EnumAction.DRINK;
   }

   public int getMaxItemUseDuration(ItemStack stack) {
      return 16;
   }

   @SideOnly(Side.CLIENT)
   public boolean hasEffect(ItemStack stack) {
      return true;
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, World world, List<String> l, ITooltipFlag advanced) {
      super.addInformation(stack, world, l, advanced);
      if (isOverloadActive(stack)) {
         String overload = "msg.overload.txt";
         if (GuiScreen.isShiftKeyDown()) {
            overload = "msg.overload_detailed.txt";
         }

         l.add((new TextComponentTranslation(overload, new Object[0])).setStyle((new Style()).setColor(TextFormatting.GREEN)).getFormattedText());
      }

      l.add((new TextComponentTranslation("msg.stored_xp.txt", new Object[]{getStoredExperience(stack), getExperienceCapacity(stack)})).setStyle((new Style()).setColor(TextFormatting.AQUA)).getFormattedText());
      if (DisenchanterConfig.experienceJar.playerCanExtractXPFromJar || DisenchanterConfig.experienceJar.playerCanInsertXPInJar) {
         if (GuiScreen.isShiftKeyDown()) {
            if (DisenchanterConfig.experienceJar.playerCanExtractXPFromJar) {
               l.add((new TextComponentTranslation("msg.extract_from_jar.txt", new Object[0])).setStyle((new Style()).setColor(TextFormatting.DARK_GRAY)).getFormattedText());
               l.add((new TextComponentTranslation("msg.extract_all_from_jar.txt", new Object[0])).setStyle((new Style()).setColor(TextFormatting.DARK_GRAY)).getFormattedText());
            }

            if (DisenchanterConfig.experienceJar.playerCanInsertXPInJar) {
               l.add((new TextComponentTranslation("msg.insert_into_jar.txt", new Object[0])).setStyle((new Style()).setColor(TextFormatting.DARK_GRAY)).getFormattedText());
            }
         } else {
            l.add((new TextComponentTranslation("msg.shift_for_tooltip.txt", new Object[0])).setStyle((new Style()).setColor(TextFormatting.DARK_GRAY).setItalic(Boolean.valueOf(true))).getFormattedText());
         }
      }

   }

   public static void giveExperienceTo(EntityPlayer player, ItemStack stack, boolean transferAll) {
      int amountToAdd = 0;
      ensureValidTag(stack);
      int xpStored = getStoredExperience(stack);
      if (transferAll) {
         amountToAdd = xpStored;
      } else {
         int xpToLevelup = MathHelper.ceil((float)player.xpBarCap() * (1.0F - player.experience));
         amountToAdd = Math.min(xpToLevelup, xpStored);
      }

      player.addExperience(amountToAdd);
      setStoredExperience(stack, xpStored - amountToAdd);
   }

   public static void takeExperienceFrom(EntityPlayer player, ItemStack stack) {
      ensureValidTag(stack);
      int xpStored = getStoredExperience(stack);
      int capacity = getExperienceCapacity(stack);
      int xpToLeveldown = Math.round((float)player.xpBarCap() * player.experience);
      int amountToRemove = 0;
      if (xpToLeveldown > 0) {
         amountToRemove = xpToLeveldown;
      } else if (player.experienceLevel > 0) {
         --player.experienceLevel;
         amountToRemove = player.xpBarCap();
      }

      if (amountToRemove > 0) {
         int amountRemoved = Math.min(amountToRemove, capacity - xpStored);
         setStoredExperience(stack, xpStored + amountRemoved);
         player.experience = (float)(amountToRemove - amountRemoved) / (float)player.xpBarCap();
      }

   }

   public static boolean ensureValidTag(ItemStack stack) {
      boolean modified = false;
      if (!stack.hasTagCompound()) {
         stack.setTagCompound(new NBTTagCompound());
         modified = true;
      }

      if (!isOverloadActive(stack)) {
         setOverload(stack, false);
         modified = true;
      }

      if (getExperienceCapacity(stack) <= 0) {
         resetExperienceCapacity(stack);
         modified = true;
      }

      if (!hasStoredExperience(stack)) {
         setStoredExperience(stack, 0);
         modified = true;
      } else if (getExperienceFillLevel(stack) > 1.0F) {
         setStoredExperience(stack, getExperienceCapacity(stack));
         modified = true;
      }

      return modified;
   }

   public static boolean isOverloadActive(ItemStack stack) {
      return stack.hasTagCompound() ? stack.getTagCompound().getBoolean("Overload") : false;
   }

   public static boolean setOverload(ItemStack stack, boolean active) {
      if (stack.hasTagCompound()) {
         stack.getTagCompound().setBoolean("Overload", active);
         return true;
      } else {
         return false;
      }
   }

   public static boolean hasAvailableExperienceCapacity(ItemStack stack) {
      return getExperienceFillLevel(stack) < 1.0F;
   }

   public static float getExperienceFillLevel(ItemStack stack) {
      int capacity = getExperienceCapacity(stack);
      return capacity == 0 ? 0.0F : (float)getStoredExperience(stack) / (float)capacity;
   }

   public static int getExperienceCapacity(ItemStack stack) {
      return stack.hasTagCompound() ? stack.getTagCompound().getInteger("ExperienceCapacity") : 0;
   }

   public static boolean setExperienceCapacity(ItemStack stack, int capacity) {
      if (stack.hasTagCompound() && capacity >= 0) {
         stack.getTagCompound().setInteger("ExperienceCapacity", capacity);
         return true;
      } else {
         return false;
      }
   }

   public static boolean resetExperienceCapacity(ItemStack stack) {
      return setExperienceCapacity(stack, DisenchanterConfig.experienceJar.jarDefaultExperienceCapacity);
   }

   public static boolean hasStoredExperience(ItemStack stack) {
      return getStoredExperience(stack) > 0;
   }

   public static int getStoredExperience(ItemStack stack) {
      return stack.hasTagCompound() ? stack.getTagCompound().getInteger("StoredExperience") : 0;
   }

   public static boolean setStoredExperience(ItemStack stack, int amount) {
      if (stack.hasTagCompound() && amount >= 0 && amount <= getExperienceCapacity(stack)) {
         stack.getTagCompound().setInteger("StoredExperience", amount);
         return true;
      } else {
         return false;
      }
   }

   public static boolean setStoredExperienceClamped(ItemStack stack, int amount) {
      return setStoredExperience(stack, MathHelper.clamp(amount, 0, getExperienceCapacity(stack)));
   }
}
