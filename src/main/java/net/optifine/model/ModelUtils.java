package net.optifine.model;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.SimpleBakedModel;
import net.minecraft.util.EnumFacing;

public class ModelUtils {
	public static void dbgModel(IBakedModel model) {
		if (model != null) {
			// Config.dbg("Model: " + model + ", ao: " + model.isAmbientOcclusion() + ",
			// gui3d: " + model.isGui3d() + ", builtIn: " + model.isBuiltInRenderer() + ",
			// particle: " + model.getParticleTexture());
			EnumFacing[] aenumfacing = EnumFacing._VALUES;

			for (int i = 0; i < aenumfacing.length; ++i) {
				EnumFacing enumfacing = aenumfacing[i];
				List<BakedQuad> list = model.getQuads(null, enumfacing, 0L);
				dbgQuads(enumfacing.getName(), list, "  ");
			}

			List<BakedQuad> list1 = model.getQuads(null, null, 0L);
			dbgQuads("General", list1, "  ");
		}
	}

	private static void dbgQuads(String name, List quads, String prefix) {
		for (Object bakedquad0 : quads) {
			BakedQuad bakedQuad = (BakedQuad) bakedquad0;
			dbgQuad(name, bakedQuad, prefix);
		}
	}

	public static void dbgQuad(String name, BakedQuad quad, String prefix) {
		// Config.dbg(prefix + "Quad: " + quad.getClass().getName() + ", type: " + name
		// + ", face: " + quad.getFace() + ", tint: " + quad.getTintIndex() + ", sprite:
		// " + quad.getSprite());
		dbgVertexData(quad.getVertexData(), "  " + prefix);
	}

	public static void dbgVertexData(int[] vd, String prefix) {
		int i = vd.length / 4;
		// Config.dbg(prefix + "Length: " + vd.length + ", step: " + i);

		for (int j = 0; j < 4; ++j) {
			int k = j * i;
			float f = Float.intBitsToFloat(vd[k + 0]);
			float f1 = Float.intBitsToFloat(vd[k + 1]);
			float f2 = Float.intBitsToFloat(vd[k + 2]);
			int l = vd[k + 3];
			float f3 = Float.intBitsToFloat(vd[k + 4]);
			float f4 = Float.intBitsToFloat(vd[k + 5]);
			// Config.dbg(prefix + j + " xyz: " + f + "," + f1 + "," + f2 + " col: " + l + "
			// u,v: " + f3 + "," + f4);
		}
	}

	public static IBakedModel duplicateModel(IBakedModel model) {
		List<BakedQuad> list = duplicateQuadList(model.getQuads(null, null, 0L));
		EnumFacing[] aenumfacing = EnumFacing._VALUES;
		Map<EnumFacing, List<BakedQuad>> list1 = new EnumMap<>(EnumFacing.class);

		for (int i = 0; i < aenumfacing.length; ++i) {
			EnumFacing enumfacing = aenumfacing[i];
			List<BakedQuad> list2 = model.getQuads(null, enumfacing, 0L);
			list1.put(enumfacing, duplicateQuadList(list2));
		}

		SimpleBakedModel simplebakedmodel = new SimpleBakedModel(list, list1, model.isAmbientOcclusion(),
				model.isGui3d(), model.getParticleTexture(), model.getItemCameraTransforms(), model.getOverrides());
		return simplebakedmodel;
	}

	public static List<BakedQuad> duplicateQuadList(List<BakedQuad> lists) {
		List<BakedQuad> list = new ArrayList<>(lists.size());

		for (BakedQuad bakedquad : lists) {
			BakedQuad bakedquad1 = duplicateQuad(bakedquad);
			list.add(bakedquad1);
		}

		return list;
	}

	public static BakedQuad duplicateQuad(BakedQuad quad) {
		BakedQuad bakedquad = new BakedQuad(quad.getVertexData().clone(), quad.getTintIndex(), quad.getFace(),
				quad.getSprite(), quad.shouldApplyDiffuseLighting(), quad.getFormat());
		return bakedquad;
	}
}
