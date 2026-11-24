package net.optifine.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.lax1dude.eaglercraft.v1_8.vector.Vector3f;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.BlockPartRotation;
import net.minecraft.client.renderer.block.model.BakedQuadRetextured;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.block.model.SimpleBakedModel;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;

public class BlockModelUtils {
	private static final float VERTEX_COORD_ACCURACY = 1.0E-6F;

	public static IBakedModel makeModelCube(String spriteName, int tintIndex) {
		EaglerTextureAtlasSprite textureatlassprite = Minecraft.getMinecraft().getTextureMapBlocks()
				.getAtlasSprite(spriteName);
		return makeModelCube(textureatlassprite, tintIndex);
	}

	public static IBakedModel makeModelCube(EaglerTextureAtlasSprite sprite, int tintIndex) {
		Map<EnumFacing, List<BakedQuad>> faces = new EnumMap<>(EnumFacing.class);
		for (EnumFacing face : EnumFacing._VALUES) {
			faces.put(face, Collections.singletonList(makeBakedQuad(face, sprite, tintIndex)));
		}
		return new SimpleBakedModel(Collections.emptyList(), faces, true, true, sprite,
				ItemCameraTransforms.DEFAULT, ItemOverrideList.NONE);
	}

	public static IBakedModel joinModelsCube(IBakedModel base, IBakedModel overlay) {
		List<BakedQuad> general = new ArrayList<>(base.getQuads(null, null, 0L));
		general.addAll(overlay.getQuads(null, null, 0L));
		Map<EnumFacing, List<BakedQuad>> faces = new EnumMap<>(EnumFacing.class);
		for (EnumFacing face : EnumFacing._VALUES) {
			List<BakedQuad> quads = new ArrayList<>(base.getQuads(null, face, 0L));
			quads.addAll(overlay.getQuads(null, face, 0L));
			faces.put(face, quads);
		}
		return new SimpleBakedModel(general, faces, base.isAmbientOcclusion(), base.isGui3d(),
				base.getParticleTexture(), base.getItemCameraTransforms(), base.getOverrides());
	}

	public static BakedQuad makeBakedQuad(EnumFacing facing, EaglerTextureAtlasSprite sprite, int tintIndex) {
		Vector3f vector3f = new Vector3f(0.0F, 0.0F, 0.0F);
		Vector3f vector3f1 = new Vector3f(16.0F, 16.0F, 16.0F);
		BlockFaceUV blockfaceuv = new BlockFaceUV(new float[] { 0.0F, 0.0F, 16.0F, 16.0F }, 0);
		BlockPartFace blockpartface = new BlockPartFace(facing, tintIndex, "#" + facing.getName(), blockfaceuv);
		ModelRotation modelrotation = ModelRotation.X0_Y0;
		BlockPartRotation blockpartrotation = null;
		boolean flag = false;
		boolean flag1 = true;
		FaceBakery facebakery = new FaceBakery();
		BakedQuad bakedquad = facebakery.makeBakedQuad(vector3f, vector3f1, blockpartface, sprite, facing,
				modelrotation, blockpartrotation, flag, flag1);
		return bakedquad;
	}

	public static IBakedModel makeModel(String modelName, String spriteOldName, String spriteNewName) {
		TextureMap texturemap = Minecraft.getMinecraft().getTextureMapBlocks();
		EaglerTextureAtlasSprite textureatlassprite = texturemap.getAtlasSprite(spriteOldName); // getSpriteSafe
		EaglerTextureAtlasSprite textureatlassprite1 = texturemap.getAtlasSprite(spriteNewName);
		return makeModel(modelName, textureatlassprite, textureatlassprite1);
	}

	public static IBakedModel makeModel(String modelName, EaglerTextureAtlasSprite spriteOld,
			EaglerTextureAtlasSprite spriteNew) {
		if (spriteOld != null && spriteNew != null) {
			ModelManager modelmanager = Minecraft.getMinecraft().getModelManager();

			if (modelmanager == null) {
				return null;
			} else {
				ModelResourceLocation modelresourcelocation = new ModelResourceLocation(modelName, "normal");
				IBakedModel ibakedmodel = modelmanager.getModel(modelresourcelocation);

				if (ibakedmodel != null && ibakedmodel != modelmanager.getMissingModel()) {
					IBakedModel ibakedmodel1 = ModelUtils.duplicateModel(ibakedmodel);
					EnumFacing[] aenumfacing = EnumFacing._VALUES;

					for (int i = 0; i < aenumfacing.length; ++i) {
						EnumFacing enumfacing = aenumfacing[i];
						List<BakedQuad> list = ibakedmodel1.getQuads(null, enumfacing, 0L);
						replaceTexture(list, spriteOld, spriteNew);
					}

					List<BakedQuad> list1 = ibakedmodel1.getQuads(null, null, 0L);
					replaceTexture(list1, spriteOld, spriteNew);
					return ibakedmodel1;
				} else {
					return null;
				}
			}
		} else {
			return null;
		}
	}

	private static void replaceTexture(List<BakedQuad> quads, EaglerTextureAtlasSprite spriteOld,
			EaglerTextureAtlasSprite spriteNew) {
		List<BakedQuad> list = new ArrayList();

		for (BakedQuad bakedquad : quads) {
			if (bakedquad.getSprite() == spriteOld) {
				bakedquad = new BakedQuadRetextured(bakedquad, spriteNew);
			}

			list.add(bakedquad);
		}

		quads.clear();
		quads.addAll(list);
	}

	public static void snapVertexPosition(Vector3f pos) {
		pos.setX(snapVertexCoord(pos.getX()));
		pos.setY(snapVertexCoord(pos.getY()));
		pos.setZ(snapVertexCoord(pos.getZ()));
	}

	private static float snapVertexCoord(float x) {
		return x > -1.0E-6F && x < 1.0E-6F ? 0.0F : (x > 0.999999F && x < 1.000001F ? 1.0F : x);
	}

	public static AxisAlignedBB getOffsetBoundingBox(AxisAlignedBB aabb, Block.EnumOffsetType offsetType,
			BlockPos pos) {
		int i = pos.getX();
		int j = pos.getZ();
		long k = (long) (i * 3129871) ^ (long) j * 116129781L;
		k = k * k * 42317861L + k * 11L;
		double d0 = ((double) ((float) (k >> 16 & 15L) / 15.0F) - 0.5D) * 0.5D;
		double d1 = ((double) ((float) (k >> 24 & 15L) / 15.0F) - 0.5D) * 0.5D;
		double d2 = 0.0D;

		if (offsetType == Block.EnumOffsetType.XYZ) {
			d2 = ((double) ((float) (k >> 20 & 15L) / 15.0F) - 1.0D) * 0.2D;
		}

		return aabb.offset(d0, d2, d1);
	}
}
