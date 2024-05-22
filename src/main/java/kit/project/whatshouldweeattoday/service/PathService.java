package kit.project.whatshouldweeattoday.service;

import kit.project.whatshouldweeattoday.domain.dto.restaurant.PersonalPath;
import kit.project.whatshouldweeattoday.domain.entity.Restaurant;
import kit.project.whatshouldweeattoday.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // num = 채팅방사람들 수, startAddres = 채팅방 사람들의 출발지list
    @Transactional
    public List<PersonalPath> getWeight(String keyword, List<String> startAddress, String searchDttm) {
        List<PersonalPath> resultSort = new ArrayList<>(); //ex) A와 B와 C에 대해서 나온 것들을 순차적으로 저장한 배열 -> 시리얼 넘버랑, 각 식당에 대해서 가지고 있음
        for (int i = 0; i < startAddress.size(); i++) { //allPathList를 채워줌
            String targetAddress = startAddress.get(i);
            List<PersonalPath> pathList = personalRestaurant(keyword, targetAddress, searchDttm);
            pathList = setSerialNumForArray(pathList);
            resultSort.addAll(pathList);
        }
        for (int i = 0; i < startAddress.size(); i++) {
            System.out.println("유저: " + startAddress.get(i));
            Map<String, Double> coordinates = tmapService.getCoordinates(startAddress.get(i));
            Double startX = coordinates.get("longitude");
            Double startY = coordinates.get("latitude");
            List<PersonalPath> pathList = setTotalTime(resultSort, startX, startY, searchDttm);
            pathList = sortByPath(pathList);
            for(int j = 0; j< pathList.size(); j++) {
                PersonalPath target = pathList.get(j);
                target.setWeight(target.getWeight()+j); //TODO 결과를 봤을 때 weight = 0 인 경우에 대해서 디버그를 통해서 하나씩 추적하기.
            }
        }
        sortPersonalPathByWeight(resultSort);

        return resultSort;
    }
    public void sortPersonalPathByWeight(List<PersonalPath> list) {
        Collections.sort(list, new Comparator<PersonalPath>() {
            @Override
            public int compare(PersonalPath p1, PersonalPath p2) {
                return Integer.compare(p1.getWeight(), p2.getWeight());
            }
        });
    }

    public List<PersonalPath> setTotalTime(List<PersonalPath> list, Double startX, Double startY, String searchDttm) {
        List<PersonalPath> results = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            PersonalPath target = list.get(i);
            target.setTotalTime(tmapService.totalTime(Double.toString(startX), Double.toString(startY),
                    Double.toString(target.getRestaurant().getLongitude()), Double.toString(target.getRestaurant().getLatitude())
            ,0, "json", 10, searchDttm));
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
        String userAddress = tmapService.getAddressByCoordinates(startX, startY);
        //3. 주소 & 키워드로 음식점 검색
        restaurants = restaurantRepository.findByKeywordAndAddress(keyword,userAddress);
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
        for (int i = 0; i < personalPaths.size(); i++) {
            personalPaths.get(i).setSerialNum(serialNum);
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

    //경로순으로 정렬
    @Transactional
    public List<PersonalPath> sortByPath(List<PersonalPath> personalPaths) {
        return personalPaths.stream()
                .sorted((r1, r2) -> Double.compare(r2.getTotalTime(), r1.getTotalTime()))
                .limit(5)
                .collect(Collectors.toList());
    }

    //개인배열 경로 추출
    @Transactional
    public List<PersonalPath> getPath(Double startX, Double startY, List<Restaurant> restaurants, String searchDttm) {
        List<PersonalPath> personalPaths = new ArrayList<>();
       /* for (int i = 0; i < restaurants.size(); i++) {
            restaurants.get(i).setPathTime(tmapService.totalTime(Double.toString(startX), Double.toString(startY),
                    Double.toString(restaurants.get(i).getLongitude()), Double.toString(restaurants.get(i).getLatitude()), 0, "json", 10, searchDttm));
        }*/
        for (int i = 0; i < restaurants.size(); i++) {
            PersonalPath target = new PersonalPath();
            target.setTotalTime(tmapService.totalTime(Double.toString(startX), Double.toString(startY),
                    Double.toString(restaurants.get(i).getLongitude()), Double.toString(restaurants.get(i).getLatitude()), 0, "json", 10, searchDttm));
            target.setRestaurant(restaurants.get(i));
            personalPaths.add(target);
        }
        return personalPaths;
    }

}
