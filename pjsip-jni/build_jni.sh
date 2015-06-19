mkdir -p src/com/plivo/endpoint/backend

if [ ! -z `printenv | grep PJSIP_PATH` ] &&  [ ! -z `printenv | grep SWIG_PATH` ]
    then
        ndk-build
    else
        echo "pjsip path or swig path not given in environment variable" 
fi

cp -r src/com/plivo/endpoint/backend ../PlivoEndpoint/src/com/plivo/endpoint/
cp -r libs/* $PLIVO_ANDROID_EXAMPLES_PATH/PlivoOutbound/libs