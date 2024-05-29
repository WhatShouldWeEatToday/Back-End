package kit.project.whatshouldweeattoday.service;

import kit.project.whatshouldweeattoday.domain.dto.restaurant.PersonalPath;
import kit.project.whatshouldweeattoday.domain.entity.Restaurant;
import kit.project.whatshouldweeattoday.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PathService {
    private final RestaurantRepository restaurantRepository;
    private final TMapService tmapService;
    private final ReviewService reviewService;
    private int serialNum = 0; // 음식점 배열 일련번호
    private List<PersonalPath> weightArray = new ArrayList<>(); // 전체 가중치

    public void registerDeparture(String meetMenu, Long chatId) {
        System.out.println("MeetMenu: " + meetMenu + ", ChatId: " + chatId);
    }

    // startAddres = 채팅방 사람들의 출발지 list
    @Transactional
    public List<PersonalPath> getWeight(String keyword, List<String> startAddress) {
        List<PersonalPath> resultSort = new ArrayList<>(); //ex) A와 B와 C에 대해서 나온 것들을 순차적으로 저장한 배열 -> 시리얼 넘버랑, 각 식당에 대해서 가지고 있음
        LocalDateTime localDateTime = LocalDateTime.now();
        String searchDttm = localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        for (String targetAddress : startAddress) { //allPathList를 채워줌
            List<PersonalPath> pathList = personalRestaurant(keyword, targetAddress, searchDttm);
            pathList = setSerialNumForArray(pathList);
            resultSort.addAll(pathList);
        }
        for (String address : startAddress) {
            System.out.println("유저: " + address);
            Map<String, Double> coordinates = tmapService.getCoordinates(address);
            Double startX = coordinates.get("longitude");
            Double startY = coordinates.get("latitude");
            List<PersonalPath> pathList = setTotalTime(resultSort, startX, startY, searchDttm);
            pathList = sortByPath(pathList);
            for (int j = 0; j < pathList.size(); j++) {
                PersonalPath target = pathList.get(j);
                target.setWeight(target.getWeight() + j); //TODO weight = 0 인 경우확인
            }
        }
        sortPersonalPathByWeightTop3(resultSort);

        return resultSort;
    }

    @Transactional
    public void sortPersonalPathByWeight(List<PersonalPath> list) {
        Collections.sort(list, Comparator.comparingInt(PersonalPath::getWeight));
    }

    //최종 3개 추출
    @Transactional
    public List<PersonalPath> sortPersonalPathByWeightTop3(List<PersonalPath> list) {
        // 리스트를 가중치 기준으로 정렬
        Collections.sort(list, Comparator.comparingInt(PersonalPath::getWeight));

        // 상위 3개의 항목을 반환
        return list.size() > 3 ? list.subList(0, 3) : list;
    }
    @Transactional
    public List<PersonalPath> setTotalTime(List<PersonalPath> list, Double startX, Double startY, String searchDttm) {
        List<PersonalPath> results = new ArrayList<>();
        for (PersonalPath target : list) {
            target.setTotalTime(tmapService.totalTime(Double.toString(startX), Double.toString(startY),
                    Double.toString(target.getRestaurant().getLongitude()), Double.toString(target.getRestaurant().getLatitude()), 0, "json", 1, searchDttm));
            results.add(target);
        }
        return results;
    }

    //1차 배열
    @Transactional
    public List<PersonalPath> personalRestaurant(String keyword, String startAddress, String searchDttm) {
        List<Restaurant> restaurants = new ArrayList<>();
        List<PersonalPath> personalPaths = new ArrayList<>();
        //1. 주소를 위치 변환
        Map<String, Double> coordinates = tmapService.getCoordinates(startAddress);
        Double startX = coordinates.get("longitude");
        Double startY = coordinates.get("latitude");
        System.out.println("이 사람의 출발 위치 :" + startX + " " + startY);
        //2. 사용자의 위치정보를 주소로 반환후 XX동 XX까지 추출
        String userAddress = tmapService.getAddressByCoordinates2(startX, startY);
        //3. 주소 & 키워드로 음식점 검색
        restaurants = restaurantRepository.findByKeywordAndAddress(keyword, userAddress);
        //4.리뷰평점순으로 20개 추출(1차 필터링)
        restaurants = sortByDegree(restaurants);
        //5. 경로구하기 -> 여기서 personalPath로 변환
        personalPaths = getPath(startX, startY, restaurants, searchDttm);
        //6. 경로순으로 정렬
        personalPaths = sortByPath(personalPaths);
        return personalPaths;
    }

    //2차 배열 -> 일련번호 넣어주기(쓰레드 공유 이슈 생길 수 있음..), 다른사람도 serialNum값을 사용함
    @Transactional
    public List<PersonalPath> setSerialNumForArray(List<PersonalPath> personalPaths) {
        for (PersonalPath personalPath : personalPaths) {
            personalPath.setSerialNum(serialNum);
            serialNum++;
        }
        return personalPaths;
    }

    //3차 배열 -> 다른사람들의 personalPath 배열들 다 합침
    @Transactional
    public List<PersonalPath> mergeList(List<PersonalPath> personalPaths1, List<PersonalPath> personalPaths2) {
        List<PersonalPath> mergedList = new ArrayList<>(personalPaths1);
        mergedList.addAll(personalPaths2);
        return mergedList;
    }

    //리뷰평점순으로 정렬
    @Transactional
    public List<Restaurant> sortByDegree(List<Restaurant> restaurants) {
        return restaurants.stream()
                .sorted((r1, r2) -> Double.compare(r2.getDegree(), r1.getDegree()))
                .limit(20)
                .collect(Collectors.toList());
    }

    //경로순으로 정렬 -> 오름차순
    @Transactional
    public List<PersonalPath> sortByPath(List<PersonalPath> personalPaths) {
        return personalPaths.stream()
                .sorted((r1, r2) -> Double.compare(r1.getTotalTime(), r2.getTotalTime()))
                .limit(5)
                .collect(Collectors.toList());
    }

    //개인배열 경로 추출
    @Transactional
    public List<PersonalPath> getPath(Double startX, Double startY, List<Restaurant> restaurants, String searchDttm) {
        List<PersonalPath> personalPaths = new ArrayList<>();
        for (Restaurant restaurant : restaurants) {
            PersonalPath target = new PersonalPath();
            target.setTotalTime(tmapService.totalTime(Double.toString(startX), Double.toString(startY),
                    Double.toString(restaurant.getLongitude()), Double.toString(restaurant.getLatitude()), 0, "json", 10, searchDttm));
            target.setRestaurant(restaurant);
            personalPaths.add(target);
        }
        return personalPaths;
    }

}

