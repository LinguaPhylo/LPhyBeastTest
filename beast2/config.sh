#!/bin/sh

## config the beast path and lib 

DIR_NAME="beast276"
# Export environment variables
#export DIR_NAME

# must define $BEAST before everything
BEAST="$HOME/LPhyBeastTest/$DIR_NAME"
#export BEAST
BEAST_LIB="$BEAST/lib"
#export BEAST_LIB

# backup beast lib folder to make easy deleting the installed b2 pkg

LIB_DIR_BAK="$BEAST/lib-bak"
#export LIB_DIR_BAK

if [ ! -d $LIB_DIR_BAK ]; then
  cp -r $BEAST_LIB $LIB_DIR_BAK
fi

