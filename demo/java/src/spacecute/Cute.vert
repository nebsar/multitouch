//uniform vec2 position;
//uniform float orientation;
uniform mat4 transform;

void main() 
{
//        float cosAngle = cos(orientation);
//        float sinAngle = sin(orientation);
//        mat4 transform = mat4( vec4( cosAngle, -sinAngle, 0.0, 0.0), 
//                               vec4( sinAngle,  cosAngle, 0.0, 0.0), 
//                               vec4(      0.0,       0.0, 1.0, 0.0), 
//                               vec4(      position      , 0.0, 1.0) );

        gl_TexCoord[0] = gl_MultiTexCoord0;
	gl_Position =  transform * gl_Vertex;
}