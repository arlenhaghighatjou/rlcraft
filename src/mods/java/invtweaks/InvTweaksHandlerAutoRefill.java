package invtweaks;

import invtweaks.api.IItemTreeItem;
import invtweaks.api.container.ContainerSection;
import invtweaks.container.ContainerSectionManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InvTweaksHandlerAutoRefill extends InvTweaksObfuscation {
   private static final Logger log = InvTweaks.log;
   @NotNull
   private InvTweaksConfig config;

   public InvTweaksHandlerAutoRefill(Minecraft mc_, @NotNull InvTweaksConfig config_) {
      super(mc_);
      this.config = config_;
   }

   public void setConfig(@NotNull InvTweaksConfig config_) {
      this.config = config_;
   }

   public void autoRefillSlot(int slot, @NotNull String wantedId, int wantedDamage) throws Exception {
      ContainerSectionManager container = new ContainerSectionManager(ContainerSection.INVENTORY);
      ItemStack replacementStack = ItemStack.EMPTY;
      int replacementStackSlot = -1;
      boolean refillBeforeBreak = this.config.getProperty("autoRefillBeforeBreak").equals("true");
      boolean hasSubtypes = false;
      Item original = (Item)Item.REGISTRY.getObject(new ResourceLocation(wantedId));
      if (original != null) {
         hasSubtypes = original.getHasSubtypes();
      }

      List<InvTweaksConfigSortingRule> matchingRules = new ArrayList();
      List<InvTweaksConfigSortingRule> rules = this.config.getRules();
      InvTweaksItemTree tree = this.config.getTree();
      if (!tree.isItemUnknown(wantedId, wantedDamage)) {
         List<IItemTreeItem> items = tree.getItems(wantedId, wantedDamage);

         for(IItemTreeItem item : items) {
            if (item.getDamage() == wantedDamage || !hasSubtypes && item.getDamage() == 32767) {
               matchingRules.add(new InvTweaksConfigSortingRule(tree, "D" + (slot - 26), item.getName(), 36, 9));
            }
         }

         if (matchingRules.isEmpty()) {
            for(IItemTreeItem item : items) {
               if (item.getDamage() == 32767) {
                  matchingRules.add(new InvTweaksConfigSortingRule(tree, "D" + (slot - 26), item.getName(), 36, 9));
               }
            }
         }

         for(InvTweaksConfigSortingRule rule : rules) {
            if (rule.getType() == InvTweaksConfigSortingRuleType.SLOT || rule.getType() == InvTweaksConfigSortingRuleType.COLUMN) {
               for(int preferredSlot : rule.getPreferredSlots()) {
                  if (slot == preferredSlot) {
                     matchingRules.add(rule);
                     break;
                  }
               }
            }
         }

         for(InvTweaksConfigSortingRule rule : matchingRules) {
            for(int i = 0; i < 36; ++i) {
               ItemStack candidateStack = container.getItemStack(i);
               if (!candidateStack.isEmpty()) {
                  List<IItemTreeItem> candidateItems = tree.getItems(candidateStack.getItem().getRegistryName().toString(), candidateStack.getItemDamage());
                  if (tree.matches(candidateItems, rule.getKeyword())) {
                     if (candidateStack.getMaxStackSize() == 1) {
                        if ((replacementStack.isEmpty() || candidateStack.getItemDamage() > replacementStack.getItemDamage()) && (!refillBeforeBreak || candidateStack.getMaxDamage() - candidateStack.getItemDamage() > this.config.getIntProperty("autoRefillDamageThreshhold"))) {
                           replacementStack = candidateStack;
                           replacementStackSlot = i;
                        }
                     } else if (replacementStack.isEmpty() || candidateStack.getCount() < replacementStack.getCount()) {
                        replacementStack = candidateStack;
                        replacementStackSlot = i;
                     }
                  }
               }
            }
         }
      } else {
         for(int i = 0; i < 36; ++i) {
            ItemStack candidateStack = container.getItemStack(i);
            if (!candidateStack.isEmpty() && Objects.equals(candidateStack.getItem().getRegistryName().toString(), wantedId) && candidateStack.getItemDamage() == wantedDamage) {
               replacementStack = candidateStack;
               replacementStackSlot = i;
               break;
            }
         }
      }

      if (!replacementStack.isEmpty() || refillBeforeBreak && !container.getSlot(slot).getStack().isEmpty()) {
         log.info("Automatic stack replacement.");
         InvTweaks.getInstance().addScheduledTask((new Runnable() {
            private ContainerSectionManager containerMgr;
            private int targetedSlot;
            private int i;
            @Nullable
            private String expectedItemId;
            private boolean refillBeforeBreak;

            @NotNull
            public Runnable init(int i_, int currentItem, boolean refillBeforeBreak_) throws Exception {
               this.containerMgr = new ContainerSectionManager(ContainerSection.INVENTORY);
               this.targetedSlot = currentItem;
               if (i_ != -1) {
                  this.i = i_;
                  this.expectedItemId = this.containerMgr.getItemStack(this.i).getItem().getRegistryName().toString();
               } else {
                  this.i = this.containerMgr.getFirstEmptyIndex();
                  this.expectedItemId = null;
               }

               this.refillBeforeBreak = refillBeforeBreak_;
               return this;
            }

            public void run() {
               if (this.i >= 0 && this.targetedSlot >= 0) {
                  ItemStack stack = this.containerMgr.getItemStack(this.i);
                  if (!stack.isEmpty() && StringUtils.equals(stack.getItem().getRegistryName().toString(), this.expectedItemId) || this.refillBeforeBreak) {
                     if (!this.containerMgr.move(this.targetedSlot, this.i) && !this.containerMgr.move(this.i, this.targetedSlot)) {
                        InvTweaksHandlerAutoRefill.log.warn("Failed to move stack for autoreplace, despite of prior tests.");
                     } else {
                        if (!InvTweaksHandlerAutoRefill.this.config.getProperty("enableSounds").equals("false")) {
                           InvTweaksHandlerAutoRefill.this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_CHICKEN_EGG, 1.0F));
                        }

                        if (!this.containerMgr.getItemStack(this.i).isEmpty() && this.i >= 27) {
                           for(int j = 0; j < 36; ++j) {
                              if (this.containerMgr.getItemStack(j).isEmpty()) {
                                 this.containerMgr.move(this.i, j);
                                 break;
                              }
                           }
                        }

                        this.containerMgr.applyChanges();
                     }
                  }

               }
            }
         }).init(replacementStackSlot, slot, refillBeforeBreak));
      }

   }
}
