# param1: classpath
# param2: path and files to compile

set -o errexit
echo "javac -cp $1 $2"
javac -cp $1 $2
