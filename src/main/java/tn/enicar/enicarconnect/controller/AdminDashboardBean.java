package tn.enicar.enicarconnect.controller;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Arrays;
import java.util.List;

@Component("adminDashboardBean")
@RequestScope
public class AdminDashboardBean {

    public String getWelcomeMessage() {
        return "Welcome to ENICAR Connect Admin Panel (Legacy Fallback)";
    }

    public List<String> getSystemStats() {
        return Arrays.asList("Status: OK", "Module: JSF Connected", "Memory Usage: Optimized");
    }
}