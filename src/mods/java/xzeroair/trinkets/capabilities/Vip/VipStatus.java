package xzeroair.trinkets.capabilities.Vip;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import xzeroair.trinkets.capabilities.CapabilityBase;
import xzeroair.trinkets.network.NetworkHandler;
import xzeroair.trinkets.network.vip.VipStatusPacket;
import xzeroair.trinkets.util.Reference;
import xzeroair.trinkets.vip.VIPHandler;
import xzeroair.trinkets.vip.VipPackage;
import xzeroair.trinkets.vip.VipUser;

public class VipStatus extends CapabilityBase<VipStatus, EntityPlayer> {
   boolean first_login = true;
   boolean login = false;
   EntityPlayer player;
   int status = 0;
   boolean checkStatus;
   List<String> Quotes;
   private VipUser user;

   public VipStatus(EntityPlayer player) {
      super(player);
      this.player = player;
      this.Quotes = new ArrayList();
   }

   public NBTTagCompound getTag() {
      NBTTagCompound tag = ((EntityPlayer)this.object).getEntityData();
      if (tag != null) {
         if (!tag.hasKey("PlayerPersisted")) {
            tag.setTag("PlayerPersisted", new NBTTagCompound());
         }

         NBTTagCompound persistentData = tag.getCompoundTag("PlayerPersisted");
         String capTag = "xat.VIP";
         if (!persistentData.hasKey("xat.VIP")) {
            persistentData.setTag("xat.VIP", new NBTTagCompound());
         }

         return persistentData.getCompoundTag("xat.VIP");
      } else {
         return super.getTag();
      }
   }

   public void onUpdate() {
      World world = this.player.world;
      if (world != null) {
         if (!this.checkStatus) {
            if (!world.isRemote) {
               try {
                  TreeMap<String, VipUser> list = VIPHandler.Vips;
                  if (list != null) {
                     this.confirmedStatus();
                     this.sendStatusToPlayer(this.player);
                  }
               } catch (Exception var3) {
                  ;
               }
            }

            this.checkStatus = true;
         }

      }
   }

   public void confirmedStatus() {
      String id = this.player.getUniqueID().toString().replaceAll("-", "");
      if (VIPHandler.Vips.containsKey(id)) {
         this.user = (VipUser)VIPHandler.Vips.get(id);
         if (this.user != null) {
            if (!this.user.getGroups().isEmpty()) {
               VipPackage group1 = (VipPackage)this.user.getGroups().get(0);
               if (group1 != null) {
                  this.status = group1.getGroupID();
               }
            }

            this.Quotes = this.user.getQuotes();
         }
      }

   }

   public void sendStatusToServer(EntityPlayer player) {
      World world = player.getEntityWorld();
      if (world.isRemote) {
         NBTTagCompound tag = new NBTTagCompound();
         this.saveToNBT(tag);
         NetworkHandler.sendToServer(new VipStatusPacket(player, tag));
      }

   }

   public void sendStatusToPlayer(EntityPlayer reciever) {
      World world = this.player.getEntityWorld();
      if (!world.isRemote && reciever instanceof EntityPlayerMP) {
         try {
            NBTTagCompound tag = new NBTTagCompound();
            this.saveToNBT(tag);
            NetworkHandler.sendTo(new VipStatusPacket(this.player, tag), (EntityPlayerMP)reciever);
         } catch (Exception var4) {
            ;
         }
      }

   }

   public void syncStatusToTracking() {
      World world = this.player.getEntityWorld();
      if (this.player instanceof EntityPlayerMP && world instanceof WorldServer) {
         NBTTagCompound tag = new NBTTagCompound();
         this.saveToNBT(tag);
         NetworkHandler.sendToClients((WorldServer)world, this.player.getPosition(), new VipStatusPacket(this.player, tag));
      }

   }

   public List<String> getQuotes() {
      return this.Quotes;
   }

   public String getRandomQuote() {
      if (!this.Quotes.isEmpty()) {
         int rand = Reference.random.nextInt(this.Quotes.size());
         return (String)this.Quotes.get(rand);
      } else {
         return "";
      }
   }

   public void setStatus(int status) {
      this.status = status;
   }

   public int getStatus() {
      return this.status;
   }

   public boolean isFirstLogin() {
      return this.first_login;
   }

   public boolean isLogin() {
      return this.login;
   }

   public void setLogin(boolean login) {
      this.login = login;
   }

   public void copyFrom(VipStatus source, boolean wasDeath, boolean keepInv) {
      this.first_login = source.first_login;
      this.login = source.login;
      this.status = source.status;
   }

   public NBTTagCompound saveToNBT(NBTTagCompound compound) {
      compound.setInteger("status", this.status);
      compound.setBoolean("login", this.login);
      compound.setString("uuid", this.player.getCachedUniqueIdString());
      return compound;
   }

   public void loadFromNBT(NBTTagCompound compound) {
      if (compound.hasKey("status")) {
         this.status = compound.getInteger("status");
      }

      if (compound.hasKey("login")) {
         this.login = compound.getBoolean("login");
      }

      this.first_login = false;
   }
}
