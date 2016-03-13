package com.cng.cloud.web;

import com.cng.cloud.util.Tools;
import org.dreamwork.misc.Base64;
import org.dreamwork.secure.IKeyFetcher;
import org.dreamwork.secure.SecureContext;
import org.dreamwork.secure.SecureUtil;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.PrivateKey;
import java.util.UUID;

/**
 * Created by game on 2016/3/14
 */
public class CardServlet extends HttpServlet {
    @Override
    protected void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String queryString = request.getQueryString ();
        System.out.println ("queryString = " + queryString);
        int cardNo = (int) (Math.random () * 1000000);
        byte[] buff = Tools.intToBytes (cardNo);

        ServletContext application = getServletContext ();
        SecureContext context = (SecureContext) application.getAttribute ("org.dreamwork.key.context");
        IKeyFetcher fetcher = (IKeyFetcher) application.getAttribute ("org.dreamwork.key.fetcher");
        SecureUtil util = new SecureUtil (context);
        try {
            buff = util.encrypt (buff, fetcher.getPrivateKey (null));
        } catch (Exception e) {
            e.printStackTrace ();
        }

        response.getOutputStream ().write (buff);
    }

    @Override
    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        createCardWriter (response);
    }

    private void createCardWriter (HttpServletResponse response) throws ServletException, IOException {
        UUID uuid = UUID.randomUUID ();
        String token = uuid.toString ();
        byte[] data = token.getBytes ();

        ServletContext application = getServletContext ();
        SecureContext context = (SecureContext) application.getAttribute ("org.dreamwork.key.context");
        IKeyFetcher fetcher = (IKeyFetcher) application.getAttribute ("org.dreamwork.key.fetcher");
        try {
            PrivateKey privateKey = fetcher.getPrivateKey (null);
            SecureUtil util = new SecureUtil (context);
            byte[] encrypted = util.encrypt (data, privateKey);
            String base64 = new String (Base64.encode (encrypted));

            ByteArrayOutputStream baos = new ByteArrayOutputStream ();
            baos.write (data);
            baos.write ("\r\n".getBytes ());
            baos.write (base64.getBytes ());
            byte[] buff = baos.toByteArray ();

            response.setContentType ("application/octet-stream");
            response.setHeader ("Content-Disposition", "attachment; filename=\"" + token + ".token" + "\"");
            response.setHeader ("Content-Length", String.valueOf (buff.length));
            response.setHeader ("X-Powered-By", "Jasmine2");
            baos.writeTo (response.getOutputStream ());
        } catch (IOException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServletException (ex);
        }
    }
}