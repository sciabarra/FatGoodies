#!/bin/bash
#set -x
# configure here:
url=http://localhost:8080/cs/ContentServer
user=fwadmin
pass=xceladmin
site=avisports
# uncomment here:
sys=unix
#sys=win
#leave as it is here:
cmd=${2:-listcs}
res=${1:-@ALL_ASSETS}
./csdt-$sys.sh $url cmd="$cmd" username=$user password=$pass fromSites=$site resources="$res"
