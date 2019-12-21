#version 430

layout (location=1) in vec3 position;
layout (location=2) in vec3 normal;
layout (location=3) in vec3 c;
layout (location=4) in vec2 texCoord;
layout (binding=0) uniform sampler2D samp;


out vec4 varyingColor;
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
uniform int textured;

void main() {
	mat4 mvMatrix = cameraMatrix * modelMatrix;

	vec4 P = mvMatrix * vec4(position, 1.0);
	vec3 N = normalize((normMatrix * vec4(normal, 1.0)).xyz);
	vec3 L = normalize(light.position - P.xyz);

	vec3 V = normalize(-P.xyz);

	vec3 R = reflect(-L, N);

	vec3 ambient = ((globalAmbient * material.ambient) + (light.ambient * material.ambient)).xyz;

	vec3 diffuse = light.diffuse.xyz * material.diffuse.xyz * max(dot(N, L), 0.0);

	vec3 specular = material.specular.xyz * light.specular.xyz * pow(max(dot(R, V), 0.0), material.shininess);
	//vec3 specular = material.specular.xyz * light.specular.xyz * pow(dot(R, V), material.shininess);

	//varyingColor = vec4((ambient + diffuse + specular), 1.0);
	//varyingColor = vec4((color * (ambient + diffuse) + specular).xyz, 1.0);
	varyingColor = vec4(((ambient + diffuse + specular)).xyz, 1.0);

	tc = texCoord;

	gl_Position = projectionMatrix * mvMatrix * vec4(position.xyz, 1.0);
}

