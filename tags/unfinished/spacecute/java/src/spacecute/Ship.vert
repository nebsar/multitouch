//uniform vec2 size;    //octopus: vec2( 0.547, 0.249 ); //beetle: vec2( 0.465, 0.271 );//rocket: vec2( 0.719, 0.238 );
//uniform vec2 center;  //shadow-offset
uniform mat4 transform;
void main() 
{
        gl_TexCoord[0] = gl_MultiTexCoord0;
	gl_Position = transform * gl_Vertex;
}
