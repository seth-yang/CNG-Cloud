package com.cng.cloud.web;

import com.cng.cloud.data.Event;
import com.cng.cloud.data.Result;
import com.cng.cloud.service.IEventService;
import com.cng.cloud.util.EventListTypeToken;
import com.google.gson.Gson;
import org.dreamwork.gson.GsonHelper;
import org.dreamwork.persistence.ServiceFactory;
import org.dreamwork.util.IOUtil;
import org.dreamwork.util.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by game on 2016/2/23
 */
public class UploadServlet extends HttpServlet {
    private static final String CONTENT_TYPE = "applications/json";
    private Type type;

    @Override
    public void init () throws ServletException {
        super.init ();
        type = new EventListTypeToken ().getType ();
    }

    @Override
    protected void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String hostId = request.getQueryString ();
        if (StringUtil.isEmpty (hostId)) {
            response.sendError (HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Result<Object> result = new Result<> ();
        Gson g = GsonHelper.getGson (true, true);
        try {
            byte[] buff = IOUtil.read (request.getInputStream ());
            String content = new String (buff, "utf-8");
            List<Event> list = g.fromJson (content, type);
            for (Event e : list)
                e.setHostId (hostId);

            IEventService service = (IEventService) ServiceFactory.getBean ("eventService");
            service.save (list);

            result.setState ("ok");
        } catch (Exception ex) {
            result.setState ("fail");
            ex.printStackTrace ();
        }
        response.setContentType (CONTENT_TYPE);
        response.getWriter ().write (g.toJson (result));
    }
}