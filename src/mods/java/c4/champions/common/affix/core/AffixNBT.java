package c4.champions.common.affix.core;

import c4.champions.Champions;
import c4.champions.common.capability.IChampionship;
import c4.champions.network.NetworkHandler;
import c4.champions.network.PacketSyncAffix;
import javax.annotation.Nonnull;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public abstract class AffixNBT {
   private IChampionship championship;
   private String identifier;

   protected AffixNBT() {
   }

   public void readData(IChampionship championship, String identifier) {
      this.championship = championship;
      this.identifier = identifier;
      this.readFromNBT(championship.getAffixData(identifier));
   }

   public abstract void readFromNBT(NBTTagCompound var1);

   public abstract NBTTagCompound writeToNBT();

   public void saveData(EntityLiving living) {
      this.championship.setAffixData(this.identifier, this.writeToNBT());
      this.syncToTracking(living);
   }

   private void syncToTracking(EntityLiving living) {
      if (living.world instanceof WorldServer) {
         WorldServer world = (WorldServer)living.world;

         for(EntityPlayer player : world.getEntityTracker().getTrackingPlayers(living)) {
            if (player instanceof EntityPlayerMP && this.championship.getRank() != null) {
               NetworkHandler.INSTANCE.sendTo(new PacketSyncAffix(living.getEntityId(), this.championship.getRank().getTier(), this.championship.getAffixData(), this.championship.getName()), (EntityPlayerMP)player);
            }
         }
      }

   }

   public static <T extends AffixNBT> T getData(@Nonnull IChampionship championship, String affix, Class<T> clazz) {
      T data = null;

      try {
         data = clazz.newInstance();
         data.readData(championship, affix);
      } catch (InstantiationException | IllegalAccessException var5) {
         Champions.logger.log(Level.ERROR, "Error reading data from class " + clazz.toString());
      }

      return data;
   }

   public static class Boolean extends AffixNBT {
      public boolean mode;

      public Boolean() {
      }

      public void readFromNBT(NBTTagCompound tag) {
         this.mode = tag.getBoolean("mode");
      }

      public NBTTagCompound writeToNBT() {
         NBTTagCompound compound = new NBTTagCompound();
         compound.setBoolean("mode", this.mode);
         return compound;
      }
   }

   public static class Integer extends AffixNBT {
      public int num;

      public Integer() {
      }

      public void readFromNBT(NBTTagCompound tag) {
         this.num = tag.getInteger("num");
      }

      public NBTTagCompound writeToNBT() {
         NBTTagCompound compound = new NBTTagCompound();
         compound.setInteger("num", this.num);
         return compound;
      }
   }

   public static class Text extends AffixNBT {
      public String text;

      public Text() {
      }

      public void readFromNBT(NBTTagCompound tag) {
         this.text = tag.getString("text");
      }

      public NBTTagCompound writeToNBT() {
         NBTTagCompound compound = new NBTTagCompound();
         compound.setString("text", this.text);
         return compound;
      }
   }
}
