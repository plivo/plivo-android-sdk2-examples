#Build plivoendpoint
#------------
function prepare {
    OUTPUTS_DIR=$PWD/build/outputs
    JNI_DIR=$PWD/pjsip-jni
    ENDPOINT_DIR=$PWD/PlivoEndpoint

    rm -rf $ENDPOINT_DIR/src/main/jniLibs/
    mkdir -p $ENDPOINT_DIR/src/main/jniLibs/
    rm -rf $ENDPOINT_DIR/src/main/java/com/plivo/endpoint/backend
    mkdir -p $ENDPOINT_DIR/src/main/java/com/plivo/endpoint/backend
    cp -r $OUTPUTS_DIR/libs/* $ENDPOINT_DIR/src/main/jniLibs/
    cp -r $JNI_DIR/src/com/plivo/endpoint/backend/*.java $ENDPOINT_DIR/src/main/java/com/plivo/endpoint/backend/
    cd $ENDPOINT_DIR
}

function output {
    cd ..
    rm -rf $OUTPUTS_DIR/aar
    mkdir -p $OUTPUTS_DIR/aar
    cp -r $ENDPOINT_DIR/build/outputs/aar/*.aar $OUTPUTS_DIR/aar
    stat $OUTPUTS_DIR/aar/*.aar
    echo "Build Complete. Output generated at $OUTPUTS_DIR/aar/"
}

function buildProd {
    echo "********* Building endpoint for PROD env **********"
    prepare
    ../gradlew clean assembleProdDebug
    output
}

function buildStage {
    echo "********* Building endpoint for STAGE env **********"
    prepare
    ../gradlew clean assembleStageDebug
    output
}


if [ "$1" == "stage" ]
then
    buildStage
elif [ "$1" == "prod" ]
  then
    buildProd
else
    buildProd
fi