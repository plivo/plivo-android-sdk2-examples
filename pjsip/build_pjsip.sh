#!/bin/bash

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

PJPROJECT_FILE_NAME="pjproject-2.8.tar.bz2"
PJPROJECT_SRC_DIR="pjproject-2.8"

CONFIG_SITE_H="$PJPROJECT_SRC_DIR/pjlib/include/pj/config_site.h"
CWD=`pwd`
SSLPATH=$2

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

build_pjsip_armeabi()
{
    echo $SSLPATH
    rm -rf $CWD/$PJPROJECT_SRC_DIR
    prep_pjsip_source
    copy_config_site_h

    echo "Building armeabi libs..."
    cd ./$PJPROJECT_SRC_DIR
    #android_ndk_r9 should be present in path environment variable
    if [ ! -z `printenv | grep ANDROID_NDK_ROOT` ]
    then
        ./configure-android --with-ssl=$1
        make dep && make clean && make
        cd $CWD
    else
        echo "ndk path not provided" 
     fi
}


build_pjsip_armv7()
{
    rm -rf $CWD/$PJPROJECT_SRC_DIR
    prep_pjsip_source
    copy_config_site_h

    echo "Building armv7 libs..."
    export TARGET_ABI="armeabi-v7a"
    cd ./$PJPROJECT_SRC_DIR
    #android_ndk_r9 should be present in path environment variable
    if [ ! -z `printenv | grep ANDROID_NDK_ROOT` ]
    then
        TARGET_ABI=armeabi-v7a ./configure-android --use-ndk-cflags --with-ssl=$1
        make dep && make clean && make
        cd $CWD
    else
        echo "ndk path not provided" 
     fi
}

build_pjsip_arm64v8a()
{
    rm -rf $CWD/$PJPROJECT_SRC_DIR
    prep_pjsip_source
    copy_config_site_h

    echo "Building arm64v8a libs..."
    export TARGET_ABI="arm64-v8a"
    cd ./$PJPROJECT_SRC_DIR
    if [ ! -z `printenv | grep ANDROID_NDK_ROOT` ]
    then
        TARGET_ABI=arm64-v8a ./configure-android --use-ndk-cflags --with-ssl=$1
        make dep && make clean && make
        cd $CWD
    else
        echo "ndk path not provided" 
     fi
}


build_pjsip_x86()
{
    rm -rf $CWD/$PJPROJECT_SRC_DIR
    prep_pjsip_source
    copy_config_site_h

    echo "Building x86 libs..."
    export TARGET_ABI="x86"
    cd ./$PJPROJECT_SRC_DIR
    
    if [ ! -z `printenv | grep ANDROID_NDK_ROOT` ]
    then
        TARGET_ABI=x86 ./configure-android --use-ndk-cflags --with-ssl=$1
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
    export TARGET_ABI="x86_64"
    cd ./$PJPROJECT_SRC_DIR
    
    if [ ! -z `printenv | grep ANDROID_NDK_ROOT` ]
    then
        TARGET_ABI=x86_64 ./configure-android --use-ndk-cflags --with-ssl=$1
        make dep && make clean && make
        cd $CWD
    else
        echo "ndk path not provided" 
     fi
}

build_pjsip_mips()
{
    rm -rf $CWD/$PJPROJECT_SRC_DIR
    prep_pjsip_source
    copy_config_site_h

    echo "Building mips libs..."
    export TARGET_ABI="mips"
    cd ./$PJPROJECT_SRC_DIR
    #android_ndk_r9 should be present in path environment variable
    if [ ! -z `printenv | grep ANDROID_NDK_ROOT` ]
    then
        TARGET_ABI=mips ./configure-android --use-ndk-cflags --with-ssl=$1
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

    echo "Building mips64 libs..."
    export TARGET_ABI="mips64"
    cd ./$PJPROJECT_SRC_DIR
    #android_ndk_r9 should be present in path environment variable
    if [ ! -z `printenv | grep ANDROID_NDK_ROOT` ]
    then
        TARGET_ABI=mips64 ./configure-android --use-ndk-cflags --with-ssl=$1
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



if [ "$1" == "armeabi-v7a" ]; then
	echo "Building ARM V7"
	build_pjsip_armv7 $2
elif [ "$1" == "x86" ]; then
	echo "Building x86"
	build_pjsip_x86 $2
elif [ "$1" == "mips64" ]; then
	echo "Building MIPS64 "
	build_pjsip_mips64 $2
elif [ "$1" == "x86_64" ]; then
	echo "Building X86 64"
	build_pjsip_x86_64 $2	
elif [ "$1" == "arm64-v8a" ]; then
	echo "Building arm64v8a"
	build_pjsip_arm64v8a $2
elif [ "$1" == "mips" ]; then
	echo "Building mips"
	build_pjsip_mips $2
elif [ "$1" == "armeabi" ]; then
	echo "Building mips"
	build_pjsip_armeabi $2
fi
