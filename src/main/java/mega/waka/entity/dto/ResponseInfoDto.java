package mega.waka.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseInfoDto {
    private String name;
    private List<JSONArray> Editors = new ArrayList<>();
    private List<JSONArray> Languages = new ArrayList<>();
    private List<JSONArray> Proejects = new ArrayList<>();
}
