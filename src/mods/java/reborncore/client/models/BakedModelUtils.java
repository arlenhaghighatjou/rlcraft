package reborncore.client.models;

import java.util.ArrayList;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.BlockPartRotation;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import org.lwjgl.util.vector.Vector3f;
import reborncore.common.misc.vecmath.Vecs3dCube;

public class BakedModelUtils {
   public BakedModelUtils() {
   }

   public static void addCubeToList(Vecs3dCube cube, ArrayList<BakedQuad> list, BlockPartFace face, ModelRotation modelRotation, TextureAtlasSprite cubeTexture, EnumFacing dir, FaceBakery faceBakery) {
      BlockFaceUV uv = new BlockFaceUV(new float[]{(float)cube.getMinX(), (float)cube.getMinY(), (float)cube.getMaxX(), (float)cube.getMaxY()}, 0);
      face = new BlockPartFace(dir, 0, "", uv);
      list.add(faceBakery.makeBakedQuad(new Vector3f((float)cube.getMinX(), (float)cube.getMinY(), (float)cube.getMinZ()), new Vector3f((float)cube.getMaxX(), (float)cube.getMinY(), (float)cube.getMaxZ()), face, cubeTexture, EnumFacing.DOWN, modelRotation, (BlockPartRotation)null, true, true));
      list.add(faceBakery.makeBakedQuad(new Vector3f((float)cube.getMinX(), (float)cube.getMaxY(), (float)cube.getMinZ()), new Vector3f((float)cube.getMaxX(), (float)cube.getMaxY(), (float)cube.getMaxZ()), face, cubeTexture, EnumFacing.UP, modelRotation, (BlockPartRotation)null, true, true));
      if (dir != EnumFacing.NORTH && dir != EnumFacing.SOUTH) {
         list.add(faceBakery.makeBakedQuad(new Vector3f((float)cube.getMinX(), (float)cube.getMinY(), (float)cube.getMinZ()), new Vector3f((float)cube.getMaxX(), (float)cube.getMaxY(), (float)cube.getMaxZ()), face, cubeTexture, EnumFacing.NORTH, modelRotation, (BlockPartRotation)null, true, true));
      } else {
         BlockFaceUV uv2 = new BlockFaceUV(new float[]{(float)cube.getMaxX() + 4.0F, (float)cube.getMinY(), 16.0F, (float)cube.getMaxY()}, 0);
         list.add(faceBakery.makeBakedQuad(new Vector3f((float)cube.getMinX(), (float)cube.getMinY(), (float)cube.getMinZ()), new Vector3f((float)cube.getMaxX(), (float)cube.getMaxY(), (float)cube.getMaxZ()), new BlockPartFace(dir, 0, "", uv2), cubeTexture, EnumFacing.NORTH, modelRotation, (BlockPartRotation)null, true, true));
      }

      list.add(faceBakery.makeBakedQuad(new Vector3f((float)cube.getMinX(), (float)cube.getMinY(), (float)cube.getMaxZ()), new Vector3f((float)cube.getMaxX(), (float)cube.getMaxY(), (float)cube.getMaxZ()), face, cubeTexture, EnumFacing.SOUTH, modelRotation, (BlockPartRotation)null, true, true));
      list.add(faceBakery.makeBakedQuad(new Vector3f((float)cube.getMaxX(), (float)cube.getMinY(), (float)cube.getMinZ()), new Vector3f((float)cube.getMaxX(), (float)cube.getMaxY(), (float)cube.getMaxZ()), face, cubeTexture, EnumFacing.EAST, modelRotation, (BlockPartRotation)null, true, true));
      list.add(faceBakery.makeBakedQuad(new Vector3f((float)cube.getMinX(), (float)cube.getMinY(), (float)cube.getMinZ()), new Vector3f((float)cube.getMinX(), (float)cube.getMaxY(), (float)cube.getMaxZ()), face, cubeTexture, EnumFacing.WEST, modelRotation, (BlockPartRotation)null, true, true));
   }
}
