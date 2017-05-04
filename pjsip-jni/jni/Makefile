#include ../../../../../build.mak
#include ../../../../../build/common.mak

# Get PJ build settings
include $(PJSIP_DIR)/build.mak
include $(PJDIR)/build/common.mak

MY_MODULE_PATH  := $(PJDIR)/pjsip-apps/build/output/pjsua-$(TARGET_NAME)
MY_MODULES      := $(MY_MODULE_PATH)/pjsua_app.o \
		   $(MY_MODULE_PATH)/pjsua_app_cli.o \
		   $(MY_MODULE_PATH)/pjsua_app_common.o \
		   $(MY_MODULE_PATH)/pjsua_app_config.o \
		   $(MY_MODULE_PATH)/pjsua_app_legacy.o

OUT_DIR		:= ../src/jni
LIBPJSUA_SO	:= ../src/libs/libpjplivo.so

# Env settings, e.g: path to SWIG, JDK, java(.exe), javac(.exe)
MY_SWIG		:= swig
MY_JNI_CFLAGS   := -D__ANDROID__ -fPIC
MY_JNI_LDFLAGS  := -Wl,-soname,libpjplivo.so
SWIG_FLAGS      := -D__ANDROID__ -c++

# Build settings
MY_CFLAGS	:= $(PJ_CXXFLAGS) $(MY_JNI_CFLAGS) $(CFLAGS)
MY_LDFLAGS	:= $(PJ_LDXXFLAGS) $(PJ_LDXXLIBS) $(MY_JNI_LDFLAGS) $(LDFLAGS)
MY_PACKAGE_NAME	:= com.plivo.endpoint.backend
MY_PACKAGE_PATH := ../src/$(subst .,/,$(MY_PACKAGE_NAME))

all: $(LIBPJSUA_SO) 

$(LIBPJSUA_SO): $(OUT_DIR)/pjsua_wrap.o
	#mkdir -p ../app/src/main/jniLibs/armeabi
	$(PJ_CXX) -shared -o $(LIBPJSUA_SO) \
	$(OUT_DIR)/pjsua_wrap.o $(OUT_DIR)/plivo_app_callback.o \
	$(MY_MODULES) \
		$(MY_CFLAGS) $(MY_LDFLAGS)

$(OUT_DIR)/pjsua_wrap.o: $(OUT_DIR)/pjsua_wrap.cpp plivo_app_callback.cpp Makefile
	$(PJ_CXX) -c $(OUT_DIR)/pjsua_wrap.cpp -o $(OUT_DIR)/pjsua_wrap.o \
		$(MY_CFLAGS)
	$(PJ_CXX) -c plivo_app_callback.cpp -o $(OUT_DIR)/plivo_app_callback.o \
		$(MY_CFLAGS)

$(OUT_DIR)/pjsua_wrap.cpp: pjplivo.i
	mkdir -p $(MY_PACKAGE_PATH)
	mkdir -p $(OUT_DIR)
	swig $(SWIG_FLAGS) -java  -package $(MY_PACKAGE_NAME) \
		-outdir $(MY_PACKAGE_PATH) \
		-o $(OUT_DIR)/pjsua_wrap.cpp pjplivo.i

clean distclean realclean:
	rm -rf $(LIBPJSUA_SO) $(OUT_DIR)/* \
		$(MY_PACKAGE_PATH)/*.java

#java:
#ifneq (,$(findstring PJMEDIA_VIDEO_DEV_HAS_ANDROID=1,$(ANDROID_CFLAGS)))
#	@echo "Copying Android camera helper components..."
#	cp $(PJDIR)/pjmedia/src/pjmedia-videodev/android/PjCamera*.java $(MY_PACKAGE_PATH)/..
#endif
