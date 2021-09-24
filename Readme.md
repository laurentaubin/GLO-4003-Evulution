# Ev-ul-tion - Team Diesel

## Prod break counter

- Benjamin: 0
- Laurent: 0
- Manef: 0
- Max: 0
- Mohammed: 0
- Sean: 0
- Toma: 0

## Run the app locally

* With Java 13 and Maven installed and the `JAVA_HOME` environment variable set;
    * On Windows: run `start.bat`
    * On Linux/OSX: run `start.sh`
    * In an IDE: run the `Evulution` class as a `Java Application`
* Once started, the app exposes the following endpoints:
    * http://localhost:8080/customers

## Run the app on heroku

> > > > > > > main

* Install the Heroku CLI from [here](https://devcenter.heroku.com/articles/heroku-cli)
* Run `sh deploy_main.sh` to deploy the `main` branch on heroku
    * You might need to do a `heroku login` to login before running the script
* Run `sh deploy_current_branch.sh` to deploy the branch on your `HEAD` to heroku
* Our heroku app is located at [ev-ul-tion](http://ev-ul-tion.herokuapp.com)

## Contributors

- Laurent Aubin
- Mohammed Bouheraoua
- Sean Canning
- Toma Gagne
- Benjamin Girard
- Maxime Sgobba
- Manef Zahra
