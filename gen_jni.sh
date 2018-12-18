for VARIABLE in "x86" "x86_64" "arm64-v8a" "armeabi-v7a" "mips" "mips64"
do
	rm -rf pjsip/pjproject-2.8
	cp -r pjsip/bkp/$VARIABLE/pjproject-2.8 pjsip/

	cd ./pjsip-jni/jni
	mkdir ../src/libs
	make clean
	make
	cd ../../

	#stat pjsip-jni/src/libs/libpjplivo.so
	rm -rf build/outputs/libs/$VARIABLE
	mkdir -p build/outputs/libs/$VARIABLE
	cp -r pjsip-jni/src/libs/*.so build/outputs/libs/$VARIABLE
	stat build/outputs/libs/$VARIABLE/libpjplivo.so
done


