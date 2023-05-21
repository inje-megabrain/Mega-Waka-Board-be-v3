package mega.waka.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mega.waka.entity.editor.SevenDaysEditor;
import mega.waka.entity.language.SevenDaysLanguage;
import mega.waka.entity.project.SevenDaysProject;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseInfoDto {
    private String name;
    private List<SevenDaysEditor> Editors = new ArrayList<>();
    private List<SevenDaysLanguage> Languages = new ArrayList<>();
    private List<SevenDaysProject> Proejects = new ArrayList<>();
}
