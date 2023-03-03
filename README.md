# HttpServer
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
