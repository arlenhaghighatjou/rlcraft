package lumien.bloodmoon.server;

import lumien.bloodmoon.config.BloodmoonConfig;
import lumien.bloodmoon.network.PacketHandler;
import lumien.bloodmoon.network.messages.MessageBloodmoonStatus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

public class BloodmoonHandler extends WorldSavedData {
   public static BloodmoonHandler INSTANCE;
   private BloodmoonSpawner bloodMoonSpawner = new BloodmoonSpawner();
   boolean bloodMoon = false;
   boolean forceBloodMoon = false;
   int nightCounter;

   public BloodmoonHandler() {
      super("Bloodmoon");
   }

   public BloodmoonHandler(String name) {
      super("Bloodmoon");
   }

   public void playerJoinedWorld(EntityJoinWorldEvent event) {
      if (!event.getWorld().isRemote && event.getEntity() instanceof EntityPlayer && this.bloodMoon) {
         PacketHandler.INSTANCE.sendTo(new MessageBloodmoonStatus(this.bloodMoon), (EntityPlayerMP)event.getEntity());
      }

   }

   public void endWorldTick(WorldTickEvent event) {
      if (event.side.isServer() && event.phase == Phase.END) {
         World world = event.world;
         if (world.provider.getDimension() == 0) {
            int time = (int)(world.getWorldTime() % 24000L);
            if (this.isBloodmoonActive()) {
               if (!BloodmoonConfig.GENERAL.RESPECT_GAMERULE || world.getGameRules().getBoolean("doMobSpawning")) {
                  for(int i = 0; i < BloodmoonConfig.SPAWNING.SPAWN_SPEED; ++i) {
                     this.bloodMoonSpawner.findChunksForSpawning((WorldServer)world, world.getDifficulty() != EnumDifficulty.PEACEFUL, false, false);
                  }
               }

               if (time >= 0 && time < 12000) {
                  this.setBloodmoon(false);
               }
            } else if (time == 12000) {
               if (BloodmoonConfig.SCHEDULE.NTH_NIGHT != 0) {
                  --this.nightCounter;
                  if (this.nightCounter < 0) {
                     this.nightCounter = BloodmoonConfig.SCHEDULE.NTH_NIGHT;
                  }

                  this.markDirty();
               }

               if (this.forceBloodMoon || Math.random() < BloodmoonConfig.SCHEDULE.CHANCE || BloodmoonConfig.SCHEDULE.FULLMOON && world.getCurrentMoonPhaseFactor() == 1.0F || BloodmoonConfig.SCHEDULE.NTH_NIGHT != 0 && this.nightCounter == 0) {
                  this.forceBloodMoon = false;
                  this.setBloodmoon(true);
                  if (BloodmoonConfig.GENERAL.SEND_MESSAGE) {
                     for(Object object : world.playerEntities) {
                        EntityPlayer player = (EntityPlayer)object;
                        player.sendMessage((new TextComponentTranslation("text.bloodmoon.notify", new Object[0])).setStyle((new Style()).setColor(TextFormatting.RED)));
                     }
                  }

                  if (this.nightCounter == 0 && BloodmoonConfig.SCHEDULE.NTH_NIGHT != 0) {
                     this.nightCounter = BloodmoonConfig.SCHEDULE.NTH_NIGHT;
                     this.markDirty();
                  }
               }
            }
         }
      }

   }

   private void setBloodmoon(boolean bloodMoon) {
      if (this.bloodMoon != bloodMoon) {
         PacketHandler.INSTANCE.sendToDimension(new MessageBloodmoonStatus(bloodMoon), 0);
         this.markDirty();
      }

      this.bloodMoon = bloodMoon;
   }

   public void updateClients() {
      PacketHandler.INSTANCE.sendToDimension(new MessageBloodmoonStatus(this.bloodMoon), 0);
   }

   public void force() {
      this.forceBloodMoon = true;
      this.markDirty();
   }

   public boolean isBloodmoonActive() {
      return this.bloodMoon;
   }

   public void readFromNBT(NBTTagCompound nbt) {
      this.bloodMoon = nbt.getBoolean("bloodMoon");
      this.forceBloodMoon = nbt.getBoolean("forceBloodMoon");
      this.nightCounter = nbt.getInteger("nightCounter");
   }

   public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
      nbt.setBoolean("bloodMoon", this.bloodMoon);
      nbt.setBoolean("forceBloodMoon", this.forceBloodMoon);
      nbt.setInteger("nightCounter", this.nightCounter);
      return nbt;
   }

   public boolean isBloodmoonScheduled() {
      return this.forceBloodMoon;
   }

   public void stop() {
      this.setBloodmoon(false);
   }
}
