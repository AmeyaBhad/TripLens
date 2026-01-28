package com.org.Triplens.Services.StateExtractor;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class StateExtractor {

    private static final List<String> STATES = List.of(
        "maharashtra","kerala","tamil nadu","karnataka","gujarat",
        "rajasthan","west bengal","odisha","punjab","assam",
        "bihar","uttar pradesh","madhya pradesh","telangana",
        "andhra pradesh","jharkhand","chhattisgarh","goa","delhi"
    );

    public String extractState(List<String> categories) {
        for (String c : categories) {
            String lower = c.toLowerCase();
            for (String s : STATES) {
                if (lower.contains(s)) {
                    return capitalize(s);
                }
            }
        }
        return "India";
    }

    private String capitalize(String s) {
        return s.substring(0,1).toUpperCase() + s.substring(1);
    }
}
