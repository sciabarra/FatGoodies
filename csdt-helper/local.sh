#!/bin/bash
# configure here
url=http://localhost:8080/cs/ContentServer
user=fwadmin
pass=xceladmin
site=avisports
# uncomment here
sys=unix
#sys=win
#leave as is here
cmd=${1:-listcs}
res=${2:-@ALL_ASSETS}
sh csdt-$sys.sh $url cmd="$cmd" username=$usr password=$pass fromSites=$site resources="$res"
