rm -rf app/libs/*.aar
scp -i /Users/anil/Downloads/forandroid.pem ubuntu@13.233.184.227:/home/ubuntu/2.8_all/plivo-android-sdk/build/outputs/aar/*.aar app/libs/
./gradlew clean assembleDebug