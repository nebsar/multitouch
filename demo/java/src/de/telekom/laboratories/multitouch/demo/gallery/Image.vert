uniform mat4 transform;

void main() 
{
        gl_TexCoord[0] = vec4( ( gl_Vertex.xy + vec2(1.0,1.0) ) * vec2(0.5,0.5), 0.0, 0.0); // gl_MultiTexCoord0;
        //gl_TexCoord[0].y = 1.0-gl_TexCoord[0].y;
	gl_Position =  transform*gl_Vertex;
}