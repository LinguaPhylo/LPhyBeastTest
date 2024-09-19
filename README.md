# LPhyBeastTest in NeSI

Comprehensive integration tests for LPhyBeast + BEAST2 tutorials

## The URLs of the pre-released versions

[prerelease.xml](beast2/prerelease.xml) defines the dependencies of lphybeast and LPhyBeastExt,
it also can be used to download their SNAPSHOT versions from the Maven repo.
These SNAPSHOT versions map to the different versions for testing.
The complex tests, therefore, can be done with minimised manual process using NeSI, 
and also are not engaging with the formal BEAST release framework.  


## Folder structure, setup beast2 and lphy

- working dir is `$HOME/LPhyBeastTest` in NeSI.

- install beast2.7.* under that folder, for example:

```bash
tar -xf BEAST.v2.7.*.Linux.x86.tgz
```

- install lphy 1.6.* under this folder, for example:

```bash
unzip lphy-studio-1.6.*.zip
```

- install all beast2 packages under `$BEAST_LIB`.

- make modified [beauti.properties](beast2/beauti.properties) available under that folder.


## Testing pipeline

1. `ssh mahuika`

2. `cd $HOME/LPhyBeastTest` and `scp` all required files by alphabetic order into that folder:

   - __beast.txt__, which is a NeSI job template for beast runs, see also `runBEAST.sh`;
   - __beauti.properties__, which points to the URL of pre-released versions;
   - __config.sh__, which sets up required environment variables; 
   - __install-pkgs.sl__, which submit a job to install the testing version of lphybeast and LPhyBeastExt, and their dependencies; 
   - __runBEAST.sh__, which submits 4 jobs (one for each tutorial) to run beast XML, and analyses log and summarises trees.

### Setup lphybeast and create XMLs

3. `rm install-pkgs.txt`, which contains the installation and lphybeast message.

4. `sbatch install-pkgs.sl`, which installs lphybeast, LPhyBeastExt and their dependencies, then call lphybeast to saves XMLs into the subfolder `xmls`.

5. `ls -l xmls` or alternatively `vi install-pkgs.txt`, after the job is done.

### Run XMLs and analyse log

6. run `runBEAST.sh` under $HOME/LPhyBeastTest.

7. after all jobs are done, transfer the entire subfolder `xmls` to the local, such as `scp -r mahuika:$HOME/LPhyBeastTest/xmls/ $LOCAL/LPhyBeastTest/lphybeast`. The subfolder should contain all beast results. 

8. run all JUnit tests under `lphybeast.tutorial`, to validate the estimated parameter values.

### TODO

- [Building pipelines using slurm dependencies](https://hpc.nih.gov/docs/job_dependencies.html)

