package levelup2.skills.mining;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import levelup2.config.LevelUpConfig;
import levelup2.skills.BaseSkill;
import levelup2.skills.SkillRegistry;
import levelup2.util.Library;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class StoneMiningBonus extends BaseSkill {
   public StoneMiningBonus() {
   }

   public String getSkillName() {
      return "levelup:prospecting";
   }

   public boolean hasSubscription() {
      return true;
   }

   public byte getSkillType() {
      return 0;
   }

   public ItemStack getRepresentativeStack() {
      return new ItemStack(Blocks.GOLD_ORE);
   }

   @SubscribeEvent
   public void onHarvest(HarvestDropsEvent evt) {
      if (this.isActive()) {
         if (evt.getHarvester() != null && !evt.getWorld().isRemote) {
            int skill = SkillRegistry.getSkillLevel(evt.getHarvester(), this.getSkillName());
            IBlockState state = evt.getState();
            EaglercraftRandom rand = evt.getHarvester().getRNG();
            this.dropOreChunks(evt, skill, state, rand);
         }

      }
   }

   @SubscribeEvent
   public void stopPlacingDupes(RightClickBlock evt) {
      ItemStack stack = evt.getItemStack();
      if (!stack.isEmpty() && stack.hasTagCompound() && stack.getTagCompound().hasKey("NoPlace") && Library.isOre(stack)) {
         evt.setUseItem(Result.DENY);
      }

   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public void addTooltip(ItemTooltipEvent evt) {
      ItemStack stack = evt.getItemStack();
      if (stack.hasTagCompound() && stack.getTagCompound().hasKey("NoPlace")) {
         evt.getToolTip().add(I18n.format("flag.noplace", new Object[0]));
      }

   }

   private void dropOreChunks(HarvestDropsEvent evt, int skill, IBlockState state, EaglercraftRandom rand) {
      if (!Library.getOreList().isEmpty() && Library.getOreList().contains(state.getBlock())) {
         if (rand.nextDouble() <= (double)skill / 20.0D) {
            int fortuneBonus = LevelUpConfig.fortuneOre && evt.getFortuneLevel() > 0 ? rand.nextInt(evt.getFortuneLevel() + 1) : 0;
            boolean foundBlock = false;

            for(ItemStack stack : evt.getDrops()) {
               if (!stack.isEmpty() && (state.getBlock() == Block.getBlockFromItem(stack.getItem()) || Library.isOre(stack))) {
                  ItemStack replace = this.getReplacementStack(stack, true, fortuneBonus);
                  if (!replace.isEmpty()) {
                     Library.removeFromList(evt.getDrops(), stack);
                     evt.getDrops().add(replace);
                     foundBlock = true;
                     break;
                  }
               }
            }

            if (!foundBlock) {
               Item item = state.getBlock().getItemDropped(state, rand, evt.getFortuneLevel());
               if (item != null) {
                  int quantity = state.getBlock().quantityDropped(state, evt.getFortuneLevel(), rand);
                  if (quantity > 0) {
                     evt.getDrops().add(new ItemStack(item, quantity, state.getBlock().damageDropped(state)));
                  }
               }
            }
         } else if (LevelUpConfig.useOreChunks && LevelUpConfig.alwaysDropChunks) {
            for(ItemStack stack : evt.getDrops()) {
               if (!stack.isEmpty() && state.getBlock() == Block.getBlockFromItem(stack.getItem())) {
                  ItemStack replace = this.getReplacementStack(stack, false, 0);
                  if (!replace.isEmpty() && !replace.isItemEqual(stack)) {
                     Library.removeFromList(evt.getDrops(), stack);
                     replace.setCount(1);
                     evt.getDrops().add(replace);
                  }
               }
            }
         }
      }

   }

   private ItemStack getReplacementStack(ItemStack stack, boolean duplication, int fortune) {
      ItemStack replace = LevelUpConfig.useOreChunks ? Library.getChunkFromName(Library.getOreNameForBlock(stack), fortune) : this.getDupeStack(stack, duplication, fortune);
      if (LevelUpConfig.useOreChunks && replace.isEmpty()) {
         replace = this.getDupeStack(stack, duplication, fortune);
      }

      return replace;
   }

   private ItemStack getDupeStack(ItemStack stack, boolean duplication, int fortune) {
      if (Library.isOre(stack)) {
         ItemStack dupe = stack.copy();
         dupe.grow(stack.getCount());
         if (duplication) {
            dupe.grow(fortune);
            if (stack.getItem() instanceof ItemBlock) {
               this.attachNoPlacement(dupe);
            }
         }

         return dupe;
      } else {
         return ItemStack.EMPTY;
      }
   }

   private void attachNoPlacement(ItemStack stack) {
      NBTTagCompound tag = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
      if (tag != null) {
         tag.setBoolean("NoPlace", true);
         stack.setTagCompound(tag);
      }

   }
}
