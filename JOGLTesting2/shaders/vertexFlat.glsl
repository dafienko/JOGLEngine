#version 430

layout (location=1) in vec3 position;

out vec4 color;

uniform vec4 vColor;
uniform mat4 projectionMatrix;
uniform mat4 modelMatrix;
uniform mat4 cameraMatrix;


void main() {
	color = vColor;
	
	mat4 mvMatrix = cameraMatrix * modelMatrix;

	gl_Position = projectionMatrix * mvMatrix * vec4(position.xyz, 1.0);
};

