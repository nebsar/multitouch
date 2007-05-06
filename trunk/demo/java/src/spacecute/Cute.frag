uniform sampler2D texture;
void main()
{
    //gl_FragColor = vec4(0.5, 0.0, 0.5, 1.0);
    //gl_FragColor = vec4(gl_TexCoord[0].st, 1.0, 1.0); 
    gl_FragColor = texture2D(texture, gl_TexCoord[0].st);
}

