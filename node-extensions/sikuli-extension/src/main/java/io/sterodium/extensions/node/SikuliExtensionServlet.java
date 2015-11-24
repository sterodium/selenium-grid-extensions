package io.sterodium.extensions.node;

import com.google.gson.Gson;
import io.sterodium.extensions.node.rmi.SikuliApplication;
import io.sterodium.rmi.protocol.MethodInvocationDto;
import io.sterodium.rmi.protocol.MethodInvocationResultDto;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.web.servlet.RegistryBasedServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alexey Nikolaenko alexey@tcherezov.com
 *         Date: 20/09/2015
 */
public class SikuliExtensionServlet extends RegistryBasedServlet {

    private static final Gson GSON = new Gson();

    private static final SikuliApplication SIKULI_APPLICATION = new SikuliApplication();

    public SikuliExtensionServlet() {
        this(null);
    }

    public SikuliExtensionServlet(Registry registry) {
        super(registry);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String objectId = getObjectId(req);
        if (objectId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Can't find object ID in URL string");
            return;
        }
        MethodInvocationDto method = GSON.fromJson(req.getReader(), MethodInvocationDto.class);
        MethodInvocationResultDto result = SIKULI_APPLICATION.invoke(objectId, method);
        resp.getWriter().write(GSON.toJson(result));
    }

    private String getObjectId(HttpServletRequest req) {
        String requestURI = req.getRequestURI();
        Pattern pattern = Pattern.compile(".+/([^/]+)");
        Matcher matcher = pattern.matcher(requestURI);
        if (!matcher.matches()) {
            return null;
        }
        return matcher.group(1);
    }

}
