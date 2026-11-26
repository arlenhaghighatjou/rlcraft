package levelup2.skills.mining;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import levelup2.config.LevelUpConfig;
import levelup2.skills.BaseSkill;
import levelup2.skills.SkillRegistry;
import levelup2.util.Library;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext.Builder;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.oredict.OreDictionary;

public class DiggingTreasureBonus extends BaseSkill {
   public DiggingTreasureBonus() {
   }

   public String getSkillName() {
      return "levelup:treasurehunting";
   }

   public boolean hasSubscription() {
      return true;
   }

   public byte getSkillType() {
      return 0;
   }

   public ItemStack getRepresentativeStack() {
      return new ItemStack(Items.GOLDEN_SHOVEL);
   }

   public void register(EventBus bus) {
      bus.register(HarvestDropsEvent.class, this::dirtLooting);
   }

   public void dirtLooting(HarvestDropsEvent evt) {
      if (this.isActive()) {
         if (evt.getHarvester() != null && !evt.getWorld().isRemote) {
            EaglercraftRandom rand = evt.getHarvester().getRNG();
            int skill = SkillRegistry.getSkillLevel(evt.getHarvester(), this.getSkillName());
            if (skill > 0 && !evt.isSilkTouching() && !evt.getDrops().isEmpty() && SkillRegistry.listContains((ItemStack)evt.getDrops().get(0), OreDictionary.getOres("dirt"))) {
               ItemStack drop = ((ItemStack)evt.getDrops().get(0)).copy();
               if (rand.nextFloat() <= (float)skill / 20.0F) {
                  ItemStack loot = this.getDigLoot(evt.getHarvester());
                  System.out.println("ItemStack: " + loot.toString());
                  if (!loot.isEmpty()) {
                     Library.removeFromList(evt.getDrops(), drop);
                     evt.getDrops().add(loot.copy());
                  }
               }
            }
         }

      }
   }

   private ItemStack getDigLoot(EntityPlayer player) {
      if (!player.getEntityWorld().isRemote) {
         Builder build = (new Builder((WorldServer)player.getEntityWorld())).withPlayer(player);
         build.withLuck((float)EnchantmentHelper.getMaxEnchantmentLevel(Enchantment.getEnchantmentByLocation("fortune"), player) + player.getLuck());
         ResourceLocation location = this.getTableFromWeightedPool(player.getRNG());
         if (location != null) {
            return (ItemStack)Library.getLootManager().getLootTableFromLocation(location).generateLootForPools(player.getRNG(), build.build()).get(0);
         }
      }

      return ItemStack.EMPTY;
   }

   private ResourceLocation getTableFromWeightedPool(EaglercraftRandom rand) {
      if (LevelUpConfig.combinedChance == 0) {
         return null;
      } else {
         int RNG = rand.nextInt(LevelUpConfig.combinedChance);
         if (LevelUpConfig.rareChance > 0 && RNG <= LevelUpConfig.rareChance) {
            return new ResourceLocation("levelup", "digging/rare_dig");
         } else {
            return LevelUpConfig.uncommonChance > 0 && RNG <= LevelUpConfig.rareChance + LevelUpConfig.uncommonChance ? new ResourceLocation("levelup", "digging/uncommon_dig") : new ResourceLocation("levelup", "digging/common_dig");
         }
      }
   }
}
