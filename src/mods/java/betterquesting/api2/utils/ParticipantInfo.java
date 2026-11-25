package betterquesting.api2.utils;

import betterquesting.api.api.QuestingAPI;
import betterquesting.api.questing.party.IParty;
import betterquesting.api2.cache.CapabilityProviderQuestCache;
import betterquesting.api2.cache.QuestCache;
import betterquesting.api2.storage.DBEntry;
import betterquesting.questing.party.PartyManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class ParticipantInfo {
   public final EntityPlayer PLAYER;
   public final UUID UUID;
   public final List<UUID> ALL_UUIDS;
   public final List<EntityPlayer> ACTIVE_PLAYERS;
   public final List<UUID> ACTIVE_UUIDS;
   public final DBEntry<IParty> PARTY_INSTANCE;

   public ParticipantInfo(@Nonnull EntityPlayer player) {
      this.PLAYER = player;
      this.UUID = QuestingAPI.getQuestingUUID(player);
      this.PARTY_INSTANCE = PartyManager.INSTANCE.getParty(this.UUID);
      MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
      if (this.PARTY_INSTANCE != null && server != null && !(player instanceof FakePlayer)) {
         List<EntityPlayer> actPl = new ArrayList();
         List<UUID> actID = new ArrayList();
         List<UUID> allID = new ArrayList();

         for(UUID mem : (this.PARTY_INSTANCE.getValue()).getMembers()) {
            allID.add(mem);
            EntityPlayer pMem = server.getPlayerList().getPlayerByUUID(mem);
            if (pMem != null) {
               actPl.add(pMem);
               actID.add(mem);
            }
         }

         this.ACTIVE_PLAYERS = Collections.unmodifiableList(actPl);
         this.ACTIVE_UUIDS = Collections.unmodifiableList(actID);
         this.ALL_UUIDS = Collections.unmodifiableList(allID);
      } else {
         this.ACTIVE_PLAYERS = Collections.singletonList(player);
         this.ACTIVE_UUIDS = Collections.singletonList(this.UUID);
         this.ALL_UUIDS = Collections.singletonList(this.UUID);
      }
   }

   public void markDirty(@Nonnull List<Integer> questIDs) {
      QuestCache qc = (QuestCache)this.PLAYER.getCapability(CapabilityProviderQuestCache.CAP_QUEST_CACHE, (EnumFacing)null);
      if (qc != null) {
         questIDs.forEach(qc::markQuestDirty);
      }

   }

   public void markDirtyParty(@Nonnull List<Integer> questIDs) {
      if (this.ACTIVE_PLAYERS.size() > 0 && questIDs.size() > 0) {
         this.ACTIVE_PLAYERS.forEach((value) -> {
            QuestCache qc = (QuestCache)value.getCapability(CapabilityProviderQuestCache.CAP_QUEST_CACHE, (EnumFacing)null);
            if (qc != null) {
               questIDs.forEach(qc::markQuestDirty);
            }

         });
      }
   }

   @Nonnull
   public int[] getSharedQuests() {
      TreeSet<Integer> active = new TreeSet();
      this.ACTIVE_PLAYERS.forEach((p) -> {
         QuestCache qc = (QuestCache)p.getCapability(CapabilityProviderQuestCache.CAP_QUEST_CACHE, (EnumFacing)null);
         if (qc != null) {
            for(int value : qc.getActiveQuests()) {
               active.add(Integer.valueOf(value));
            }
         }

      });
      int[] shared = new int[active.size()];
      int i = 0;

      int value;
      for(Iterator var4 = active.iterator(); var4.hasNext(); shared[i++] = value) {
         value = ((Integer)var4.next()).intValue();
      }

      return shared;
   }
}
