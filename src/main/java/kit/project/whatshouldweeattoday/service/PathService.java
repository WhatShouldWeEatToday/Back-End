package kit.project.whatshouldweeattoday.service;

import kit.project.whatshouldweeattoday.domain.dto.restaurant.PersonalPathDTO;
import kit.project.whatshouldweeattoday.domain.dto.restaurant.RestaurantResponseDTO;
import kit.project.whatshouldweeattoday.domain.entity.Restaurant;
import kit.project.whatshouldweeattoday.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PathService {
    private final RestaurantRepository restaurantRepository;
    private final TMapService tmapService;
    private final ReviewService reviewService;
    private int serialNum = 0; // 음식점 배열 일련번호
    private List<PersonalPathDTO> weightArray = new ArrayList<>(); // 전체 가중치

    // startAddres = 채팅방 사람들의 출발지 list
    @Transactional
    public List<PersonalPathDTO> getWeight(@Param("keyword") String keyword, @Param("startAddress") List<String> startAddress) {
        List<PersonalPathDTO> resultSort = new ArrayList<>(); //ex) A와 B와 C에 대해서 나온 것들을 순차적으로 저장한 배열 -> 시리얼 넘버랑, 각 식당에 대해서 가지고 있음
        LocalDateTime localDateTime = LocalDateTime.now();
        String searchDttm = localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        List<String> addressInfo = new ArrayList<>();
        Map<String, Double> coordinates;
        Double startX = 0.0;
        Double startY = 0.0;
        for (int i = 0; i < startAddress.size(); i++) {
            //1. 주소를 위치 변환
            coordinates = tmapService.getCoordinates(startAddress.get(i));
            startX = coordinates.get("longitude");
            startY = coordinates.get("latitude");
          //  System.out.println("이 사람의 출발 위치 :" + startX + " " + startY);
            //2. 사용자의 위치정보를 주소로 반환후 XX동 추출
            String userAddress = tmapService.getAddressByCoordinates(startX, startY);

            if (addressInfo.contains(userAddress)) {
                // 만약 addressInfo 배열에 같은 주소가 있으면 세부 주소를 더 뽑음
                userAddress = tmapService.getAddressByCoordinates2(startX, startY);
            }
            addressInfo.add(userAddress);
        }

        // 개인별 배열 만들고 일련번호 주입
        for (String targetAddress : addressInfo) { //allPathList를 채워줌
            List<PersonalPathDTO> pathList = personalRestaurant(keyword, targetAddress, searchDttm, startX, startY);
            pathList = setSerialNumForArray(pathList);
            resultSort.addAll(pathList);
        }

        //-> 가중치 계산
        for (String address : startAddress) {
            Map<String, Double> coordinates2 = tmapService.getCoordinates(address);
            Double startX2 = coordinates2.get("longitude");
            Double startY2 = coordinates2.get("latitude");
            List<PersonalPathDTO> pathList = setTotalTime(resultSort, startX2, startY2, searchDttm);
            pathList = sortByPath(pathList);
            for (int j = 0; j < pathList.size(); j++) {
                PersonalPathDTO target = pathList.get(j);
                target.setWeight(target.getWeight() + j); //TODO weight = 0 인 경우 확인
            }
        }
        //상위 3개 반환
        resultSort = sortPersonalPathByWeightTop3(resultSort);
        for (PersonalPathDTO personalPathDTO : resultSort) {
            log.info("사용자 경로 반환 : serialNum = {}, weight = {}, totalTime = {}, routeInfo = {}",
                    personalPathDTO.getSerialNum(), personalPathDTO.getWeight(), personalPathDTO.getTotalTime(), personalPathDTO.getRouteInfo());
        }
        return resultSort;
    }

    public void changeSubArray(int start, int end, List<PersonalPathDTO> resultSort) {
        List<PersonalPathDTO> subArray = resultSort.subList(start, end + 1);
        //별점 내림차순
        Collections.sort(subArray, Comparator.comparingDouble((PersonalPathDTO dto) -> dto.getRestaurantResponseDTO().getDegree()).reversed());
    }

    @Transactional
    public void sortPersonalPathByWeight(List<PersonalPathDTO> list) {
        Collections.sort(list, Comparator.comparingInt(PersonalPathDTO::getWeight));
    }

    //최종 3개 추출
    public List<PersonalPathDTO> sortPersonalPathByWeightTop3(List<PersonalPathDTO> list) {
        // 리스트를 가중치 기준으로 정렬
        Collections.sort(list, Comparator.comparingInt(PersonalPathDTO::getWeight));

        int start = -1; //SubArray를 만들기 위함
        int end = -1; //SubArray를 만들기 위함
        int size = list.size();
        for (int i = 0; i < size - 1; i++) {
            if (Objects.equals(list.get(i).getWeight(), list.get(i + 1).getWeight())) { //현재 보는 객체의 가중치와 다음 가중치가 같다면?
                start = i;
                int j = i;
                while (Objects.equals(list.get(j).getWeight(), list.get(j + 1).getWeight())) {
                    j++;
                    if(j == size -1) {
                        break;
                    }
                }
                end = j;
                i = j;
                changeSubArray(start, end, list);
            }
        }
        // 상위 3개의 항목을 반환
        return list.size() > 3 ? list.subList(0, 3) : list;
    }

    //경로 시간 추출
    @Transactional
    public List<PersonalPathDTO> setTotalTime(List<PersonalPathDTO> list, Double startX, Double startY, String searchDttm) {
        List<PersonalPathDTO> results = new ArrayList<>();
        for (PersonalPathDTO target : list) {
            target.setTotalTime(tmapService.totalTime(Double.toString(startX), Double.toString(startY),
                    Double.toString(target.getRestaurantResponseDTO().getLongitude()), Double.toString(target.getRestaurantResponseDTO().getLatitude()), 0, "json", 1, searchDttm));
            results.add(target);
        }
        return results;
    }

    //1차 배열
    @Transactional
    public List<PersonalPathDTO> personalRestaurant(String keyword, String userAddress, String searchDttm, Double startX, Double startY) {
        List<Restaurant> restaurants = new ArrayList<>();
        List<PersonalPathDTO> personalPaths = new ArrayList<>();

        //1-1. 주소필터링
        restaurants = restaurantRepository.findByOnlyAddress(userAddress);
        /*System.out.println("주소로 검색된 식당 수 : " + restaurants.size());
        for (int i = 0; i < restaurants.size(); i++) {
            System.out.println(restaurants.get(i).getName());
            System.out.println(restaurants.get(i).getAddressNumber());
        }*/
        //1-2. 키워드필터링
        restaurants = filterByKeyword(restaurants, keyword);
        /*System.out.println("키워드 검색된 식당 수 : " + restaurants.size());
        for (int i = 0; i < restaurants.size(); i++) {
            System.out.println(restaurants.get(i).getName());
            System.out.println(restaurants.get(i).getAddressNumber());
        }*/

        //2.리뷰평점순으로 20개 추출(1차 필터링)
        restaurants = sortByDegree(restaurants);
        //3. 경로구하기 -> 여기서 personalPath로 변환
        personalPaths = getPath(startX, startY, restaurants, searchDttm);
        //4. 경로순으로 정렬
        personalPaths = sortByPath(personalPaths);
        return personalPaths;
    }

    //2차 배열 -> 일련번호 넣어주기(쓰레드 공유 이슈 생길 수 있음..), 다른사람도 serialNum값을 사용함
    @Transactional
    public List<PersonalPathDTO> setSerialNumForArray(List<PersonalPathDTO> personalPaths) {
        for (PersonalPathDTO personalPath : personalPaths) {
            personalPath.setSerialNum(serialNum);
            serialNum++;
        }
        return personalPaths;
    }

    //3차 배열 -> 다른사람들의 personalPath 배열들 다 합침
    @Transactional
    public List<PersonalPathDTO> mergeList(List<PersonalPathDTO> personalPaths1, List<PersonalPathDTO> personalPaths2) {
        List<PersonalPathDTO> mergedList = new ArrayList<>(personalPaths1);
        mergedList.addAll(personalPaths2);
        return mergedList;
    }

    //리뷰평점순으로 정렬 -> 20개
    @Transactional
    public List<Restaurant> sortByDegree(List<Restaurant> restaurants) {
        return restaurants.stream()
                .sorted((r1, r2) -> Double.compare(r2.getDegree(), r1.getDegree()))
                .limit(20)
                .collect(Collectors.toList());
    }

    //리뷰평점순으로 정렬
    @Transactional
    public List<Restaurant> sortByDegree2(List<Restaurant> restaurants) {
        return restaurants.stream()
                .sorted((r1, r2) -> Double.compare(r2.getDegree(), r1.getDegree()))
                .collect(Collectors.toList());
    }

    //경로순으로 정렬 -> 오름차순
    @Transactional
    public List<PersonalPathDTO> sortByPath(List<PersonalPathDTO> personalPaths) {
        return personalPaths.stream()
                .sorted((r1, r2) -> Double.compare(r1.getTotalTime(), r2.getTotalTime()))
                .limit(5)
                .collect(Collectors.toList());
    }

    //개인배열 경로 추출
    @Transactional
    public List<PersonalPathDTO> getPath(Double startX, Double startY, List<Restaurant> restaurants, String searchDttm) {
        List<PersonalPathDTO> personalPaths = new ArrayList<>();
        for (Restaurant restaurant : restaurants) {
            PersonalPathDTO target = new PersonalPathDTO();
            target.setTotalTime(tmapService.totalTime(
                    Double.toString(startX), Double.toString(startY),
                    Double.toString(restaurant.getLongitude()), Double.toString(restaurant.getLatitude()),
                    0, "json", 1, searchDttm));

            // Convert Restaurant to RestaurantResponseDTO
            RestaurantResponseDTO restaurantResponseDTO = new RestaurantResponseDTO(
                    restaurant
            );

            target.setRestaurantResponseDTO(restaurantResponseDTO);
            personalPaths.add(target);
        }
        return personalPaths;
    }

    //키워드로 식당 필터링
    private List<Restaurant> filterByKeyword(List<Restaurant> restaurants, String keyword) {
        return restaurants.stream()
                .filter(restaurant -> restaurant.getName().contains(keyword) ||
                        restaurant.getMenus().contains(keyword))
                .collect(Collectors.toList());
    }

}

