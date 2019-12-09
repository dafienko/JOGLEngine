#version 430

layout (location=1) in vec3 position;
layout (location=2) in vec3 normal;
layout (location=3) in vec3 c;
layout (location=4) in vec2 texCoord;
layout (binding=0) uniform sampler2D samp;


out vec3 varyingNormal;
out vec3 varyingLightDir;
out vec3 varyingVertPos;
out vec3 color;
out vec2 tc;


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

uniform mat4 modelMatrix;
uniform vec4 globalAmbient;
uniform mat4 cameraMatrix;
uniform mat4 projectionMatrix;
uniform mat4 normMatrix;


void main() {
	mat4 mvMatrix = cameraMatrix * modelMatrix;

	varyingVertPos = (mvMatrix * vec4(position, 1.0)).xyz;
	varyingLightDir = light.position - varyingVertPos;
	varyingNormal = (normMatrix * vec4(normal, 1.0)).xyz;

	//color = v;
	color = vec3(texCoord.x, texCoord.y, 1);

	tc = texCoord;

	gl_Position = projectionMatrix * mvMatrix * vec4(position.xyz, 1.0);
};

