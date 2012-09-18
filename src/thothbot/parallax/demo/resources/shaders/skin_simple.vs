uniform vec4 offsetRepeat;

varying vec3 vNormal;
varying vec2 vUv;

varying vec3 vViewPosition;

[*]

void main() {

	vec4 mvPosition = modelViewMatrix * vec4( position, 1.0 );
	vec4 mPosition = modelMatrix * vec4( position, 1.0 );

	vViewPosition = -mvPosition.xyz;

	vNormal = normalMatrix * normal;

	vUv = uv * offsetRepeat.zw + offsetRepeat.xy;

	gl_Position = projectionMatrix * mvPosition;

[*]

}
