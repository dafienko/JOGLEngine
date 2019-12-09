package engine;

import org.joml.*;

public final class Materials {
	public static final float[] GOLD_AMBIENT = new float[] {0.2473f, 0.1995f, 0.0745f, 1};
	public static final float[] GOLD_DIFFUSE = new float[] {0.7516f, 0.6065f, 0.2265f, 1};
	public static final float[] GOLD_SPECULAR = new float[] {0.6823f, 0.5559f, 0.3661f, 1};
	public static final float GOLD_SHININESS = 52.1f;
	
	public static Material getGoldMaterial() {
		Material gold = new Material();
		gold.ambient = GOLD_AMBIENT;
		gold.diffuse = GOLD_DIFFUSE;
		gold.specular = GOLD_SPECULAR;
		gold.shininess = GOLD_SHININESS;
		return gold;
	}
	
	public static final float[] JADE_AMBIENT = new float[] {0.135f, .2225f, .1575f, .95f};
	public static final float[] JADE_DIFFUSE = new float[] {.54f, .89f, .63f, .95f};
	public static final float[] JADE_SPECULAR = new float[] {.2966f, .2966f, .2966f, .922f};
	public static final float JADE_SHININESS = 12.8f;
	
	public static Material getJadeMaterial() {
		Material gold = new Material();
		gold.ambient = JADE_AMBIENT;
		gold.diffuse = JADE_DIFFUSE;
		gold.specular = JADE_SPECULAR;
		gold.shininess = JADE_SHININESS;
		return gold;
	}
	
	
	public static final float[] DEF_AMBIENT = new float[] {1f, 1f, 1f, 1f};
	public static final float[] DEF_DIFFUSE = new float[] {.5f, .5f, .5f, .5f};
	public static final float[] DEF_SPECULAR = new float[] {.4966f, .4966f, .4966f, .922f};
	public static final float DEF_SHININESS = 12.8f;
	
	public static Material getDefMaterial() {
		Material def = new Material();
		def.ambient = DEF_AMBIENT;
		def.diffuse = DEF_DIFFUSE;
		def.specular = DEF_SPECULAR;
		def.shininess = DEF_SHININESS;
		return def;
	}
	
}
