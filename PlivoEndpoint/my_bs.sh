TARGET_ABI=x86
TARGET_PATH=src/main/jniLibs/$TARGET_ABI
rm $TARGET_PATH/*.so
scp -i /Users/anil/Downloads/forandroid.pem ubuntu@13.233.184.227:/home/ubuntu/2.8_all/plivo-android-sdk/pjsip-jni/src/libs/*.so  $TARGET_PATH
../gradlew clean assembleDebug

APP_PATH=/Users/anil/Desktop/office/cflag/flx/plv/plivo-android-sdk2-examples/PlivoAddressBook
rm $APP_PATH/app/libs/*.aar
cp ./build/outputs/aar/*.aar $APP_PATH/app/libs/