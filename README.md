# GlicoseBot , Facebook chatbot in Scala and using playframework + slick + redis

## Program intent
This was built on top of play framework and play-slick.
Was based on previous code I built to try out a store inside facebook chat,
and was now modified to be used to register Glicose values from Diabetic patients.

The code has some tests for someone to check if it runs ok, before to running it on a public server.

## Configuration Variables
The environment variables must be set.

1. export REDIS_URL="redis://h:PASSWORD_OF_REDIS_MACHINE@IP_OF_REDIS_MACHINE:PORT_OF_REDIS_MACHINE"
2. export DBURLHOST="jdbc:mysql://IP OF DATABASE/glicosebot"
 glicosebot is the database name of the database
3. export DBUSERNAME="database user"
4. export DBPASSWORD="database password"
5. export PAGE_ACCESS_TOKEN="SOME TOKEN SUPPLIED BY FACEBOOK"
6. export TOKEN_STRING="TOKEN STRING TO THE FACEBOOK"
7. export DOMAIN="domain.localdomain"

# Notice
You must whitelist domains to provide attachments links to the end user.
Or white list any domains you send to a user.


# Running


```bash
sbt run
```

And then go to <http://localhost:9000> to see the running web application.

There are several demonstration files available in this template.

## Controllers

- HomeController.scala:

  Shows how to handle simple HTTP requests.

- AsyncController.scala:

  Shows how to do asynchronous programming when handling a request.

- CountController.scala:

  Shows how to inject a component into a controller and use the component when
  handling requests.

- BotController

  Receives the calls from the HTTP GET and POST

## Components

- Module.scala:

  Shows how to use Guice to bind all the components needed by your application.
  Loads Redis Provider

- Counter.scala:

  An example of a component that contains state, in this case a simple counter.

- ApplicationTimer.scala:

  An example of a component that starts when the application starts and stops
  when the application stops.

## Filters

- Filters.scala:

  Creates the list of HTTP filters used by your application.

- ExampleFilter.scala

  A simple filter that adds a header to every response.
