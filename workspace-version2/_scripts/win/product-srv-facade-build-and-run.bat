SET NAME=ProductSrvFacade

cd ../../%NAME%

mvn clean install && java -jar target/%NAME%-1.0.0.jar server config.yml