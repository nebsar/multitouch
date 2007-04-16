uniform vec2 size;    //octopus: vec2( 0.547, 0.249 ); //beetle: vec2( 0.465, 0.271 );//rocket: vec2( 0.719, 0.238 );
uniform vec2 center;  //shadow-offset

void main() 
{
        gl_TexCoord[0] = gl_MultiTexCoord0;
	gl_Position = vec4(gl_Vertex.xy * size + center, gl_Vertex.zw);
}
