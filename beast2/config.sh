#!/bin/sh

## config the beast path and lib 

DIR_NAME="beast277"
# Export environment variables
#export DIR_NAME
ROOT_DIR="$HOME/LPhyBeastTest"

# lphy lib
LPHY_LIB="$ROOT_DIR/lphy-studio-1.6.1/lib"

# must define $BEAST before everything
BEAST="$ROOT_DIR/$DIR_NAME"
#export BEAST
BEAST_LIB="$BEAST/lib"
#export BEAST_LIB

# backup beast lib folder to make easy deleting the installed b2 pkg

LIB_DIR_BAK="$BEAST/lib-bak"
#export LIB_DIR_BAK

if [ ! -d $LIB_DIR_BAK ]; then
  cp -r $BEAST_LIB $LIB_DIR_BAK
fi

