package com.cng.cloud.web;

import com.cng.cloud.data.*;
import com.cng.cloud.service.IEventService;
import com.cng.cloud.util.EventListTypeToken;
import com.cng.cloud.util.EventTypeTranslator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;
import org.dreamwork.gson.DateTranslator;
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
    private static final Logger logger = Logger.getLogger (UploadServlet.class);
    private static final String CONTENT_TYPE = "application/json";
    private Type type;

    @Override
    public void init () throws ServletException {
        super.init ();
        type = new EventListTypeToken ().getType ();
    }

    @Override
    protected void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String hostId = request.getQueryString ();
        if (logger.isDebugEnabled ())
            logger.debug ("hostId = " + hostId);

        if (StringUtil.isEmpty (hostId)) {
            response.sendError (HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Result<Object> result = new Result<> ();
        Gson g = new GsonBuilder ()
                .excludeFieldsWithoutExposeAnnotation ()
                .registerTypeHierarchyAdapter (
                        java.util.Date.class,
                        new DateTranslator.LongDateTranslator ()
                ).registerTypeAdapter (EventType.class, new EventTypeTranslator ())
                .create ();
/*
        Gson g = GsonHelper.getGson (
                true,
                true,
                new TypeAdapterWrapper (
                        EventType.class, TypeAdapterWrapper.AdapterType.Normal, new EventTypeTranslator ())
        );
*/
        try {
            byte[] buff = IOUtil.read (request.getInputStream ());
            String content = new String (buff, "utf-8");
            if (logger.isDebugEnabled ())
                logger.debug ("the posted content is: " + content);

            UploadData uploaded = g.fromJson (content, type);
            if (uploaded.getData () != null) {
                List<EnvData> list = uploaded.getData ();
                for (EnvData e : list)
                    e.setHostId (hostId);

                IEventService service = (IEventService) ServiceFactory.getBean ("eventService");
                service.save (list);
            }
            if (uploaded.getEvent () != null) {
                List<Event> list = uploaded.getEvent ();
                for (Event e : list)
                    e.setHostId (hostId);

                IEventService service = (IEventService) ServiceFactory.getBean ("eventService");
                service.save (Event.class, list);
            }
            result.setState ("ok");
        } catch (Exception ex) {
            result.setState ("fail");
            ex.printStackTrace ();
        }
        response.setContentType (CONTENT_TYPE);
        response.getWriter ().write (g.toJson (result));
    }
}