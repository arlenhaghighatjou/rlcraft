package vazkii.quark.client.feature;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiShulkerBox;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Post;
import net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent.Pre;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.api.ICustomSearchHandler;
import vazkii.quark.api.IItemSearchBar;
import vazkii.quark.api.capability.ISearchHandler;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.base.module.Feature;
import vazkii.quark.management.feature.FavoriteItems;

public class ChestSearchBar extends Feature {
   public static String text = "";
   public static GuiTextField searchBar;
   public static boolean skip;
   public static boolean moveToCenterBar;
   private long lastClick;
   private int matched;
   public static List<String> classnames;

   public ChestSearchBar() {
   }

   public void setupConfig() {
      String[] classnamesArr = this.loadPropStringList("Forced GUIs", "GUIs in which the sort button should be forced to show up. Use the \"Debug Classnames\" option in chest buttons to find the names.", new String[0]);
      classnames = Lists.newArrayList(classnamesArr);
      boolean invtweaks = this.loadPropBool("Avoid Invtweaks Buttons", "Automatically move the search bar if Inventory Tweaks is loaded so it doesn't end up in the same place as their buttons.", true);
      moveToCenterBar = this.loadPropBool("Move to Center Bar", "Set to true to move to the center bar, next to the \"Inventory\" text.", false);
      moveToCenterBar |= invtweaks && Loader.isModLoaded("inventorytweaks");
   }

   @SubscribeEvent
   public void initGui(Post event) {
      GuiScreen gui = event.getGui();
      if (!(gui instanceof IItemSearchBar) && !classnames.contains(gui.getClass().getName()) && !(gui instanceof GuiChest) && !(gui instanceof GuiShulkerBox)) {
         searchBar = null;
      } else {
         GuiContainer chest = (GuiContainer)gui;
         searchBar = new GuiTextField(12831, gui.mc.fontRenderer, chest.getGuiLeft() + 81, chest.getGuiTop() + 6, 88, 10);
         if (moveToCenterBar) {
            searchBar.y = chest.getGuiTop() + chest.getYSize() - 95;
         }

         searchBar.setText(text);
         searchBar.setFocused(false);
         searchBar.setMaxStringLength(32);
         searchBar.setEnableBackgroundDrawing(false);
         if (gui instanceof IItemSearchBar) {
            ((IItemSearchBar)gui).onSearchBarAdded(searchBar);
         }
      }

   }

   @SubscribeEvent
   public void onKeypress(Pre event) {
      if (searchBar != null && searchBar.isFocused() && Keyboard.getEventKeyState()) {
         char eventChar = Keyboard.getEventCharacter();
         int eventCode = Keyboard.getEventKey();
         searchBar.textboxKeyTyped(eventChar, eventCode);
         text = searchBar.getText();
         event.setCanceled(eventCode != 1);
      }

   }

   @SubscribeEvent
   public void onClick(net.minecraftforge.client.event.GuiScreenEvent.MouseInputEvent.Pre event) {
      if (searchBar != null && Mouse.getEventButtonState()) {
         Minecraft mc = Minecraft.getMinecraft();
         GuiScreen gui = event.getGui();
         int x = Mouse.getEventX() * gui.width / mc.displayWidth;
         int y = gui.height - Mouse.getEventY() * gui.height / mc.displayHeight - 1;
         int button = Mouse.getEventButton();
         searchBar.mouseClicked(x, y, button);
         long time = System.currentTimeMillis();
         long delta = time - this.lastClick;
         if (delta < 200L && searchBar.isFocused()) {
            searchBar.setText("");
            text = "";
         }

         this.lastClick = time;
      }

   }

   @SubscribeEvent
   public void onRender(net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent.Post event) {
      if (searchBar != null && !skip) {
         this.renderElements(event.getGui());
      }

      skip = false;
   }

   @SubscribeEvent
   public void drawTooltipEvent(net.minecraftforge.client.event.RenderTooltipEvent.Pre event) {
      if (searchBar != null) {
         this.renderElements(Minecraft.getMinecraft().currentScreen);
         skip = true;
      }

   }

   private void renderElements(GuiScreen gui) {
      this.drawBackground(gui, searchBar.x - 2, searchBar.y - 2);
      if (!text.isEmpty() && gui instanceof GuiContainer) {
         GuiContainer guiContainer = (GuiContainer)gui;
         Container container = guiContainer.inventorySlots;
         int guiLeft = guiContainer.getGuiLeft();
         int guiTop = guiContainer.getGuiTop();
         this.matched = 0;

         for(Slot s : container.inventorySlots) {
            ItemStack stack = s.getStack();
            if (!namesMatch(stack, text)) {
               int x = guiLeft + s.xPos;
               int y = guiTop + s.yPos;
               GlStateManager.disableDepth();
               Gui.drawRect(x, y, x + 16, y + 16, -1442840576);
            } else {
               ++this.matched;
            }
         }
      }

      if (this.matched == 0 && !text.isEmpty()) {
         searchBar.setTextColor(16733525);
      } else {
         searchBar.setTextColor(16777215);
      }

      searchBar.drawTextBox();
   }

   private void drawBackground(GuiScreen gui, int x, int y) {
      if ((!(gui instanceof IItemSearchBar) || !((IItemSearchBar)gui).renderBackground(x, y)) && gui != null) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableLighting();
         Minecraft.getMinecraft().getTextureManager().bindTexture(LibMisc.GENERAL_ICONS_RESOURCE);
         Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 244.0F, 90, 12, 256.0F, 256.0F);
      }
   }

   public static boolean namesMatch(ItemStack stack, String search) {
      search = TextFormatting.getTextWithoutFormattingCodes(search.trim().toLowerCase());
      if (search != null && !search.isEmpty()) {
         if (stack.isEmpty()) {
            return false;
         } else {
            Item item = stack.getItem();
            if (item instanceof ItemShulkerBox) {
               NBTTagCompound cmp = ItemNBTHelper.getCompound(stack, "BlockEntityTag", true);
               if (cmp != null && cmp.hasKey("Items", 9)) {
                  NonNullList<ItemStack> itemList = NonNullList.withSize(27, ItemStack.EMPTY);
                  ItemStackHelper.loadAllItems(cmp, itemList);

                  for(ItemStack innerStack : itemList) {
                     if (namesMatch(innerStack, search)) {
                        return true;
                     }
                  }
               }
            }

            String name = stack.getDisplayName();
            name = TextFormatting.getTextWithoutFormattingCodes(name.trim().toLowerCase());
            ICustomSearchHandler.StringMatcher matcher = String::contains;
            if (search.length() >= 3 && search.startsWith("\"") && search.endsWith("\"")) {
               search = search.substring(1, search.length() - 1);
               matcher = String::equals;
            }

            if (search.length() >= 3 && search.startsWith("/") && search.endsWith("/")) {
               search = search.substring(1, search.length() - 1);
               matcher = (s1, s2) -> {
                  return Pattern.compile(s2).matcher(s1).find();
               };
            }

            if (stack.isItemEnchanted()) {
               Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);

               for(Enchantment e : enchants.keySet()) {
                  if (e != null && matcher.matches(e.getTranslatedName(((Integer)enchants.get(e)).intValue()).toLowerCase(), search)) {
                     return true;
                  }
               }
            }

            List<String> potionNames = new ArrayList();
            PotionUtils.addPotionTooltip(stack, potionNames, 1.0F);

            for(String s : potionNames) {
               if (matcher.matches(TextFormatting.getTextWithoutFormattingCodes(s.trim().toLowerCase()), search)) {
                  return true;
               }
            }

            if (stack.getItem() == Items.ENCHANTED_BOOK) {
               NBTTagList enchants = ItemEnchantedBook.getEnchantments(stack);

               for(int i = 0; i < enchants.tagCount(); ++i) {
                  NBTTagCompound cmp = enchants.getCompoundTagAt(i);
                  int id = cmp.getInteger("id");
                  int lvl = cmp.getInteger("lvl");
                  Enchantment e = Enchantment.getEnchantmentByID(id);
                  if (e != null && matcher.matches(e.getTranslatedName(lvl).toLowerCase(), search)) {
                     return true;
                  }
               }
            }

            CreativeTabs tab = item.getCreativeTab();
            if (tab != null && matcher.matches(I18n.format(tab.getTranslatedTabLabel(), new Object[0]).toLowerCase(), search)) {
               return true;
            } else if (search.matches("favou?rites?") && FavoriteItems.isItemFavorited(stack)) {
               return true;
            } else {
               ResourceLocation itemName = (ResourceLocation)Item.REGISTRY.getNameForObject(item);
               ModContainer mod = (ModContainer)Loader.instance().getIndexedModList().get(((ResourceLocation)Objects.requireNonNull(itemName)).getResourceDomain());
               if (matcher.matches(mod.getName().toLowerCase(), search)) {
                  return true;
               } else if (matcher.matches(name, search)) {
                  return true;
               } else {
                  return ISearchHandler.hasHandler(stack) && ISearchHandler.getHandler(stack).stackMatchesSearchQuery(search, matcher, ChestSearchBar::namesMatch);
               }
            }
         }
      } else {
         return true;
      }
   }

   public boolean hasSubscriptions() {
      return this.isClient();
   }
}
