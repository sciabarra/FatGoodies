cmd=${1:?invoke script required}
sel=${2:-@ALL_ASSETS}
force=${3:-n}
sh $cmd listcs $sel | tail +5 | grep -F '|||' | awk '{ print $1 ":" $3 }' | while read a
do 
  out=out/${1%%.sh}/${a/:/\/}
  if test "$force" = "-f" ; then rm $out ; fi
  if test -e $out
  then echo "--- $a skipped ---"
  else
      echo "+++ $a exporting +++"
      mkdir -p $(dirname $out)
      sh $cmd export $a | tee $out
  fi
done
