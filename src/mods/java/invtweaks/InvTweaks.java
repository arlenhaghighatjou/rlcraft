package invtweaks;

import com.google.common.collect.Multimap;
import invtweaks.api.IItemTree;
import invtweaks.api.IItemTreeItem;
import invtweaks.api.SortingMethod;
import invtweaks.api.container.ContainerSection;
import invtweaks.container.ContainerSectionManager;
import invtweaks.container.DirectContainerManager;
import invtweaks.container.IContainerManager;
import invtweaks.forge.InvTweaksMod;
import invtweaks.integration.ItemListChecker;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class InvTweaks extends InvTweaksObfuscation {
   public static Logger log;
   private static InvTweaks instance;
   @NotNull
   private final ItemStack[] hotbarClone = new ItemStack[9];
   @NotNull
   private final List<String> queuedMessages = new ArrayList();
   private final ItemListChecker itemListChecker = new ItemListChecker();
   @Nullable
   private InvTweaksConfigManager cfgManager = null;
   private SortingMethod chestAlgorithm = SortingMethod.DEFAULT;
   private long chestAlgorithmClickTimestamp = 0L;
   private boolean chestAlgorithmButtonDown = false;
   @NotNull
   private ItemStack storedStack = ItemStack.EMPTY;
   @Nullable
   private String storedStackId = null;
   private int storedStackDamage = 32767;
   private int storedFocusedSlot = -1;
   private boolean hadFocus = true;
   private boolean mouseWasDown = false;
   private boolean wasInGUI = false;
   private boolean previousRecipeBookVisibility = false;
   private int tickNumber = 0;
   private int lastPollingTickNumber = -3;
   private long sortingKeyPressedDate = 0L;
   private boolean sortKeyDown = false;
   private boolean sortKeyEnabled = true;
   private boolean textboxMode = false;
   private boolean itemPickupPending = false;
   private int itemPickupTimeout = 0;
   private String mostRecentComparison = "";
   private boolean debugTree = false;

   public InvTweaks(Minecraft mc_) {
      super(mc_);

      for(int i = 0; i < this.hotbarClone.length; ++i) {
         this.hotbarClone[i] = ItemStack.EMPTY;
      }

      instance = this;
      this.cfgManager = new InvTweaksConfigManager(this.mc);
      if (this.cfgManager.makeSureConfigurationIsLoaded()) {
         log.info("Mod initialized");
      } else {
         log.error("Mod failed to initialize!");
      }

   }

   public static void logInGameStatic(@NotNull String message) {
      getInstance().logInGame(message);
   }

   public static void logInGameErrorStatic(@NotNull String message, @NotNull Exception e) {
      getInstance().logInGameError(message, e);
   }

   public static InvTweaks getInstance() {
      return instance;
   }

   public static Minecraft getMinecraftInstance() {
      return instance.mc;
   }

   @Nullable
   public static InvTweaksConfigManager getConfigManager() {
      return instance == null ? null : instance.cfgManager;
   }

   @NotNull
   public static IContainerManager getContainerManager(@NotNull Container container) {
      return new DirectContainerManager(container);
   }

   @NotNull
   public static IContainerManager getCurrentContainerManager() {
      return getContainerManager(InvTweaksObfuscation.getCurrentContainer());
   }

   private static int getContainerRowSize(@NotNull GuiContainer guiContainer) {
      return getSpecialChestRowSize(guiContainer.inventorySlots);
   }

   @NotNull
   private static String buildLogString(@NotNull Level level, String message, @Nullable Exception e) {
      if (e != null) {
         StackTraceElement[] trace = e.getStackTrace();
         if (trace.length == 0) {
            return buildLogString(level, message) + ": " + e.getMessage();
         } else {
            StackTraceElement exceptionLine = trace[0];
            return exceptionLine != null && exceptionLine.getFileName() != null ? buildLogString(level, message) + ": " + e.getMessage() + " (l" + exceptionLine.getLineNumber() + " in " + exceptionLine.getFileName().replace("InvTweaks", "") + ")" : buildLogString(level, message) + ": " + e.getMessage();
         }
      } else {
         return buildLogString(level, message);
      }
   }

   @NotNull
   private static String buildLogString(@NotNull Level level, String message) {
      return "InvTweaks: " + (level.equals(Level.SEVERE) ? "[ERROR] " : "") + message;
   }

   private static int compareMaxDamage(ItemStack i, ItemStack j) {
      int maxDamage1 = i.getMaxDamage() <= 0 ? Integer.MAX_VALUE : i.getMaxDamage();
      int maxDamage2 = j.getMaxDamage() <= 0 ? Integer.MAX_VALUE : j.getMaxDamage();
      return maxDamage2 - maxDamage1;
   }

   private static int compareCurDamage(ItemStack i, ItemStack j) {
      int curDamage1 = i.getItemDamage();
      int curDamage2 = j.getItemDamage();
      return i.isItemStackDamageable() && !getConfigManager().getConfig().getProperty("invertToolDamageSorting").equals("true") ? curDamage2 - curDamage1 : curDamage1 - curDamage2;
   }

   public static String getToolClass(ItemStack itemStack, Item item) {
      if (itemStack != null && item != null) {
         Set<String> toolClassSet = item.getToolClasses(itemStack);
         if (toolClassSet.contains("sword")) {
            Set<String> newClassSet = new HashSet();

            for(String toolClass : toolClassSet) {
               if (toolClass != "sword") {
                  newClassSet.add(toolClass);
               }
            }

            toolClassSet = newClassSet;
         }

         if (toolClassSet.isEmpty()) {
            if (item instanceof ItemHoe) {
               return "hoe";
            } else if (item instanceof ItemShears) {
               return "shears";
            } else {
               return item instanceof ItemFishingRod ? "fishingrod" : "";
            }
         } else if (toolClassSet.size() == 1) {
            return (String)toolClassSet.toArray()[0];
         } else {
            String[] prefOrder = new String[]{"pickaxe", "axe", "shovel", "hoe", "shears", "wrench"};

            for(int i = 0; i < prefOrder.length; ++i) {
               if (toolClassSet.contains(prefOrder[i])) {
                  return prefOrder[i];
               }
            }

            return (String)toolClassSet.toArray()[0];
         }
      } else {
         return "";
      }
   }

   public void addScheduledTask(Runnable task) {
      InvTweaksMod.proxy.addClientScheduledTask(task);
   }

   public void onTickInGame() {
      synchronized(this) {
         if (this.onTick()) {
            this.handleAutoRefill();
            if (this.wasInGUI) {
               this.wasInGUI = false;
               this.textboxMode = false;
            }

         }
      }
   }

   public void onTickInGUI(GuiScreen guiScreen) {
      if (this.mc.playerController.isSpectator()) {
         this.onTick();
      } else {
         synchronized(this) {
            this.handleMiddleClick(guiScreen);
            if (this.onTick()) {
               if (this.isTimeForPolling()) {
                  this.unlockKeysIfNecessary();
               }

               if (isGuiContainer(guiScreen)) {
                  this.handleGUILayout((GuiContainer)guiScreen);
               }

               if (!this.wasInGUI) {
                  this.mouseWasDown = true;
               }

               if (isGuiContainer(guiScreen)) {
                  this.handleShortcuts((GuiContainer)guiScreen);
               }

               ItemStack currentStack = this.getFocusedStack();
               this.storedStackId = currentStack.isEmpty() ? null : currentStack.getItem().getRegistryName().toString();
               this.storedStackDamage = currentStack.isEmpty() ? 0 : currentStack.getItemDamage();
               if (!this.wasInGUI) {
                  this.wasInGUI = true;
               }

            }
         }
      }
   }

   public final void onSortingKeyPressed() {
      synchronized(this) {
         if (this.cfgManager.makeSureConfigurationIsLoaded()) {
            GuiScreen guiScreen = this.getCurrentScreen();
            if (guiScreen == null || isGuiContainer(guiScreen) && (isValidChest(((GuiContainer)guiScreen).inventorySlots) || isValidInventory(((GuiContainer)guiScreen).inventorySlots))) {
               this.handleSorting(guiScreen);
            }

         }
      }
   }

   public void onItemPickup() {
      if (this.cfgManager.makeSureConfigurationIsLoaded()) {
         InvTweaksConfig config = this.cfgManager.getConfig();
         if (this.cfgManager.getConfig().getProperty("enableSortingOnPickup").equals("false")) {
            this.itemPickupPending = false;
         } else {
            try {
               ContainerSectionManager containerMgr = new ContainerSectionManager(ContainerSection.INVENTORY);
               int currentSlot = -1;

               for(int i = 0; i < 9; ++i) {
                  ItemStack currentHotbarStack = containerMgr.getItemStack(i + 27);
                  if (!currentHotbarStack.isEmpty() && currentHotbarStack.getAnimationsToGo() > 0 && this.hotbarClone[i].isEmpty()) {
                     currentSlot = i + 27;
                  }
               }

               if (currentSlot != -1) {
                  this.itemPickupPending = false;
                  IItemTree tree = config.getTree();
                  ItemStack stack = containerMgr.getItemStack(currentSlot);
                  List<IItemTreeItem> items = tree.getItems(stack.getItem().getRegistryName().toString(), stack.getItemDamage());
                  List<Integer> preferredPositions = (List)config.getRules().stream().filter((rule) -> {
                     return tree.matches(items, rule.getKeyword());
                  }).flatMapToInt((ex) -> {
                     return Arrays.stream(e.getPreferredSlots());
                  }).boxed().collect(Collectors.toList());
                  boolean hasToBeMoved = true;
                  Iterator i = preferredPositions.iterator();

                  while(i.hasNext()) {
                     int newSlot = ((Integer)i.next()).intValue();
                     if (newSlot == currentSlot) {
                        hasToBeMoved = false;
                        break;
                     }

                     if (containerMgr.getItemStack(newSlot).isEmpty() && containerMgr.move(currentSlot, newSlot)) {
                        break;
                     }
                  }

                  if (hasToBeMoved) {
                     for(int i = 0; i < containerMgr.getSize() && (!containerMgr.getItemStack(i).isEmpty() || !containerMgr.move(currentSlot, i)); ++i) {
                        ;
                     }
                  }

                  containerMgr.applyChanges();
               } else if (--this.itemPickupTimeout == 0) {
                  this.itemPickupPending = false;
               }
            } catch (Exception var11) {
               this.logInGameError("Failed to move picked up stack", var11);
               this.itemPickupPending = false;
            }

         }
      }
   }

   public int compareItems(@NotNull ItemStack i, @NotNull ItemStack j) {
      return this.compareItems(i, j, this.getItemOrder(i), this.getItemOrder(j), false);
   }

   public int compareItems(@NotNull ItemStack i, @NotNull ItemStack j, boolean onlyTreeSort) {
      return this.compareItems(i, j, this.getItemOrder(i), this.getItemOrder(j), onlyTreeSort);
   }

   int compareItems(@NotNull ItemStack i, @NotNull ItemStack j, int orderI, int orderJ) {
      return this.compareItems(i, j, orderI, orderJ, false);
   }

   int compareItems(@NotNull ItemStack i, @NotNull ItemStack j, int orderI, int orderJ, boolean api) {
      if (i.isEmpty() && j.isEmpty()) {
         if (this.debugTree) {
            this.mostRecentComparison = "Both stacks are Empty.";
         }

         return 0;
      } else if (j.isEmpty()) {
         if (this.debugTree) {
            this.mostRecentComparison = "J is Empty.";
         }

         return -1;
      } else if (!i.isEmpty() && orderI != -1) {
         if (this.debugTree) {
            this.mostRecentComparison = "";
         }

         if (api) {
            if (this.debugTree) {
               this.mostRecentComparison = "API Active, ";
            }

            int lastOrder = this.cfgManager.getConfig().getTree().getLastTreeOrder();
            if (orderI > lastOrder) {
               orderI = Integer.MAX_VALUE;
            }

            if (orderJ > lastOrder) {
               orderJ = Integer.MAX_VALUE;
            }
         }

         if (this.debugTree) {
            this.mostRecentComparison = this.mostRecentComparison + "I: " + orderI + ", J: " + orderJ;
         }

         if (orderI != orderJ) {
            if (this.debugTree) {
               this.mostRecentComparison = this.mostRecentComparison + ", Normal: " + (orderI - orderJ);
            }

            return orderI - orderJ;
         } else if (orderI == Integer.MAX_VALUE && orderJ == Integer.MAX_VALUE && api) {
            if (this.debugTree) {
               this.mostRecentComparison = this.mostRecentComparison + ", API Bailout.";
            }

            return 0;
         } else {
            Item iItem = i.getItem();
            Item jItem = j.getItem();
            int cTool = this.compareTools(i, j, iItem, jItem);
            if (this.debugTree) {
               this.mostRecentComparison = this.mostRecentComparison + ", Tool: " + cTool;
            }

            if (cTool != 0) {
               return cTool;
            } else {
               int cSword = this.compareSword(i, j, iItem, jItem);
               if (this.debugTree) {
                  this.mostRecentComparison = this.mostRecentComparison + ", Sword: " + cSword;
               }

               if (cSword != 0) {
                  return cSword;
               } else {
                  int cArmor = this.compareArmor(i, j, iItem, jItem);
                  if (this.debugTree) {
                     this.mostRecentComparison = this.mostRecentComparison + ", Armor: " + cArmor;
                  }

                  if (cArmor != 0) {
                     return cArmor;
                  } else {
                     int cName = this.compareNames(i, j);
                     if (this.debugTree) {
                        this.mostRecentComparison = this.mostRecentComparison + ", Name" + cName;
                     }

                     if (cName != 0) {
                        return cName;
                     } else {
                        int cEnchant = this.compareEnchantment(i, j);
                        if (cEnchant != 0) {
                           return cEnchant;
                        } else {
                           int maxDamage = compareMaxDamage(i, j);
                           if (this.debugTree) {
                              this.mostRecentComparison = this.mostRecentComparison + ", Max Damage: " + maxDamage;
                           }

                           if (maxDamage != 0) {
                              return maxDamage;
                           } else {
                              int curDamage = compareCurDamage(i, j);
                              if (this.debugTree) {
                                 this.mostRecentComparison = this.mostRecentComparison + ", Current Damage: " + curDamage;
                              }

                              if (curDamage != 0) {
                                 return curDamage;
                              } else if (j.getCount() != i.getCount()) {
                                 if (this.debugTree) {
                                    this.mostRecentComparison = this.mostRecentComparison + ", Stack Size";
                                 }

                                 return j.getCount() - i.getCount();
                              } else {
                                 if (this.debugTree) {
                                    this.mostRecentComparison = this.mostRecentComparison + ", Final: " + ObjectUtils.compare(i.getItem().getRegistryName().toString(), j.getItem().getRegistryName().toString());
                                 }

                                 return ObjectUtils.compare(i.getItem().getRegistryName().toString(), j.getItem().getRegistryName().toString());
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      } else {
         if (this.debugTree) {
            this.mostRecentComparison = "I is Empty or orderI was -1.";
         }

         return 1;
      }
   }

   private int compareNames(ItemStack i, ItemStack j) {
      boolean iHasName = i.hasDisplayName();
      boolean jHasName = j.hasDisplayName();
      String iDisplayName = i.getDisplayName();
      String jDisplayName = j.getDisplayName();
      if (iHasName || jHasName) {
         if (!iHasName) {
            if (this.debugTree) {
               this.mostRecentComparison = this.mostRecentComparison + ", J has custom Name";
            }

            return -1;
         }

         if (!jHasName) {
            if (this.debugTree) {
               this.mostRecentComparison = this.mostRecentComparison + ", I has custom Name";
            }

            return 1;
         }
      }

      if (!iDisplayName.equals(jDisplayName)) {
         if (this.debugTree) {
            this.mostRecentComparison = this.mostRecentComparison + ", Name: " + iDisplayName.compareTo(jDisplayName);
         }

         return iDisplayName.compareTo(jDisplayName);
      } else {
         return 0;
      }
   }

   private int compareTools(ItemStack i, ItemStack j, Item iItem, Item jItem) {
      String toolClass1 = getToolClass(i, iItem);
      String toolClass2 = getToolClass(j, jItem);
      if (this.debugTree) {
         this.mostRecentComparison = this.mostRecentComparison + ", ToolClass (" + toolClass1 + ", " + toolClass2 + ")";
      }

      boolean isTool1 = toolClass1 != "";
      boolean isTool2 = toolClass2 != "";
      if (isTool1 && isTool2) {
         int toolClassComparison = toolClass1.compareTo(toolClass2);
         if (toolClassComparison != 0) {
            return toolClassComparison;
         } else {
            int harvestLevel1 = iItem.getHarvestLevel(i, toolClass1, (EntityPlayer)null, (IBlockState)null);
            int harvestLevel2 = jItem.getHarvestLevel(j, toolClass2, (EntityPlayer)null, (IBlockState)null);
            int toolLevelComparison = harvestLevel2 - harvestLevel1;
            if (this.debugTree) {
               this.mostRecentComparison = this.mostRecentComparison + ", HarvestLevel (" + harvestLevel1 + ", " + harvestLevel2 + ")";
            }

            return toolLevelComparison != 0 ? Integer.compare(harvestLevel2, harvestLevel1) : compareMaxDamage(i, j);
         }
      } else {
         return Boolean.compare(isTool2, isTool1);
      }
   }

   private int compareSword(ItemStack itemStack1, ItemStack itemStack2, Item iItem, Item jItem) {
      Multimap<String, AttributeModifier> multimap1 = itemStack1 != null ? itemStack1.getAttributeModifiers(EntityEquipmentSlot.MAINHAND) : null;
      Multimap<String, AttributeModifier> multimap2 = itemStack2 != null ? itemStack2.getAttributeModifiers(EntityEquipmentSlot.MAINHAND) : null;
      String attackDamageName = SharedMonsterAttributes.ATTACK_DAMAGE.getName();
      String attackSpeedName = SharedMonsterAttributes.ATTACK_SPEED.getName();
      boolean hasDamage1 = itemStack1 != null ? multimap1.containsKey(attackDamageName) : false;
      boolean hasDamage2 = itemStack2 != null ? multimap2.containsKey(attackDamageName) : false;
      boolean hasSpeed1 = itemStack1 != null ? multimap1.containsKey(attackSpeedName) : false;
      boolean hasSpeed2 = itemStack2 != null ? multimap2.containsKey(attackSpeedName) : false;
      if (this.debugTree) {
         this.mostRecentComparison = this.mostRecentComparison + ", HasDamage (" + hasDamage1 + ", " + hasDamage2 + ")";
      }

      if (hasDamage1 && hasDamage2) {
         Collection<AttributeModifier> damageMap1 = multimap1.get(attackDamageName);
         Collection<AttributeModifier> damageMap2 = multimap2.get(attackDamageName);
         Double attackDamage1 = ((AttributeModifier)damageMap1.toArray()[0]).getAmount();
         Double attackDamage2 = ((AttributeModifier)damageMap2.toArray()[0]).getAmount();
         int damageComparison = Double.compare(attackDamage2.doubleValue(), attackDamage1.doubleValue());
         if (damageComparison == 0 && hasSpeed1 && hasSpeed2) {
            Collection<AttributeModifier> speedMap1 = multimap1.get(attackSpeedName);
            Collection<AttributeModifier> speedMap2 = multimap2.get(attackSpeedName);
            Double speed1 = ((AttributeModifier)speedMap1.toArray()[0]).getAmount();
            Double speed2 = ((AttributeModifier)speedMap2.toArray()[0]).getAmount();
            int speedComparison = Double.compare(speed2.doubleValue(), speed1.doubleValue());
            if (speedComparison != 0) {
               return speedComparison;
            }
         } else if (damageComparison != 0) {
            return damageComparison;
         }

         return compareMaxDamage(itemStack1, itemStack2);
      } else {
         return Boolean.compare(hasDamage2, hasDamage1);
      }
   }

   private int compareArmor(ItemStack i, ItemStack j, Item iItem, Item jItem) {
      int isArmor1 = iItem instanceof ItemArmor ? 1 : 0;
      int isArmor2 = jItem instanceof ItemArmor ? 1 : 0;
      if (isArmor1 != 0 && isArmor2 != 0) {
         ItemArmor a1 = (ItemArmor)iItem;
         ItemArmor a2 = (ItemArmor)jItem;
         if (a1.armorType != a2.armorType) {
            return a2.armorType.compareTo(a1.armorType);
         } else if (a1.damageReduceAmount != a2.damageReduceAmount) {
            return a2.damageReduceAmount - a1.damageReduceAmount;
         } else if (a1.toughness != a2.toughness) {
            return a2.toughness > a1.toughness ? -1 : 1;
         } else {
            return compareMaxDamage(i, j);
         }
      } else {
         return isArmor2 - isArmor1;
      }
   }

   private int compareEnchantment(ItemStack i, ItemStack j) {
      Map<Enchantment, Integer> iEnchs = EnchantmentHelper.getEnchantments(i);
      Map<Enchantment, Integer> jEnchs = EnchantmentHelper.getEnchantments(j);
      if (iEnchs.size() != jEnchs.size()) {
         if (this.debugTree) {
            this.mostRecentComparison = this.mostRecentComparison + ", Enchantment Count";
         }

         return jEnchs.size() - iEnchs.size();
      } else {
         int iEnchMaxId = 0;
         int iEnchMaxLvl = 0;
         int jEnchMaxId = 0;
         int jEnchMaxLvl = 0;

         for(Entry<Enchantment, Integer> ench : iEnchs.entrySet()) {
            int enchId = Enchantment.getEnchantmentID((Enchantment)ench.getKey());
            if (((Integer)ench.getValue()).intValue() > iEnchMaxLvl) {
               iEnchMaxId = enchId;
               iEnchMaxLvl = ((Integer)ench.getValue()).intValue();
            } else if (((Integer)ench.getValue()).intValue() == iEnchMaxLvl && enchId > iEnchMaxId) {
               iEnchMaxId = enchId;
            }
         }

         for(Entry<Enchantment, Integer> ench : jEnchs.entrySet()) {
            int enchId = Enchantment.getEnchantmentID((Enchantment)ench.getKey());
            if (((Integer)ench.getValue()).intValue() > jEnchMaxLvl) {
               jEnchMaxId = enchId;
               jEnchMaxLvl = ((Integer)ench.getValue()).intValue();
            } else if (((Integer)ench.getValue()).intValue() == jEnchMaxLvl && enchId > jEnchMaxId) {
               jEnchMaxId = enchId;
            }
         }

         if (iEnchMaxId != jEnchMaxId) {
            if (this.debugTree) {
               this.mostRecentComparison = this.mostRecentComparison + ", Highest Enchantment";
            }

            return jEnchMaxId - iEnchMaxId;
         } else if (iEnchMaxLvl != jEnchMaxLvl) {
            if (this.debugTree) {
               this.mostRecentComparison = this.mostRecentComparison + ", Highest Enchantment Level";
            }

            return jEnchMaxLvl - iEnchMaxLvl;
         } else {
            if (this.debugTree) {
               this.mostRecentComparison = this.mostRecentComparison + ", Enchantment Level same";
            }

            return 0;
         }
      }
   }

   public void setItemPickupPending(boolean value) {
      this.itemPickupPending = value;
      this.itemPickupTimeout = 5;
   }

   public void setSortKeyEnabled(boolean enabled) {
      this.sortKeyEnabled = enabled;
   }

   public void setTextboxMode(boolean enabled) {
      this.textboxMode = enabled;
   }

   public void logInGame(@NotNull String message) {
      this.logInGame(message, false);
   }

   public void printQueuedMessages() {
      if (this.mc.ingameGUI != null && !this.queuedMessages.isEmpty()) {
         this.queuedMessages.forEach(this::addChatMessage);
         this.queuedMessages.clear();
      }

   }

   public void logInGame(@NotNull String message, boolean alreadyTranslated) {
      String formattedMsg = buildLogString(Level.INFO, alreadyTranslated ? message : I18n.format(message, new Object[0]));
      if (this.mc.ingameGUI == null) {
         this.queuedMessages.add(formattedMsg);
      } else {
         this.addChatMessage(formattedMsg);
      }

      log.info(formattedMsg);
   }

   public void logInGameError(@NotNull String message, @NotNull Exception e) {
      String formattedMsg = buildLogString(Level.SEVERE, I18n.format(message, new Object[0]), e);
      log.error(formattedMsg, e);
      if (this.mc.ingameGUI == null) {
         this.queuedMessages.add(formattedMsg);
      } else {
         this.addChatMessage(formattedMsg);
      }

   }

   private boolean onTick() {
      this.printQueuedMessages();
      ++this.tickNumber;
      if (this.mc.playerController.isSpectator()) {
         return false;
      } else {
         InvTweaksConfig config = this.cfgManager.getConfig();
         if (config == null) {
            return false;
         } else {
            if (this.itemPickupPending) {
               this.onItemPickup();
            }

            GuiScreen currentScreen = this.getCurrentScreen();
            if (currentScreen == null || isGuiInventory(currentScreen)) {
               this.cloneHotbar();
            }

            if (this.isSortingShortcutDown()) {
               if (!this.sortKeyDown) {
                  this.sortKeyDown = true;
                  this.onSortingKeyPressed();
               }
            } else {
               this.sortKeyDown = false;
            }

            this.handleConfigSwitch();
            return true;
         }
      }
   }

   private void handleConfigSwitch() {
      InvTweaksConfig config = this.cfgManager.getConfig();
      GuiScreen currentScreen = this.getCurrentScreen();
      this.cfgManager.getShortcutsHandler().updatePressedKeys();
      InvTweaksShortcutMapping switchMapping = this.cfgManager.getShortcutsHandler().isShortcutDown(InvTweaksShortcutType.MOVE_TO_SPECIFIC_HOTBAR_SLOT);
      if (this.isSortingShortcutDown() && switchMapping != null) {
         String newRuleset = null;
         int pressedKey = ((Integer)switchMapping.getKeyCodes().get(0)).intValue();
         if (pressedKey >= 2 && pressedKey <= 10) {
            newRuleset = config.switchConfig(pressedKey - 2);
         } else {
            switch(pressedKey) {
            case 71:
               newRuleset = config.switchConfig(6);
               break;
            case 72:
               newRuleset = config.switchConfig(7);
               break;
            case 73:
               newRuleset = config.switchConfig(8);
            case 74:
            case 78:
            default:
               break;
            case 75:
               newRuleset = config.switchConfig(3);
               break;
            case 76:
               newRuleset = config.switchConfig(4);
               break;
            case 77:
               newRuleset = config.switchConfig(5);
               break;
            case 79:
               newRuleset = config.switchConfig(0);
               break;
            case 80:
               newRuleset = config.switchConfig(1);
               break;
            case 81:
               newRuleset = config.switchConfig(2);
            }
         }

         if (newRuleset != null) {
            this.logInGame(String.format(I18n.format("invtweaks.loadconfig.enabled", new Object[0]), newRuleset), true);
            this.sortingKeyPressedDate = 2147483647L;
         }
      }

      if (this.isSortingShortcutDown()) {
         long currentTime = System.currentTimeMillis();
         if (this.sortingKeyPressedDate == 0L) {
            this.sortingKeyPressedDate = currentTime;
         } else if (currentTime - this.sortingKeyPressedDate > 1000L && this.sortingKeyPressedDate != 2147483647L) {
            String previousRuleset = config.getCurrentRulesetName();
            String newRuleset = config.switchConfig();
            if (previousRuleset != null && newRuleset != null && !previousRuleset.equals(newRuleset)) {
               this.logInGame(String.format(I18n.format("invtweaks.loadconfig.enabled", new Object[0]), newRuleset), true);
               this.handleSorting(currentScreen);
            }

            this.sortingKeyPressedDate = currentTime;
         }
      } else {
         this.sortingKeyPressedDate = 0L;
      }

   }

   private String ListOfClassNameKind(Object o) {
      String resString = "";
      Class testClass = o.getClass();

      while(testClass != null) {
         resString = resString + testClass.getName().toLowerCase();
         testClass = testClass.getSuperclass();
         if (testClass != null) {
            resString = resString + ", ";
         }
      }

      return resString;
   }

   private void handleSorting(GuiScreen guiScreen) {
      ItemStack selectedItem = ItemStack.EMPTY;
      int focusedSlot = this.getFocusedSlot();
      NonNullList<ItemStack> mainInventory = this.getMainInventory();
      if (focusedSlot < mainInventory.size() && focusedSlot >= 0) {
         selectedItem = (ItemStack)mainInventory.get(focusedSlot);
      }

      if (this.debugTree && selectedItem != null && !selectedItem.isEmpty()) {
         this.logInGame("Hand Item Details:", true);
         this.logInGame(selectedItem.toString(), true);
         this.logInGame("Classes: " + this.ListOfClassNameKind(selectedItem.getItem()));
         this.logInGame("Item Order Index: " + this.getItemOrder(selectedItem), true);
         ItemStack offhandStack = this.getOffhandStack();
         if (offhandStack != null && !offhandStack.isEmpty()) {
            this.logInGame("Off-Hand Item Details:", true);
            this.logInGame(offhandStack.toString(), true);
            this.logInGame("Item Order Index: " + this.getItemOrder(offhandStack), true);
            this.logInGame("Comparator result: " + this.compareItems(selectedItem, offhandStack), true);
            this.logInGame("Comparator debug: " + this.mostRecentComparison, true);
         }
      }

      try {
         (new InvTweaksHandlerSorting(this.mc, this.cfgManager.getConfig(), ContainerSection.INVENTORY, SortingMethod.INVENTORY, 9)).sort();
      } catch (Exception var6) {
         this.logInGameError("invtweaks.sort.inventory.error", var6);
         var6.printStackTrace();
      }

      this.playClick();
   }

   private void handleAutoRefill() {
      ItemStack currentStack = this.getFocusedStack();
      ItemStack offhandStack = this.getOffhandStack();
      String currentStackId = currentStack.isEmpty() ? null : currentStack.getItem().getRegistryName().toString();
      int currentStackDamage = currentStack.isEmpty() ? 0 : currentStack.getItemDamage();
      int focusedSlot = this.getFocusedSlot() + 27;
      InvTweaksConfig config = this.cfgManager.getConfig();
      if (this.storedFocusedSlot != focusedSlot) {
         this.storedFocusedSlot = focusedSlot;
      } else if (!ItemStack.areItemsEqual(currentStack, this.storedStack) && this.storedStackId != null && !this.storedStack.isEmpty() && !ItemStack.areItemStacksEqual(offhandStack, this.storedStack)) {
         if (currentStack.isEmpty() || currentStack.getItem() == Items.BOWL && Objects.equals(this.storedStackId, "minecraft:mushroom_stew") && (this.getCurrentScreen() == null || isGuiEditSign(this.getCurrentScreen()))) {
            if (config.isAutoRefillEnabled(this.storedStackId, this.storedStackDamage)) {
               try {
                  this.cfgManager.getAutoRefillHandler().autoRefillSlot(focusedSlot, this.storedStackId, this.storedStackDamage);
               } catch (Exception var11) {
                  this.logInGameError("invtweaks.sort.autorefill.error", var11);
               }
            }
         } else {
            int itemMaxDamage = currentStack.getMaxDamage();
            int autoRefillThreshhold = config.getIntProperty("autoRefillDamageThreshhold");
            if (this.canToolBeReplaced(currentStackDamage, itemMaxDamage, autoRefillThreshhold) && config.getProperty("autoRefillBeforeBreak").equals("true") && config.isAutoRefillEnabled(this.storedStackId, this.storedStackDamage)) {
               try {
                  this.cfgManager.getAutoRefillHandler().autoRefillSlot(focusedSlot, this.storedStackId, this.storedStackDamage);
               } catch (Exception var10) {
                  this.logInGameError("invtweaks.sort.autorefill.error", var10);
               }
            }
         }
      }

      this.storedStack = currentStack.copy();
      this.storedStackId = currentStackId;
      this.storedStackDamage = currentStackDamage;
   }

   private boolean canToolBeReplaced(int currentStackDamage, int itemMaxDamage, int autoRefillThreshhold) {
      return itemMaxDamage != 0 && itemMaxDamage - currentStackDamage < autoRefillThreshhold && itemMaxDamage - this.storedStackDamage >= autoRefillThreshhold;
   }

   private void handleMiddleClick(GuiScreen guiScreen) {
      if (Mouse.isButtonDown(2)) {
         if (!this.cfgManager.makeSureConfigurationIsLoaded()) {
            return;
         }

         InvTweaksConfig config = this.cfgManager.getConfig();
         if (config.getProperty("enableMiddleClick").equals("true") && isGuiContainer(guiScreen)) {
            GuiContainer guiContainer = (GuiContainer)guiScreen;
            Container container = guiContainer.inventorySlots;
            if (!this.chestAlgorithmButtonDown) {
               this.chestAlgorithmButtonDown = true;
               IContainerManager containerMgr = getContainerManager(container);
               Slot slotAtMousePosition = InvTweaksObfuscation.getSlotAtMousePosition((GuiContainer)this.getCurrentScreen());
               ContainerSection target = null;
               if (slotAtMousePosition != null) {
                  target = containerMgr.getSlotSection(getSlotNumber(slotAtMousePosition));
               }

               if (isValidChest(container)) {
                  if (ContainerSection.CHEST.equals(target)) {
                     this.playClick();
                     long timestamp = System.currentTimeMillis();
                     if (timestamp - this.chestAlgorithmClickTimestamp > 2000L || getContainerRowSize(guiContainer) > 9) {
                        this.chestAlgorithm = SortingMethod.DEFAULT;
                     }

                     try {
                        (new InvTweaksHandlerSorting(this.mc, this.cfgManager.getConfig(), ContainerSection.CHEST, this.chestAlgorithm, getContainerRowSize(guiContainer))).sort();
                     } catch (Exception var13) {
                        this.logInGameError("invtweaks.sort.chest.error", var13);
                        var13.printStackTrace();
                     }

                     this.chestAlgorithm = SortingMethod.values()[(this.chestAlgorithm.ordinal() + 1) % 3];
                     this.chestAlgorithmClickTimestamp = timestamp;
                  } else if (!ContainerSection.CRAFTING_IN.equals(target) && !ContainerSection.CRAFTING_IN_PERSISTENT.equals(target)) {
                     if (ContainerSection.INVENTORY_HOTBAR.equals(target) || ContainerSection.INVENTORY_NOT_HOTBAR.equals(target)) {
                        this.handleSorting(guiScreen);
                     }
                  } else {
                     try {
                        (new InvTweaksHandlerSorting(this.mc, this.cfgManager.getConfig(), target, SortingMethod.EVEN_STACKS, containerMgr.getSize(target) == 9 ? 3 : 2)).sort();
                     } catch (Exception var12) {
                        this.logInGameError("invtweaks.sort.crafting.error", var12);
                        var12.printStackTrace();
                     }
                  }
               } else if (isValidInventory(container)) {
                  if (!ContainerSection.CRAFTING_IN.equals(target) && !ContainerSection.CRAFTING_IN_PERSISTENT.equals(target)) {
                     this.handleSorting(guiScreen);
                  } else {
                     try {
                        (new InvTweaksHandlerSorting(this.mc, this.cfgManager.getConfig(), target, SortingMethod.EVEN_STACKS, containerMgr.getSize(target) == 9 ? 3 : 2)).sort();
                     } catch (Exception var11) {
                        this.logInGameError("invtweaks.sort.crafting.error", var11);
                        var11.printStackTrace();
                     }
                  }
               }
            }
         }
      } else {
         this.chestAlgorithmButtonDown = false;
      }

   }

   private boolean hasRecipeButton(@NotNull GuiContainer guiContainer) {
      if (guiContainer instanceof GuiInventory) {
         return true;
      } else {
         return guiContainer instanceof GuiCrafting;
      }
   }

   private boolean isRecipeBookVisible(@NotNull GuiContainer guiContainer) {
      if (guiContainer instanceof GuiInventory) {
         return ((GuiInventory)guiContainer).recipeBookGui.isVisible();
      } else {
         return guiContainer instanceof GuiCrafting ? ((GuiCrafting)guiContainer).recipeBookGui.isVisible() : false;
      }
   }

   private void handleGUILayout(@NotNull GuiContainer guiContainer) {
      InvTweaksConfig config = this.cfgManager.getConfig();
      Container container = guiContainer.inventorySlots;
      boolean isValidChest = isValidChest(container);
      if (showButtons(container)) {
         int w = 10;
         int h = 10;
         boolean isItemListVisible = this.itemListChecker.isVisible();
         boolean wasItemListVisible = this.itemListChecker.wasVisible();
         boolean isRecipeBookVisible = this.isRecipeBookVisible(guiContainer);
         boolean wasRecipeBookVisible = this.previousRecipeBookVisibility;
         boolean relayout = isItemListVisible != wasItemListVisible || isRecipeBookVisible != wasRecipeBookVisible;
         this.previousRecipeBookVisibility = isRecipeBookVisible;
         boolean customButtonsAdded = false;
         List<GuiButton> controlList = guiContainer.buttonList;
         List<GuiButton> toRemove = new ArrayList();

         for(GuiButton button : controlList) {
            if (button.id >= 54696386 && button.id < 54696390) {
               if (!relayout) {
                  customButtonsAdded = true;
                  break;
               }

               toRemove.add(button);
            }
         }

         controlList.removeAll(toRemove);
         guiContainer.buttonList = controlList;
         if (!customButtonsAdded) {
            boolean customTextureAvailable = this.hasTexture(new ResourceLocation("inventorytweaks", "textures/gui/button10px.png"));
            int id = 54696386;
            int x = guiContainer.guiLeft + guiContainer.xSize - 16;
            int y = guiContainer.guiTop + 5;
            if (!isValidChest) {
               controlList.add(new InvTweaksGuiSettingsButton(this.cfgManager, id, x, y, w, h, "...", I18n.format("invtweaks.button.settings.tooltip", new Object[0]), customTextureAvailable));
            } else {
               this.chestAlgorithmClickTimestamp = 0L;
               boolean isChestWayTooBig = isLargeChest(guiContainer.inventorySlots);
               if (isChestWayTooBig && isItemListVisible) {
                  x -= 20;
                  y += 50;
               }

               controlList.add(new InvTweaksGuiSettingsButton(this.cfgManager, id++, isChestWayTooBig ? x + 22 : x - 1, isChestWayTooBig ? y - 3 : y, w, h, "...", I18n.format("invtweaks.button.settings.tooltip", new Object[0]), customTextureAvailable));
               if (!config.getProperty("showChestButtons").equals("false")) {
                  int rowSize = getContainerRowSize(guiContainer);
                  GuiButton button = new InvTweaksGuiSortingButton(this.cfgManager, id++, isChestWayTooBig ? x + 22 : x - 37, isChestWayTooBig ? y + 38 : y, w, h, "s", I18n.format("invtweaks.button.chest1.tooltip", new Object[0]), SortingMethod.DEFAULT, rowSize, customTextureAvailable);
                  controlList.add(button);
                  if (rowSize <= 9) {
                     button = new InvTweaksGuiSortingButton(this.cfgManager, id++, isChestWayTooBig ? x + 22 : x - 13, isChestWayTooBig ? y + 12 : y, w, h, "h", I18n.format("invtweaks.button.chest3.tooltip", new Object[0]), SortingMethod.HORIZONTAL, rowSize, customTextureAvailable);
                     controlList.add(button);
                     button = new InvTweaksGuiSortingButton(this.cfgManager, id++, isChestWayTooBig ? x + 22 : x - 25, isChestWayTooBig ? y + 25 : y, w, h, "v", I18n.format("invtweaks.button.chest2.tooltip", new Object[0]), SortingMethod.VERTICAL, rowSize, customTextureAvailable);
                     controlList.add(button);
                  }
               }
            }
         }
      } else if (isGuiInventoryCreative(guiContainer)) {
         List<GuiButton> controlList = guiContainer.buttonList;
         GuiButton buttonToRemove = null;

         for(GuiButton o : controlList) {
            if (o.id == 54696386) {
               buttonToRemove = o;
               break;
            }
         }

         if (buttonToRemove != null) {
            controlList.remove(buttonToRemove);
         }
      }

   }

   private void handleShortcuts(@NotNull GuiContainer guiScreen) {
      if (isValidChest(guiScreen.inventorySlots) || isValidInventory(guiScreen.inventorySlots)) {
         if (!Mouse.isButtonDown(0) && !Mouse.isButtonDown(1)) {
            this.mouseWasDown = false;
         } else if (!this.mouseWasDown) {
            this.mouseWasDown = true;
            if (this.cfgManager.getConfig().getProperty("enableShortcuts").equals("true")) {
               this.cfgManager.getShortcutsHandler().handleShortcut();
            }
         }

      }
   }

   private int getItemOrder(@NotNull ItemStack itemStack) {
      List<IItemTreeItem> items = this.cfgManager.getConfig().getTree().getItems(itemStack.getItem().getRegistryName().toString(), itemStack.getItemDamage(), itemStack.getTagCompound());
      return items.size() > 0 ? ((IItemTreeItem)items.get(0)).getOrder() : Integer.MAX_VALUE;
   }

   private boolean isSortingShortcutDown() {
      if (this.sortKeyEnabled && !this.textboxMode) {
         int keyCode = this.cfgManager.getConfig().getSortKeyCode();
         return keyCode > 0 ? Keyboard.isKeyDown(keyCode) : Mouse.isButtonDown(100 + keyCode);
      } else {
         return false;
      }
   }

   private boolean isTimeForPolling() {
      if (this.tickNumber - this.lastPollingTickNumber >= 3) {
         this.lastPollingTickNumber = this.tickNumber;
      }

      return this.tickNumber - this.lastPollingTickNumber == 0;
   }

   private void unlockKeysIfNecessary() {
      boolean hasFocus = Display.isActive();
      if (!this.hadFocus && hasFocus) {
         Keyboard.destroy();
         boolean firstTry = true;

         while(!Keyboard.isCreated()) {
            try {
               Keyboard.create();
            } catch (LWJGLException var4) {
               if (firstTry) {
                  this.logInGameError("invtweaks.keyboardfix.error", var4);
                  firstTry = false;
               }
            }
         }

         if (!firstTry) {
            this.logInGame("invtweaks.keyboardfix.recover");
         }
      }

      this.hadFocus = hasFocus;
   }

   private void cloneHotbar() {
      NonNullList<ItemStack> mainInventory = this.getMainInventory();

      for(int i = 0; i < 9; ++i) {
         this.hotbarClone[i] = ((ItemStack)mainInventory.get(i)).copy();
      }

   }

   private void playClick() {
      if (!this.cfgManager.getConfig().getProperty("enableSounds").equals("false")) {
         this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
      }

   }
}
