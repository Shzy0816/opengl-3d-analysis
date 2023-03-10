#include <gtc/matrix_transform.hpp>
#include "Model3DAnimSample.h"
#include "../util/GLUtils.h"
#include "animation.h"
#include "animator.h"

Model3DAnimSample::Model3DAnimSample(std::string fileName) {
    m_AngleX = 0;
    m_AngleY = 0;

    m_ScaleX = 1.0f;
    m_ScaleY = 1.0f;

    m_pModel = nullptr;
    m_pShader = nullptr;
    m_pAnimator = nullptr;

    this->fileName = fileName;

    initMatrix();

}

Model3DAnimSample::~Model3DAnimSample() {

}

void Model3DAnimSample::Init() {
    LOGCATE("Init start");

    if (m_pModel != nullptr && m_pShader != nullptr)
        return;

    char vShaderStr[] =
            "#version 300 es\n"
            "precision mediump float;\n"
            "layout (location = 0) in vec3 a_position;\n"
            "layout (location = 1) in vec3 a_normal;\n"
            "layout (location = 2) in vec2 a_texCoord;\n"
            "layout (location = 3) in vec3 tangent; \n"
            "layout (location = 4) in vec3 bitangent;\n"
            "layout (location = 5) in ivec4 boneIds; \n"
            "layout (location = 6) in vec4 weights;\n"
            "out vec2 v_texCoord;\n"
            "uniform mat4 u_MVPMatrix;\n"
            "uniform mat4 u_ModelMatrix;\n"
            "uniform vec3 lightPos;\n"
            "uniform vec3 lightColor;\n"
            "uniform vec3 viewPos;\n"
            "const int MAX_BONES = 100;\n"
            "const int MAX_BONE_INFLUENCE = 4;\n"
            "uniform mat4 finalBonesMatrices[MAX_BONES];\n"
            "out vec3 ambient;\n"
            "out vec3 diffuse;\n"
            "out vec3 specular;\n"
            "void main()\n"
            "{\n"
            "    v_texCoord = a_texCoord;    \n"

            "    vec4 position = vec4(0.0f);\n"
            "    for(int i = 0 ; i < MAX_BONE_INFLUENCE ; i++)\n"
            "    {\n"
            "        if(boneIds[i] == -1) \n"
            "            continue;\n"
            "        if(boneIds[i] >=MAX_BONES) \n"
            "        {\n"
            "            position = vec4(a_position,1.0f);\n"
            "            break;\n"
            "        }\n"
            "        vec4 localPosition = finalBonesMatrices[boneIds[i]] * vec4(a_position,1.0f);\n"
            "        position += localPosition * weights[i];\n"
            "   }\n"

            //            "    vec4 position = vec4(a_position, 1.0);\n"
            "    gl_Position = u_MVPMatrix * position;\n"

            "    vec3 fragPos = vec3(u_ModelMatrix * position);\n"
            "\n"
            "    // Ambient\n"
            "    float ambientStrength = 1.0;\n"
            "    ambient = ambientStrength * lightColor;\n"
            "\n"
            "    // Diffuse\n"
            "    float diffuseStrength = 1.0;\n"
            "    vec3 unitNormal = normalize(vec3(u_ModelMatrix * vec4(a_normal, 1.0)));\n"
            "    vec3 lightDir = normalize(lightPos - fragPos);\n"
            "    float diff = max(dot(unitNormal, lightDir), 0.0);\n"
            "    diffuse = diffuseStrength * diff * lightColor;\n"
            "\n"
            "    // Specular\n"
            "    float specularStrength = 0.5;\n"
            "    vec3 viewDir = normalize(viewPos - fragPos);\n"
            "    vec3 reflectDir = reflect(-lightDir, unitNormal);\n"
            "    float spec = pow(max(dot(unitNormal, reflectDir), 0.0), 16.0);\n"
            "    specular = specularStrength * spec * lightColor;\n"
            "}";

    char fShaderStr[] =
            "#version 300 es\n"
            "precision mediump float;"
            "out vec4 outColor;\n"
            "in vec2 v_texCoord;\n"
            "in vec3 ambient;\n"
            "in vec3 diffuse;\n"
            "in vec3 specular;\n"
            "uniform sampler2D texture_diffuse1;\n"
            "void main()\n"
            "{    \n"
            "    vec4 objectColor = texture(texture_diffuse1, v_texCoord);\n"
            "    vec3 finalColor = (ambient + diffuse + specular) * vec3(objectColor);\n"
            "    outColor = vec4(finalColor, 1.0);\n"
            "}";

    char fNoTextureShaderStr[] =
            "#version 300 es\n"
            "precision highp float;\n"
            "out vec4 outColor;\n"
            "in vec3 ambient;\n"
            "in vec3 diffuse;\n"
            "in vec3 specular;\n"
            "void main()\n"
            "{    \n"
            "    vec4 objectColor = vec4(1.0, 1.0, 1.0, 1.0);\n"
            "    vec3 finalColor = (ambient + diffuse + specular) * vec3(objectColor);\n"
            "    outColor = vec4(finalColor, 1.0);\n"
            "}";


    //app?????????model?????????????????? /sdcard/Android/data/com.chenxf.opengles/files/Download ??????????????????????????????????????????
    std::string path(DEFAULT_OGL_ASSETS_DIR);
    std::string modelPath(path + "/model/" + fileName);
    //std::string modelPath(path + "/model/avata1/eva.obj");
    m_pModel = new ModelAnim(modelPath);
    m_pAnimation = new Animation(modelPath, m_pModel);
    m_pAnimator = new Animator(m_pAnimation);
    if (m_pModel->ContainsTextures()) {
        LOGCATE("ContainsTextures, will create shader");
        m_pShader = new Shader(vShaderStr, fShaderStr);
    } else {
        m_pShader = new Shader(vShaderStr, fNoTextureShaderStr);
    }
    LOGCATE("Init done");
}

void Model3DAnimSample::LoadImage(NativeImage *pImage) {
    LOGCATE("Model3DAnimSample::LoadImage pImage = %p", pImage->ppPlane[0]);
}


void Model3DAnimSample::Draw(int screenW, int screenH) {
    if (m_pModel == nullptr || m_pShader == nullptr) return;

    //update animation firstly
    float deltaTime = 0.03f;//base on seconds, 30fps, each frame is about 0.03 seconds
    //???????????????????????? ????????????
    m_pAnimator->UpdateAnimation(deltaTime);

    LOGCATE("Draw start");

    glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    glEnable(GL_DEPTH_TEST);
    //??????MVP??????
    UpdateMVPMatrix(m_MVPMatrix, m_AngleX, m_AngleY, (float) screenW / screenH);

    m_pShader->use();
    m_pShader->setMat4("u_MVPMatrix", m_MVPMatrix);
    m_pShader->setMat4("u_ModelMatrix", m_ModelMatrix);
    m_pShader->setVec3("lightPos", glm::vec3(0, m_pModel->GetMaxViewDistance(), m_pModel->GetMaxViewDistance()));
    m_pShader->setVec3("lightColor", glm::vec3(1.0f, 1.0f, 1.0f));
    m_pShader->setVec3("viewPos", m_ViewPosition);

    //???????????????????????????
    auto transforms = m_pAnimator->GetFinalBoneMatrices();

    LOGCATE("Draw, transform size %d", transforms.size());
    //?????????vertex shader, ??????????????????????????????????????????
    for (int i = 0; i < transforms.size(); ++i)
        m_pShader->setMat4("finalBonesMatrices[" + std::to_string(i) + "]", transforms[i]);

    //??????DrawCall??????????????????
    m_pModel->Draw((*m_pShader));
    LOGCATE("Draw done");
}

void Model3DAnimSample::Destroy() {
    LOGCATE("Model3DAnimSample::Destroy");
    if (m_pModel != nullptr) {
        m_pModel->Destroy();
        delete m_pModel;
        m_pModel = nullptr;
    }

    if (m_pShader != nullptr) {
        m_pShader->Destroy();
        delete m_pShader;
        m_pShader = nullptr;
    }
}

void Model3DAnimSample::UpdateMVPMatrix(glm::mat4 &mvpMatrix, int angleX, int angleY, float ratio) {
    LOGCATE("Model3DAnimSample::UpdateMVPMatrix angleX = %d, angleY = %d, ratio = %f", angleX,
            angleY, ratio);
    angleX = angleX % 360;
    angleY = angleY % 360;

    //??????????????????
    float radiansX = static_cast<float>(MATH_PI / 180.0f * angleX);
    float radiansY = static_cast<float>(MATH_PI / 180.0f * angleY);

    // Projection matrix
    m_ProjectionMatrix = glm::frustum(-ratio, ratio, -1.0f, 1.0f, 1.0f,
                                      m_pModel->GetMaxViewDistance() * 10);

    // Model matrix
    glm::mat4 Model = glm::mat4(1.0f);
    Model = glm::scale(Model, glm::vec3(m_ScaleX, m_ScaleY, 1.0f));
    // Model = glm::rotate(Model, radiansX, glm::vec3(1.0f, 0.0f, 0.0f));
    Model = glm::rotate(Model, radiansY, glm::vec3(0.0f, 1.0f, 0.0f));
    glm::vec3 adjust = glm::vec3(m_adjustX, m_adjustY, m_adjustZ);
    Model = glm::translate(Model, -m_pModel->GetAdjustModelPosVec() + adjust);
    m_ModelMatrix = Model;
    mvpMatrix = m_ProjectionMatrix * m_ViewMatrix * m_ModelMatrix;
}

void
Model3DAnimSample::UpdateTransformMatrix(float rotateX, float rotateY, float scaleX, float scaleY) {
    GLSampleBase::UpdateTransformMatrix(rotateX, rotateY, scaleX, scaleY);
    m_AngleX = static_cast<int>(rotateX);
    m_AngleY = static_cast<int>(rotateY);
    m_ScaleX = scaleX;
    m_ScaleY = scaleY;
}

void Model3DAnimSample::initMatrix() {
    //glm::mat4 Projection = glm::perspective(45.0f,ratio, 0.1f,100.f);
    // View matrix
    m_ViewMatrix = glm::lookAt(
            glm::vec3(0, 50, -50), // Camera is at (0,0,1), in World Space
            glm::vec3(0, 0, 0), // and looks at the origin
            glm::vec3(0, 1, 0)  // Head is up (set to 0,-1,0 to look upside-down)
    );
    m_ViewPosition = glm::vec3 (0,50,-50);
    LOGCATE("Model3DAnimSample::initMatrix");
}

void Model3DAnimSample::setViewMatrix(int eyeX, int eyeY, int eyeZ, int lookAtX, int lookAtY,
                                      int lookAtZ, int upX, int upY, int upZ) {
    m_ViewMatrix = glm::lookAt(
            glm::vec3(eyeX, eyeY, eyeZ), // Camera is at (0,0,1), in World Space
            glm::vec3(lookAtX, lookAtY, lookAtZ), // and looks at the origin
            glm::vec3(upX, upY, upZ)  // Head is up (set to 0,-1,0 to look upside-down)
    );
    m_ViewPosition = glm::vec3 (eyeX,eyeY,eyeZ);
    LOGCATE("Model3DAnimSample::setViewMatrix");
}

void Model3DAnimSample::AdjustModelPosition(int x, int y, int z) {
    m_adjustX = x;
    m_adjustY = y;
    m_adjustZ = z;
}


