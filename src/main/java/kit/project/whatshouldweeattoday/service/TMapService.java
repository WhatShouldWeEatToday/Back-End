package kit.project.whatshouldweeattoday.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kit.project.whatshouldweeattoday.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TMapService {

    @Value("${tmap.api.key}")
    private String tmapKey;

    private final RestaurantRepository restaurantRepository;

    // 주소를 위도와 경도로 변환
    public Map<String, Double> getCoordinates(String address) {
        try {
            String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(String.format("https://apis.openapi.sk.com/tmap/geo/fullAddrGeo?version=1&addressFlag=F00&coordType=WGS84GEO&fullAddr=%s&appKey=%s",
                            encodedAddress, tmapKey)))
                    .header("accept", "application/json")
                    .GET()
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            return parseCoordinates(response.body());
        } catch (InterruptedException | IOException e) {
            System.err.println("Failed to fetch coordinates: " + e.getMessage());
            return Collections.emptyMap();
        }
    }

    //경도,위도 받아서 주소로 변환 api Reverse Geocoding 사용 https://apis.openapi.sk.com/tmap/geo/reversegeocoding
    public String getAddressByCoordinates(double startX, double startY) {
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("getAddressByCoordinates 들어옴");
        String coordType = "WGS84GEO"; // 좌표계 유형 설정
        String addressType = "A00"; // 변환할 주소 유형 설정
        String coordYn = "Y"; // 좌푯값 반환 여부
        int version = 1; // API 버전
        try {
            String requestUrl = String.format(
                    "https://apis.openapi.sk.com/tmap/geo/reversegeocoding?version=1&lat=%f&lon=%f&appKey=%s",
                    startY, startX, tmapKey
            );
            System.out.println("requestURL : " + requestUrl);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestUrl))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            String body = response.body();
            JsonNode rootNode = mapper.readTree(body);
            JsonNode addressInfo = rootNode.path("addressInfo");
            JsonNode dong = addressInfo.path("legalDong");

            return dong.asText();
        } catch (IOException | InterruptedException e) {
            System.err.println("Failed to retrieve legalDong: " + e.getMessage());
            return "error";
        }
    }


        // JSON 응답 파싱
        private Map<String, Double> parseCoordinates (String responseBody) throws IOException {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(responseBody);
            JsonNode coordinateNode = rootNode.path("coordinateInfo").path("coordinate");

            if (!coordinateNode.isMissingNode() && coordinateNode.isArray() && coordinateNode.size() > 0) {
                JsonNode firstCoordinate = coordinateNode.get(0);
                double lat = firstCoordinate.get("newLat").asDouble();
                double lon = firstCoordinate.get("newLon").asDouble();
                Map<String, Double> result = new HashMap<>();
                result.put("latitude", lat);
                result.put("longitude", lon);
                return result;
            } else {
                System.err.println("No coordinates found in the response");
                return Collections.emptyMap();
            }
        }

        // 직선 거리 계산
        public Double getDirectDistance ( double startX, double startY, double endX, double endY){
            System.out.println("api를 사용한 직선거리 계산 출발지 정보 " + startX + " " + startY + " 도착지 정보 " + endX + " " + endY);
            try {

                // API 요청 URL 생성
                String requestUrl = String.format("https://apis.openapi.sk.com/tmap/routes?version=1&startX=%s&startY=%s&endX=%s&endY=%s&appKey=%s",
                        Double.toString(startX), Double.toString(startY), Double.toString(endX), Double.toString(endY), tmapKey);
                System.out.println("requestURL : " + requestUrl);
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(requestUrl))
                        .header("accept", "application/json")
                        .header("Content-Type", "application/json")
                        .GET()
                        .build();

                // API 요청 및 응답 수신
                HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                return parseDistance(response.body());
            } catch (IOException | InterruptedException e) {
                System.err.println("Failed to calculate direct distance: " + e.getMessage());
                return (double) -1; // 오류 발생 시 -1 반환
            }
        }

        // 응답에서 거리 파싱
        private double parseDistance (String responseBody) throws IOException {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(responseBody);
            if (rootNode.has("features")) {
                JsonNode features = rootNode.get("features");
                if (features.isArray() && features.size() > 0) {
                    JsonNode properties = features.get(0).get("properties");
                    if (properties != null && properties.has("totalDistance")) {
                        return properties.get("totalDistance").asDouble() / 1000; // 미터를 킬로미터로 변환
                    }
                }
            }
            throw new IOException("Distance data not found in the response");
        }

        //api 사용 안한 직선거리 계산
        public double calculateDistance ( double startLat, double startLon, double endX, double endY){
            // 직선 거리 계산
            double earthRadius = 6371.01; // 지구의 반지름(km)
            double dLat = Math.toRadians(endY - startLat);
            double dLon = Math.toRadians(endX - startLon);
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                    Math.cos(Math.toRadians(startLat)) * Math.cos(Math.toRadians(endY)) *
                            Math.sin(dLon / 2) * Math.sin(dLon / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            return earthRadius * c; // 거리 반환 (킬로미터 단위)
        }
    }