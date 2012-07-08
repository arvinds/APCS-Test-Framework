# param1: classpath
# param2: path and files to compile


set -o errexit
echo "java -cp $1 $2"
java -cp $1 $2
