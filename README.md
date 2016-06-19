# TEMPLATE-SITE 0.1.1
The **Template Site** is a project created by [Tetrao](https://tetrao.eu/), a technology startup company specialized in
Internet intelligent process automation. Our mission is enabling connections between people and businesses radically
faster in an efficient and cost-effective way.

This is an internal project and we decided to publish it to promote the use of a lot of open source projects we
love and we use every day and make our job easier.

You can see this project running here: [https://template-site.tetrao.eu/](https://template-site.tetrao.eu/).

**Template Site** is an skeleton of a web application based on:

* [Scala 2.11.8 as programming language](http://www.scala-lang.org/)
* [SBT 0.13.11 as build tool](http://www.scala-sbt.org/)
* [Play Framework 2.5.4 as web application framework](https://www.playframework.com/)
* [Play2-auth 0.14.2 as authentication module for Play](https://github.com/t2v/play2-auth/)
* [PostgreSQL 9.5 as database](http://www.postgresql.org)
* [Slick 3.1.1 as database access layer](http://slick.lightbend.com)
* [Slick-pg 0.14.1 extending slick for support PostgreSQL data types](https://github.com/tminglei/slick-pg)
* [Foundation 6.2.3 as front-end framework](http://foundation.zurb.com)

The application is a website where you have to login to enter in a restricted page. The restricted page is a messages
page, where you can see, add or delete messages that are shared beetween all the users. Also is possible to edit your
own user and, if you are and admin user, you can add other non admin users.

Default users are `admin@tetrao.eu`, the admin user, and `bob@tetrao.eu` a *normal* user. Both users have the same
password and it is `password`.

## Getting the project
As usual:

    git clone https://github.com/tetrao-eu/template-site.git

The following commands are written for a GNU/Linux environment (**Debian**, if you want to know). So please adapt it
to your system if it is necessary. Also they are running supposing that you are locating inside the folder
`template-site`, the root of this project.

## Database

### Database setup
Check the [PostgreSQL website](http://www.postgresql.org/download/) to instalation instructions.

Create a cluster, an user and a database:

    sudo -u postgres pg_createcluster -p 55555 --start 9.5 templatesite_cluster
    sudo -u postgres psql -p 55555 -c "CREATE USER templatesite_user PASSWORD '123';"
    sudo -u postgres psql -p 55555 -c "CREATE DATABASE templatesite_db WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'en_US.utf8' LC_CTYPE = 'en_US.utf8';"
    sudo -u postgres psql -p 55555 -c "ALTER DATABASE templatesite_db OWNER TO templatesite_user;"

**IMPORTANT!** If you change any database config value, please remember to update the config file
`conf/application.conf`

Next create the tables and add some data to the database using the script `conf/database.sql`

    sudo -u postgres psql -p 55555 -d templatesite_db -f conf/database.sql

This script will create an account table with two users and a message table with one message.

### Database mapping code
The file `models.db.Tables.scala` contains the database mapping code. It has been generated running the main class
`utils.db.SourceCodeGenerator`. If you want to regenerate the database mapping code for any reason, check the
config file `conf/application.conf` and run:

    sbt tables

## SBT

To run the project execute:

    sbt run

And open a browser with the url [http://localhost:9000](http://localhost:9000)

The plugin [sbt-updates](https://github.com/rtimush/sbt-updates) is installed (see `plugins.sbt`). To check
if all the dependencies are up to date, it is necessary to execute:

    sbt dependencyUpdates

## License
Licensing conditions (MIT) can be found in `LICENSE` file.
