package org.jwaresoftware.mcmods.armorunder;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jwaresoftware.mcmods.lib.IniRegistries;
import org.jwaresoftware.mcmods.lib.Tooltips;
import org.jwaresoftware.mcmods.lib.api.IUiTipped;
import org.jwaresoftware.mcmods.lib.impl.ItemBase;

public class ModItem extends ItemBase implements IUiTipped {
   public ModItem(String oid, boolean hidden) {
      super(oid, hidden);
   }

   public ModItem(String oid) {
      this(oid, false);
   }

   protected final String itemid_prefix() {
      return "auw_";
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flags) {
      Tooltips.addDocTipDefault(ModInfo.ID, stack, tooltip);
   }

   public static final <T> T autoregisterItem(Item item, String oid) {
      return (T)IniRegistries.autoregisterItem("armorunder", item, oid);
   }

   public final ModItem autoregister() {
      ModItems.instances.add(this);
      autoregisterItem(this, this._oid);
      return this;
   }

   public static final ModItem simpleItem(String oid, boolean hidden, @Nullable Item container, @Nullable CreativeTabs tab) {
      ModItem item = new ModItem(oid, hidden);
      if (tab != null && !hidden) {
         item.setCreativeTab(tab);
      }

      if (container != null) {
         item.setContainerItem(container);
      }

      return item.autoregister();
   }

   public static final ModItem modicon() {
      return simpleItem("mod_icon", true, (Item)null, (CreativeTabs)null);
   }

   public static final ModItem simple(String oid) {
      return simpleItem(oid, false, (Item)null, (CreativeTabs)null);
   }

   public static final ModItem unlimited(String oid) {
      ModItem out = simpleItem(oid, false, (Item)null, CreativeTabs.TOOLS);
      out.setContainerItem(out);
      out.setMaxStackSize(1);
      return out;
   }

   public static final ModItem special(String oid) {
      ModItem out = simpleItem(oid, false, (Item)null, (CreativeTabs)null);
      out.setMaxStackSize(1);
      out.setRarity(EnumRarity.UNCOMMON);
      return out;
   }

   public static final ModItem shinyshiny(String oid) {
      ModItem out = special(oid);
      out.setRarity(EnumRarity.RARE);
      out.setGlimmerEffect(true);
      return out;
   }
}
