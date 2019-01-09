# build/os-auto.mak.  Generated from os-auto.mak.in by configure.

export OS_CFLAGS   := $(CC_DEF)PJ_AUTOCONF=1  -fpic -ffunction-sections -funwind-tables -no-canonical-prefixes -march=armv7-a -mfpu=vfpv3-d16 -mfloat-abi=softfp -mthumb -Os -DNDEBUG -Ijni -DANDROID -Wa,--noexecstack -Wformat -Werror=format-security -isystem /Users/anil/Library/Android/sdk/android-ndk-r13b/platforms/android-24/arch-arm/usr/include  -I/Users/anil/Library/Android/sdk/android-ndk-r13b//sources/cxx-stl/gnu-libstdc++/4.9/include -I/Users/anil/Library/Android/sdk/android-ndk-r13b//sources/cxx-stl/gnu-libstdc++/4.9/libs/armeabi-v7a/include -DPJ_IS_BIG_ENDIAN=0 -DPJ_IS_LITTLE_ENDIAN=1 -I/Users/anil/Desktop/office/cflag/flx/plv/plivo-android-sdk/openssl/armeabi-v7a/include

export OS_CXXFLAGS := $(CC_DEF)PJ_AUTOCONF=1  -shared --sysroot=/Users/anil/Library/Android/sdk/android-ndk-r13b/platforms/android-24/arch-arm -lgcc -no-canonical-prefixes -march=armv7-a -Wl,--fix-cortex-a8 -Wl,--build-id -Wl,--no-undefined -Wl,-z,noexecstack -Wl,-z,relro -Wl,-z,now -Wl,--warn-shared-textrel -Wl,--fatal-warnings -lc -lm -fexceptions -frtti

export OS_LDFLAGS  :=  --sysroot=/Users/anil/Library/Android/sdk/android-ndk-r13b/platforms/android-24/arch-arm -L/Users/anil/Library/Android/sdk/android-ndk-r13b//sources/cxx-stl/gnu-libstdc++/4.9/libs/armeabi-v7a/ -L/Users/anil/Desktop/office/cflag/flx/plv/plivo-android-sdk/openssl/armeabi-v7a/lib -lssl -lcrypto -lm -lgnustl_static  -lc -lgcc -ldl -lOpenSLES -llog -lGLESv2 -lEGL -landroid

export OS_SOURCES  := 


