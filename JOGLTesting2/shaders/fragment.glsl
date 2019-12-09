#version 430

in vec4 varyingColor;

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

uniform mat4 cameraMatrix;
uniform mat4 projectionMatrix;
uniform mat4 mvMatrix;
uniform vec4 c;
uniform mat4 normMatrix;

void main() {
	fragColor = varyingColor;
}
