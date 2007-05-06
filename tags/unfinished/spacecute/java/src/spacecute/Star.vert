uniform mat4 transform;
void main() 
{
        gl_TexCoord[0] = gl_MultiTexCoord0;
	gl_Position = transform * gl_Vertex;
}
