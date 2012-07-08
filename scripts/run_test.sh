#param1: assignment name
#param2: StudentId

set -o errexit
path_to_assignment=assignments/$1/$2/

# compile the assignment
echo "Compiling Assignment "$1" for user "$2
scripts/compile_java_files.sh lib/junit-4.11-SNAPSHOT-20120416-1530.jar $path_to_assignment*.java


# compile the assignment
echo "Compiling All Tests"
scripts/compile_java_files.sh lib/junit-4.11-SNAPSHOT-20120416-1530.jar:$path_to_assignment tests/*.java

#run the test
echo "Running"
scripts/run_java_files.sh tests/:lib/junit-4.11-SNAPSHOT-20120416-1530.jar:$path_to_assignment "org.junit.runner.JUnitCore Test$1"

# compile the assignment
#echo "Compiling All Tests"
#scripts/compile_java_files.sh lib/junit-4.11-SNAPSHOT-20120416-1530.jar tests/*.java
