#version 430

layout (binding=0) uniform sampler2D samp;

in vec4 varyingColor;
in vec2 tc;


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
uniform int textured;

void main() {
	if (textured == 1) {
		fragColor = texture(samp, tc) * varyingColor;
	} else {
		fragColor = varyingColor;
	}
}
