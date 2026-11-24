EAGLER_IN(vec2, v_position2f)
EAGLER_FRAG_OUT()

uniform sampler2D u_mask;
uniform vec2 u_texel;
uniform vec2 u_blurDir;

vec4 readMask(vec2 offset) {
	return EAGLER_TEXTURE_2D_LOD(u_mask, v_position2f + offset * u_texel, 0.0);
}

void main() {
	vec4 center = readMask(vec2(0.0));
	if (u_blurDir.x + u_blurDir.y == 0.0) {
		vec4 left = readMask(vec2(-1.0, 0.0));
		vec4 right = readMask(vec2(1.0, 0.0));
		vec4 up = readMask(vec2(0.0, -1.0));
		vec4 down = readMask(vec2(0.0, 1.0));
		float edge = abs(center.a - left.a) + abs(center.a - right.a)
				+ abs(center.a - up.a) + abs(center.a - down.a);
		vec3 color = center.rgb * center.a + left.rgb * left.a + right.rgb * right.a
				+ up.rgb * up.a + down.rgb * down.a;
		EAGLER_FRAG_COLOR = vec4(color * 0.2, clamp(edge, 0.0, 1.0));
	} else {
		vec4 sum = center + readMask(u_blurDir) + readMask(-u_blurDir)
				+ readMask(u_blurDir * 2.0) + readMask(u_blurDir * -2.0);
		EAGLER_FRAG_COLOR = vec4(sum.rgb * 0.2, sum.a);
	}
}
