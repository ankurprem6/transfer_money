About the Service -
This service provides simple implementation of create account, get account and transfer money.
You can call the REST endpoints defined in com.dws.challenge.web.AccountsController on port 18080.

To make this code production ready few things can be improved -

1) There must be multiple yaml or properties file based on the code environment i.e QA, DEV, local and Production.

2) Built-in monitoring, health checks, and metrics make it easier to create production-ready applications.

3) Authentication and authorisation should be provided to rest end points.

4) We can't rely on Java locking mechanism at all for transactions Synchronisation as in production there will be multiple applications deployed on infrastructure. So same account transaction on given point  in time can happen on multiple instances. One application instance won't be aware about transaction of other applications instances. This will lead to system inconsistency. So we will have to take support of either database table or distributed cache for maintaining account level lock.

5) Glb configuration should be there for load balancing.

6) For production ready, accounts needs to be store using database.

7) For Observability use Dynatrace and Splunk.