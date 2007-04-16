uniform vec2 size;    //vec2( 0.5 0.5 );
uniform vec2 center;  //shadow-offset

void main() 
{
        gl_TexCoord[0] = gl_MultiTexCoord0;
	gl_Position = vec4(gl_Vertex.xy * size + center, gl_Vertex.zw);
}
