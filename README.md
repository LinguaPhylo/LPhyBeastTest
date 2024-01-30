# LPhyBeastTest in NeSI

Comprehensive integration tests for LPhyBeast + BEAST2 tutorials

## Folder structure, setup beast2 and lphy

- Working dir is `$HOME/LPhyBeastTest`

- install beast2.7.* under this folder, for example:

```bash
tar -xvzf BEAST.v2.7.6.Linux.x86.tgz
```

- install lphy under this folder, for example:

```bash
unzip lphy-studio-1.5.0.zip
```

- install all beast2 packages under `$BEAST_LIB`.

- make modified [beauti.properties](./beauti.properties) available under this folder.


## Testing pipeline

1. `ssh mahuika`

2. `cd $HOME/LPhyBeastTest`

### Setup lphybeast and create XMLs

3. `rm *.txt`, this cleans previous output.

4. `sbatch install-pkgs.sl`, which installs all beast2 or lphy packages, and creates XMLs.

5. `vi install-pkgs.txt`, after the job is done.

### Run XMLs and analyse log



