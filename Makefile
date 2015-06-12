all: build_jni build_gradle
release: build_jni build_change build_gradle

build_pjsip:
	@echo 'give make build_pjsip_v7 for armv7 architecture'
	@echo 'give make build_pjsip_arm64 for arm64 architecture'
	@echo 'give make build_psjip_mips for mips64 architecture'
	@echo 'give make build_pjsip_x86 for x86_64 architecture'

build_pjsip_v7:
	cd pjsip; \
	chmod a+x ./build_pjsip.sh; \
	./build_pjsip.sh 1; \
	cd ..
	
build_pjsip_arm64:
	cd pjsip; \
	chmod a+x ./build_pjsip.sh; \
	./build_pjsip.sh 2; \
	cd ..
	
build_pjsip_mips:
	cd pjsip; \
	chmod a+x ./build_pjsip.sh; \
	./build_pjsip.sh 3; \
	cd ..
	
build_pjsip_x86:
	cd pjsip; \
	chmod a+x ./build_pjsip.sh; \
	./build_pjsip.sh 4; \
	cd ..

build_jni:
	cd pjsip-jni; \
	chmod a+x ./build_jni.sh; \
	./build_jni.sh; \
	cd ..
	
build_change:
	cd PlivoEndpoint; \	
	chmod a+x ./change.sh; \
	./change.sh; \
	cd ..

build_replace:
	cd PlivoEndpoint; \
	chmod a+x replace.sh; \
	./replace.sh; \
	cd ..

build_gradle:
	cd PlivoEndpoint; \
	gradle build; \
	gradle -q copyFiles; \
	cd .. 
