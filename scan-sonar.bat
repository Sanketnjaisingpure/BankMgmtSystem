@echo off
echo --------------------------------------
echo 🚀 Building Spring Boot project
echo --------------------------------------
mvn clean verify

echo --------------------------------------
echo 🧪 Running SonarQube scan with Docker
echo --------------------------------------
docker run --rm ^
  -e SONAR_HOST_URL="http://host.docker.internal:9000" ^
  -e SONAR_TOKEN="squ_010b5ed05a17e2d130a97ba01c62202885b75628" ^
  -v "%cd%:/usr/src" ^
  sonarsource/sonar-scanner-cli ^
  "-Dsonar.projectKey=BankMgmtSystem" ^
  "-Dsonar.sources=src" ^
  "-Dsonar.java.binaries=target/classes" ^
  "-Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml"

echo --------------------------------------
echo ✅ Scan complete! View at: http://localhost:9000
pause
