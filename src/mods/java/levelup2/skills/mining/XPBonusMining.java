package levelup2.skills.mining;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import levelup2.skills.BaseSkill;
import levelup2.skills.SkillRegistry;
import levelup2.util.Library;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.oredict.OreDictionary;

public class XPBonusMining extends BaseSkill {
   public XPBonusMining() {
   }

   public boolean hasSubscription() {
      return true;
   }

   public String getSkillName() {
      return "levelup:mining_bonus";
   }

   public int getLevelCost(int currentLevel) {
      return -1;
   }

   public byte getSkillType() {
      return 0;
   }

   public String[] getPrerequisites() {
      return new String[0];
   }

   public void register(EventBus bus) {
      bus.register(HarvestDropsEvent.class, this::giveMiningXP);
   }

   public void giveMiningXP(HarvestDropsEvent evt) {
      if (evt.getHarvester() != null && !evt.getWorld().isRemote && SkillRegistry.getSkillLevel(evt.getHarvester(), this.getSkillName()) > 0) {
         IBlockState state = evt.getState();
         ItemStack stack = new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state));
         boolean oreFound = false;

         for(String ore : SkillRegistry.getOreBonusXP().keySet()) {
            if (SkillRegistry.listContains(stack, OreDictionary.getOres(ore))) {
               SkillRegistry.addExperience(evt.getHarvester(), ((Integer)SkillRegistry.getOreBonusXP().get(ore)).intValue());
               oreFound = true;
               break;
            }
         }

         if (!oreFound) {
            for(ItemStack s : evt.getDrops()) {
               String ore = Library.getOreNameForBlock(s);
               if (ore != null && SkillRegistry.getOreBonusXP().keySet().contains(ore)) {
                  SkillRegistry.addExperience(evt.getHarvester(), ((Integer)SkillRegistry.getOreBonusXP().get(ore)).intValue());
                  oreFound = true;
                  break;
               }
            }

            if (!oreFound) {
               for(ItemStack s : evt.getDrops()) {
                  String ore = Library.getOreNameForBlock(s);
                  if (ore != null && ore.startsWith("ore")) {
                     int xp = state.getBlock().getHarvestLevel(state) + 1;
                     SkillRegistry.addExperience(evt.getHarvester(), xp);
                     break;
                  }
               }
            }
         }
      }

   }

   public int getSkillRow() {
      return 0;
   }

   public int getSkillColumn() {
      return 0;
   }

   public ItemStack getRepresentativeStack() {
      return new ItemStack(Items.DIAMOND_PICKAXE);
   }

   public boolean isMaxLevel(int level) {
      return true;
   }

   public int getMaxLevel() {
      return 1;
   }

   public boolean isEnabled() {
      return true;
   }

   public boolean isActive() {
      return true;
   }

   public boolean hasExternalJson() {
      return false;
   }
}
