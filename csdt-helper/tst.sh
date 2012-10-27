cmd=${1:-listcs}
res=${2:-@ALL_ASSETS}
sh csdt.sh http://localhost:8080/cs/ContentServer cmd="$cmd" username=fwadmin password=xceladmin fromSites=avisports resources="$res"
