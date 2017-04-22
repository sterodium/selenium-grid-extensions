package io.sterodium.extensions.node;


import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.sterodium.extensions.node.closer.BrowserCloser;
import io.sterodium.extensions.node.restart.RestartProcessor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CleanerServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        switch (action) {
            case "restartNode":
                try {
                    new RestartProcessor().restart();
                } catch (Exception e) {
                    resp.sendError(500, e.getMessage());
                }
                break;
            case "closeBrowsers":
                new BrowserCloser().closeAll();
                resp.setStatus(200);
                break;
            case "removeFiles":
                JsonElement body = new JsonParser().parse(req.getReader());
                List<String> remaining = new ArrayList<>();

                for (JsonElement element : body.getAsJsonArray()) {
                    String file = element.getAsString();
                    try {
                        FileUtils.forceDelete(new File(file));
                    } catch (IOException e) {
                        remaining.add(file);
                    }
                }

                if (!remaining.isEmpty()) {
                    StringUtils.join(remaining, ", ");
                    resp.sendError(500, "Unable to delete files" + remaining);
                } else {
                    resp.setStatus(200);
                }
                break;
            default:
                resp.sendError(400, "Unknown action");
        }
    }

}
