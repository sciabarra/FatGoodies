set -x
host=${1:-localhost}
port=${2:-8080}
user=${3:-fwadmin}
pass=${4:-xceladmin}
java -Xms256M -Xmx1024M -Xss1M -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=384M -jar sbt-launch.jar "run $host $port $user $pass"

