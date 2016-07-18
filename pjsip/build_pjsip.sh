#!/bin/sh

#  build_pjsip.sh
#  Script to build pjsip for plivo-ios-sdk
#  This script build for these architectures:
#   - armv7
#   - arm64
#   - mips64
#   - x86_64
#
#  Copyright (c) 2013 Plivo Inc. All rights reserved.

#TODO: Read for input after every arch compilation
# Check if the library exists in the individual folder.

PJPROJECT_FILE_NAME="pjproject-2.2.1.tar.bz2"
PJPROJECT_SRC_DIR="pjproject-2.2.1"

CONFIG_SITE_H="$PJPROJECT_SRC_DIR/pjlib/include/pj/config_site.h"
CWD=`pwd`

#copy our config_site.h
copy_config_site_h()
{
    echo "Copy $CONFIG_SITE_H"
    cp -v config_site.h $CONFIG_SITE_H
}

prep_pjsip_source()
{
    echo "Untar $PJPROJECT_FILE_NAME"
    tar xvjf $PJPROJECT_FILE_NAME
}

build_pjsip_armv7()
{
    rm -rf $CWD/$PJPROJECT_SRC_DIR
    prep_pjsip_source
    copy_config_site_h

    echo "Building armv7 libs..."
    cd ./$PJPROJECT_SRC_DIR
    #android_ndk_r9 should be present in path environment variable
    if [ ! -z `printenv | grep ANDROID_NDK_ROOT` ]
    then
        TARGET_ABI=armeabi-v7a ./configure-android
        make dep && make clean && make
        cd $CWD
    else
        echo "ndk path not provided" 
     fi
}

build_pjsip_arm64()
{
    rm -rf $CWD/$PJPROJECT_SRC_DIR
    prep_pjsip_source
    copy_config_site_h

    echo "Building arm64 libs..."
    cd ./$PJPROJECT_SRC_DIR
    if [ ! -z `printenv | grep ANDROID_NDK_ROOT` ]
    then
        TARGET_ABI=armeabi-arm64 ./configure-android
        make dep && make clean && make
        cd $CWD
    else
        echo "ndk path not provided" 
     fi
}

build_pjsip_x86_64()
{
    rm -rf $CWD/$PJPROJECT_SRC_DIR
    prep_pjsip_source
    copy_config_site_h

    echo "Building x86_64 libs..."
    cd ./$PJPROJECT_SRC_DIR
    
    if [ ! -z `printenv | grep ANDROID_NDK_ROOT` ]
    then
        TARGET_ABI=x86 ./configure-android --simulator
        make dep && make clean && make
        cd $CWD
    else
        echo "ndk path not provided" 
     fi
}

build_pjsip_mips64()
{
    rm -rf $CWD/$PJPROJECT_SRC_DIR
    prep_pjsip_source
    copy_config_site_h

    echo "Building mips libs..."
    cd ./$PJPROJECT_SRC_DIR
    #android_ndk_r9 should be present in path environment variable
    if [ ! -z `printenv | grep ANDROID_NDK_ROOT` ]
    then
        TARGET_ABI=armeabi-mips64 ./configure-android
        make dep && make clean && make
        cd $CWD
    else
        echo "ndk path not provided" 
     fi
}


echo "PJSIP library builder"

#build_pjsip_armv7
#build_pjsip_arm64
#build_pjsip_mips64
#build_pjsip_x86_64


if [ "x$1" == "x1" ]; then
    echo "Building ARM V7"
    build_pjsip_armv7
elif [ "x$1" == "x2" ]; then
    echo "Building ARM64"
    build_pjsip_arm64
elif [ "x$1" == "x3" ]; then
    echo "Building MIPS64 "
    build_pjsip_mips64
elif [ "x$1" == "x4" ]; then
    echo "Building X86 64"
    build_pjsip_x86_64	
fi
