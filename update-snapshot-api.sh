#!/bin/bash

# switches to the gh-pages branch and updates the API of the
# latest core and extensions module snapshots by copying contents
# from
#
#   james/james-.../target/apidocs/
#
# to
# 
#   api/.../snapshot/
#
# commits and pushes changes, and then switches back to master branch

# switch to gh-pages branch
echo \# checking out gh-pages...
git checkout gh-pages
# terminate script if switching branch failed
if [ $? -ne 0 ]; then
    echo "Aborting..."
    exit 1
fi

# copy API folders
echo \# updating james-core API...
cp -R james/james-core/target/apidocs/* api/core/snapshot/
echo \# updating james-extensions API...
cp -R james/james-extensions/target/apidocs/* api/ext/snapshot/

# add new API files
echo \# git adding new API files...
git add api

# commit
echo \# committing changes...
git commit -m "updated APIs"

# push
echo \# pushing...
git push origin gh-pages

# switch back to master branch
echo \# switching back to master...
git checkout master
