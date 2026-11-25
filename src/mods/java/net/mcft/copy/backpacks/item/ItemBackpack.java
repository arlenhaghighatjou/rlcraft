package net.mcft.copy.backpacks.item;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.common.container.ContainerPlayerExpanded;
import java.util.List;
import net.mcft.copy.backpacks.WearableBackpacks;
import net.mcft.copy.backpacks.api.BackpackHelper;
import net.mcft.copy.backpacks.api.BackpackRegistry;
import net.mcft.copy.backpacks.api.IBackpack;
import net.mcft.copy.backpacks.api.IBackpackData;
import net.mcft.copy.backpacks.api.IBackpackType;
import net.mcft.copy.backpacks.client.KeyBindingHandler;
import net.mcft.copy.backpacks.config.ModConfig;
import net.mcft.copy.backpacks.container.ContainerBackpack;
import net.mcft.copy.backpacks.misc.BackpackDataItems;
import net.mcft.copy.backpacks.misc.BackpackSize;
import net.mcft.copy.backpacks.misc.util.LangUtils;
import net.mcft.copy.backpacks.misc.util.NbtUtils;
import net.mcft.copy.backpacks.misc.util.WorldUtils;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;

public class ItemBackpack extends Item implements IBackpackType, IDyeableItem, IBauble {
   public static final int DEFAULT_COLOR = 10511680;
   public static final ResourceLocation LOOT_TABLE = new ResourceLocation("wearablebackpacks", "backpack/default");
   public static final String[] TAG_CUSTOM_ARMOR = new String[]{"backpack", "armor"};
   public static final String[] TAG_CUSTOM_SIZE = new String[]{"backpack", "size"};

   public ItemBackpack() {
      this.setUnlocalizedName("wearablebackpacks.backpack");
      this.setMaxStackSize(1);
      this.setCreativeTab(CreativeTabs.TOOLS);
   }

   public BaubleType getBaubleType(ItemStack stack) {
      return getBaubleType();
   }

   public static BaubleType getBaubleType() {
      return BaubleType.BODY;
   }

   public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
      if (player != null && !player.world.isRemote && player.ticksExisted % 9 == 0 && ModConfig.server.doPotionEffect) {
         player.addPotionEffect(new PotionEffect(ModConfig.server.getBackpackEffect(), 10, ModConfig.server.potionEffectAmplifier, true, false));
      }

   }

   public boolean canUnequip(ItemStack itemStack, EntityLivingBase player) {
      return false;
   }

   public boolean canEquip(ItemStack itemStack, EntityLivingBase player) {
      return !(player instanceof EntityPlayer) || !(((EntityPlayer)player).openContainer instanceof ContainerPlayerExpanded);
   }

   public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
      if (repair.isEmpty()) {
         return false;
      } else {
         int leatherOreID = OreDictionary.getOreID("leather");
         return ArrayUtils.contains(OreDictionary.getOreIDs(repair), leatherOreID);
      }
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      IBackpack backpack = BackpackHelper.getBackpack(Minecraft.getMinecraft().player);
      boolean isEquipped = backpack != null && backpack.getStack() == stack;
      if (LangUtils.tooltipIsShiftKeyDown(tooltip)) {
         boolean equipAsBauble = ModConfig.server.equipAsBauble;
         boolean enableSelfInteraction = ModConfig.server.enableSelfInteraction;
         if (enableSelfInteraction && (isEquipped || !equipAsBauble)) {
            LangUtils.formatTooltipKey(tooltip, "openHint", KeyBindingHandler.openBackpack);
         }

         if (isEquipped) {
            LangUtils.formatTooltip(tooltip, "unequipHint");
         } else {
            LangUtils.formatTooltip(tooltip, "equipHint" + (!equipAsBauble ? ".extended" : ""));
         }
      }

      if (isEquipped && backpack.getPlayersUsing() > 0) {
         LangUtils.formatTooltipPrepend(tooltip, "\u00a8o", "used");
      }

      if (flagIn.isAdvanced()) {
         NBTBase customSize = NbtUtils.get(stack, TAG_CUSTOM_SIZE);
         if (customSize != null) {
            try {
               tooltip.add("Custom Size: " + BackpackSize.parse(customSize));
            } catch (Exception var9) {
               ;
            }
         }

      }
   }

   public boolean isEnchantable(ItemStack stack) {
      return false;
   }

   public int getItemEnchantability() {
      return 0;
   }

   public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
      return false;
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
      return false;
   }

   public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      IBlockState state = worldIn.getBlockState(pos);
      if (state.getBlock().isReplaceable(worldIn, pos)) {
         state = worldIn.getBlockState(pos.offset(EnumFacing.DOWN));
         if (!state.isSideSolid(worldIn, pos, EnumFacing.UP)) {
            return EnumActionResult.FAIL;
         }
      } else {
         if (facing != EnumFacing.UP) {
            return EnumActionResult.FAIL;
         }

         pos = pos.offset(EnumFacing.UP);
      }

      if (worldIn.getBlockState(pos).getMaterial() instanceof MaterialLiquid) {
         return EnumActionResult.FAIL;
      } else {
         return BackpackHelper.placeBackpack(worldIn, pos, player.getHeldItem(hand), player, false) ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
      }
   }

   public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
      if (!playerIn.world.isRemote && playerIn.isCreative() && BackpackRegistry.canEntityWearBackpacks(target) && !(target instanceof EntityPlayer)) {
         IBackpack backpack = BackpackHelper.getBackpack(target);
         if (backpack != null) {
            backpack.getType().onFaultyRemoval(target, backpack);
         }

         stack = stack.splitStack(1);
         IBackpackData data = BackpackHelper.getBackpackType(stack).createBackpackData(stack);
         BackpackHelper.setEquippedBackpack(target, stack, data);
         return true;
      } else {
         return false;
      }
   }

   public void onSpawnedWith(EntityLivingBase entity, IBackpack backpack, String lootTable) {
      IBackpackData data = backpack.getData();
      if (lootTable != null && data instanceof BackpackDataItems) {
         ((BackpackDataItems)data).setLootTable(lootTable, entity.world.rand.nextLong());
      }

   }

   public void onEquip(EntityLivingBase entity, TileEntity tileEntity, IBackpack backpack) {
   }

   public void onUnequip(EntityLivingBase entity, TileEntity tileEntity, IBackpack backpack) {
   }

   public void onPlacedInteract(EntityPlayer player, final TileEntity tileEntity, IBackpack backpack) {
      if (!player.world.isRemote) {
         (new ContainerBackpack(player, backpack) {
            public boolean canInteractWith(EntityPlayer player) {
               return player.isEntityAlive() && !tileEntity.isInvalid() && player.world.getTileEntity(tileEntity.getPos()) == tileEntity && player.getDistanceSq(tileEntity.getPos()) <= 64.0D;
            }
         }).open();
      }
   }

   public void onEquippedInteract(EntityPlayer player, final EntityLivingBase target, IBackpack backpack) {
      if (!player.world.isRemote) {
         (new ContainerBackpack(player, backpack) {
            public boolean canInteractWith(EntityPlayer player) {
               return BackpackHelper.canInteractWithEquippedBackpack(player, target);
            }
         }).open();
      }
   }

   public void onEquippedTick(EntityLivingBase entity, IBackpack backpack) {
   }

   public void onDeath(EntityLivingBase entity, IBackpack backpack) {
      if (backpack.getData() instanceof BackpackDataItems) {
         BackpackDataItems dataItems = (BackpackDataItems)backpack.getData();
         WorldUtils.dropStacksFromEntity(entity, dataItems.getItems(entity.world, (EntityPlayer)null), 4.0F);
      }
   }

   public void onEquippedBroken(EntityLivingBase entity, IBackpack backpack) {
      this.onDeath(entity, backpack);
   }

   public void onFaultyRemoval(EntityLivingBase entity, IBackpack backpack) {
      this.onDeath(entity, backpack);
   }

   public void onBlockBreak(TileEntity tileEntity, IBackpack backpack) {
      if (backpack.getData() instanceof BackpackDataItems) {
         BackpackDataItems dataItems = (BackpackDataItems)backpack.getData();
         WorldUtils.dropStacksFromBlock(tileEntity, dataItems.getItems(tileEntity.getWorld(), (EntityPlayer)null));
      }
   }

   public IBackpackData createBackpackData(ItemStack stack) {
      BackpackSize size = ModConfig.server.getBackpackSize();
      NBTBase customSize = NbtUtils.get(stack, TAG_CUSTOM_SIZE);
      if (customSize != null) {
         try {
            size = BackpackSize.parse(customSize);
         } catch (Exception var5) {
            WearableBackpacks.LOG.error("Error trying to deserialize backpack size from custom size NBT tag.", var5);
         }
      }

      return new BackpackDataItems(size);
   }

   public boolean isDamageable() {
      return !this.isInvulnurable();
   }

   public boolean isDamaged(ItemStack stack) {
      return this.isInvulnurable() ? false : super.isDamaged(stack);
   }

   public int getMaxDamage(ItemStack stack) {
      return this.isInvulnurable() ? 32767 : super.getMaxDamage(stack);
   }

   public int getDamage(ItemStack stack) {
      return this.isInvulnurable() ? 0 : super.getDamage(stack);
   }

   public void setDamage(ItemStack stack, int damage) {
      if (this.isInvulnurable()) {
         damage = 0;
      }

      super.setDamage(stack, damage);
   }

   private boolean isInvulnurable() {
      return true;
   }
}
