package goblinbob.mobends.core.animation.keyframe;

import goblinbob.mobends.core.util.SerialHelper;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.http.util.ByteArrayBuffer;

public class BinaryAnimationLoader {
   private static final String HEADER = "BENDSANIM";
   private static final byte HAS_POSITION = 1;
   private static final byte HAS_ROTATION = 2;
   private static final byte HAS_SCALE = 4;

   public BinaryAnimationLoader() {
   }

   public static KeyframeAnimation loadFromBinaryInputStream(InputStream stream) throws IOException {
      DataInputStream dataInputStream = new DataInputStream(stream);
      String header = SerialHelper.readChar(dataInputStream, "BENDSANIM".length());
      if (!header.equals("BENDSANIM")) {
         throw new IOException("File doesn't start with the header.");
      } else {
         KeyframeAnimation animation = new KeyframeAnimation();
         animation.bones = new HashMap();
         int version = dataInputStream.readInt();
         int amountOfKeyframes = dataInputStream.readInt();
         int amountOfBones = dataInputStream.readInt();

         for(int i = 0; i < amountOfBones; ++i) {
            ByteArrayBuffer buffer = new ByteArrayBuffer(16);

            for(byte character = dataInputStream.readByte(); character != 0; character = dataInputStream.readByte()) {
               buffer.append(character);
            }

            String boneName = new String(buffer.toByteArray(), StandardCharsets.UTF_8);
            Bone bone = new Bone();
            bone.keyframes = new ArrayList();

            for(int j = 0; j < amountOfKeyframes; ++j) {
               Keyframe frame = new Keyframe();
               byte flags = dataInputStream.readByte();
               if ((flags & 1) != 0) {
                  frame.position = new float[]{dataInputStream.readFloat(), dataInputStream.readFloat(), dataInputStream.readFloat()};
               } else {
                  frame.position = new float[]{0.0F, 0.0F, 0.0F};
               }

               if ((flags & 2) != 0) {
                  frame.rotation = new float[]{dataInputStream.readFloat(), dataInputStream.readFloat(), dataInputStream.readFloat(), dataInputStream.readFloat()};
               } else {
                  frame.rotation = new float[]{0.0F, 0.0F, 0.0F, 1.0F};
               }

               if ((flags & 4) != 0) {
                  frame.scale = new float[]{dataInputStream.readFloat(), dataInputStream.readFloat(), dataInputStream.readFloat()};
               } else {
                  frame.scale = new float[]{1.0F, 1.0F, 1.0F};
               }

               bone.keyframes.add(frame);
            }

            animation.bones.put(boneName, bone);
         }

         return animation;
      }
   }
}
