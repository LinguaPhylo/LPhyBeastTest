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
ls $BEAST
echo ""

### 2. install base

# rm lib folder where all b2 pkgs are installed
rm -r $BEAST_LIB
cp -r $LIB_DIR_BAK $BEAST_LIB

echo "clean beast libs :"
ls $BEAST_LIB
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
ls $BEAST_LIB
echo ""


### 3. install lphybeast and LPhyBeastExt

JAVA="java -Xms256m -Xmx8g -Duser.language=en"
PKG_MG="beast.pkgmgmt.PackageManager"

$JAVA -Dbeast.user.package.dir="$BEAST_LIB" -Djava.library.path="$BEAST_LIB" -cp "$BEAST_LIB/launcher.jar" $PKG_MG -add lphybeast

$JAVA -Dbeast.user.package.dir="$BEAST_LIB" -Djava.library.path="$BEAST_LIB" -cp "$BEAST_LIB/launcher.jar" $PKG_MG -add LPhyBeastExt

### 4. list pkg

echo ""

$JAVA -Dbeast.user.package.dir="$BEAST_LIB" -Djava.library.path="$BEAST_LIB" -cp "$BEAST_LIB/launcher.jar" $PKG_MG -list

