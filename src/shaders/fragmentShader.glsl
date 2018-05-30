#version 400 core

in vec2 pass_textureCoords;
in vec3 surfaceNormal;
in vec3 toLightNormal[4];
in vec3 toCameraVector;
in float visibility;
in vec4 shadowCoords;

out vec4 out_Color;

uniform sampler2D textureSampler;
uniform sampler2D shadowMap;
uniform vec3 lightColor[4];
uniform vec3 attenuation[4];
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColor;
uniform float levels;

uniform int pcfCount;
uniform float mapSize;

void main(void) {

    float totalTexels = (pcfCount * 2.0 + 1.0) * (pcfCount * 2.0 + 1.0);
    float texelSize = 1.0 / mapSize;
    float total = 0.0;

    for (int x = -pcfCount; x <= pcfCount; x++){
            for (int y = -pcfCount; y <= pcfCount; y++){
                float objectNearestLight = texture(shadowMap, shadowCoords.xy + vec2(x, y) * texelSize).r;
                if (shadowCoords.z > objectNearestLight + 0.002){
                    total += 1.0;
                }
            }
        }

    total /= totalTexels;
    float lightFactor = 1.0 - (total * shadowCoords.w);

	vec3 unitNormal = normalize(surfaceNormal);
	vec3 unitVectorToCamera = normalize(toCameraVector);

    vec3 totalDiffuse = vec3(0.0);
    vec3 totalSpecular = vec3(0.0);

	for (int i = 0; i < 4; i++){
	    float distance = length(toLightNormal[i]);
	    float attenuationFactor = attenuation[i].x + (attenuation[i].y * distance) + (attenuation[i].z * distance * distance);
	    vec3 unitLightVector = normalize(toLightNormal[i]);
    	float nDot1 = dot(unitNormal, unitLightVector);
    	float brightness = max(nDot1, 0.0);

        if (levels != 0){
            float level = floor(brightness * levels);
            brightness = level / levels;
        }

    	vec3 lightDirection = -unitLightVector;
    	vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
    	float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
    	specularFactor = max(specularFactor, 0.0);
    	float dampedFactor = pow(specularFactor, shineDamper);

    	if (levels != 0){
    	    float level = floor(dampedFactor * levels);
            dampedFactor = level / levels;
    	}

    	totalDiffuse = totalDiffuse + (brightness * lightColor[i]) / attenuationFactor;
    	totalSpecular = totalSpecular + (dampedFactor * reflectivity * lightColor[i]) / attenuationFactor;

    	vec4 textureColor = texture(textureSampler, pass_textureCoords);
    	if (textureColor.a < 0.5){
    	    discard;
    	}
	}

	totalDiffuse = max(totalDiffuse * lightFactor, 0.4);

	out_Color = vec4(totalDiffuse, 1.0) * texture(textureSampler, pass_textureCoords) + vec4(totalSpecular, 1.0);
	out_Color = mix(vec4(skyColor, 1.0), out_Color, visibility);
}