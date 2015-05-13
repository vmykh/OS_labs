package dev.vmykh.labs.taskmanager;

import static spark.Spark.get;

/**
 * Created by mrgibbs on 07.05.15.
 */
public class ProcsResource {

    public ProcsResource() {
        setupEndpoints();
    }

    private void setupEndpoints() {
        get("/bingo", (req, res) -> "Hello Mr Jones!!");
    }
}
