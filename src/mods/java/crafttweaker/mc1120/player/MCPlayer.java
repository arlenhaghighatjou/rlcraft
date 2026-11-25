package crafttweaker.mc1120.player;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.chat.IChatMessage;
import crafttweaker.api.data.IData;
import crafttweaker.api.entity.IEntityItem;
import crafttweaker.api.formatting.IFormattedText;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IFoodStats;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.util.Position3f;
import crafttweaker.mc1120.CraftTweaker;
import crafttweaker.mc1120.data.NBTConverter;
import crafttweaker.mc1120.entity.MCEntityItem;
import crafttweaker.mc1120.entity.MCEntityLivingBase;
import crafttweaker.mc1120.network.MessageCopyClipboard;
import crafttweaker.mc1120.network.MessageOpenBrowser;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.items.ItemHandlerHelper;

public class MCPlayer extends MCEntityLivingBase implements IPlayer {
   private final EntityPlayer player;

   public MCPlayer(EntityPlayer player) {
      super(player);
      this.player = player;
   }

   public EntityPlayer getInternal() {
      return this.player;
   }

   public String getName() {
      return this.player.getName();
   }

   public IData getData() {
      return NBTConverter.from(this.player.getEntityData(), true);
   }

   public int getXP() {
      return this.player.experienceLevel;
   }

   public void setXP(int xp) {
      this.player.addExperienceLevel(-this.player.experienceLevel);
      this.player.addExperienceLevel(xp);
   }

   public void removeXP(int xp) {
      this.player.addExperience(-xp);
   }

   public void update(IData data) {
      NBTConverter.updateMap(this.player.getEntityData(), data);
   }

   public void sendChat(IChatMessage message) {
      Object internal = message.getInternal();
      if (!(internal instanceof ITextComponent)) {
         CraftTweakerAPI.logError("not a valid chat message");
      } else {
         ITextComponent text = (ITextComponent)internal;
         if (text.getUnformattedText().length() > 30000) {
            int msgCount = (int)Math.ceil((double)text.getUnformattedText().length() / 30000.0D);
            CraftTweakerAPI.logWarning("Message too long, splitting into " + msgCount + " messages");

            for(int i = 0; i < msgCount; ++i) {
               this.player.sendMessage(new TextComponentString(text.getUnformattedText().substring(30000 * i, Math.min(30000 * (i + 1), text.getUnformattedText().length()))));
            }
         } else {
            this.player.sendMessage(text);
         }

      }
   }

   public void sendChat(String message) {
      String[] words = message.split(" ");
      StringBuilder out = new StringBuilder();
      int i = 0;

      for(int wordsLength = words.length; i < wordsLength; ++i) {
         String word = words[i];
         out.append(word);
         if (i < wordsLength - 1) {
            out.append(' ');
         }

         if (out.length() > 25000) {
            this.player.sendMessage(new TextComponentString(out.toString()));
            out = new StringBuilder();
         }
      }

      if (out.length() > 0) {
         this.player.sendMessage(new TextComponentString(out.toString()));
      }

   }

   public void sendStatusMessage(String message, boolean hotBar) {
      this.player.sendStatusMessage(new TextComponentString(message), hotBar);
   }

   public void sendStatusMessage(IFormattedText message, boolean hotBar) {
      this.player.sendStatusMessage(new TextComponentString(message.getText()), hotBar);
   }

   public int getHotbarSize() {
      return 9;
   }

   public IItemStack getHotbarStack(int i) {
      return i >= 0 && i < 9 ? CraftTweakerMC.getIItemStack(this.player.inventory.getStackInSlot(i)) : null;
   }

   public int getInventorySize() {
      return this.player.inventory.getSizeInventory();
   }

   public IItemStack getInventoryStack(int i) {
      return CraftTweakerMC.getIItemStack(this.player.inventory.getStackInSlot(i));
   }

   public IItemStack getCurrentItem() {
      return CraftTweakerMC.getIItemStack(this.player.inventory.getCurrentItem());
   }

   public boolean isCreative() {
      return this.player.capabilities.isCreativeMode;
   }

   public boolean isAdventure() {
      return !this.player.capabilities.allowEdit;
   }

   public void openBrowser(String url) {
      if (this.player instanceof EntityPlayerMP) {
         CraftTweaker.NETWORK.sendTo(new MessageOpenBrowser(url), (EntityPlayerMP)this.player);
      }

   }

   public void setCooldown(IItemStack item, int time) {
      this.player.getCooldownTracker().setCooldown(CraftTweakerMC.getItemStack(item).getItem(), time);
   }

   public float getCooldown(IItemStack item) {
      return this.player.getCooldownTracker().getCooldown(CraftTweakerMC.getItemStack(item).getItem(), 0.0F);
   }

   public void copyToClipboard(String value) {
      if (this.player instanceof EntityPlayerMP) {
         CraftTweaker.NETWORK.sendTo(new MessageCopyClipboard(value), (EntityPlayerMP)this.player);
      }

   }

   public boolean equals(Object other) {
      return other != null && other.getClass() == this.getClass() && ((MCPlayer)other).player == this.player;
   }

   public int hashCode() {
      int hash = 5;
      hash = 23 * hash + (this.player != null ? this.player.hashCode() : 0);
      return hash;
   }

   public void give(IItemStack stack) {
      ItemStack itemstack = CraftTweakerMC.getItemStack(stack).copy();
      ItemHandlerHelper.giveItemToPlayer(this.player, itemstack);
   }

   public void teleport(Position3f pos) {
      this.player.setPosition((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
   }

   public void changeDimension(int dimensionId) {
      this.player.changeDimension(dimensionId);
   }

   public int getScore() {
      return this.player.getScore();
   }

   public void addScore(int amount) {
      this.player.addScore(amount);
   }

   public void setScore(int amount) {
      this.player.setScore(amount);
   }

   public IFoodStats getFoodStats() {
      return new MCFoodStats(this.player.getFoodStats());
   }

   public void executeCommand(String rawCommand) {
      MinecraftServer server = this.player.getServer();
      if (server != null) {
         server.getCommandManager().executeCommand(this.player, rawCommand);
      }

   }

   public boolean isDamageDisabled() {
      return this.player.capabilities.disableDamage;
   }

   public void setDamageDisabled(boolean disabled) {
      this.player.capabilities.disableDamage = disabled;
   }

   public boolean canFly() {
      return this.player.capabilities.allowFlying;
   }

   public void setCanFly(boolean canFly) {
      this.player.capabilities.allowFlying = canFly;
   }

   public boolean canEdit() {
      return this.player.capabilities.allowEdit;
   }

   public void setCanEdit(boolean canEdit) {
      this.player.capabilities.allowEdit = canEdit;
   }

   public String getUUID() {
      return EntityPlayer.getUUID(this.player.getGameProfile()).toString().toLowerCase();
   }

   public IEntityItem dropItem(boolean dropAll) {
      return new MCEntityItem(this.player.dropItem(dropAll));
   }

   public IEntityItem dropItem(IItemStack item) {
      return new MCEntityItem(this.player.dropItem(CraftTweakerMC.getItemStack(item), false));
   }
}
