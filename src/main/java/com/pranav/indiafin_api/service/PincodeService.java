package com.pranav.indiafin_api.service;

import com.pranav.indiafin_api.model.PostOffice;
import jakarta.annotation.PostConstruct;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class PincodeService {

    private final Map<String, List<PostOffice>> byPincode = new HashMap<>();

    @PostConstruct
    public void peek() throws IOException{
        System.out.println(">>> PincodeService is running");

        ClassPathResource path = new ClassPathResource("/data/pincodes.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(path.getInputStream()));

        CSVParser parser = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .get()
                .parse(reader);

        for(CSVRecord row : parser){
            PostOffice postOffice = new PostOffice(
                    row.get("officename"),
                    row.get("pincode"),
                    row.get("officetype"),
                    "Delivery".equals(row.get("delivery")),
                    row.get("district"),
                    row.get("statename"),
                    parseCoordinate(row.get("latitude")),
                    parseCoordinate(row.get("longitude"))
            );
            byPincode.computeIfAbsent(postOffice.pincode(), key -> new ArrayList<>()).add(postOffice);
        }

        parser.close();

        System.out.println("Unique pincodes: " + byPincode.size());

    }

    public Optional<List<PostOffice>> find(String pincode) {
        return Optional.ofNullable(byPincode.get(pincode));
    }

    private Double parseCoordinate(String value) {
        if (value == null || value.isBlank() || value.equals("NA")) {
            return null;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
