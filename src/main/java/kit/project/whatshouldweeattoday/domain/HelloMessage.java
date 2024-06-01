package kit.project.whatshouldweeattoday.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class HelloMessage {

    private String name;

    public HelloMessage() {
    }

    public HelloMessage(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
