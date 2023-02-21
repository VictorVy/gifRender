package persistence;

import org.json.JSONObject;

import java.io.IOException;

/* adapted from CPSC 210 JsonSerializationDemo at https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo */

public interface Writable {
    // EFFECTS: returns this as JSON object
    JSONObject toJson() throws IOException;
}
