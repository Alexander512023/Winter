# Winter Framework
I maid this project for educational purposes. The main goal was to manage different tecnologies, such as Java concurrencie, REST API, caching, logging, JSON serialization by implementing them as POJO.
### Basics
This project works like a framework, so it don't contain any domain logic and has no sense without corresponding client code.
Functionality includes:
1. HTTP server
* Multi-threaded server that allows processing client requests in synchronous mode
* Supports REST API and JSON format
* Connects with clien code via implementation of Controller interface
2. Concurrent cache
* Works between repository and DAO layers
* Supports automatic cleanup
* Maintains the size within the given parameter
3. Concurrent logger
* Works according to standard Java logging practices which I was able to find in Internet
* Implements producer-concumer design in order to solve concurrency problems with logwriting from different threads

### Development
Its Maven build and dependes on JUnit, whitch provide testing functionality.
### Deployment
T

Works according to standard Java logging practices which I was able to find in Internet. Logger implements producer-concumer design in order to solve concurrency problems with logwriting from different threads.

This is my educational project, implementation of concurrent Cache. Project builds via Maven. Cache has a good concurrency and works between repository and DAO layers, which makes it possible to provide data via cache when it's up to date. When exceeding maximum size cahce automatically cleans. When your application changing cached data, you should not forgot to remove that from cache. This project is based on Strategy GOF pattern to correspond SOLID principles, especially open closed principle. To use cache you should implement DAO, DataAccessStrategy and KeyExtractStrategy interfaces.

This is my educational project. It represents implementation of concurrent HTTP/REST server. It runs as simple application and was written on core Java.
Project builds via Maven. It's kinda mini-framework, so if you choose it to be your server, you should implement Controller interface and use annotations for request mapping pretty similar to Spring MVC-framework.
Just look at following example:
![image](https://user-images.githubusercontent.com/122008693/212290444-8c6d49a5-187f-4ab2-be4b-7a2908d695d7.png)
# Main goals of this project:
1. Thread management;
2. HTTP request handling;
3. JSON deserialization using regular expressions;
4. JSON serialization;
5. Java reflection class and object management;
6. REST API.
# Dependencies
This module has strong dependency on my another educational module - LoggingMech, so you should add that to your local Maven repository if you want to start and check server.
