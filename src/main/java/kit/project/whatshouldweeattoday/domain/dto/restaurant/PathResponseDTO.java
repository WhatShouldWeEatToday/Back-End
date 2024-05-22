package kit.project.whatshouldweeattoday.domain.dto.restaurant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PathResponseDTO {

    @JsonProperty("metaData")
    private MetaData metaData;

    @Getter
    @Setter
    public static class MetaData {
        @JsonProperty("requestParameters")
        private RequestParameters requestParameters;

        @JsonProperty("plan")
        private Plan plan;
    }

    @Getter
    @Setter
    public static class RequestParameters {
        @JsonProperty("busCount")
        private int busCount;
        @JsonProperty("expressbusCount")
        private int expressbusCount;
        @JsonProperty("subwayCount")
        private int subwayCount;
        @JsonProperty("airplaneCount")
        private int airplaneCount;
        @JsonProperty("locale")
        private String locale;
        @JsonProperty("endY")
        private String endY;
        @JsonProperty("endX")
        private String endX;
        @JsonProperty("wideareaRouteCount")
        private int wideareaRouteCount;
        @JsonProperty("subwayBusCount")
        private int subwayBusCount;
        @JsonProperty("startY")
        private String startY;
        @JsonProperty("startX")
        private String startX;
        @JsonProperty("ferryCount")
        private int ferryCount;
        @JsonProperty("trainCount")
        private int trainCount;
        @JsonProperty("reqDttm")
        private String reqDttm;
    }

    @Getter
    @Setter
    public static class Plan {
        @JsonProperty("itineraries")
        private List<Itinerary> itineraries;
    }

    @Getter
    @Setter
    public static class Itinerary {
        @JsonProperty("fare")
        private Fare fare;
        @JsonProperty("totalTime")
        private int totalTime;
        @JsonProperty("legs")
        private List<Leg> legs;
        @JsonProperty("totalWalkTime")
        private int totalWalkTime;
        @JsonProperty("transferCount")
        private int transferCount;
        @JsonProperty("totalDistance")
        private int totalDistance;
        @JsonProperty("pathType")
        private int pathType;
        @JsonProperty("totalWalkDistance")
        private int totalWalkDistance;
    }

    @Getter
    @Setter
    public static class Fare {
        @JsonProperty("regular")
        private Regular regular;
    }

    @Getter
    @Setter
    public static class Regular {
        @JsonProperty("totalFare")
        private int totalFare;
        @JsonProperty("currency")
        private Currency currency;
    }

    @Getter
    @Setter
    public static class Currency {
        @JsonProperty("symbol")
        private String symbol;
        @JsonProperty("currency")
        private String currency;
        @JsonProperty("currencyCode")
        private String currencyCode;
    }

    @Getter
    @Setter
    public static class Leg {
        @JsonProperty("mode")
        private String mode;
        @JsonProperty("sectionTime")
        private int sectionTime;
        @JsonProperty("distance")
        private int distance;
        @JsonProperty("start")
        private Start start;
        @JsonProperty("end")
        private End end;
        @JsonProperty("steps")
        private List<Step> steps;
        @JsonProperty("routeColor")
        private String routeColor;
        @JsonProperty("type")
        private int type;
        @JsonProperty("route")
        private String route;
        @JsonProperty("routeId")
        private String routeId;
        @JsonProperty("service")
        private int service;
        @JsonProperty("passStopList")
        private PassStopList passStopList;
        @JsonProperty("passShape")
        private PassShape passShape;
        @JsonProperty("Lane")
        private List<Lane> lane; // Add this line
    }

    @Getter
    @Setter
    public static class Lane {
        @JsonProperty("routeColor")
        private String routeColor;
        @JsonProperty("route")
        private String route;
        @JsonProperty("routeId")
        private String routeId;
        @JsonProperty("service")
        private int service;
        @JsonProperty("type")
        private int type;
    }

    @Getter
    @Setter
    public static class Start {
        @JsonProperty("name")
        private String name;
        @JsonProperty("lon")
        private double lon;
        @JsonProperty("lat")
        private double lat;
    }

    @Getter
    @Setter
    public static class End {
        @JsonProperty("name")
        private String name;
        @JsonProperty("lon")
        private double lon;
        @JsonProperty("lat")
        private double lat;
    }

    @Getter
    @Setter
    public static class Step {
        @JsonProperty("streetName")
        private String streetName;
        @JsonProperty("distance")
        private int distance;
        @JsonProperty("description")
        private String description;
        @JsonProperty("linestring")
        private String linestring;
    }

    @Getter
    @Setter
    public static class PassStopList {
        @JsonProperty("stationList")
        private List<Station> stationList;
    }

    @Getter
    @Setter
    public static class Station {
        @JsonProperty("index")
        private int index;
        @JsonProperty("stationName")
        private String stationName;
        @JsonProperty("lon")
        private String lon;
        @JsonProperty("lat")
        private String lat;
        @JsonProperty("stationID")
        private String stationID;
    }

    @Getter
    @Setter
    public static class PassShape {
        @JsonProperty("linestring")
        private String linestring;
    }
}