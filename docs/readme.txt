
# to compile and run as desktop application
cd mathQuiz/desktop/src/main/java
javac Tutorial.java
jar cfe mathQuiz.jar Tutorial *.class
rm *.class
java -jar mathQuiz.jar

