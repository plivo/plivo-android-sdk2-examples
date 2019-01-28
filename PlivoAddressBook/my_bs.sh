rm -rf app/libs/*.aar
#scp -i /Users/anil/Downloads/forandroid.pem ubuntu@13.233.184.227:/home/ubuntu/plivo-android-sdk/build/outputs/aar/*.aar app/libs/
cp -r /Users/anil/Desktop/office/cflag/flx/plv/plivo-android-sdk/build/outputs/aar/* app/libs/
./gradlew clean assembleDebug