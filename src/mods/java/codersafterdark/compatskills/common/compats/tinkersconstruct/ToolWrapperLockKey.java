package codersafterdark.compatskills.common.compats.tinkersconstruct;

import codersafterdark.compatskills.common.compats.tinkersconstruct.modifierlocks.ModifierLockKey;
import codersafterdark.compatskills.common.compats.tinkersconstruct.toollocks.ToolTypeLockKey;
import codersafterdark.reskillable.api.data.ParentLockKey;
import codersafterdark.reskillable.api.data.RequirementHolder;
import codersafterdark.reskillable.base.LevelLockHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;

public class ToolWrapperLockKey implements ParentLockKey {
   private ItemStack stack;

   public ToolWrapperLockKey(ItemStack stack) {
      this.stack = stack;
   }

   public RequirementHolder getSubRequirements() {
      Material mat = TinkerUtil.getMaterialFromStack(this.stack);
      if (!mat.equals(Material.UNKNOWN)) {
         return LevelLockHandler.getLocks(Material.class, new Material[]{mat});
      } else {
         List<RequirementHolder> holders = new ArrayList();
         RequirementHolder toolHolder = LevelLockHandler.getLockByKey(new ToolTypeLockKey(this.stack.getItem()));
         if (!toolHolder.equals(LevelLockHandler.EMPTY_LOCK)) {
            holders.add(toolHolder);
         }

         NBTTagList list = TagUtil.getBaseMaterialsTagList(this.stack);
         if (list.hasNoTags()) {
            return toolHolder;
         } else {
            TinkerUtil.getMaterialsFromTagList(list).stream().map((material) -> {
               return LevelLockHandler.getLocks(Material.class, new Material[]{material});
            }).forEach(holders::add);
            TinkerUtil.getTraitsOrdered(this.stack).stream().map((trait) -> {
               return LevelLockHandler.getLockByKey(new ModifierLockKey(trait));
            }).forEach(holders::add);
            return holders.isEmpty() ? LevelLockHandler.EMPTY_LOCK : new RequirementHolder((RequirementHolder[])holders.toArray(new RequirementHolder[0]));
         }
      }
   }
}
