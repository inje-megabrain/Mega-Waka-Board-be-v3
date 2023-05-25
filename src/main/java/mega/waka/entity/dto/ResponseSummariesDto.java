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
    @Builder.Default
    private List<JSONArray> summariesEditors = new ArrayList<>();
    @Builder.Default
    private List<JSONArray> summariesLanguages = new ArrayList<>();
    @Builder.Default
    private List<JSONArray> summariesProjects = new ArrayList<>();
}
