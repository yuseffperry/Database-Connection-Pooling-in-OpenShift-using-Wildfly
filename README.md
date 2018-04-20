# Database Connection Pooling in OpenShift using Wildfly 

### Through OpenShift's MySQL Application Terminal, create a password for the user to login

* `mysql -u root`

* `mysql> USE mysql;`

* `mysql> UPDATE user SET authentication_string=PASSWORD("ENTER_PASSWORD_HERE") WHERE User='root';`

* `mysql> FLUSH PRIVILEGES;`

* `mysql> quit`

### Now login to the terminal and create the database

* `mysql -u root -p`

* `mysql> show databases;`

* `mysql> CREATE DATABASE testerDB;`

* `mysql> USE testerDB;`

* `mysql> CREATE TABLE technical_editors (tech_id int(11) NOT NULL AUTO_INCREMENT, tech_username varchar(20) DEFAULT NULL, PRIMARY KEY (tech_id));`

* `mysql> INSERT INTO technical_editors (tech_id, tech_username) VALUES (1, 'Java');`

* `mysql> INSERT INTO technical_editors (tech_id, tech_username) VALUES (2, 'Python');`

* `mysql> SELECT * FROM technical_editors;`

* `mysql> DESC technical_editors;`

### Use the OC (Referenced from https://stackoverflow.com/questions/43912409/what-is-openshift-mysql-enabled-environment-variable-in-openshift-v3)

1. Log into the OC using `oc login -u developer -p`.

2. Select the relevant project.

3. Use `oc get pods` to list all of the pods and find the running WildFly instance.

4. Enter the container's SSH console with `oc rsh <<POD_NAME>>`.

5. Edit the `standalone.xml` file with `vi /wildfly/standalone/configuration/standalone.xml`.

6. Search for the word "datasource" by typing `/datasource` on the vi editor and then press enter.

7. Find the MySQL datasource connection already created and change it accordingly. (To change, press `i` to enter vi's insert mode) (The "mysql:3306" for the connection-url comes from the OpenShift MySQL selector name)

```
<datasource jndi-name="java:jboss/datasources/JdbcPool" pool-name="JdbcPool" enabled="true" use-java-context="true" use-ccm="true">
    <connection-url>jdbc:mysql://mysql:3306/testerDB</connection-url>
    <driver>mysql</driver>
    <pool>
        <flush-strategy>IdleConnections</flush-strategy>
    </pool>
    <security>
        <user-name>root</user-name>
        <password>admin</password>
    </security>
    <validation>
        <check-valid-connection-sql>SELECT 1</check-valid-connection-sql>
        <background-validation>true</background-validation>
        <background-validation-millis>60000</background-validation-millis>
    </validation>
</datasource>
```

8. Press the `Esc` key then enter `:x` to save.

9. Continue to use the `oc rsh <<POD_NAME>>` or the terminal on the web console and enter jboss-cli using the command `/wildfly/bin/jboss-cli.sh`.

10. Type `connect` to log into the WildFly console. There will be a prompt for user's username and password. To gain these credentials, exit the console and create a management user by executing the script `/wildfly/bin/add-user.sh`.

11. Check the data source properties by typing `data-source read-resource --name=<<POOL_NAME>> --include-runtime=true --recursive=true` and follow up on the "enabled" property to make sure it's true.

12. If the datasource is still disabled, enable it by entering the command `data-source enable --name=<<POOL_NAME>>`.

13. Reload WildFly by entering the `reload` command. Once WildFly reboots, enter jboss-cli.sh and log into the console again.

14. Test the datasource connection using the command `/subsystem=datasources/data-source=<<POOL_NAME>>:test-connection-in-pool` in jboss-cli.sh. If the output is:

```
    {
        "outcome" => "success",
        "result" => [true]
    }
```
    
. . . then the datasource is up and running.

15. Check server logs at `/wildfly/standalone/log/server.log` for errors.
