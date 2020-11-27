#Multithreading Application Example

Application based on java.util.concurrent library. 

##DESCRIPTION

* Manager constantly asks tasks from user in the console. 
* Report Manager creates report tasks on schedule.
* Developers distribute these tasks among themselves.  
    * If it is a usual task, it requires developing and testing. Testing takes twice a long.
    * If it is a report task, it requires only developing time.
* There are some servers, but it can be not enough for all developers.

Initial properties (number of servers, number of workers, report interval) are located in work.properties

Work results are written to log files.
