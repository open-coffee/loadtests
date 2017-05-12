CoffeeNet load testing
===============================

To test it out, simply execute the following command:

```bash
./mvnw gatling:execute
```
to execute all simulations.

```bash
./mvnw gatling:execute -Dgatling.simulationClass=${pathToSimulation}
```

e.g.
```bash
./mvnw gatling:execute -Dgatling.simulationClass=coffee.synyx.loadtests.auth.AuthLoginLogoutSimulation
```

You can find more information about gatling in their [documentation](http://gatling.io/docs/current/)


## Recorder

> The Gatling Recorder helps you to quickly generate scenarios, by either acting as a HTTP proxy between the browser and the HTTP server or converting HAR (Http ARchive) files. Either way, the Recorder generates a simple simulation that mimics your recorded navigation.

More information at [the recorder documentation of gatling](http://gatling.io/docs/current/http/recorder/)

To start just type:

```bash
./mvnw gatling:recorder
```
