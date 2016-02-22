package com.cng.cloud.web;

import com.cng.cloud.data.Host;
import com.cng.cloud.data.Result;
import com.cng.cloud.service.IHostService;
import com.google.gson.Gson;
import org.dreamwork.gson.GsonHelper;
import org.dreamwork.persistence.ServiceFactory;
import org.dreamwork.util.IOUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by game on 2016/2/23
 */
public class RegisterServlet extends HttpServlet  {
    private static final String CONTENT_TYPE = "application/json;charset=utf-8";

    @Override
    protected void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        byte[] buff = IOUtil.read (request.getInputStream ());
        String content = new String (buff, "utf-8");

        Gson g = GsonHelper.getGson ();
        Host host = g.fromJson (content, Host.class);
        IHostService service = (IHostService) ServiceFactory.getBean ("hostService");
        Result <Object> result = new Result<> ();
        if (service.existsHost (host.getMac ())) {
            result.setState ("ok");
        } else {
            try {
                service.save (host);
                result.setState ("ok");
                result.setUserData (host);
            } catch (Exception ex) {
                result.setState ("fail");
            }
        }
        content = g.toJson (result);
        int length = content.getBytes ("utf-8").length;
        response.setContentType (CONTENT_TYPE);
        response.setContentLength (length);
        response.getWriter ().write (content);
    }
}