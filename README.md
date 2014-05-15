Start the main process with:

```
java -jar main.jar <topology_file> [n]
```

where *n* is the number of APs that will be instantiated externally.

Case *n* > 0, run *n* processes in a separate terminal:

```
java -jar ap.jar <ap_number> <serverIP:port>
```

The APs will log to apX.log files. To removed them, use ```rm ap?.log*```.

It is wise to grep the PID of your IDE process with e.g. ```pgrep eclipse``` so that when you run the processes you can kill them with ```pkill java```. If ```pgrep java``` lists the IDE PID as the first one, one could ```for pid in $(pgrep java | tail); do kill $pid; done```.