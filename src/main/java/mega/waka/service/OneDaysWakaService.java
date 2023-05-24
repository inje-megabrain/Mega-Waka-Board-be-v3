package mega.waka.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import mega.waka.entity.Member;
import mega.waka.entity.dto.ResponseInfoThirtyDaysDto;
import mega.waka.entity.dto.ResponseThirtyDaysSummariesDto;
import mega.waka.entity.editor.OneDaysEditor;
import mega.waka.entity.language.OneDaysLanguage;
import mega.waka.entity.project.OneDaysProject;
import mega.waka.repository.MemberRepository;
import mega.waka.repository.editor.OneDayEditorRepository;
import mega.waka.repository.language.OneDayLanguageRepository;
import mega.waka.repository.project.OneDayProjectRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.*;

@Service
public class OneDaysWakaService {

    private final OneDayProjectRepository oneDaysProjectRepository;

    private final OneDayEditorRepository oneDaysEditorRepository;
    private final OneDayLanguageRepository oneDaysLanguageRepository;
    private final MemberRepository memberRepository;

    private Map<String,String> languageList = new HashMap<>();
    private Map<String,String> projectList = new HashMap<>();
    private Map<String,String> editList = new HashMap<>();


    public OneDaysWakaService(OneDayProjectRepository oneDaysProjectRepository, OneDayEditorRepository oneDaysEditorRepository, OneDayLanguageRepository oneDaysLanguageRepository, MemberRepository memberRepository) {
        this.oneDaysProjectRepository = oneDaysProjectRepository;
        this.oneDaysEditorRepository = oneDaysEditorRepository;
        this.oneDaysLanguageRepository = oneDaysLanguageRepository;
        this.memberRepository = memberRepository;
    }
    public ResponseThirtyDaysSummariesDto get_One_Month(UUID id){
        Optional<Member> findMember = memberRepository.findById(id);
        findMember.orElseThrow(()->{
            throw new IllegalArgumentException("해당하는 멤버가 없습니다.");
        });
        List<OneDaysProject> oneDaysProjects  = new ArrayList<>();
        List<OneDaysEditor> oneDaysEditors = new ArrayList<>();
        List<OneDaysLanguage> oneDaysLanguages = new ArrayList<>();

        for (OneDaysProject project : findMember.get().getOneDaysProjects()) {
            if(project.getCreateDate().isAfter(LocalDate.now().minusDays(30)) || project.getCreateDate().isEqual(LocalDate.now())){
                oneDaysProjects.add(project);
            }
        }
        for(OneDaysLanguage language : findMember.get().getOneDaysLanguages()){
            if(language.getCreateDate().isAfter(LocalDate.now().minusDays(30))|| language.getCreateDate().isEqual(LocalDate.now())){
                oneDaysLanguages.add(language);
            }
        }
        for(OneDaysEditor editor : findMember.get().getOneDaysEditors()){
            if(editor.getCreateDate().isAfter(LocalDate.now().minusDays(30)) && editor.getCreateDate().isBefore(LocalDate.now()))
                oneDaysEditors.add(editor);
        }
        ResponseThirtyDaysSummariesDto dto = new ResponseThirtyDaysSummariesDto();

        oneDaysLanguages.clear();
        oneDaysEditors.clear();
        oneDaysProjects.clear();
        return dto;
    }
    public void update_OneDays() {
        List<Member> members = memberRepository.findAll();
        String responseData="";
        try {
            RestTemplate restTemplate = new RestTemplate();
            String apiUrl = "https://wakatime.com/api/v1/users/current/summaries?range=yesterday";
            for (Member member : members) {

                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl);
                HttpHeaders headers = new HttpHeaders();
                headers.setBasicAuth(member.getSecretKey(),"");

                ResponseEntity<String> response = restTemplate.exchange(
                        builder.toUriString(),
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        String.class
                );
                responseData = response.getBody();
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(responseData);
                JSONArray data = (JSONArray) jsonObject.get("data");

                JSONObject range = (JSONObject) data.get(0);
                JSONObject date = (JSONObject) range.get("range");
                member.setOneDay(date.get("date").toString());
                memberRepository.save(member);
                JSONObject obj = (JSONObject) data.get(0);
                JSONArray languages = (JSONArray) obj.get("languages");
                JSONArray editors = (JSONArray) obj.get("editors");
                JSONArray projects = (JSONArray) obj.get("projects");

                if(languages.isEmpty() || editors.isEmpty() || projects.isEmpty()) continue;
                else {
                    set_Language(languages);
                    set_Project(projects);
                    set_Editor(editors);
                }
                set_Member_By_Language(member);
                set_Member_By_Editor(member);
                set_Member_By_Project(member);
                editList.clear();
                languageList.clear();
                projectList.clear();
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    private Map<String,String> set_Language(JSONArray languages){
        for(int j=0;j<languages.size();j++) {
            JSONObject index = (JSONObject) languages.get(j);
            String name = (String) index.get("name");
            Long hour = (Long) index.get("hours");
            Long min = (Long) index.get("minutes");
            if(min>=60){
                hour+=min/60;
                min = min%60;
            }
            if(languageList.containsKey(name)){
                String[] time = languageList.get(name).split(":");
                Long hour2 = Long.parseLong(time[0]);
                Long min2 = Long.parseLong(time[1]);
                hour += hour2;
                min += min2;
                if(min>=60){
                    hour+=min/60;
                    min = min%60;
                }
                languageList.replace(name,hour+":"+min);
            }
            else{

                languageList.put(name,hour+":"+min);
            }
        }

        return languageList;
    }
    private Map<String,String> set_Project(JSONArray projects){

        for(int j=0;j<projects.size();j++) {
            JSONObject index = (JSONObject) projects.get(j);
            String name = (String) index.get("name");
            Long hour = (Long) index.get("hours");
            Long min = (Long) index.get("minutes");
            if(min>=60){
                hour+=min/60;
                min = min%60;
            }
            if(projectList.containsKey(name)){
                String[] time = projectList.get(name).split(":");
                Long hour2 = Long.parseLong(time[0]);
                Long min2 = Long.parseLong(time[1]);
                hour += hour2;
                min += min2;
                if(min>=60){
                    hour+=min/60;
                    min = min%60;
                }
                projectList.replace(name,hour+":"+min);
            }
            else{

                projectList.put(name,hour+":"+min);
            }
        }
        return projectList;

    }
    private Map<String,String> set_Editor(JSONArray editor){

        for(int j=0;j<editor.size();j++) {
            JSONObject index = (JSONObject) editor.get(j);
            String name = (String) index.get("name");
            Long hour = (Long) index.get("hours");
            Long min = (Long) index.get("minutes");
            if(min>=60){
                hour+=min/60;
                min = min%60;
            }
            if(editList.containsKey(name)){
                String[] time = editList.get(name).split(":");
                Long hour2 = Long.parseLong(time[0]);
                Long min2 = Long.parseLong(time[1]);
                hour += hour2;
                min += min2;
                if(min>=60){
                    hour+=min/60;
                    min = min%60;
                }
                editList.replace(name,hour+":"+min);
            }
            else{

                editList.put(name,hour+":"+min);
            }
        }
        return editList;

    }

    private void set_Member_By_Language(Member member) {
        if (member.getOneDaysLanguages().size() == 0) {
            for (String key : languageList.keySet()) {
                OneDaysLanguage language = new OneDaysLanguage().builder()
                        .name(key)
                        .time(languageList.get(key))
                        .createDate(LocalDate.now().minusDays(1))
                        .member(member)
                        .build();
                oneDaysLanguageRepository.save(language);
                member.getOneDaysLanguages().add(language);
                memberRepository.save(member);
            }
        } else {
            for (int i = 0; i < member.getOneDaysLanguages().size(); i++) {
                boolean flag = false;
                String name = "";
                for (String key : languageList.keySet()) {
                    name = key;
                    if (member.getOneDaysLanguages().get(i).getName().equals(key) && member.getOneDaysLanguages().get(i).getCreateDate().equals(LocalDate.now().minusDays(1))) {
                        member.getOneDaysLanguages().get(i).setTime(languageList.get(key));
                        flag = true;
                    }
                    if(member.getOneDaysLanguages().get(i).getTime().equals("0:0")){
                        member.getOneDaysLanguages().remove(i);
                    }
                }
                if (flag == false) {
                    OneDaysLanguage language = new OneDaysLanguage().builder()
                            .name(name)
                            .time(languageList.get(name))
                            .createDate(LocalDate.now().minusDays(1))
                            .member(member)
                            .build();
                    oneDaysLanguageRepository.save(language);
                    member.getOneDaysLanguages().add(language);
                    memberRepository.save(member);
                }
            }
        }
    }
    private void set_Member_By_Project(Member member) {
        if(member.getOneDaysProjects().size()==0){
            for (String key : projectList.keySet()) {
                OneDaysProject project = new OneDaysProject().builder()
                        .name(key)
                        .time(projectList.get(key))
                        .createDate(LocalDate.now().minusDays(1))
                        .member(member)
                        .build();
                oneDaysProjectRepository.save(project);
                member.getOneDaysProjects().add(project);
                memberRepository.save(member);
            }
        }
        else{
            for(int i=0; i<member.getOneDaysProjects().size();i++){
                boolean flag = false;
                String name="";
                for(String key : projectList.keySet()){
                    name = key;
                    if(member.getOneDaysProjects().get(i).getName().equals(key) && member.getOneDaysProjects().get(i).getCreateDate().equals(LocalDate.now().minusDays(1))){
                        member.getOneDaysProjects().get(i).setTime(projectList.get(key));
                        flag=true;
                    }
                    if(member.getOneDaysProjects().get(i).getTime().equals("0:0")){
                        member.getOneDaysProjects().remove(i);
                    }
                }
                if(flag==false){
                    OneDaysProject project = new OneDaysProject().builder()
                            .name(name)
                            .time(projectList.get(name))
                            .createDate(LocalDate.now().minusDays(1))
                            .member(member)
                            .build();
                    oneDaysProjectRepository.save(project);
                    member.getOneDaysProjects().add(project);
                    memberRepository.save(member);
                }
            }
        }
    }
    private void set_Member_By_Editor(Member member) {
        if(member.getOneDaysEditors().size()==0){
            for (String key : editList.keySet()) {

                OneDaysEditor editor = new OneDaysEditor().builder()
                        .name(key)
                        .time(editList.get(key))
                        .createDate(LocalDate.now().minusDays(1))
                        .member(member)
                        .build();
                oneDaysEditorRepository.save(editor);
                member.getOneDaysEditors().add(editor);
                memberRepository.save(member);
            }
        }
        else{
            for(int i=0; i<member.getOneDaysEditors().size();i++){
                boolean flag = false;
                String name="";
                for(String key : editList.keySet()){
                    name = key;
                    if(member.getOneDaysEditors().get(i).getName().equals(key)&& member.getOneDaysEditors().get(i).getCreateDate().equals(LocalDate.now().minusDays(1))){
                        member.getOneDaysEditors().get(i).setTime(editList.get(key));
                        flag=true;
                    }
                    if(member.getOneDaysEditors().get(i).getTime().equals("0:0")){
                        member.getOneDaysEditors().remove(i);
                    }
                }
                if(flag==false){
                    OneDaysEditor editor = new OneDaysEditor().builder()
                            .name(name)
                            .time(editList.get(name))
                            .createDate(LocalDate.now().minusDays(1))
                            .member(member)
                            .build();
                    oneDaysEditorRepository.save(editor);
                    member.getOneDaysEditors().add(editor);
                    memberRepository.save(member);
                }
            }
        }
    }
}
