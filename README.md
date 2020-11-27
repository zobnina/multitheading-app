<h1>Multithreading Application Example</h1>

Application based on java.util.concurrent library. 

<h2>DESCRIPTION</h2>

* The manager constantly asks for tasks from user in the console. 
* Report Manager creates report tasks on schedule.
* Developers distribute these tasks among themselves.  
    * If it is a usual task, it requires developing and testing. Testing takes twice a long.
    * If it is a report task, it requires only developing time.
* There are some servers, but it can be not enough for all developers.

Initial properties (number of servers, number of workers, report interval) are located in work.properties

Work results are written to log files. Logging properties are located in log4j2.xml
