About the Service
This service provides simple implementation of create account, get account and transfer money.
You can call some REST endpoints defined in com.dws.challenge.web.AccountsController on port 18080.

To make this code production ready few things can be improved -

1) There must multiple yaml or properties file based on the env code is running i.e QA, DEV, local and Production.

2) Built-in monitoring, health checks, and metrics make it easier to create production-ready applications.

3) Authentication and authorisation should be provided to rest end points.

4) In springboot application, account level locking should be replaced with distributed cache like redis.