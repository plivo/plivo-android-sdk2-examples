if [ ! -z `printenv | grep PJSIP_PATH` ] &&  [ ! -z `printenv | grep SWIG_PATH` ]
    then
        ndk-build
    else
        echo "pjsip path or swig path not given in environment variable" 
fi

cp -r src/com/plivo/endpoint/backend ../PlivoEndpoint/src/com/plivo/endpoint/
cp -r libs/* ../plivo-android-examples/PlivoOutbound/libs