# Dell R710 IPMI Fan Controller

**Summary**
This Py script is use to take control of the fan speed of the R710 server. This code only been tested in The R710.
this Script is intended to be use with IPMI without the necessity of using a database to collect the servers sensor data.

If you are using Grafana with influxdb to monitor your R710 you will be able to use a second script that i build in Python. you have to modify the script changing the variables to mach yor database and server info.
*See the Link*. [**The Python Script**](https://github.com/dinghy26)

## Requirements

1. install Java JDK(Sudo apt install openjdk-11-jdk-headless)
2. ipmitools (Sudo apt install ipmitools)
3. create cronjob running the script in the interval desired.

this code is really simple and it can be modified to your needs. Pleas message me if you have any Questions.

