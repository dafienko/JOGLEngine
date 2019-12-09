#version 430

layout (binding=0) uniform sampler2D samp;

in vec3 color;
in vec2 tc;
in vec3 varyingNormal;
in vec3 varyingVertPos;
in vec3 varyingLightDir;

out vec4 fragColor;

struct PositionalLight
{
	vec4 ambient;
	vec4 diffuse;
	vec4 specular;
	vec3 position;
};

struct Material {
	vec4 ambient;
	vec4 diffuse;
	vec4 specular;
	float shininess;
};

uniform PositionalLight light;
uniform Material material;

uniform vec4 globalAmbient;

void main() {
	vec3 L = normalize(varyingLightDir);
	vec3 N = normalize(varyingNormal);
	vec3 V = normalize(-varyingVertPos);

	vec3 R = normalize(reflect(-L, N));

	float cosTheta = dot(L, N);

	float cosPhi = dot(V, R);

	vec3 ambient = ((globalAmbient * material.ambient) + (light.ambient * material.ambient)).xyz;
	vec3 diffuse = light.diffuse.xyz * material.diffuse.xyz * max(cosTheta, 0.0);
	vec3 specular = light.specular.xyz * material.specular.xyz * pow(max(cosPhi, 0.0), material.shininess);

	//fragColor = vec4(color, 1);
	//fragColor = texture(samp, tc);
	fragColor = texture(samp, tc) * vec4((ambient + diffuse + specular).xyz, 1.0);
}
