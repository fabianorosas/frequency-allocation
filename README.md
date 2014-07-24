Start the main process with:

```
java -jar main.jar <topology_file> [n]
```

where *n* is the number of APs that will be instantiated externally.

Case *n* > 0, run *n* processes in a separate terminal:

```
java -jar ap.jar <ap_number> <serverIP:port>
```

The APs will log to apX.log files.