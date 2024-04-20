package kit.project.whatshouldweeattoday.service;


import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
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

    //좌표로 변환
    public Map<String, Double> getCoordinates(String address) {
        try {
            // 주소 URL 인코딩
            String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);
            // 요청 준비
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(String.format("https://apis.openapi.sk.com/tmap/geo/fullAddrGeo?version=1&addressFlag=F00&coordType=WGS84GEO&fullAddr=%s&appKey=%s",
                            encodedAddress, tmapKey)))
                    .header("accept", "application/json")
                    .GET()
                    .build();
            // 요청을 보내고 응답을 받음
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            // JSON 파서를 이용한 문자열 파싱
            JSONParser parser = new JSONParser();
            JSONObject responseObject = (JSONObject) parser.parse(response.body());

            // 'coordinateInfo' 오브젝트
            JSONObject coordinateInfo = (JSONObject) responseObject.get("coordinateInfo");

            // 'coordinate' 배열
            JSONArray coordinatesArray = (JSONArray) coordinateInfo.get("coordinate");


            // 첫 번째 좌표 객체
            if (coordinatesArray != null && !coordinatesArray.isEmpty()) {
                JSONObject firstCoordinate = (JSONObject) coordinatesArray.get(0);
                System.out.println("첫번쨰 좌표 객체 : "+firstCoordinate.toJSONString());

                // 위도와 경도 추출
                String latStr = (String) firstCoordinate.get("newLat");
                String lonStr = (String) firstCoordinate.get("newLon");
                System.out.println("위도와 경도 : "+latStr+" "+lonStr);// => 출력성공!
                double lat =Double.parseDouble(latStr);
                double lon =Double.parseDouble(lonStr);

                System.out.println("결과 : "+ lat +" "+lon);
                // 결과를 맵에 저장
                Map<String, Double> result = new HashMap<>();
                result.put("latitude", lat);
                result.put("longitude", lon);

                return result;
            }
        } catch (ParseException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyMap();
    }

}


