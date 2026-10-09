package com.pranav.indiafin_api.service;

import com.pranav.indiafin_api.model.Branch;
import jakarta.annotation.PostConstruct;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class IfscService {

    private final Map<String, Branch> branches = new HashMap<>();

    @PostConstruct
    public void peek() throws IOException {
        System.out.println(">>> IfscService is running");

        ClassPathResource path = new ClassPathResource("data/IFSC.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(path.getInputStream()));

        CSVParser parser = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .get()
                .parse(reader);



        for (CSVRecord row : parser) {
            Branch branch = new Branch(
                    row.get("IFSC"),
                    clean(row.get("BANK")),
                    row.get("BRANCH"),
                    row.get("ADDRESS"),
                    row.get("CITY"),
                    row.get("DISTRICT"),
                    row.get("STATE"),
                    clean(row.get("MICR")),
                    clean(row.get("CONTACT")),
                    Boolean.parseBoolean(row.get("NEFT")),
                    Boolean.parseBoolean(row.get("RTGS")),
                    Boolean.parseBoolean(row.get("IMPS")),
                    Boolean.parseBoolean(row.get("UPI"))
            );
            branches.put(branch.ifsc(), branch);
        }

        System.out.println("Loaded: " + branches.size());
        System.out.println(branches.get("SBIN0000001"));

    }
    private String clean(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
    public Optional<Branch> find(String ifsc){
        return Optional.ofNullable(branches.get(ifsc));
    }
}
