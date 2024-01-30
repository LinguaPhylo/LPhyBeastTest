#!/bin/sh
#SBATCH -J install pkgs		# The job name
#SBATCH -A nesi00390		# The account code
#SBATCH --time=3:00:00         # The walltime
#SBATCH --mem=4G 	        # in total
#SBATCH --cpus-per-task=2       # OpenMP Threads
#SBATCH --ntasks=1              # not use MPI
#SBATCH --hint=multithread      # A multithreaded job, also a Shared-Memory Processing (SMP) job
#SBATCH -D ./			# The initial directory
#SBATCH -o B2PKG.txt		# The output file
#SBATCH -e B2PKG.txt		# The error file


#module load beagle-lib/4.0.0-GCC-11.3.0
module load Java/17

### 1. load config

./config

### 2. install base

# rm lib folder where all b2 pkgs are installed
rm -r $BEAST_LIB
cp -r $BEAST/lib-bak $BEAST_LIB

# customised beauti.properties
cp beauti.properties $BEAST_LIB

# have to install base manually
mkdir $BEAST_LIB/BEAST.base
cp $BEAST_LIB/packages/BEAST.base.version.xml $BEAST_LIB/BEAST.base/version.xml
mkdir $BEAST_LIB/BEAST.base/lib
cp $BEAST_LIB/packages/BEAST.base*.jar $BEAST_LIB/BEAST.base/lib

ls -la $BEAST_LIB/BEAST.base/lib

# install lphybeast, lphybeast-ext
ls -la $BEAST_LIB


### 3. install lphybeast and LPhyBeastExt

CMD_PKGMG="java -Xms256m -Xmx8g -Dbeast.user.package.dir=$BEAST_LIB -Djava.library.path=$BEAST_LIB -Duser.language=en -cp $BEAST_LIB/launcher.jar beast.pkgmgmt.PackageManager"

$CMD_PKGMG -add lphybeast

$CMD_PKGMG -add LPhyBeastExt

### 4. list pkg

ls -la $BEAST_LIB

$CMD_PKGMG -list

