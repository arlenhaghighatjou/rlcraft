package com.ferreusveritas.dynamictrees.items;

import com.ferreusveritas.dynamictrees.ModTabs;
import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import com.ferreusveritas.dynamictrees.blocks.BlockBranch;
import com.ferreusveritas.dynamictrees.blocks.BlockTrunkShell;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.util.SafeChunkBounds;
import com.ferreusveritas.dynamictrees.worldgen.JoCode;
import com.google.common.collect.Multimap;
import java.awt.Color;
import java.util.List;
import java.util.Optional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Staff extends Item {
   public static final String HANDLE = "handle";
   public static final String COLOR = "color";
   public static final String READONLY = "readonly";
   public static final String TREE = "tree";
   public static final String CODE = "code";
   public static final String USES = "uses";
   public static final String MAXUSES = "maxuses";

   public Staff() {
      this("staff");
   }

   public Staff(String name) {
      this.setRegistryName(name);
      this.setUnlocalizedName(this.getRegistryName().toString());
      this.setMaxStackSize(1);
      this.setHarvestLevel("axe", 3);
      this.setCreativeTab(ModTabs.dynamicTreesTab);
   }

   public float getDestroySpeed(ItemStack stack, IBlockState state) {
      return !(state.getBlock() instanceof BlockBranch) && !(state.getBlock() instanceof BlockTrunkShell) ? super.getDestroySpeed(stack, state) : 64.0F;
   }

   public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
      if (!(state.getBlock() instanceof BlockBranch) && !(state.getBlock() instanceof BlockTrunkShell)) {
         return false;
      } else {
         if (this.decUses(stack)) {
            stack.shrink(1);
         }

         return true;
      }
   }

   public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      ItemStack heldStack = player.getHeldItem(hand);
      IBlockState clickedBlockState = world.getBlockState(pos);
      if (!this.isReadOnly(heldStack)) {
         Species species = TreeHelper.getBestGuessSpecies(world, pos);
         if (species.isValid()) {
            this.setSpecies(heldStack, species);
            if (!player.isSneaking()) {
               EnumFacing playerFacing = player.getHorizontalFacing();
               Optional<JoCode> joCode = TreeHelper.getJoCode(world, pos, playerFacing, species);
               if (joCode.isPresent()) {
                  String code = ((JoCode)joCode.get()).toString();
                  this.setCode(heldStack, code);
                  if (world.isRemote) {
                     GuiScreen.setClipboardString(code);
                  }
               }
            }

            return EnumActionResult.SUCCESS;
         }
      }

      Species species = this.getSpecies(heldStack);
      if (species.isValid() && species.isAcceptableSoil(world, pos, clickedBlockState)) {
         species.getJoCode(this.getCode(heldStack)).setCareful(true).generate(world, species, pos, world.getBiome(pos), player.getHorizontalFacing(), 8, SafeChunkBounds.ANY);
         if (!player.isCreative()) {
            if (this.hasMaxUses(heldStack)) {
               if (this.decUses(heldStack)) {
                  heldStack.shrink(1);
               }
            } else {
               heldStack.shrink(1);
            }
         }

         return EnumActionResult.SUCCESS;
      } else {
         return EnumActionResult.FAIL;
      }
   }

   public boolean showDurabilityBar(ItemStack stack) {
      return this.hasMaxUses(stack);
   }

   public int getMaxItemUseDuration(ItemStack stack) {
      return super.getMaxItemUseDuration(stack);
   }

   public double getDurabilityForDisplay(ItemStack stack) {
      double damage = (double)this.getUses(stack) / (double)this.getMaxUses(stack);
      return 1.0D - damage;
   }

   public NBTTagCompound getNBT(ItemStack itemStack) {
      return itemStack.hasTagCompound() ? itemStack.getTagCompound() : new NBTTagCompound();
   }

   public boolean isReadOnly(ItemStack itemStack) {
      return this.getNBT(itemStack).getBoolean("readonly");
   }

   public Staff setReadOnly(ItemStack itemStack, boolean readonly) {
      NBTTagCompound nbt = this.getNBT(itemStack);
      nbt.setBoolean("readonly", readonly);
      itemStack.setTagCompound(nbt);
      return this;
   }

   public Staff setSpecies(ItemStack itemStack, Species species) {
      NBTTagCompound nbt = this.getNBT(itemStack);
      nbt.setString("tree", species.toString());
      itemStack.setTagCompound(nbt);
      return this;
   }

   public Staff setCode(ItemStack itemStack, String code) {
      NBTTagCompound nbt = this.getNBT(itemStack);
      nbt.setString("code", code);
      itemStack.setTagCompound(nbt);
      return this;
   }

   public Species getSpecies(ItemStack itemStack) {
      NBTTagCompound nbt = this.getNBT(itemStack);
      if (nbt.hasKey("tree")) {
         return TreeRegistry.findSpecies(new ResourceLocation(nbt.getString("tree")));
      } else {
         Species species = TreeRegistry.findSpeciesSloppy("oak");
         this.setSpecies(itemStack, species);
         return species;
      }
   }

   public int getUses(ItemStack itemStack) {
      NBTTagCompound nbt = this.getNBT(itemStack);
      if (nbt.hasKey("uses")) {
         return nbt.getInteger("uses");
      } else {
         int uses = this.getMaxUses(itemStack);
         this.setUses(itemStack, uses);
         return uses;
      }
   }

   public void setUses(ItemStack itemStack, int value) {
      this.getNBT(itemStack).setInteger("uses", value);
   }

   public int getMaxUses(ItemStack itemStack) {
      NBTTagCompound nbt = this.getNBT(itemStack);
      return nbt.hasKey("maxuses") ? nbt.getInteger("maxuses") : 0;
   }

   public void setMaxUses(ItemStack itemStack, int value) {
      this.getNBT(itemStack).setInteger("maxuses", value);
   }

   public boolean hasMaxUses(ItemStack itemStack) {
      return this.getNBT(itemStack).hasKey("maxuses");
   }

   public boolean decUses(ItemStack itemStack) {
      int uses = Math.max(0, this.getUses(itemStack) - 1);
      this.setUses(itemStack, uses);
      return uses <= 0;
   }

   public int getColor(ItemStack itemStack, int tint) {
      if (tint == 0) {
         NBTTagCompound nbt = this.getNBT(itemStack);
         int color = 5981999;
         if (nbt.hasKey("handle")) {
            try {
               color = Color.decode(nbt.getString("handle")).getRGB();
            } catch (NumberFormatException var6) {
               nbt.removeTag("handle");
            }
         } else {
            color = this.getSpecies(itemStack).getFamily().getWoodColor();
         }

         return color;
      } else if (tint == 1) {
         NBTTagCompound nbt = this.getNBT(itemStack);
         int color = 65535;
         if (nbt.hasKey("color")) {
            try {
               color = Color.decode(nbt.getString("color")).getRGB();
            } catch (NumberFormatException var7) {
               nbt.removeTag("color");
            }
         }

         return color;
      } else {
         return -1;
      }
   }

   public Staff setColor(ItemStack itemStack, String colStr) {
      NBTTagCompound nbt = this.getNBT(itemStack);
      nbt.setString("color", colStr);
      itemStack.setTagCompound(nbt);
      return this;
   }

   public String getCode(ItemStack itemStack) {
      String code = "P";
      NBTTagCompound nbt = this.getNBT(itemStack);
      if (nbt.hasKey("code")) {
         code = nbt.getString("code");
      } else {
         nbt.setString("code", code);
         itemStack.setTagCompound(nbt);
      }

      return code;
   }

   public EnumAction getItemUseAction(ItemStack itemStack) {
      return EnumAction.BLOCK;
   }

   @SideOnly(Side.CLIENT)
   public boolean isFull3D() {
      return true;
   }

   public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flagIn) {
      Species species = this.getSpecies(stack);
      tooltip.add(getTranslationText("tooltip.woodland_staff.tree") + " " + (species != null ? "\u00c2\u00a7a" + species.getLocalizedName() : getTranslationText("tooltip.woodland_staff.tree_not_set")));
      tooltip.add("JoCode: \u00c2\u00a76" + this.getCode(stack));
   }

   public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot equipmentSlot, ItemStack stack) {
      Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot, stack);
      if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
         multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 5.0D, 0));
         multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -2.4D, 0));
      }

      return multimap;
   }

   private static String getTranslationText(String path) {
      return (new TextComponentTranslation(path, new Object[0])).getUnformattedComponentText();
   }
}
