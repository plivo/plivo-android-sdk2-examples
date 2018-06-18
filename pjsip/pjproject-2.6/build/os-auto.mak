# build/os-auto.mak.  Generated from os-auto.mak.in by configure.

export OS_CFLAGS   := $(CC_DEF)PJ_AUTOCONF=1  -fpic -ffunction-sections -funwind-tables -no-canonical-prefixes -g -O2 -DNDEBUG -Ijni -DANDROID -D__ANDROID_API__=28 -Wa,--noexecstack -Wformat -Werror=format-security --sysroot /Users/altanaibisht/android-ndk-r17/sysroot -isystem /Users/altanaibisht/android-ndk-r17/sysroot/usr/include/aarch64-linux-android  -I/Users/altanaibisht/android-ndk-r17/sources/cxx-stl/gnu-libstdc++/4.9/include -I/Users/altanaibisht/android-ndk-r17/sources/cxx-stl/gnu-libstdc++/4.9/libs/arm64-v8a/include -DPJ_IS_BIG_ENDIAN=0 -DPJ_IS_LITTLE_ENDIAN=1 -I/Users/altanaibisht/StudioProjects/plivo-android-sdk/openssl/arm64-v8a/include

export OS_CXXFLAGS := $(CC_DEF)PJ_AUTOCONF=1  -fpic -ffunction-sections -funwind-tables -no-canonical-prefixes -g -O2 -DNDEBUG -Ijni -DANDROID -D__ANDROID_API__=28 -Wa,--noexecstack -Wformat -Werror=format-security --sysroot /Users/altanaibisht/android-ndk-r17/sysroot -isystem /Users/altanaibisht/android-ndk-r17/sysroot/usr/include/aarch64-linux-android  -I/Users/altanaibisht/android-ndk-r17/sources/cxx-stl/gnu-libstdc++/4.9/include -I/Users/altanaibisht/android-ndk-r17/sources/cxx-stl/gnu-libstdc++/4.9/libs/arm64-v8a/include  -shared --sysroot=/Users/altanaibisht/android-ndk-r17/platforms/android-28/arch-arm64 -lgcc -Wl,--exclude-libs,libgcc.a -latomic -Wl,--exclude-libs,libatomic.a -no-canonical-prefixes -Wl,--build-id -Wl,--no-undefined -Wl,-z,noexecstack -Wl,-z,relro -Wl,-z,now -Wl,--warn-shared-textrel -Wl,--fatal-warnings -lc -lm -fexceptions -frtti

export OS_LDFLAGS  :=  --sysroot=/Users/altanaibisht/android-ndk-r17/platforms/android-28/arch-arm64 -L/Users/altanaibisht/android-ndk-r17/sources/cxx-stl/gnu-libstdc++/4.9/libs/arm64-v8a/ -L/Users/altanaibisht/StudioProjects/plivo-android-sdk/openssl/arm64-v8a/lib -lm -lgnustl_static  -lc -lgcc -ldl -lOpenSLES -llog -lGLESv2 -lEGL -landroid

export OS_SOURCES  := 


