package com.goit.timeservlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static com.goit.timeservlet.utils.ServletUtils.TIMEZONE_PARAMETER;
import static com.goit.timeservlet.utils.ServletUtils.*;

@WebServlet("/time")
public class UserTimeServlet extends HttpServlet {
    public static final String DEFAULT_TZ ="UTC";
    private static final String DATETIME_ZONE_PATTERN = "yyyy-MM-dd  HH:mm:ss z";
    public static final String LAST_TIMEZONE_COOKIE_NAME = "lastTimezone";
    private transient TemplateEngine engine;
    @Override
    public void init() throws ServletException{
        //initialize thymeleaf
        engine=new TemplateEngine();
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("./templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Context simpleContext = new Context(req.getLocale());
        Map<String, Object> variables = new LinkedHashMap<>();

        String timezone = replaceGapWithPlusInTimeZoneParamFromURL(getTimeZoneParameter(req)); // try Optional
        if(!timezone.equals(DEFAULT_TZ)){
            addLastTimeZoneToCookie(resp,timezone); //saves timezone to cookie
        }
        variables.put("timeZone",timezone );
        variables.put("dateTime", getDateTime(timezone));


        simpleContext.setVariables(variables);
        engine.process("time", simpleContext, resp.getWriter());
        resp.getWriter().close();
    }

    private String getDateTime(String stringZoneID){

        ZonedDateTime zonedDateTime;
        try{
            ZoneId zoneId = ZoneId.of(replaceGapWithPlusInTimeZoneParamFromURL(stringZoneID));
            zonedDateTime = LocalDateTime.now(zoneId).atZone(zoneId);
        }catch(DateTimeException e){
            return "From doGet: Invalid time zone:"+stringZoneID;
        }
        // return formatted as string
        return zonedDateTime.format(DateTimeFormatter.ofPattern(DATETIME_ZONE_PATTERN));
    }

    private String getTimeZoneParameter(HttpServletRequest req){

        //String[] params = new String[1]; // the same as String but String array type
       // params[0]=req.getParameter("timezone");

        String timeZoneParam = req.getParameter( TIMEZONE_PARAMETER);
        if (timeZoneParam != null && !timeZoneParam.isEmpty()) {
            return timeZoneParam;
        }
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            Optional<Cookie> lastTimeZoneCookie = Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals(LAST_TIMEZONE_COOKIE_NAME))
                    .findFirst();

            if (lastTimeZoneCookie.isPresent()) {
                return lastTimeZoneCookie.get().getValue();
            }
        }

        return DEFAULT_TZ;

//--------------------------------------------------------------------
       /* if(Optional.ofNullable( params[0]).isEmpty()) {
           Optional<Cookie[]> optionalCookies = Optional.ofNullable(cookies);
            if(optionalCookies.isPresent()){
                params[0] =  (String) (Stream.of(optionalCookies.get())
                        .filter(cookie -> cookie.getName().equals(LAST_TIMEZONE_COOKIE_NAME))
                        .map(Cookie::getValue)
                        .toArray())[0];
                return params[0];
            } else {
                return DEFAULT_TZ;
            }
        }else{
            return params[0];
        }*/
    }

    private void addLastTimeZoneToCookie(HttpServletResponse resp,String timeZone){
        resp.addCookie(new Cookie(LAST_TIMEZONE_COOKIE_NAME,
                        replaceGapWithPlusInTimeZoneParamFromURL(timeZone))) ;
    }



}
