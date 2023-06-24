//package com.example.codewarsplugin.services;
//
//import com.example.codewarsplugin.models.KataRecord;
//import org.springframework.http.ResponseEntity;
//import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
//import org.springframework.web.client.RestTemplate;
//
//import java.net.http.HttpClient;
//
//public class KataIdService {
//
//    private static final String BASE_URL = "https://www.codewars.com/api/v1/code-challenges/";
//
//    private KataIdService(){
//    }
//
//    public static KataRecord getKataRecord(String id) {
//        id = formatString(id);
//        RestTemplate restTemplate = new RestTemplate();
//        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
//
//        String url = BASE_URL + id;
//        ResponseEntity<KataRecord> response = restTemplate.getForEntity(url, KataRecord.class);
//
//        if (response.getStatusCode().is2xxSuccessful()) {
//            KataRecord kata = response.getBody();
//            return kata;
//        } else {
//            System.out.println("Request failed with status: " + response.getStatusCode());
//            return null;
//        }
//    }
//
//    private static String formatString(String id) {
//        return id.toLowerCase().replaceAll("\\s+-\\s+", "-").replaceAll("-\\s+|\\s+-|\\s+", "-");
//    }
//}
