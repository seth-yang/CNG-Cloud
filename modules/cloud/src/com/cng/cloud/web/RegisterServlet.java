package com.cng.cloud.web;

import com.cng.cloud.data.Host;
import com.cng.cloud.data.Result;
import com.cng.cloud.service.IHostService;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.dreamwork.gson.GsonHelper;
import org.dreamwork.persistence.Operator;
import org.dreamwork.persistence.Parameter;
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
    private static final Logger logger = Logger.getLogger (RegisterServlet.class);

    @Override
    protected void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (logger.isDebugEnabled ())
            logger.debug ("got a post request");
        byte[] buff = IOUtil.read (request.getInputStream ());
        String content = new String (buff, "utf-8");

        if (logger.isDebugEnabled ())
            logger.debug ("content = " + content);

        Gson g = GsonHelper.getGson ();
        Host host = g.fromJson (content, Host.class);
        IHostService service = (IHostService) ServiceFactory.getBean ("hostService");
        Result <Object> result = new Result<> ();
        if (service.existsHost (host.getMac ())) {
            if (logger.isDebugEnabled ())
                logger.debug ("The mac is saved in database.");
            Host saved = service.get (new Parameter ("mac", host.getMac (), Operator.EQ));
            result.setUserData (saved);
            result.setState ("ok");
        } else {
            if (logger.isDebugEnabled ())
                logger.debug ("The mac is new, save it and generate an uuid for it.");
            try {
                service.save (host);
                result.setState ("ok");
                result.setUserData (host);
            } catch (Exception ex) {
                result.setState ("fail");
            }
        }
        content = g.toJson (result);
        if (logger.isDebugEnabled ())
            logger.debug ("The result is : " + content);
        int length = content.getBytes ("utf-8").length;
        response.setContentType (CONTENT_TYPE);
        response.setContentLength (length);
        response.getWriter ().write (content);
    }
}