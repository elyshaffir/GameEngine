#version 400 core

in vec4 clipSpace;
in vec2 textureCoords;
in vec3 toCameraVector;
in vec3 fromLightVector;

out vec4 out_Color;

uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;
uniform sampler2D dudvMap;
uniform sampler2D normalMap;
uniform sampler2D depthMap;

uniform vec3 lightColor;

uniform float moveFactor;
uniform float nearPlane;
uniform float farPlane;

uniform float waveStrength;
uniform float shineDamper;
uniform float reflectivity;

void main(void) {

    vec2 ndc = (clipSpace.xy / clipSpace.w) / 2.0 + 0.5;
    vec2 reflectTextureCoords = vec2(ndc.x, -ndc.y);
    vec2 refractTextureCoords = vec2(ndc.x, ndc.y);


    float depth = texture(depthMap, refractTextureCoords).r;
    float floorDistance = 2.0 * nearPlane * farPlane / (farPlane + nearPlane - (2.0 * depth - 1.0) * (farPlane - nearPlane));
    depth = gl_FragCoord.z;
    float waterDistance = 2.0 * nearPlane * farPlane / (farPlane + nearPlane - (2.0 * depth - 1.0) * (farPlane - nearPlane));
    float waterDepth = floorDistance - waterDistance;

    vec2 distortedTextureCoords = texture(dudvMap, vec2(textureCoords.x + moveFactor, textureCoords.y)).rg * 0.1;
    distortedTextureCoords = textureCoords + vec2(distortedTextureCoords.x, distortedTextureCoords.y + moveFactor);
    vec2 totalDistortion = (texture(dudvMap, distortedTextureCoords).rg * 2.0 - 1.0) * waveStrength * clamp(waterDepth / 20.0, 0.0, 1.0);

    refractTextureCoords += totalDistortion;
    refractTextureCoords = clamp(refractTextureCoords, 0.001, 0.999);

    reflectTextureCoords += totalDistortion;
    reflectTextureCoords.x = clamp(reflectTextureCoords.x, 0.001, 0.999);
    reflectTextureCoords.y = clamp(reflectTextureCoords.y, -0.999, -0.001);

    vec4 reflectColor = texture(reflectionTexture, reflectTextureCoords);
    vec4 refractColor = texture(refractionTexture, refractTextureCoords);

    vec4 normalMapColor = texture(normalMap, distortedTextureCoords);
    vec3 normal = vec3(normalMapColor.r * 2.0 - 1.0, normalMapColor.b * 3.0, normalMapColor.g * 2.0 - 1.0);
    normal = normalize(normal);

    vec3 viewVector = normalize(toCameraVector);
    float refractiveFactor = dot(viewVector, normal);
    refractiveFactor = pow(refractiveFactor, 0.5);
    refractiveFactor = clamp(refractiveFactor, 0.0, 1.0);

    vec3 reflectedLight = reflect(normalize(fromLightVector), normal);
    float specular = max(dot(reflectedLight, viewVector), 0.0);
    specular = pow(specular, shineDamper);
    vec3 specularHighlights = lightColor * specular * reflectivity * clamp(waterDepth / 5.0, 0.0, 1.0);;

	out_Color = mix(reflectColor, refractColor, refractiveFactor);
	out_Color = mix(out_Color, vec4(0.0, 0.3, 0.5, 1.0), 0.2) + vec4(specularHighlights, 0.0);
	out_Color.a = clamp(waterDepth / 5.0, 0.0, 1.0);

}