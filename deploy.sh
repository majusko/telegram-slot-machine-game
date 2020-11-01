git pull origin main
rm -rf target
mvn package -Dmaven.test.skip=true
pkill -f java
nohup java -jar target/service.jar > log.log 2>&1 &