# build/os-auto.mak.  Generated from os-auto.mak.in by configure.

export OS_CFLAGS   := $(CC_DEF)PJ_AUTOCONF=1  -ffunction-sections -funwind-tables -no-canonical-prefixes -O2 -DNDEBUG -Ijni -DANDROID -Wa,--noexecstack -Wformat -Werror=format-security -isystem /home/ubuntu/android-ndk-r13b/platforms/android-24/arch-x86_64/usr/include  -I/home/ubuntu/android-ndk-r13b//sources/cxx-stl/gnu-libstdc++/4.9/include -I/home/ubuntu/android-ndk-r13b//sources/cxx-stl/gnu-libstdc++/4.9/libs/x86_64/include -DPJ_IS_BIG_ENDIAN=0 -DPJ_IS_LITTLE_ENDIAN=1 -I/home/ubuntu/2.8_all/plivo-android-sdk/openssl/x86_64/include

export OS_CXXFLAGS := $(CC_DEF)PJ_AUTOCONF=1  -shared --sysroot=/home/ubuntu/android-ndk-r13b/platforms/android-24/arch-x86_64 -lgcc -no-canonical-prefixes -Wl,--build-id -Wl,--no-undefined -Wl,-z,noexecstack -Wl,-z,relro -Wl,-z,now -Wl,--warn-shared-textrel -Wl,--fatal-warnings -lc -lm -fexceptions -frtti

export OS_LDFLAGS  :=  --sysroot=/home/ubuntu/android-ndk-r13b/platforms/android-24/arch-x86_64 -L/home/ubuntu/android-ndk-r13b//sources/cxx-stl/gnu-libstdc++/4.9/libs/x86_64/ -L/home/ubuntu/2.8_all/plivo-android-sdk/openssl/x86_64/lib -lssl -lcrypto -lm -lgnustl_static  -lc -lgcc -ldl -lOpenSLES -llog -lGLESv2 -lEGL -landroid

export OS_SOURCES  := 


