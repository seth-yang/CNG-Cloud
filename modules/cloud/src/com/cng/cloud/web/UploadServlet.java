package com.cng.cloud.web;

import org.dreamwork.util.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by game on 2016/2/23
 */
public class UploadServlet extends HttpServlet {
    @Override
    protected void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String hostId = request.getQueryString ();
        if (StringUtil.isEmpty (hostId)) {
            response.sendError (HttpServletResponse.SC_BAD_REQUEST);
            return;
        }


    }
}