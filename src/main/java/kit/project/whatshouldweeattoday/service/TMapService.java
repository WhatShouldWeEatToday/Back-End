package kit.project.whatshouldweeattoday.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import kit.project.whatshouldweeattoday.domain.dto.restaurant.PathResponseDTO;
import kit.project.whatshouldweeattoday.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TMapService {

    @Value("${tmap.api.key}")
    private String tmapKey;

    private final RestaurantRepository restaurantRepository;

    // 주소를 위도와 경도로 변환
    @Transactional
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
    @Transactional
    public String getAddressByCoordinates(double startX, double startY) {
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("getAddressByCoordinates 들어옴");
        /*String coordType = "WGS84GEO"; // 좌표계 유형 설정
        String addressType = "A00"; // 변환할 주소 유형 설정
        String coordYn = "Y"; // 좌푯값 반환 여부
        int version = 1; // API 버전*/
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

    //음식점주소의 XX동XX번지 추출
    @Transactional
    public String getAddressByCoordinates2(double startX, double startY) {
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("getAddressByCoordinates 들어옴");
        /*String coordType = "WGS84GEO"; // 좌표계 유형 설정
        String addressType = "A00"; // 변환할 주소 유형 설정
        String coordYn = "Y"; // 좌푯값 반환 여부
        int version = 1; // API 버전*/
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
            JsonNode bunji = addressInfo.path("bunji");
            String fullAddress = dong.asText() + bunji.asText();
            System.out.println("주소 : " + fullAddress);
            return fullAddress;
        } catch (IOException | InterruptedException e) {
            System.err.println("Failed to retrieve legalDong: " + e.getMessage());
            return "error";
        }
    }


    // JSON 응답 파싱
    @Transactional
    public Map<String, Double> parseCoordinates(String responseBody) throws IOException {
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
    @Transactional
    public Double getDirectDistance(double startX, double startY, double endX, double endY) {
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
    @Transactional
    public double parseDistance(String responseBody) throws IOException {
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
    @Transactional
    public double calculateDistance(double startLat, double startLon, double endX, double endY) {
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

    //최적경로 반환 https://apis.openapi.sk.com/transit/routes
    //출발지&목적지에 대한 대중교통 경로탐색 정보와 전체 보행자 이동 경로를 제공
    //출발지 및 도착지 위도경도 필요
    @Transactional
    public int totalTime(String startX, String startY, String endX, String endY, int lang, String format, int count, String searchDttm) {
        System.out.println("경로 반환 함수 출발지 " + startX + " " + startY + " 도착지 정보 " + endX + " " + endY);
        ObjectMapper mapper = new ObjectMapper();

        try {
            String jsonBody = String.format(
                    "{\"startX\":\"%s\",\"startY\":\"%s\",\"endX\":\"%s\",\"endY\":\"%s\",\"lang\":%d,\"format\":\"%s\",\"count\":%d,\"searchDttm\":\"%s\"}",
                    startX, startY, endX, endY, lang, format, count, searchDttm);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://apis.openapi.sk.com/transit/routes"))
                    .header("accept", "application/json")
                    .header("appKey", tmapKey)
                    .method("POST", HttpRequest.BodyPublishers.ofString(jsonBody)) // 변경된 부분
                    .build();

            System.out.println("Url : " + request.uri().toString());
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
          //  System.out.println("body : " + body);

            if (body.contains("error")) {
                System.out.println("에러입니다");
                JsonNode rootNode = mapper.readTree(body);
                JsonNode errorNode = rootNode.path("error");
                System.out.println("Error response: " + errorNode.toString());
                return -1; // 오류가 있는 경우 -1 반환
            }

            JsonNode rootNode = mapper.readTree(body);
            JsonNode itinerariesNode = rootNode.path("metaData").path("plan").path("itineraries");
            if (itinerariesNode.isArray() && itinerariesNode.size() > 0) {
                JsonNode firstItinerary = itinerariesNode.get(0);
                JsonNode totalTimeInfo = firstItinerary.path("totalTime");
               // System.out.println("총 소요시간은 : " + totalTimeInfo.toString());
                return totalTimeInfo.asInt();
            }
            return 0;
        } catch (IOException | InterruptedException e) {
            System.err.println("Failed to calculate transit time: " + e.getMessage());
            return -1; // 오류 발생 시 -1 반환
        }
    }

    @Transactional
    public int totalTime2(String departure, String destination, int lang, String format, int count, String searchDttm) {
        System.out.println("경로 반환 함수 출발지 :" + departure + " 도착지 정보 " + destination);
        Map<String, Double> depCoordinates = this.getCoordinates(departure);
        Map<String, Double> destCoordinates = this.getCoordinates(destination);

        System.out.println("출발지 위치 :" + depCoordinates.get("latitude")+" "+depCoordinates.get("longitude")+ " 도착지 위치 " + destCoordinates.get("latitude")+" "+destCoordinates.get("longitude"));
        String startY = Double.toString(depCoordinates.get("latitude"));
        String startX = Double.toString(depCoordinates.get("longitude"));
        String endY = Double.toString(destCoordinates.get("latitude"));
        String endX = Double.toString(destCoordinates.get("longitude"));

        ObjectMapper mapper = new ObjectMapper();

        try {
            String jsonBody = String.format(
                    "{\"startX\":\"%s\",\"startY\":\"%s\",\"endX\":\"%s\",\"endY\":\"%s\",\"lang\":%d,\"format\":\"%s\",\"count\":%d,\"searchDttm\":\"%s\"}",
                    startX, startY, endX, endY, lang, format, count, searchDttm);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://apis.openapi.sk.com/transit/routes"))
                    .header("accept", "application/json")
                    .header("appKey", tmapKey)
                    .method("POST", HttpRequest.BodyPublishers.ofString(jsonBody)) // 변경된 부분
                    .build();

            System.out.println("Url : " + request.uri().toString());
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            System.out.println("body : " + body);

            if (body.contains("error")) {
                System.out.println("에러입니다");
                JsonNode rootNode = mapper.readTree(body);
                JsonNode errorNode = rootNode.path("error");
                System.out.println("Error response: " + errorNode.toString());
                return -1; // 오류가 있는 경우 -1 반환
            }

            JsonNode rootNode = mapper.readTree(body);
            JsonNode itinerariesNode = rootNode.path("metaData").path("plan").path("itineraries");
            if (itinerariesNode.isArray() && itinerariesNode.size() > 0) {
                JsonNode firstItinerary = itinerariesNode.get(0);
                JsonNode totalTimeInfo = firstItinerary.path("totalTime");
                System.out.println("총 소요시간은 : " + totalTimeInfo.toString());
                return totalTimeInfo.asInt();
            }
            return 0;
        } catch (IOException | InterruptedException e) {
            System.err.println("Failed to calculate transit time: " + e.getMessage());
            return -1; // 오류 발생 시 -1 반환
        }
    }

    @Transactional
    public PathResponseDTO getTransitRoute(String departure, String destination, int lang, String format, int count, String searchDttm) {
        System.out.println("경로 반환 함수 출발지 :" + departure + " 도착지 정보 " + destination);
        Map<String, Double> depCoordinates = this.getCoordinates(departure);
        Map<String, Double> destCoordinates = this.getCoordinates(destination);

        System.out.println("출발지 위치 :" + depCoordinates.get("latitude") + " " + depCoordinates.get("longitude") + " 도착지 위치 " + destCoordinates.get("latitude") + " " + destCoordinates.get("longitude"));
        String startY = Double.toString(depCoordinates.get("latitude"));
        String startX = Double.toString(depCoordinates.get("longitude"));
        String endY = Double.toString(destCoordinates.get("latitude"));
        String endX = Double.toString(destCoordinates.get("longitude"));

        ObjectMapper mapper = new ObjectMapper();

        try {
            String jsonBody = String.format(
                    "{\"startX\":\"%s\",\"startY\":\"%s\",\"endX\":\"%s\",\"endY\":\"%s\",\"lang\":%d,\"format\":\"%s\",\"count\":%d,\"searchDttm\":\"%s\"}",
                    startX, startY, endX, endY, lang, format, count, searchDttm);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://apis.openapi.sk.com/transit/routes"))
                    .header("accept", "application/json")
                    .header("appKey", tmapKey)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            System.out.println("Url : " + request.uri().toString());
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            System.out.println("body : " + body);

            //body를 받아오는 시점

            if (body.contains("error")) {
                System.out.println("에러입니다");
                JsonNode rootNode = mapper.readTree(body);
                JsonNode errorNode = rootNode.path("error");
                System.out.println("Error response: " + errorNode.toString());
                return null; // 오류가 있는 경우 null 반환
            }

            JsonNode rootNode = mapper.readTree(body);
            JsonNode metaData = rootNode.path("metaData");
            JsonNode requestParameter = metaData.path("requestParameters");
            JsonNode itinerariesNode = metaData.path("plan").path("itineraries");// List
            JsonNode legs = itinerariesNode.get(0).path("legs"); //List

            //DTO를 구성해 -> 들어갈 정보들
            //requestParameters.path("expressBusCount") -> requestParameters는 앞의 예시와 같이
            //itinerariesNode의 경우 .get(0).path("fare").path("regular").path("totalPrice");
            //itinerariesNode의 totalTime의 경우 .get(0).path("totalTime);
            //legs의 경우에는 여러 index가 있어서 for문으로 전부 돌아서 원하는 정보 위와 같이 넣어주면 될 거 같음
            //TODO 구조 잘 보고 넣을 것!!!!
            return null;
        } catch (IOException | InterruptedException e) {
            System.err.println("Failed to calculate transit time: " + e.getMessage());
            return null; // 오류 발생 시 null 반환
        }
    }

    @Transactional
    public JsonNode getJsonByTransitRoute(String departure, String destination, int lang, String format, int count, String searchDttm) {
        System.out.println("경로 반환 함수 출발지 :" + departure + " 도착지 정보 " + destination);
        Map<String, Double> depCoordinates = this.getCoordinates(departure);
        Map<String, Double> destCoordinates = this.getCoordinates(destination);

        System.out.println("출발지 위치 :" + depCoordinates.get("latitude") + " " + depCoordinates.get("longitude") + " 도착지 위치 " + destCoordinates.get("latitude") + " " + destCoordinates.get("longitude"));
        String startY = Double.toString(depCoordinates.get("latitude"));
        String startX = Double.toString(depCoordinates.get("longitude"));
        String endY = Double.toString(destCoordinates.get("latitude"));
        String endX = Double.toString(destCoordinates.get("longitude"));

        try {
            String jsonBody = String.format(
                    "{\"startX\":\"%s\",\"startY\":\"%s\",\"endX\":\"%s\",\"endY\":\"%s\",\"lang\":%d,\"format\":\"%s\",\"count\":%d,\"searchDttm\":\"%s\"}",
                    startX, startY, endX, endY, lang, format, count, searchDttm);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://apis.openapi.sk.com/transit/routes"))
                    .header("accept", "application/json")
                    .header("appKey", tmapKey)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            System.out.println("Url : " + request.uri().toString());
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            System.out.println("body : " + body);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(body);

            if (rootNode.has("error")) {
                System.out.println("에러입니다");
                return rootNode; // 오류가 있는 경우 오류 응답 반환
            }

            return rootNode; // JSON 응답을 그대로 반환

        } catch (IOException | InterruptedException e) {
            System.err.println("Failed to calculate transit time: " + e.getMessage());
            return null; // 오류 발생 시 null 반환
        }
    }

    @Transactional
    public PathResponseDTO getTransitRoute2(String departure, String destination, int lang, String format, int count, String searchDttm) {
        System.out.println("경로 반환 함수 출발지 :" + departure + " 도착지 정보 " + destination);
        Map<String, Double> depCoordinates = this.getCoordinates(departure);
        Map<String, Double> destCoordinates = this.getCoordinates(destination);

        System.out.println("출발지 위치 :" + depCoordinates.get("latitude") + " " + depCoordinates.get("longitude") + " 도착지 위치 " + destCoordinates.get("latitude") + " " + destCoordinates.get("longitude"));
        String startY = Double.toString(depCoordinates.get("latitude"));
        String startX = Double.toString(depCoordinates.get("longitude"));
        String endY = Double.toString(destCoordinates.get("latitude"));
        String endX = Double.toString(destCoordinates.get("longitude"));

        try {
            String jsonBody = String.format(
                    "{\"startX\":\"%s\",\"startY\":\"%s\",\"endX\":\"%s\",\"endY\":\"%s\",\"lang\":%d,\"format\":\"%s\",\"count\":%d,\"searchDttm\":\"%s\"}",
                    startX, startY, endX, endY, lang, format, count, searchDttm);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://apis.openapi.sk.com/transit/routes"))
                    .header("accept", "application/json")
                    .header("appKey", tmapKey)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            System.out.println("Url : " + request.uri().toString());
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            System.out.println("body : " + body);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(body);

            if (rootNode.has("error")) {
                System.out.println("에러입니다");
                return null; // 오류가 있는 경우 null 반환
            }

            // JSON 응답을 PathResponseDTO 객체로 매핑
            PathResponseDTO pathResponseDTO = mapper.treeToValue(rootNode, PathResponseDTO.class);
            return pathResponseDTO;

        } catch (IOException | InterruptedException e) {
            System.err.println("Failed to calculate transit time: " + e.getMessage());
            return null; // 오류 발생 시 null 반환
        }
    }
}

