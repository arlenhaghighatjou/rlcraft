package betterquesting.handlers;

import betterquesting.api.api.QuestingAPI;
import betterquesting.api.client.gui.misc.INeedsRefresh;
import betterquesting.api.events.DatabaseEvent;
import betterquesting.api.events.QuestEvent;
import betterquesting.api.placeholders.FluidPlaceholder;
import betterquesting.api.properties.NativeProps;
import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.party.IParty;
import betterquesting.api.storage.BQ_Settings;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api2.cache.CapabilityProviderQuestCache;
import betterquesting.api2.cache.QuestCache;
import betterquesting.api2.client.gui.GuiScreenTest;
import betterquesting.api2.client.gui.themes.gui_args.GArgsNone;
import betterquesting.api2.client.gui.themes.presets.PresetGUIs;
import betterquesting.api2.storage.DBEntry;
import betterquesting.client.BQ_Keybindings;
import betterquesting.client.gui2.GuiHome;
import betterquesting.client.themes.ThemeRegistry;
import betterquesting.core.BetterQuesting;
import betterquesting.network.handlers.NetBulkSync;
import betterquesting.network.handlers.NetNameSync;
import betterquesting.network.handlers.NetNotices;
import betterquesting.network.handlers.NetQuestSync;
import betterquesting.questing.QuestDatabase;
import betterquesting.questing.party.PartyInvitations;
import betterquesting.questing.party.PartyManager;
import betterquesting.storage.LifeDatabase;
import betterquesting.storage.NameCache;
import betterquesting.storage.QuestSettings;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.GameType;
import net.minecraftforge.client.event.TextureStitchEvent.Pre;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.world.WorldEvent.Save;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EventHandler {
   public static final EventHandler INSTANCE = new EventHandler();
   private final ArrayDeque<EntityPlayerMP> opQueue = new ArrayDeque();
   private boolean openToLAN = false;

   public EventHandler() {
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void onKey(KeyInputEvent event) {
      Minecraft mc = Minecraft.getMinecraft();
      if (mc.currentScreen == null && BQ_Keybindings.openQuests.isPressed()) {
         if (mc.player.isSneaking() && mc.player.getName().equalsIgnoreCase("Funwayguy")) {
            mc.displayGuiScreen(new GuiScreenTest(mc.currentScreen));
         } else if (BQ_Settings.useBookmark && GuiHome.bookmark != null) {
            mc.displayGuiScreen(GuiHome.bookmark);
         } else {
            mc.displayGuiScreen(ThemeRegistry.INSTANCE.getGui(PresetGUIs.HOME, GArgsNone.NONE));
         }
      }

   }

   @SubscribeEvent
   public void onCapabilityPlayer(AttachCapabilitiesEvent<Entity> event) {
      if (event.getObject() instanceof EntityPlayer) {
         event.addCapability(CapabilityProviderQuestCache.LOC_QUEST_CACHE, new CapabilityProviderQuestCache());
      }
   }

   @SubscribeEvent
   public void onPlayerClone(Clone event) {
      QuestCache oCache = (QuestCache)event.getOriginal().getCapability(CapabilityProviderQuestCache.CAP_QUEST_CACHE, (EnumFacing)null);
      QuestCache nCache = (QuestCache)event.getEntityPlayer().getCapability(CapabilityProviderQuestCache.CAP_QUEST_CACHE, (EnumFacing)null);
      if (oCache != null && nCache != null) {
         nCache.deserializeNBT(oCache.serializeNBT());
      }

   }

   @SubscribeEvent
   public void onLivingUpdate(LivingUpdateEvent event) {
      if (!event.getEntityLiving().world.isRemote) {
         if (event.getEntityLiving() instanceof EntityPlayerMP) {
            if (event.getEntityLiving().ticksExisted % 20 == 0) {
               EntityPlayerMP player = (EntityPlayerMP)event.getEntityLiving();
               QuestCache qc = (QuestCache)player.getCapability(CapabilityProviderQuestCache.CAP_QUEST_CACHE, (EnumFacing)null);
               boolean editMode = ((Boolean)QuestSettings.INSTANCE.getProperty(NativeProps.EDIT_MODE)).booleanValue();
               if (qc != null) {
                  List<DBEntry<IQuest>> activeQuests = QuestDatabase.INSTANCE.bulkLookup(qc.getActiveQuests());
                  List<DBEntry<IQuest>> pendingAutoClaims = QuestDatabase.INSTANCE.bulkLookup(qc.getPendingAutoClaims());
                  QuestCache.QResetTime[] pendingResets = qc.getScheduledResets();
                  EaglercraftUUID uuid = QuestingAPI.getQuestingUUID(player);
                  boolean refreshCache = false;
                  if (!editMode && player.ticksExisted % 60 == 0) {
                     List<Integer> com = new ArrayList();

                     for(DBEntry<IQuest> quest : activeQuests) {
                        if (((IQuest)quest.getValue()).isUnlocked(uuid)) {
                           if (((IQuest)quest.getValue()).canSubmit(player)) {
                              ((IQuest)quest.getValue()).update(player);
                           }

                           if (((IQuest)quest.getValue()).isComplete(uuid) && !((IQuest)quest.getValue()).canSubmit(player)) {
                              refreshCache = true;
                              qc.markQuestDirty(quest.getID());
                              com.add(Integer.valueOf(quest.getID()));
                              if (!((Boolean)((IQuest)quest.getValue()).getProperty(NativeProps.SILENT)).booleanValue()) {
                                 postPresetNotice(quest.getValue(), player, 2);
                              }
                           }
                        }
                     }

                     MinecraftForge.EVENT_BUS.post(new QuestEvent(QuestEvent.Type.COMPLETED, uuid, com));
                  }

                  if (!editMode && player.getServer() != null) {
                     List<Integer> res = new ArrayList();
                     long totalTime = System.currentTimeMillis();

                     for(QuestCache.QResetTime rTime : pendingResets) {
                        IQuest entry = (IQuest)QuestDatabase.INSTANCE.getValue(rTime.questID);
                        if (totalTime < rTime.time || entry.canSubmit(player)) {
                           break;
                        }

                        if (((Boolean)entry.getProperty(NativeProps.GLOBAL)).booleanValue()) {
                           entry.resetUser((EaglercraftUUID)null, false);
                        } else {
                           entry.resetUser(uuid, false);
                        }

                        refreshCache = true;
                        qc.markQuestDirty(rTime.questID);
                        res.add(Integer.valueOf(rTime.questID));
                        if (!((Boolean)entry.getProperty(NativeProps.SILENT)).booleanValue()) {
                           postPresetNotice(entry, player, 1);
                        }
                     }

                     MinecraftForge.EVENT_BUS.post(new QuestEvent(QuestEvent.Type.RESET, uuid, res));
                  }

                  if (!editMode) {
                     for(DBEntry<IQuest> entry : pendingAutoClaims) {
                        if (((IQuest)entry.getValue()).canClaim(player)) {
                           ((IQuest)entry.getValue()).claimReward(player);
                           refreshCache = true;
                           qc.markQuestDirty(entry.getID());
                        }
                     }
                  }

                  if (refreshCache || player.ticksExisted % 200 == 0) {
                     qc.updateCache(player);
                  }

                  if (qc.getDirtyQuests().length > 0) {
                     NetQuestSync.sendSync(player, qc.getDirtyQuests(), false, true);
                  }

                  qc.cleanAllQuests();
               }
            }
         }
      }
   }

   private static void postPresetNotice(IQuest quest, EntityPlayer player, int preset) {
      if (player instanceof EntityPlayerMP) {
         ItemStack icon = ((BigItemStack)quest.getProperty(NativeProps.ICON)).getBaseStack();
         String mainText = "";
         String subText = (String)quest.getProperty(NativeProps.NAME);
         String sound = "";
         switch(preset) {
         case 0:
            mainText = "betterquesting.notice.unlock";
            sound = (String)quest.getProperty(NativeProps.SOUND_UNLOCK);
            break;
         case 1:
            mainText = "betterquesting.notice.update";
            sound = (String)quest.getProperty(NativeProps.SOUND_UPDATE);
            break;
         case 2:
            mainText = "betterquesting.notice.complete";
            sound = (String)quest.getProperty(NativeProps.SOUND_COMPLETE);
         }

         NetNotices.sendNotice(((Boolean)quest.getProperty(NativeProps.GLOBAL)).booleanValue() ? null : new EntityPlayerMP[]{(EntityPlayerMP)player}, icon, mainText, subText, sound);
      }
   }

   @SubscribeEvent
   public void onConfigChanged(OnConfigChangedEvent event) {
      if (event.getModID().equals("betterquesting")) {
         ConfigHandler.config.save();
         ConfigHandler.initConfigs();
      }

   }

   @SubscribeEvent
   public void onWorldSave(Save event) {
      if (!event.getWorld().isRemote && BQ_Settings.curWorldDir != null && event.getWorld().provider.getDimension() == 0) {
         SaveLoadHandler.INSTANCE.saveDatabases();
      }

   }

   @SubscribeEvent
   public void onPlayerJoin(PlayerLoggedInEvent event) {
      if (!event.player.world.isRemote && event.player.getServer() != null && event.player instanceof EntityPlayerMP) {
         EntityPlayerMP mpPlayer = (EntityPlayerMP)event.player;
         if (BetterQuesting.proxy.isClient() && !mpPlayer.getServer().isDedicatedServer() && event.player.getServer().getServerOwner().equals(mpPlayer.getGameProfile().getName())) {
            NameCache.INSTANCE.updateName(mpPlayer);
         } else {
            NetBulkSync.sendReset(mpPlayer, true, true);
         }
      }
   }

   @SubscribeEvent
   public void onPlayerRespawn(PlayerRespawnEvent event) {
      if (((Boolean)QuestSettings.INSTANCE.getProperty(NativeProps.HARDCORE)).booleanValue() && event.player instanceof EntityPlayerMP && !((EntityPlayerMP)event.player).queuedEndExit) {
         EntityPlayerMP mpPlayer = (EntityPlayerMP)event.player;
         int lives = LifeDatabase.INSTANCE.getLives(QuestingAPI.getQuestingUUID(mpPlayer));
         if (lives <= 0) {
            MinecraftServer server = mpPlayer.getServer();
            if (server == null) {
               return;
            }

            mpPlayer.setGameType(GameType.SPECTATOR);
            if (!server.isDedicatedServer()) {
               mpPlayer.getServerWorld().getGameRules().setOrCreateGameRule("spectatorsGenerateChunks", "false");
            }
         } else if (lives == 1) {
            mpPlayer.sendStatusMessage(new TextComponentString("This is your last life!"), true);
         } else {
            mpPlayer.sendStatusMessage(new TextComponentString(lives + " lives remaining!"), true);
         }
      }

   }

   @SubscribeEvent
   public void onLivingDeath(LivingDeathEvent event) {
      if (!event.getEntityLiving().world.isRemote && ((Boolean)QuestSettings.INSTANCE.getProperty(NativeProps.HARDCORE)).booleanValue()) {
         if (event.getEntityLiving() instanceof EntityPlayer) {
            EaglercraftUUID uuid = QuestingAPI.getQuestingUUID((EntityPlayer)event.getEntityLiving());
            int lives = LifeDatabase.INSTANCE.getLives(uuid);
            LifeDatabase.INSTANCE.setLives(uuid, lives - 1);
         }

      }
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void onTextureStitch(Pre event) {
      if (event.getMap() == Minecraft.getMinecraft().getTextureMapBlocks()) {
         event.getMap().registerSprite(FluidPlaceholder.fluidPlaceholder.getStill());
      }

   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void onDataUpdated(DatabaseEvent.Update event) {
      GuiScreen screen = Minecraft.getMinecraft().currentScreen;
      if (screen instanceof INeedsRefresh) {
         Minecraft var10000 = Minecraft.getMinecraft();
         INeedsRefresh var10001 = (INeedsRefresh)screen;
         ((INeedsRefresh)screen).getClass();
         var10000.addScheduledTask(var10001::refreshGui);
      }

   }

   @SubscribeEvent
   public void onCommand(CommandEvent event) {
      MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
      if (server != null && (event.getCommand().getName().equalsIgnoreCase("op") || event.getCommand().getName().equalsIgnoreCase("deop"))) {
         EntityPlayerMP playerMP = server.getPlayerList().getPlayerByUsername(event.getParameters()[0]);
         if (playerMP != null) {
            this.opQueue.add(playerMP);
         }
      }

   }

   @SubscribeEvent
   public void onServerTick(ServerTickEvent event) {
      if (event.phase == Phase.END) {
         MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
         if (!server.isDedicatedServer()) {
            boolean tmp = this.openToLAN;
            this.openToLAN = server instanceof IntegratedServer && ((IntegratedServer)server).getPublic();
            if (this.openToLAN && !tmp) {
               this.opQueue.addAll(server.getPlayerList().getPlayers());
            }
         } else if (!this.openToLAN) {
            this.openToLAN = true;
         }

         while(!this.opQueue.isEmpty()) {
            EntityPlayerMP playerMP = (EntityPlayerMP)this.opQueue.poll();
            if (playerMP != null && NameCache.INSTANCE.updateName(playerMP)) {
               DBEntry<IParty> party = PartyManager.INSTANCE.getParty(QuestingAPI.getQuestingUUID(playerMP));
               if (party != null) {
                  NetNameSync.quickSync((EntityPlayerMP)null, party.getID());
               } else {
                  NetNameSync.sendNames(new EntityPlayerMP[]{playerMP}, new EaglercraftUUID[]{QuestingAPI.getQuestingUUID(playerMP)}, (String[])null);
               }
            }
         }

         if (server.getTickCounter() % 60 == 0) {
            PartyInvitations.INSTANCE.cleanExpired();
         }

      }
   }
}
