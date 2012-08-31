attribute vec4 center;
varying vec4 vCenter;

void main() {

	vCenter = center;
	gl_Position = projectionMatrix * modelViewMatrix * vec4( position, 1.0 );

}
