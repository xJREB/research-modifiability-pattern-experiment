cd ../CategorySrv/
mvn clean verify sonar:sonar
cd ../CustomerSrv/
mvn clean verify sonar:sonar
cd ../NotificationSrv/
mvn clean verify sonar:sonar
cd ../OrderSrv/
mvn clean verify sonar:sonar
cd ../ProductSrv/
mvn clean verify sonar:sonar
cd ../WarehouseSrv/
mvn clean verify sonar:sonar

# does not exist in workspace version 1:
cd ../OrderProcessSrv/
mvn clean verify sonar:sonar
cd ../ProductSrvFacade/
mvn clean verify sonar:sonar
