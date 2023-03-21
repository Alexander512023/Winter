# Winter Framework
I maid this project for educational purposes. The main goal was to manage different tecnologies, such as Java concurrencie, REST API, caching, logging, JSON serialization by implementing them as POJO.
### Basics
This project works like a framework, so it don't contain any domain logic and should be called by corresponding client code.
Functionality includes:
1. HTTP server
* Multithreaded server that allows processing client requests in synchronous mode
* Supports REST API and JSON
* Connects to client code via implementation of com.goryaninaa.winter.web.http.server.Controller 
  interface
2. Concurrent cache
* Works between repository and DAO layers
* Supports automatic cleanup
* Maintains the size within the given parameter
3. Concurrent logger
* Works according to standard Java logging practices which I was able to find in Internet
* Implements producer-concumer design in order to solve concurrency problems with logwriting from different threads

### Development
Project is Maven build and dependes on JUnit, whitch provide testing functionality. It consists only from Java code.
Directory structure is based on Maven quickstart archetype. Packages reflect the basic functionality and are devided into three branches: server, cache, logger.
All code is covered with unit-tests. In test branch you could find the examples of how framework can be used by client side.

[Javadoc](https://alexander512023.github.io/WinterJavadoc/) - review for further details about 
main packages, classes and interfaces.

Project receives a series of properties via corresponding Properties object, which you should configure on client side(values entered for example):

        Winter.HttpServer.Port=8080 
        Winter.HttpServer.ThreadsNumber=4
        Winter.LoggingMech.logsDirPathUrl=/Users/alexandrgoryanin/temp/bank/logs
        Winter.LoggingMech.bytesPerFile=100000
        Winter.LoggingMech.amountOfLogs=10
        Winter.LoggingMech.Level=DEBUG
        Winter.Cache.size=5

Further I'll present example of main method, which runs the application, based on this Framework:

        public static void main(String[] args) throws IOException {
                Properties properties = new Properties();
                properties.load(App.class.getResourceAsStream("/config.properties"));
                LoggingMech.getInstance().apply(properties);
                LoggingMech.getInstance().startLogging();
                ApplicationAssembler applicationAssembler = new ApplicationAssembler(properties); //ApplicationAssembler - assemble your client side application
                HttpServer httpServer = new HttpServer(properties, applicationAssembler.getControllers());
                httpServer.start();
        }}
The project code was inspected with a series of linters, among which were built-in INTELLIJ IDEA, 
SonarLint, CodeStyle, PMD.

### Deployment
Build the project simply by getting source code from github and typing mvn clean install comand. The dependencie on this framework should be among dependencies in your client code POM.
