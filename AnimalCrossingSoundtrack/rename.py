from os import walk
from os import rename
f = []
for (dirpath,dirnames, filenames) in walk("."):
    f.extend(filenames)
    for name in filenames:
        #print name
        rename(name,name.strip())
    break
