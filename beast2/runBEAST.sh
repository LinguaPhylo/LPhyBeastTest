#!/bin/sh

### 1. load config

. $HOME/LPhyBeastTest/config.sh

echo "set BEAST = $BEAST"

### 2. check XMLs

XML_DIR="$ROOT_DIR/xmls"

echo "XML_DIR = $XML_DIR , which has the following XMLs : "
# print each line in new line
ls $XML_DIR/*.xml| while read line; do echo $line; done

### 3. run beast

cd $XML_DIR

seed=777
# sed cannot accept / in the s command, pre-process path to escape the slashes
bdir=$(echo "$BEAST" | sed 's_/_\\/_g')
echo "bdir = $bdir"

tutorial="h5n1"
if [ -f $tutorial.xml ]; then
   sed "s/BEASTDIR/$bdir/g;s/STEM/$tutorial/g;s/SEED/$seed/g" ../beast.txt > "$tutorial.sl"
   sbatch "$tutorial.sl" 
else
   echo "Error: cannot find $tutorial.xml!"
fi


# RSV2long.xml
tutorial="RSV2long"
if [ -f $tutorial.xml ]; then
   sed "s/BEASTDIR/$bdir/g;s/STEM/$tutorial/g;s/SEED/$seed/g" ../beast.txt > "$tutorial.sl"
   sbatch "$tutorial.sl" 
else
   echo "Error: cannot find $tutorial.xml !"
fi

tutorial="hcv_coal"
if [ -f $tutorial.xml ]; then
   sed "s/BEASTDIR/$bdir/g;s/STEM/$tutorial/g;s/SEED/$seed/g" ../beast.txt > "$tutorial.sl"
   sbatch "$tutorial.sl" 
else
   echo "Error: cannot find $tutorial.xml !"
fi


tutorial="h3n2"
if [ -f $tutorial.xml ]; then
   sed "s/BEASTDIR/$bdir/g;s/STEM/$tutorial/g;s/SEED/$seed/g" ../beast.txt > "$tutorial.sl"
   sbatch "$tutorial.sl" 
else
   echo "Error: cannot find $tutorial.xml !"
fi

# suppose back to $ROOT_DIR
cd $ROOT_DIR


