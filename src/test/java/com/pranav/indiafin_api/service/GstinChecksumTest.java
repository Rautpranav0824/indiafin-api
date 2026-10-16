package com.pranav.indiafin_api.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GstinChecksumTest {

    @Test
    void realSampleGstinIsValid(){
        assertTrue(GstinChecksum.isValid("27AAPFU0939F1ZV"));
    }

    @Test
    void gstinWithWrongCheckCharacterIsRejected() {
        assertFalse(GstinChecksum.isValid("27ABCPE1234F1Z5"));
    }
}
