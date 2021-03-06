package dev.vmykh.labs.taskmanager;

import dev.vmykh.labs.taskmanager.helpers.JsonTransformer;
import dev.vmykh.labs.taskmanager.model.OS;

import static spark.Spark.get;
import static spark.Spark.post;

/**
 * Created by mrgibbs on 07.05.15.
 */
public class ProcsResource {
    private OS os;
    private JsonTransformer jsonTransformer;
    public ProcsResource(OS os) {
        this.os = os;
        jsonTransformer = new JsonTransformer();
        setupEndpoints();
    }

    private void setupEndpoints() {

//        get("/bingo", (req, res) -> "Hello Mr Jones!!");

        get("/currentProcessesInfo", "application/json", (req, res) -> {
            return os.getCurrentProcsInfo();
        }, jsonTransformer);

        post("/killProcess/:pid", (req, res) -> {
            os.killProcess(Integer.parseInt(req.params(":pid")));
            System.out.println("Kill Request received. Process id:  = " + req.params(":pid"));
            return "";
        });
    }
}
