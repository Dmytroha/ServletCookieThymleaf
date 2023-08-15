package com.goit.timeservlet;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

import static com.goit.timeservlet.utils.ServletUtils.TIMEZONE_PARAMETER;
import static com.goit.timeservlet.utils.ServletUtils.isValidTimeZone;

@WebFilter(value="/time")
public class TimezoneValidateFilter extends HttpFilter  {




    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {

        Optional<String> optionalS = getRequestedTimeZone(req);

         if(optionalS.isPresent()) {

             if (!isValidTimeZone(String.join("+",optionalS.get().split(" ")))) {
                 res.setContentType("text/html; charset=utf-8");
                 res.sendError(400, "From doFilter: Invalid TimeZone: " + optionalS.get());
             } else {
                 chain.doFilter(req, res);
             }
         } else {
             chain.doFilter(req,res);
         }
    }
    private Optional <String> getRequestedTimeZone(HttpServletRequest req){
      return  Optional.ofNullable(req.getParameter(TIMEZONE_PARAMETER));
   }


}