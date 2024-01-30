#!/bin/sh

### 1. load config

. $HOME/LPhyBeastTest/config.sh

echo "set BEAST = $BEAST"

### 2. check XMLs

XML_DIR="$ROOT_DIR/xmls"

echo "XML_DIR = $XML_DIR , which has the following XMLs : "
echo $(ls "$XML_DIR/*.xml")


### 3. run beast

TUTORIAL="h5n1"
FILE="$XML_DIR/$TUTORIAL.xml"
if [ -f $FILE ]; then
   # sed cannot accept / in the s command, pre-process path to escape the slashes
   xmlfile=$(echo "$FILE" | sed 's_/_\\/_g')
   echo "xmlfile = $xmlfile"

   sed "s/BEAST_DIR/$BEAST/g;s/XML/$xmlfile/g" ./beast.txt > "$TUTORIAL.sl"
   sbatch "$TUTORIAL.sl" 
else
   echo "Error: cannot find $FILE !"
fi


TUTORIAL="RSV2"
FILE="$XML_DIR/$TUTORIAL.xml"
if [ -f $FILE ]; then
   # sed cannot accept / in the s command, pre-process path to escape the slashes
   xmlfile=$(echo "$FILE" | sed 's_/_\\/_g')
   echo "xmlfile = $xmlfile"

   sed "s/BEAST_DIR/$BEAST/g;s/XML/$xmlfile/g" ./beast.txt > "$TUTORIAL.sl"
   sbatch "$TUTORIAL.sl" 
else
   echo "Error: cannot find $FILE !"
fi

TUTORIAL="hcv_coal"
FILE="$XML_DIR/$TUTORIAL.xml"
if [ -f $FILE ]; then
   # sed cannot accept / in the s command, pre-process path to escape the slashes
   xmlfile=$(echo "$FILE" | sed 's_/_\\/_g')
   echo "xmlfile = $xmlfile"

   sed "s/BEAST_DIR/$BEAST/g;s/XML/$xmlfile/g" ./beast.txt > "$TUTORIAL.sl"
   sbatch "$TUTORIAL.sl" 
else
   echo "Error: cannot find $FILE !"
firor: cannot find $FILE !"
fi


TUTORIAL="h3n2"
FILE="$XML_DIR/$TUTORIAL.xml"
if [ -f $FILE ]; then
   # sed cannot accept / in the s command, pre-process path to escape the slashes
   xmlfile=$(echo "$FILE" | sed 's_/_\\/_g')
   echo "xmlfile = $xmlfile"

   sed "s/BEAST_DIR/$BEAST/g;s/XML/$xmlfile/g" ./beast.txt > "$TUTORIAL.sl"
   sbatch "$TUTORIAL.sl" 
else
   echo "Error: cannot find $FILE !"
fi

