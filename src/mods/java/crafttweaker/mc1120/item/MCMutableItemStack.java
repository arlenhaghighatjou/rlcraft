package crafttweaker.mc1120.item;

import crafttweaker.api.data.IData;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.item.IMutableItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import crafttweaker.mc1120.data.NBTConverter;
import crafttweaker.mc1120.data.NBTUpdater;
import java.util.Random;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

public class MCMutableItemStack extends MCItemStack implements IMutableItemStack {
   public MCMutableItemStack(ItemStack itemStack) {
      super(itemStack, NBTConverter.from(itemStack.getTagCompound(), false));
   }

   public MCMutableItemStack(ItemStack itemStack, boolean wildcardSize) {
      super(itemStack, NBTConverter.from(itemStack.getTagCompound(), false), wildcardSize);
   }

   public void shrink(int quality) {
      this.origin.shrink(quality);
   }

   public void grow(int quality) {
      this.origin.grow(quality);
   }

   public boolean attemptDamageItem(int amount, IPlayer player) {
      EntityPlayer mcplayer = CraftTweakerMC.getPlayer(player);
      return this.origin.attemptDamageItem(amount, mcplayer != null ? mcplayer.world.rand : new Random(), mcplayer instanceof EntityPlayerMP ? (EntityPlayerMP)mcplayer : null);
   }

   public IItemStack withTag(IData tag, boolean matchTagExact) {
      this.matchTagExact = matchTagExact;
      this.tag = tag;
      this.origin.setTagCompound(CraftTweakerMC.getNBTCompound(tag));
      return this;
   }

   public IItemStack updateTag(IData tagUpdate, boolean matchTagExact) {
      this.matchTagExact = matchTagExact;
      if (this.tag == null) {
         if (this.origin.getTagCompound() == null) {
            return this.withTag(tagUpdate, matchTagExact);
         }

         this.tag = NBTConverter.from(this.origin.getTagCompound(), false);
      }

      NBTUpdater.updateMap(this.origin.getTagCompound(), tagUpdate);
      this.tag = this.tag.update(tagUpdate);
      return this;
   }

   public IItemStack withEmptyTag() {
      NBTTagCompound nbt = new NBTTagCompound();
      this.tag = NBTConverter.from(nbt, false);
      this.origin.setTagCompound(nbt);
      return this;
   }

   public IItemStack removeTag(String tag) {
      if (tag == null) {
         this.origin.setTagCompound((NBTTagCompound)null);
      } else {
         this.origin.getTagCompound().removeTag(tag);
      }

      this.tag = NBTConverter.from(this.origin.getTagCompound(), false);
      return this;
   }

   public IItemStack withAmount(int amount) {
      this.origin.setCount(amount);
      return this;
   }

   public IItemStack withDamage(int damage) {
      this.origin.setItemDamage(damage);
      return this;
   }

   public IItemStack withDisplayName(String name) {
      NBTTagCompound tagComp;
      if (this.origin.hasTagCompound() && this.origin.getTagCompound() != null) {
         tagComp = this.origin.getTagCompound();
      } else {
         tagComp = new NBTTagCompound();
      }

      NBTTagCompound display;
      if (tagComp.hasKey("display") && tagComp.getTag("display") instanceof NBTTagCompound) {
         display = (NBTTagCompound)tagComp.getTag("display");
      } else {
         display = new NBTTagCompound();
      }

      display.setString("Name", name);
      tagComp.setTag("display", display);
      this.origin.setTagCompound(tagComp);
      return this;
   }

   public IItemStack withLore(String[] lore) {
      NBTTagCompound tagComp;
      if (this.origin.hasTagCompound() && this.origin.getTagCompound() != null) {
         tagComp = this.origin.getTagCompound();
      } else {
         tagComp = new NBTTagCompound();
      }

      NBTTagCompound display;
      if (tagComp.hasKey("display") && tagComp.getTag("display") instanceof NBTTagCompound) {
         display = (NBTTagCompound)tagComp.getTag("display");
      } else {
         display = new NBTTagCompound();
      }

      NBTTagList loreList;
      if (display.hasKey("Lore") && display.getTag("Lore") instanceof NBTTagList) {
         loreList = (NBTTagList)display.getTag("Lore");
      } else {
         loreList = new NBTTagList();
      }

      for(String s : lore) {
         loreList.appendTag(new NBTTagString(s));
      }

      display.setTag("Lore", loreList);
      tagComp.setTag("display", display);
      this.origin.setTagCompound(tagComp);
      return this;
   }

   public IItemStack copy() {
      return new MCItemStack(this.origin.copy());
   }

   public IMutableItemStack mutable() {
      return this.origin.isEmpty() ? null : this;
   }

   public void damageItem(int amount, IEntity entity) {
      if (entity.getInternal() instanceof EntityLivingBase) {
         this.origin.damageItem(amount, (EntityLivingBase)entity.getInternal());
      }

   }

   public Object getInternal() {
      return this.origin;
   }
}
