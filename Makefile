all: build_jni build_gradle

build_pjsip:
	cd pjsip; \
	chmod a+x ./build_pjsip.sh; \
	./build_pjsip.sh; \
	cd ..

build_jni:
	cd pjsip-jni; \
	chmod a+x ./build_jni.sh; \
	./build_jni.sh; \
	cd ..
    
build_gradle:
	cd PlivoEndpoint; \
	gradle build; \
	gradle -q copyFiles; \
	cd ..