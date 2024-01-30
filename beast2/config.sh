#!/bin/sh

## config the beast path and lib 

DIR_NAME="beast276"

# must define $BEAST before everything
BEAST="$HOME/LPhyBeastTest/$DIR_NAME"

BEAST_LIB="$BEAST/lib"

echo "set BEAST_LIB = $BEAST_LIB"

# backup beast lib folder to make easy deleting the installed b2 pkg

LIB_DIR_BAK="$BEAST/lib-bak"

if [ ! -d $LIB_DIR_BAK ]; then
  cp -r $BEAST_LIB $LIB_DIR_BAK
fi

