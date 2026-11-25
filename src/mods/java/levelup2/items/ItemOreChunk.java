package levelup2.items;

import java.util.List;
import levelup2.config.LevelUpConfig;
import levelup2.skills.SkillRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemOreChunk extends Item {
   private List<String> oreTypes;

   public ItemOreChunk(List<String> oreTypes) {
      this.setHasSubtypes(true);
      this.setCreativeTab(CreativeTabs.MATERIALS);
      this.oreTypes = oreTypes;
   }

   @SideOnly(Side.CLIENT)
   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
      if (!((String)this.oreTypes.get(0)).equals("null") && this.isInCreativeTab(tab) && LevelUpConfig.useOreChunks) {
         for(int i = 0; i < this.oreTypes.size(); ++i) {
            list.add(new ItemStack(this, 1, i));
         }
      }

   }

   public String getItemStackDisplayName(ItemStack stack) {
      return !((String)this.oreTypes.get(0)).equals("null") ? I18n.translateToLocalFormatted("item.levelup:orechunk.name", new Object[]{this.getOreName(stack)}) : I18n.translateToLocalFormatted("item.levelup:orechunk_null.name", new Object[0]);
   }

   private String getOreName(ItemStack stack) {
      int meta = stack.getMetadata();
      if (meta < this.oreTypes.size()) {
         ItemStack check = SkillRegistry.getOreEntry((String)this.oreTypes.get(meta));
         if (!check.isEmpty()) {
            String name = check.getUnlocalizedName();
            if (!name.endsWith(".name")) {
               name = name + ".name";
            }

            return I18n.translateToLocalFormatted(name, new Object[0]);
         }
      }

      return "Ore";
   }
}
