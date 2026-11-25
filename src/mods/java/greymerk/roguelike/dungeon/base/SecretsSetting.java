package greymerk.roguelike.dungeon.base;

import com.google.common.collect.Lists;
import greymerk.roguelike.dungeon.rooms.RoomSetting;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

public class SecretsSetting {
   private final List<RoomSetting> secretRoomSettings = Lists.newArrayList();

   public SecretsSetting() {
   }

   public SecretsSetting(SecretsSetting toCopy) {
      this.secretRoomSettings.addAll(toCopy.secretRoomSettings);
   }

   public SecretsSetting(SecretsSetting base, SecretsSetting other) {
      if (base != null) {
         this.secretRoomSettings.addAll(base.secretRoomSettings);
      }

      if (other != null) {
         this.secretRoomSettings.addAll(other.secretRoomSettings);
      }

   }

   public static SecretsSetting getRandom(EaglercraftRandom rand, int count) {
      SecretsSetting secrets = new SecretsSetting();
      IntStream.range(0, count).mapToObj((i) -> {
         return RoomType.getRandomSecret(rand);
      }).forEach((type) -> {
         secrets.add(type.newSingleRoomSetting());
      });
      return secrets;
   }

   public void add(RoomSetting roomSetting) {
      IntStream.range(0, roomSetting.getCount()).forEach((value) -> {
         this.secretRoomSettings.add(roomSetting);
      });
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof SecretsSetting)) {
         return false;
      } else {
         SecretsSetting other = (SecretsSetting)o;
         if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$secretRoomSettings = this.getSecretRoomSettings();
            Object other$secretRoomSettings = other.getSecretRoomSettings();
            if (this$secretRoomSettings == null) {
               if (other$secretRoomSettings != null) {
                  return false;
               }
            } else if (!this$secretRoomSettings.equals(other$secretRoomSettings)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(Object other) {
      return other instanceof SecretsSetting;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $secretRoomSettings = this.getSecretRoomSettings();
      result = result * 59 + ($secretRoomSettings == null ? 43 : $secretRoomSettings.hashCode());
      return result;
   }

   public List<RoomSetting> getSecretRoomSettings() {
      return this.secretRoomSettings;
   }

   public String toString() {
      return "SecretsSetting(secretRoomSettings=" + this.getSecretRoomSettings() + ")";
   }
}
