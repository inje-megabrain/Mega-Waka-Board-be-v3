package mega.waka.entity.dto;

import lombok.*;
import mega.waka.entity.editor.SevenDaysEditor;
import mega.waka.entity.language.SevenDaysLanguage;
import mega.waka.entity.project.SevenDaysProject;
import org.json.simple.JSONArray;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseSummariesDto {

    private List<JSONArray> summariesEditors = new ArrayList<>();
    private List<JSONArray> summariesLanguages = new ArrayList<>();
    private List<JSONArray> summariesProjects = new ArrayList<>();
}
