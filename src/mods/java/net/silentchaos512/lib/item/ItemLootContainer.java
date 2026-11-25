package net.silentchaos512.lib.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootContext.Builder;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.util.PlayerHelper;

public class ItemLootContainer extends Item {
   private static final String NBT_ROOT = "silentlib.LootContainer";
   private static final String NBT_LOOT_TABLE = "LootTable";
   private final ResourceLocation defaultLootTable;

   public ItemLootContainer(ResourceLocation defaultLootTable) {
      this.defaultLootTable = defaultLootTable;
   }

   public ItemStack getStack() {
      return this.getStack(this.defaultLootTable);
   }

   public ItemStack getStack(ResourceLocation lootTable) {
      ItemStack result = new ItemStack(this);
      result.getOrCreateSubCompound("silentlib.LootContainer").setString("LootTable", lootTable.toString());
      return result;
   }

   protected Collection<ItemStack> getLootDrops(ItemStack heldItem, EntityPlayerMP player) {
      ResourceLocation lootTable = this.getLootTable(heldItem);
      LootContext lootContext = (new Builder(player.getServerWorld())).withLootedEntity(player).withPlayer(player).withLuck(player.getLuck()).build();
      return new ArrayList(player.world.getLootTableManager().getLootTableFromLocation(lootTable).generateLootForPools(player.getRNG(), lootContext));
   }

   private ResourceLocation getLootTable(ItemStack stack) {
      NBTTagCompound tags = stack.getOrCreateSubCompound("silentlib.LootContainer");
      return tags.hasKey("LootTable") ? new ResourceLocation(tags.getString("LootTable")) : this.defaultLootTable;
   }

   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      if (flagIn.isAdvanced()) {
         tooltip.add(TextFormatting.DARK_GRAY + "Loot Table: " + this.getLootTable(stack));
      }

   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      ItemStack heldItem = playerIn.getHeldItem(handIn);
      if (!(playerIn instanceof EntityPlayerMP)) {
         return ActionResult.newResult(EnumActionResult.SUCCESS, heldItem);
      } else {
         EntityPlayerMP playerMP = (EntityPlayerMP)playerIn;
         Collection<ItemStack> lootDrops = this.getLootDrops(heldItem, playerMP);
         if (lootDrops.isEmpty()) {
            SilentLib.LOGGER.warn("ItemLootContainer has no drops? {}", heldItem);
         }

         lootDrops.forEach((stack) -> {
            PlayerHelper.giveItem(playerMP, stack);
         });
         playerMP.world.playSound((EntityPlayer)null, playerMP.posX, playerMP.posY, playerMP.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((playerMP.getRNG().nextFloat() - playerMP.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
         heldItem.shrink(1);
         return ActionResult.newResult(EnumActionResult.SUCCESS, heldItem);
      }
   }

   public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      this.onItemRightClick(worldIn, player, hand).getType();
      return EnumActionResult.FAIL;
   }

   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
      if (this.isInCreativeTab(tab)) {
         items.add(this.getStack());
      }
   }
}
