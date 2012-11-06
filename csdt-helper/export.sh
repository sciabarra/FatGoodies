#!/bin/bash
cmd=${1:?invoke script required}
sel=${2:-@ALL_ASSETS}
force=${3:-n}
bash $cmd $sel listcs |  tail -n +5 | grep -F '|||' | awk '{ print $1 ":" $3 }' | while read a
do 
  out=out/${1%%.sh}/${a/:/\/}
  if test "$force" = "-f" ; then rm $out ; fi
  if test -e $out
  then echo "--- $a skipped ---"
  else
      echo "+++ $a exporting +++"
      mkdir -p $(dirname $out)
      bash $cmd $a export | tee $out
  fi
done
