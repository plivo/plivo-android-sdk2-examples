#!/bin/sh
for VARIABLE in "armeabi-v7a" "x86" "mips64" "x86_64" "arm64-v8a" "mips" "armeabi" 
do
	#cd ./pjsip 
    	export TARGET_ABI=$VARIABLE
	#./build_pjsip.sh $VARIABLE
	echo $TARGET_ABI
	#cd ../
	#cd ./pjsip-jni/jni
	#make clean
	#make
	#cd ../../
	#mkdir -p ./libs/$TARGET_ABI
	#cp ./pjsip-jni/src/libs/libpjplivo.so ./libs/$TARGET_ABI 
done
cp -r libs/* ./PlivoEndpoint/src/main/jniLibs/
cp pjsip-jni/src/com/plivo/endpoint/backend/*.java ./PlivoEndpoint/src/com/plivo/endpoint/backend/
cd ./PlivoEndpoint
gradle clean
gradle build
