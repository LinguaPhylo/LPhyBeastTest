#!/bin/sh
#SBATCH -J install-pkgs		# The job name
#SBATCH -A nesi00390		# The account code
#SBATCH --time=3:00:00         # The walltime
#SBATCH --mem=4G 	        # in total
#SBATCH --cpus-per-task=2       # OpenMP Threads
#SBATCH --ntasks=1              # not use MPI
#SBATCH --hint=multithread      # A multithreaded job, also a Shared-Memory Processing (SMP) job
#SBATCH -D ./			# The initial directory
#SBATCH -o install-pkgs.txt		# The output file
#SBATCH -e install-pkgs.txt		# The error file


#module load beagle-lib/4.0.0-GCC-11.3.0
module load Java/17

### 1. load config

. $HOME/LPhyBeastTest/config.sh

echo "set BEAST = $BEAST"
echo "set BEAST_LIB = $BEAST_LIB, LIB_DIR_BAK = $LIB_DIR_BAK"
echo "ls beast dir :"
echo $(ls "$BEAST")
echo ""

### 2. install base

# rm lib folder where all b2 pkgs are installed
rm -r $BEAST_LIB
cp -r $LIB_DIR_BAK $BEAST_LIB

echo "clean beast libs :"
echo $(ls "$BEAST_LIB")
echo ""

# customised beauti.properties
cp beauti.properties $BEAST_LIB

# have to install base manually
mkdir $BEAST_LIB/BEAST.base
cp $BEAST_LIB/packages/BEAST.base.version.xml $BEAST_LIB/BEAST.base/version.xml
mkdir $BEAST_LIB/BEAST.base/lib
cp $BEAST_LIB/packages/BEAST.base*.jar $BEAST_LIB/BEAST.base/lib


# check base
echo "check if base is installed : "
echo $(ls "$BEAST_LIB")
echo ""


### 3. install lphybeast and LPhyBeastExt

JAVA="java -Xms256m -Xmx8g -Duser.language=en"
PKG_MG="beast.pkgmgmt.PackageManager"

$JAVA -Dbeast.user.package.dir="$BEAST_LIB" -Djava.library.path="$BEAST_LIB" -cp "$BEAST_LIB/launcher.jar" $PKG_MG -add lphybeast

$JAVA -Dbeast.user.package.dir="$BEAST_LIB" -Djava.library.path="$BEAST_LIB" -cp "$BEAST_LIB/launcher.jar" $PKG_MG -add LPhyBeastExt

# require Babel for h5n1 estimating transitions
$JAVA -Dbeast.user.package.dir="$BEAST_LIB" -Djava.library.path="$BEAST_LIB" -cp "$BEAST_LIB/launcher.jar" $PKG_MG -add Babel

### 4. list pkg

echo ""

$JAVA -Dbeast.user.package.dir="$BEAST_LIB" -Djava.library.path="$BEAST_LIB" -cp "$BEAST_LIB/launcher.jar" $PKG_MG -list

### 5. set LPHY_LIB

echo ""
echo ""
echo ""

echo "Set LPHY_LIB = $LPHY_LIB"
echo $(ls "$LPHY_LIB")

# prepare dir to store xmls, if exist then delete
XML_DIR="$ROOT_DIR/xmls"
if [ -d $XML_DIR ]; then
  rm -r $XML_DIR
fi
# create xmls dir
echo $(mkdir "$XML_DIR")

# lphy scripts
LPHY_SCRIPTS="$LPHY_LIB/../tutorials"
echo $(ls "$LPHY_SCRIPTS")
echo ""


### 6. create XMLs

# must set -Dpicocli.disable.closures=true using picocli:4.7.0
# otherwise it will throw otherwise java.lang.NoClassDefFoundError: groovy.lang.Closure
MORE_ARG="-Dpicocli.disable.closures=true -Dlauncher.wait.for.exit=true"
APP_LB="beast.pkgmgmt.launcher.AppLauncherLauncher lphybeast"

# h5n1 : 3M chain length
LB_ARGS="-l 3000000 -o $XML_DIR/h5n1.xml $LPHY_SCRIPTS/h5n1.lphy"
$JAVA $MORE_ARG -Dbeast.user.package.dir="$BEAST_LIB" -Djava.library.path="$BEAST_LIB" -cp "$BEAST_LIB/launcher.jar:$LPHY_LIB/*" $APP_LB $LB_ARGS
echo ""

# RSV2 : 20M chain length
LB_ARGS="-l 20000000 -o $XML_DIR/RSV2long.xml $LPHY_SCRIPTS/RSV2.lphy"
$JAVA $MORE_ARG -Dbeast.user.package.dir="$BEAST_LIB" -Djava.library.path="$BEAST_LIB" -cp "$BEAST_LIB/launcher.jar:$LPHY_LIB/*" $APP_LB $LB_ARGS
echo ""

# hcv_coal : 30M
LB_ARGS="-l 30000000 -o $XML_DIR/hcv_coal.xml $LPHY_SCRIPTS/hcv_coal.lphy"
$JAVA $MORE_ARG -Dbeast.user.package.dir="$BEAST_LIB" -Djava.library.path="$BEAST_LIB" -cp "$BEAST_LIB/launcher.jar:$LPHY_LIB/*" $APP_LB $LB_ARGS
echo ""

# h3n2 : 3M
LB_ARGS="-l 3000000 -o $XML_DIR/h3n2.xml $LPHY_SCRIPTS/h3n2.lphy"
$JAVA $MORE_ARG -Dbeast.user.package.dir="$BEAST_LIB" -Djava.library.path="$BEAST_LIB" -cp "$BEAST_LIB/launcher.jar:$LPHY_LIB/*" $APP_LB $LB_ARGS


echo ""
echo ""
echo ""
echo $(ls "$XML_DIR")

mkdir $ROOT_DIR/lphybeast
# mv all lphybeast true logs and trees, except xmls
mv xmls/!(*.xml) lphybeast/
